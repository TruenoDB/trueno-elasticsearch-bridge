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

var socket = Socket('http://localhost:8009',connectionOptions);
var limit = 10000000;
var total = 0;
var counter = 0;
const INTERVAL = 1;
var queue = [];

/* input for test1 */
// const input = __dirname + '/directors-20k.csv';
const input = __dirname + '/../../../neo4j-benchmark/performance/data/films-50k.csv';

var hrstart;
var hrend;

function getDirector(director) {

    // director = "The Big Noise";

    var q = "{\"query\":{\"bool\":{\"filter\":{\"term\":{\"prop.filmId\":\""+director+"\"}}}}}";

    return new Promise((resolve, reject) => {
       // if(++counter <= limit){
            /* the payload object */
            var payload = {
                '@class': 'SearchObject',
                query: q,
                index: "films",
                type: "v",
                size: 1000
            };
            /* adding the payload */
            socket.emit('search', payload, function(results) {
               // console.log(results, ' --> ', q);
                var hrend = process.hrtime(hrstart);
                //console.info("Execution time: %dms", hrend[1]/1000000);
                total++;
                resolve();
            });
        //}
    });
}

function singleReads() {

    let directors = [];
    let promiseArray = [];
    let column;

    return new Promise((resolve, reject) => {

        fs.createReadStream(input)
            .pipe(csv({separator: ','}))
            .on('headers', function (headerList) {
                column = headerList[0]
            })
            .on('data', function(data) {
                // console.log('-->', data[column]);
                directors.push(data[column]);
            })
            .on('end', function() {
                console.log('----> end');

                hrstart = process.hrtime();

                for (let k in directors) {
                    promiseArray.push(
                            /* Retrieve films */
                            getDirector(directors[k])
                    )
                }

                Promise.all(promiseArray).then(() => {
                    hrend = process.hrtime(hrstart);
                    console.log('Single Reads     %ds %dms\t', hrend[0], hrend[1]/1000000,
                        total, "\trecords\t",(total/(hrend[0] + hrend[1]/1000000000)) + "\tdocs/s");
                    resolve();
                });
            })
    })
}

socket.on('connect', function(){
    console.log('connected');
    /* start reading */
    singleReads();
});

socket.on('disconnect', function(){
    console.log('disconnected');
});

