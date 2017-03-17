"use strict";

/**
 * bulk-test.js
 * This file generates bulk requests to the ES socket server
 *
 * @version 0.0.0.1
 * @author  maverick-zhn(Servio Palacios)
 * @updated 2017.03.01
 *
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * Do NOT forget to reference the ORIGINAL author of the code.
 */

/* import modules */
var ProgressBar = require("progress");
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

/* socket.io */
var socket = Socket('http://localhost:8007',connectionOptions);

/* variables to keep track of batches and progress */
var limit = 10000000;
var counter = 0;

/* keeps the current batch to be sent */
var bulkOperations = [];

/* Elastic Search Index to be used */
const indexName = "movies";

/* source datasets/documents [download datasets from java-script-driver] */
const vertices = require('./vertices.json');
//const edges = require('./edges.json');

/* amount of records per request */
const batchSize  = 500;

/* set this variable to vertices if you want that kind of documents */
let vQueue = Object.keys(vertices);

/* set this variable to edges if you want that kind of documents */
//let vQueue = Object.keys(edges);

let total = vQueue.length, current = 0;

/* set this variables to simulate delete or insert */
var strRequest = "persist";
//var strRequest = "destroy";

/* showing request progress bar */
var previous = 0;
var bar = new ProgressBar("ElasticSearch Bulk Request [:bar] :percent :elapsed", {
    complete: '=',
    incomplete: ' ',
    width: 40,
    total: 100
});

/**
 * Insert/Delete Vertices/Edges per batches
 * @param operations -> [][]
 */
function bulkESRequest(operations) {

    return new Promise((resolve, reject) => {
        if(++counter <= limit){

            /* the payload object */
            var payload = {
                '@class': 'BulkObject',
                index: indexName,
                operations: operations
            };

            /* sending the payload */
            socket.emit('bulk', payload, function(results) {
                total++;
                resolve();
            });
        }
    });

}//bulkESRequest

/**
 * Pushes the operation string and parameter into the bulk list.
 * @param {string} op - The operation to be inserted into the bulk list.
 * @param {object} obj - The operation object.
 */
function pushOperation(op, obj){
    bulkOperations.push({op: op, content: obj});
}

/**
 *  insert/delete vertices/edges in batch function
 *
 */
function insertDeleteVertices(arr,op) {

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

    /* send vertices' batches to the socket server */
    _bulk().then( (result) => {

        var currentTick = Math.floor(current/total*100);
        if(currentTick>previous){
            previous = currentTick;
            bar.tick();
        }

        /* Continue inserting */
        if (vQueue.length) {
            insertDeleteVertices(vQueue.splice(0, batchSize),op);
        }else{
            console.timeEnd("time");
            process.exit();
        }

    }, (error) => {

        console.log("Error: Vertices batch creation failed.", error, current / total);
        /* Continue inserting */
        if (vQueue.length) {
            insertDeleteVertices(vQueue.splice(0, batchSize),op);
        } else {
            process.exit();
        }

    });

}//insertVertex

/**
 * Builds bulk array request
 * @param b
 * @param reject
 * @returns {Array}
 */
function buildBulkOperations(b, reject) {

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

                /* adding operations */
                arrOperations.push(["index",e.content.type,e.content.obj.id.toString(),
                                    JSON.stringify(e.content.obj._prop)]);

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

}//buildBulk

/**
 * Execute all operation in the batch on one call.
 * @return {Promise} - Promise with the bulk operations results.
 */
function _bulk() {

    let operations = buildBulkOperations(bulkOperations);

    /* if no operations to submit return empty promise */
    if (bulkOperations.length === 0) {

        return new Promise((resolve, reject)=> {
            resolve({
                took: 0,
                errors: false,
                items: []
            });
        });

    }//if no operations

    /* return promise with the async operation */
    return new Promise((resolve, reject)=> {

        /* call Elastic Search Bulk Request */
        bulkESRequest(operations).then((results)=> {

            /* prepare array to take new batch of vertices/edges */
            bulkOperations = [];

            /* resolve the promise with the results */
            resolve(results);

        }, (err)=> {

            reject(err);

        });

    });

}//_bulk

/**
 * Uses vertices queue to create bulkOperations
 */
function buildVerticesFromJSON(){
    var count = Object.keys(vertices).length;
    console.log(count);
    /* Initiating vertex insertion */
    insertDeleteVertices(vQueue.splice(0, batchSize),strRequest);
}

/* on connection to the Server for elastic search */
socket.on('connect', function(){
    console.log('connected');

    console.time("time");

    /* start bulk read and request to socket server */
    buildVerticesFromJSON();

});

/* on disconnection to the Server for elastic search */
socket.on('disconnect', function(){
    console.log('disconnected');
});
