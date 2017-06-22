"use strict";

/**
 * @author Edgardo A. Barsallo Yi (ebarsallo)
 *
 * Test suite for Trueno Elasticsearch Bridge Server.
 * Persist test.
 * @module test/read-test
 * @see test:lib/test
 */

/* import modules */
const Promise = require("bluebird");
const csv = require('csv-parser');
const Socket = require('uws');
const fs = require('fs');

/* socket communication */
var ws;
/* server address */
var url = 'ws://localhost:8007';

/* input for test */
const input = __dirname + '/../data/film-10.csv';
/* database */
const dbName = "film";
/* lowerbound id used for inserted objects */
var baseId = 2000000;

function write(id, resolve, reject, total) {

    /* Callbacks */
    let cb_ok = 'films-ok-' + id;
    let cb_fail = 'films-fail-' + id;
    /* Object to be inserted */
    let obj = {};
    obj.id=baseId++;
    obj._prop={};
    obj._prop.age = id;
    obj._prop.complete = 971;
    obj._prop.gender = 0;
    obj._prop.region = 'Westworld';

    /* the payload object */
    var internal = {
        index: dbName,
        type: "v",
        id: obj.id,
        source: obj
    };

    var payload = {
        callbackIdOK: cb_ok,
        callbackIdError: cb_fail,
        action: "PERSIST",
        object: internal
    };

    ws.send(JSON.stringify(payload));

    /* adding OK callback */
    callbacks[cb_ok] = function(results){
        console.log(' [OK] ==> {', obj.id, '}', results.callbackId);
        results.resultSet.forEach((item) => {
            console.log(item._source);
        });

        rcvrequest++;
        /* Check halt condition */
        if(rcvrequest >= total){
            resolve();
        }
    };

    /* adding Failure callback */
    callbacks[cb_fail] = function(results){
        console.log('ERR [%d] ==> ', id, results);
        reject(results);
    };



}

function singleWrite() {

    let keys = [];
    let column;

    fs.createReadStream(input)
        .pipe(csv({separator: ','}))
        .on('headers', function (headerList) {
            column = headerList[0]
        })
        .on('data', function(data) {
            // console.log('-->', data[column]);
            keys.push(data[column]);
        })
        .on('end', function() {

            let total = Object.keys(keys).length;
            let bigPromise = new Promise((resolve, reject) => {
                for (let k in keys) {
                    /* Retrieve data */
                    write(keys[k], resolve, reject, total);
                }
            });

            bigPromise
                .then(count => {
                    console.log('*done!');
                })
                .catch(error => {
                    console.log('*error!');
                    console.log(error);
                })
        });
}

/* Create callbacks reference */
var callbacks  = {};
/* Received request */
var rcvrequest = 0;

ws = new Socket(url);

/* socket communication callbacks */
ws.on('open', function open() {
    console.log('connected');
    /* launch tests */
    singleWrite();
});

ws.on('error', function error(err) {
    console.log('Error connecting to ', url);
    console.log(err);
});

ws.on('message', function(data, flags) {
    let obj = JSON.parse(data);
    // console.log('--> ', obj.object[0]._source.prop.control);
    // control += obj.object[0]._source.prop.control;

    /* invoke the callback */
    callbacks[obj.callbackId](obj);
});

ws.on('close', function(code, message) {
    console.log('Disconnection: ' + code + ', ' + message);
});

