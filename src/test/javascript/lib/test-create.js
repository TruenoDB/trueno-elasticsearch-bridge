"use strict";

/**
 * @author Edgardo A. Barsallo Yi (ebarsallo)
 * This module decription
 * @module path/moduleFileName
 * @see module:path/referencedModuleName
 */

/* import modules */
const Promise = require("bluebird");
const Socket = require("uws");

const dbName = "film";
const url = "ws://127.0.0.1:8007";

var ws = new Socket("ws://127.0.0.1:8007");

/* Create callbacks reference */
var callbacks = {};

ws.on("open", function open() {
    console.log('connected');
    create();
});

ws.on("error", function error() {
    console.log("Error connecting!");
});

ws.on("message", function(data, flags) {
    console.log("Message: " + data);
    var obj = JSON.parse(data);
    callbacks[obj.callbackIndex](obj);

    console.log("Message: " + data);
});

ws.on("close", function(code, message) {
    console.log("Disconnection: " + code + ", " + message);
});

/* the payload object */
var internal = {
    index: dbName
};
var counter = "create_1";

var payload = {
    callbackIndex: counter,
    action: "create",
    object: internal
};

function create() {
    console.log("send --> ", JSON.stringify(payload));
    ws.send(JSON.stringify(payload));
    /* adding callback */
    callbacks[counter] = function(results){
        console.log('done');
    };
}

/** Description of the class */
class className {

    /**
     * Create a template object.
     * @param {object} [param= {}] - Parameter with default value of object {}.
     */
    constructor(param = {}) {

        this._property = param.prop || 'someValue';
    }

    /**
     * Class function description.
     * @param {string} myParam - A string to be asignned asynchronously after 1 second.
     * @return {boolean} A true hardcoded value.
     */
    create(myParam) {


        console.log("send --> ", JSON.stringify(payload));
        ws.send(JSON.stringify(payload));
        /* adding callback */
        callbacks[counter] = function(results){
            console.log('done');
        };
    }
}


/* exporting the module */
module.exports = className;