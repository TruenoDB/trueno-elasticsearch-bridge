"use strict";

/* import modules */
const Promise = require("bluebird");
var Socket = require("socket.io-client");
const fs = require("fs");

var connectionOptions =  {
    "force new connection" : true,
    "reconnection": true,
    "reconnectionDelay": 2000,                  //starts with 2 secs delay, then 4, 6, 8, until 60 where it stays forever until it reconnects
    "reconnectionDelayMax" : 60000,             //1 minute maximum delay between connections
    "reconnectionAttempts": "Infinity",         //to prevent dead clients, having the user to having to manually reconnect after a server restart.
    "timeout" : 10000,                           //before connect_error and connect_timeout are emitted.
    "transports" : ["websocket"]                //forces the transport to be only websocket. Server needs to be setup as well/
};

var socket = Socket('http://localhost:8007',connectionOptions);
var limit = 10000000;
var counter = 0;
var bulkOperations = [];
const indexName = "movies";
const vertices = require('./vertices.json');
const batchSize  = 500;
let vQueue = Object.keys(vertices);
let total = vQueue.length, current = 0;
var strRequest = "persist";
//var strRequest = "destroy";

/**
 * Insert Bulk Vertices
 * @param operations
 */
function insertBulkVertices(operations) {

    return new Promise((resolve, reject) => {
        if(++counter <= limit){

            /* the payload object */
            var payload = {
                '@class': 'BulkObject',
                index: indexName,
                operations: operations
            };

            /* adding the payload */
            socket.emit('bulk', payload, function(results) {
                total++;
                resolve();
            });
        }
    });
}

/**
 * Pushes the operation string and parameter into the bulk list.
 * @param {string} op - The operation to be inserted into the bulk list.
 * @param {object} obj - The operation object.
 */
function pushOperation(op, obj){
    bulkOperations.push({op: op, content: obj});
}

/**
 *  insert vertices in batch function
 *
 */
function insertDeleteVertex(arr,op) {

    /* Persist all vertices */
    arr.forEach((vkey)=> {
        let v = {};
        v.id = null;
        v._label = null;
        v._prop = {};

        v.id = parseInt(vkey);

        for (let prop in vertices[vkey]) {
            if (prop == "label") {
                v._label = vertices[vkey][prop];
            } else {
                v._prop[prop] = vertices[vkey][prop];
            }
        }//for

        /* building the message */
        let payload = {
                graph: "movies",
                type: "v",
                obj: v
        };

        if(op === "persist"){
            pushOperation("ex_persist", payload);
        }else{
            pushOperation("ex_destroy", payload);
        }

        current++;
    });

    /* send bulk vertices to socket server */

    _bulk().then( (result) => {
        console.log("Vertices batch created.", current / total);
        /* Continue inserting */
        if (vQueue.length) {
            insertDeleteVertex(vQueue.splice(0, batchSize),op);
        }else{
            console.timeEnd("time");
            process.exit();
        }
    }, (error) => {
        console.log("Error: Vertices batch creation failed.", error, current / total);
        /* Continue inserting */
        if (vQueue.length) {
            insertDeleteVertex(vQueue.splice(0, batchSize),op);
        } else {
            process.exit();
        }

    });

}//insertVertex

/**
 * Execute all operation in the batch on one call.
 * @return {Promise} - Promise with the bulk operations results.
 */
function _bulk() {

    let operations = buildBulk(bulkOperations);

    /* if no operations to submit return empty promise */
    if (bulkOperations.length === 0) {
        return new Promise((resolve, reject)=> {
            resolve({
                took: 0,
                errors: false,
                items: []
            });
        });
    }

    /* return promise with the async operation */
    return new Promise((resolve, reject)=> {
        insertBulkVertices(operations).then((results)=> {
            bulkOperations = [];
            resolve(results);
        }, (err)=> {
            reject(err);
        });
    });

}//_bulk

/**
 * Builds bulk array request
 * @param b
 * @param reject
 * @returns {Array}
 */
function buildBulk(b, reject) {

    /* the bulk operations array */
    let operations = [];
    var arrOperations = [];

    /* for each operation build the bulk corresponding operation */
    b.forEach((e)=> {
        /* content to be constructed according to the operation */
        let meta = {};
        switch (e.op) {
            case 'ex_persist':
                meta = {"index": {"_type": e.content.type}};
                /* setting id if present */
                if (!Number.isInteger(e.content.obj.id)) {
                    reject('All vertices and edges must have an integer id');
                    return;
                }
                /* setting id if present */
                meta.index._id = e.content.obj.id;

                /* adding meta */
                operations.push(meta);

                /* adding content */
                operations.push(e.content.obj);

                /* I use this 2d array - adding operations */
                arrOperations.push(["index",e.content.type,e.content.obj.id.toString(),e.content.obj._prop.toString()]);

                //info[0] = index or delete
//                    info[1] = type {v, e}
//                    info[2] = id
//                    info[3] = '{name:pedro,age:15}'

                break;
            case 'ex_destroy':
                meta = {"delete": {"_type": e.content.type, _id: e.content.obj.id}};
                /* adding meta */
                operations.push(meta);

                /* I use this 2d array - adding operations */
                arrOperations.push(["delete",e.content.type,e.content.obj.id.toString()]);
                break;
        }
    });

    return arrOperations;
}

/**
 * Uses vertices queue to create bulkOperations
 */
function buildVerticesFromJSON(){
    /* Initiating vertex insertion */
    insertDeleteVertex(vQueue.splice(0, batchSize),strRequest);
}

/* on connection to the Server for elastic search */
socket.on('connect', function(){
    console.log('connected');

    console.time("time");
    /* start bulk read and request to socket server */
    buildVerticesFromJSON();

});

socket.on('disconnect', function(){
    console.log('disconnected');
});
