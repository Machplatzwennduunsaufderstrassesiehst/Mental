/*
 * (c) Sven Langner 2015
 * 
 * 
 */

// TODO <<<<<<<<<<<<< maybe remove this later
"use strict";

// sleep time between request queue checks
var actRate = 250;
var maxWaitTimeout = 2500;
var gameServerPort = 6382;
var pingServerPort = 6383;


    
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


var serverConnections = [];
function getConnectionByHost(host) {
    for (var i = 0; i < serverConnections.length; i++) {
        if (serverConnections[i].host == host) return serverConnections[i];
    }
}
// class constructor definition
/*
 * The "Subject" part of the Observer pattern. 
*/
function ServerConnection(host, port) {
    serverConnections.push(this);
    this.host = host;
    var socket = new WebSocket("ws://"+host+":"+String(port));
    var observers = [];
    var onopen = function(){};
    var self = this;
    
    socket.onopen = function(event) {
        onopen();
    }
    
    this.close = function() {
        socket.close();
        // remove this from serverConnections
        var i = serverConnections.indexOf(self);
        serverConnections.splice(i);
    }
    
    socket.onclose = function(event) {
        
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
    function startGetRequestScheduler() {
        if (currentRequest == null && commandRequestQueue.length > 0) {
            // get the first request on the queue and remove it from the queue
            currentRequest = commandRequestQueue.shift();
        }
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



function NetworkManager() {
    var openServerConnections = [];
    var localIP = false;
    var scanning = false;
    var onScanReady = false;
    updateLocalIP();
    
    // basic scan: nur 4. stelle der ip
    // leider viel zu langsam für scan auf 3. und 4. stelle, habe noch keinen besseren Ansatz...
    this.scan = function(onScanReadyHandler) {
        openServerConnections = [];
        if (onScanReadyHandler) onScanReady = onScanReadyHandler;
        if (!localIP) return;
        var localIP_ = localIP.split(".");
        scanning = true;
        tryPing("localhost");
        checkNext([localIP_[0], localIP_[1], localIP_[2], 0], true, 0);
    }
    
    this.scanManually = function(ip) {
        var s = new ServerConnection(ip, gameServerPort);
        s.setOnOpen(function(){addServer(s);listAvailableGames();});
    }
    
    function checkNext(ipArray, isBasic, c) {
        if (!scanning || c > 255*255) return;
        var ip = String(ipArray[0]) + "." + String(ipArray[1]) + "." + String((Math.floor(ipArray[2])+256)%256) + "." + String(ipArray[3]);
        tryPing(ip);
        ipArray[3] = ipArray[3] + 1;
        if (ipArray[3] >= 255) {
            if (isBasic) {setTimeout(onScanReady,100);console.log("scan ready");return;}
            ipArray[3] = 0;
            var diff = ipArray[2] - localIP[2];
            ipArray[2] -= (diff * 2 + Math.signum(diff)/2);
            console.log(ip);
        }
        setTimeout(function(){checkNext(ipArray, isBasic, c+1);}, 20);
    }
    
    function tryPing(ip) {
        try {
            var img = new Image();
            img.onload = function(){tryConnect(ip);};
            img.src = "http://" + ip + ":" + pingServerPort + "/ping.gif";
        } catch (e) {}
    }
    
    function tryConnect(ip) {
        console.log(ip);
        var s = new ServerConnection(ip, gameServerPort);
        s.setOnOpen(function(){addServer(s);});
    }
    
    this.setOnScanReady = function(f) {
        onScanReady = f;
    }
    
    function addServer(conn) {
        openServerConnections.push(conn);
    }
    
    this.getOpenServerConnections = function() {
        return openServerConnections;
    }
    
    this.popOpenConnection = function() {
        return openServerConnections.pop();
    }
    
    // kleines workaround um die lokale IP des Users zu ermitteln
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


