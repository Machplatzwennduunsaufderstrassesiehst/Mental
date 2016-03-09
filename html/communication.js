/*
 * (c) Sven Langner 2015
 * 
 * 
 * Old style classes are used due to the attempt to make this script compatible to almost all browsers.
 */

// TODO <<<<<<<<<<<<< maybe remove this later
"use strict";

// sleep time between GUI checks
var actRate = 500;

// class constructor definition
function GetRequest(jc, hl, eHl) {
    this.jsonCmd = jc; // is an object
    this.handler = hl; // is a function
    this.errorHandler = eHl; // is a function and optional
    
    // these functions are called by receive() when data is received
    this.ok = function(msg) {
        this.handler(msg);
    }
    this.notok = function(msg) {
        if (errorHandler != null) {
            this.errorHandler(msg);
        } else {
            
        }
    }
    this.data = function(msg) {
        this.handler(msg);
    }
}


// class constructor definition
/* This is part of an observer pattern.
 * 
 * This class allows you to create a connection between a cmdType (in JSON)
 * and a handler function.
*/
function Observer(cmdType, handler) {
    this.cmdType = cmdType;
    this.handler = handler;
}

// class constructor definition
/*
 * The "Subject" part of the Observer pattern. 
*/
function ServerConnection(host, port) {
    var socket = new WebSocket("ws://"+host+":"+String(port));
    var observers = [];
    var onopen = function(){};
    
    socket.onopen = function(event) {
        onopen();
    }
    
    socket.onclose = function(event) {
        show("welcome");
    }
    
    this.setOnOpen = function(func) {
        onopen = func;
    }
    
    socket.onmessage = function(event) {
        var msg = "";
        try {
            msg = JSON.parse(event.data);
            console.log(msg);
            if (currentRequest != null && "_"+currentRequest.jsonCmd.type+"_" == msg.type) {
                removeRequest(currentRequest);
                currentRequest.handler(msg);
            } else {
                notify(msg);
            }
        } catch (e) {
            console.log(e);
        }
    }
    
    // always call this function to add a command to the command queue
    function communicate(jsonCmd, handler, errorHandler) {
        commandRequestQueue.push(new GetRequest(jsonCmd, handler, errorHandler));
    }
    
    this.communicate = communicate;
    
    // send json command
    // >Object< that is then stringified must be passed!!
    function send(jsonCmd) {
        var jsonStr = "{}";
        try {
            jsonStr = JSON.stringify(jsonCmd);
        } catch (e) {
            console.log(e);
        }
        socket.send(jsonStr);
    }
    
    this.send = send;
     
    function addObserver(observer) {
        observers.push(observer);
    }
    function removeObserver(observer) {
        var pos = observers.indexOf(observer);
        observers.splice(pos);
    }
    this.addObserver = addObserver;
    this.removeObserver = removeObserver;
    
    function notify(msg) {
        var l = observers.length;
        for (var i = 0; i < l; i++) {
            if (observers[i].cmdType == msg.type) {
                observers[i].handler(msg);
            }
        }
    }
    
    
    var commandRequestQueue = new Array();
    var currentRequest = null;
    
    function removeRequest(request) {
        var index = commandRequestQueue.indexOf(request);
        commandRequestQueue.splice(index);
    }
    this.removeRequest = removeRequest;
    // scheduler function to schedule the CommandRequests on the Queue
    
    
    //  /\
    //  ||
    // add remote functionalities above

    function startGetRequestScheduler() {
        if (currentRequest == null && commandRequestQueue.length > 0) {
            // get thekk first request on the queue and remove it from the queue
            currentRequest = commandRequestQueue.shift();
        }
        if (currentRequest != null) {
            send(serverRemote.currentRequest.jsonCmd);
        }
        setTimeout(function(){startGetRequestScheduler();},actRate);
    }
    
    startGetRequestScheduler();
}
