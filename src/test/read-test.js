"use strict";

/**
 * @author Edgardo A. Barsallo Yi (ebarsallo)
 * @modified Victor Santos Uceta (vsantos)
 * This module decription
 * @module path/moduleFileName
 * @see module:path/referencedModuleName
 */

/* import modules */
const Promise = require("bluebird");
const csv = require('csv-parser');
var Socket = require('socket.io-client');
const fs = require('fs');

var connectionOptions =  {
    "force new connection" : true,
    "reconnection": true,
    "reconnectionDelay": 2000,                  //starts with 2 secs delay, then 4, 6, 8, until 60 where it stays forever until it reconnects
    "reconnectionDelayMax" : 60000,             //1 minute maximum delay between connections
    "reconnectionAttempts": "Infinity",         //to prevent dead clients, having the user to having to manually reconnect after a server restart.
    "timeout" : 10000,                           //before connect_error and connect_timeout are emitted.
    "transports" : ["websocket"]                //forces the transport to be only websocket. Server needs to be setup as well/
}

var socket = Socket('http://localhost:8007',connectionOptions);
var limit = 10000000;
var total = 0;
var counter = 0;
const INTERVAL = 1;
var queue = [];

/* input for test1 */
const input = __dirname + '/directors-5000.csv';

var hrstart = [];
var hrend = [];

function getDirector(director) {

    director = "White Girls Got Azz Too";

    var q = "{\"term\":{\"prop.name\":\"" + director + "\"}}";

    return new Promise((resolve, reject) => {
        if(++counter <= limit){
            /* the payload object */
            var payload = {
                '@class': 'SearchObject',
                query: q,
                index: "movies",
                type: "v",
                size: 1000
            };
            /* adding the payload */
            socket.emit('search', payload, function(results) {
                var hrend = process.hrtime(hrstart);
                //console.info("Execution time: %dms", hrend[1]/1000000);
                total++;
                resolve();
            });
        }
    });
}

function singleReads() {

    let directors = [];
    let promiseArray = [];

    return new Promise((resolve, reject) => {

        fs.createReadStream(input)
            .pipe(csv({separator: ','}))
            .on('data', function(data) {
                directors.push(data.name);
            })
            .on('end', function() {
                console.log('----> end');

                hrstart[0] = process.hrtime();

                for (let k in directors) {
                    promiseArray.push(
                            /* Retrieve films */
                            getDirector(directors[k])
                    )
                }

                Promise.all(promiseArray).then(() => {
                    hrend[0] = process.hrtime(hrstart[0]);
                    console.log('Single Reads     %ds %dms', hrend[0][0], hrend[0][1]/1000000, (total/hrend[0][0]) + " docs/s");
                    resolve();
                });
            })
    })
}

function buildBulk(b, reject) {
    /* the bulk operations array */
    let operations = [];

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
                break;
            case 'ex_destroy':
                meta = {"delete": {"_type": e.content.type, _id: e.content.obj.id}};
                /* adding meta */
                operations.push(meta);
                break;
        }
    });

    return operations;
}

socket.on('connect', function(){
    console.log('connected');
    /* start reading */
    singleReads();
});

socket.on('disconnect', function(){
    console.log('disconnected');
});
