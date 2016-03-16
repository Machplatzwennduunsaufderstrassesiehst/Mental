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
var maxWaitTimeout = 2500;
var gameServerPort = 6382;


    
// class constructor definition
function GetRequest(jc, hl, eHl) {
    this.jsonCmd = jc; // is an object
    this.handler = hl; // is a function
    this.errorHandler = eHl; // is a function and optional
    this.errorCounter = 0;
    
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
    var self = this;
    
    socket.onopen = function(event) {
        onopen();
    }
    
    this.close = function() {
        socket.close();
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
            console.log("Received: " + event.data);
            if (currentRequest != null && "_"+currentRequest.jsonCmd.type+"_" == msg.type) {
                removeRequest(currentRequest);
                currentRequest.handler(msg);
                currentRequest = null;
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
        console.log("Sent: " + jsonStr);
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
            // get the first request on the queue and remove it from the queue
            currentRequest = commandRequestQueue.shift();
        }
        var timeout;
        if (currentRequest != null) {
            if (currentRequest.errorCounter < 3) {
                send(currentRequest.jsonCmd);
                currentRequest.errorCounter += 1;
            } else {
                currentRequest = null;
            }
        }
        setTimeout(function(){startGetRequestScheduler();},actRate);
    }
    
    startGetRequestScheduler();
}










function NetworkScanner() {
    var possibleHosts = [];
    var openServerConnections = [];
    var localIP = false;
    var scanning = false;
    updateLocalIP();
    
    // leider viel zu langsam, habe noch keinen besseren Ansatz...
    this.scan = function() {
        var ipParts = [getArrayFromTo(1,255),getArrayFromTo(1,255),getArrayFromTo(1,255),getArrayFromTo(1,255)];
        if (localIP) {
            var elements = localIP.split(".");
            ipParts[0] = [elements[0]];
            ipParts[1] = [elements[1]];
            ipParts[2].unshift(elements[2]); // add the 3rd part of local ip to the beginning of array, so it is checked first
        }
        console.log(ipParts);
        scanning = true;
        for (var i0 = 0; i0 < ipParts[0].length; i0++) {
            for (var i1 = 0; i1 < ipParts[1].length; i1++) {
                for (var i2 = 0; i2 < ipParts[2].length; i2++) {
                    for (var i3 = 0; i3 < ipParts[3].length; i3++) {
                        var ip = ipParts[0][i0]+"."+ipParts[1][i1]+"."+ipParts[2][i2]+"."+ipParts[3][i3];
                        tryConnect(ip);
                    }
                }
            }
        }
        scanning = false;
    }
    
    function tryConnect(ip) {
        try {
            if (scanning == true) {throw new Exception();}
            var s = new ServerConnection(ip, gameServerPort);
            s.setOnOpen(function(){addServer(s, ip);});
        } catch (e) {
            setTimeout(function(){tryConnect(ip);}, 2000);
        }
    }
    
    function addServer(conn, host) {
        openServerConnections.push(conn);
        possibleHosts.push(host);
    }
    
    function getArrayFromTo(from, to) {
        var a = [];
        for (var i = from; i <= to; i++) {
            a.push(i);
        }
        return a;
    }
    
    // kleiner hack um die
    function updateLocalIP(){
        window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;   //compatibility for firefox and chrome
        var pc = new RTCPeerConnection({iceServers:[]}); 
        pc.createDataChannel("");    //create a bogus data channel
        pc.createOffer(pc.setLocalDescription.bind(pc), uselessFunction);    // create offer and set local description
        pc.onicecandidate = function(ice){  //listen for candidate events
            if(!ice || !ice.candidate || !ice.candidate.candidate)  return;
            localIP = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/.exec(ice.candidate.candidate)[1];
            pc.onicecandidate = uselessFunction;
        };
    }
    
    this.getLocalIP = function() {
        return localIP;
    }
    
    this.getLocalIPSub = function() {
        var a = localIP.split(".");
        return a[0] + "." + a[1] + ".";
    }
}
