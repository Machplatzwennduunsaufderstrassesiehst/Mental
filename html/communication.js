/*
 * (c) Sven Langner 2015
 * 
 * 
 */

/* global uselessFunction, welcomeFrame, welcomeFrame, navigation */

// sleep time between request queue checks
var actRate = 250;
var maxWaitTimeout = 5000;
var gameServerPort = 1297;
var pingServerPort = 6383;
var httpDirectory = "/mental";

if (!window.WebSocket) {
    if (window.WebkitWebSocket) {
        window.WebSocket = window.WebkitWebSocket;
    } else if (window.MozWebSocket) {
        window.WebSocket = window.MozWebSocket;
    }
}

// class constructor definition
function GetRequest(jc, hl, eHl) {
    this.jsonCmd = jc; // is an object
    this.handler = hl; // is a function
    var errorHandler = this.errorHandler = eHl; // is a function and optional
    this.errorCounter = 0;
    var sent = false;
    var failed = false;
    
    // these functions are called by receive() when data is received
    this.ok = function(msg) {
        this.handler(msg);
    };
    this.notok = function(msg) {
        if (errorHandler !== undefined) {
            errorHandler(msg);
        } else {
            
        }
    };
    this.data = function(msg) {
        this.handler(msg);
    };
    
    this.notifySent = function(msg) {
        sent = true;
        setTimeout(function(){failed = true;}, maxWaitTimeout);
    };
    
    this.hasFailed = function() {
        return failed;
    };
    
    this.isSent = function() {
        return sent;
    };
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
        if (serverConnections[i].host === host) return serverConnections[i];
    }
}
// class constructor definition
/*
 * The "Subject" part of the Observer pattern. 
*/
function ServerConnection(host, port) {
    serverConnections.push(this);
    this.host = host;
    var socket = new WebSocket("ws://"+host+":"+String(port) + httpDirectory);
    log("Connecting to " + "ws://"+host+":"+String(port) + httpDirectory);
    var observers = [];
    var onopen = function(){};
    var onclose = uselessFunction;
    var self = this;
    var manuallyClosed = false;
    
    showMsgBox("Verbindung zum Server wird hergestellt...");
    
    socket.onopen = function(event) {
        unshowMsgBox();
        onopen();
        log("Socket opened");
    };
    
    socket.onclose = function(event) {
        //unshowMsgBox();
        onclose();
        log("Socket closed");
        console.log(event);
        if (manuallyClosed) return; // normal socket close
        showMsgBox("Verbindung zum Server geschlossen (oder nicht möglich): Code: "+event.code+", Phase: " + event.eventPhase, "msgBoxError");
        setTimeout(function(){unshowMsgBox();}, 5000);
    };
    
    socket.onerror = function(event) {
        //unshowMsgBox();
        showMsgBox("Fehler beim Verbinden mit Server: Code: "+event.code+", Phase: "+event.eventPhase, "msgBoxError");
        setTimeout(function(){unshowMsgBox();}, 5000);
    };
    
    this.setOnOpen = function(func) {
        onopen = func;
    };
    
    this.setOnClose = function(func) {
        onclose = func;
    };
    
    this.close = function() {
        socket.close();
        // remove this from serverConnections
        serverConnections.remove(self);
        manuallyClosed = true;
        setTimeout(function(){manuallyClosed = false;}, 2000);
    };
    
    socket.onmessage = function(event) {
        var msg = "";
        if (ServerConnection.DEBUG) log(event.data);
        try {
            msg = JSON.parse(event.data);
            if (currentRequest !== null && "_"+currentRequest.jsonCmd.type+"_" === msg.type) {
                removeRequest(currentRequest);
                currentRequest.handler(msg);
                currentRequest = null;
            } else {
                notify(msg);
            }
        } catch (e) {
            log(e);
        }
    };
    
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
            log(e);
        }
        socket.send(jsonStr);
        if (ServerConnection.DEBUG) log("Sent: " + jsonStr);
    }
    
    this.send = send;
     
    function addObserver(observer) {
        observers.push(observer);
    }
    function removeObserver(observer) {
        observers.remove(observer);
    }
    this.addObserver = addObserver;
    this.removeObserver = removeObserver;
    
    function notify(msg) {
        var l = observers.length;
        for (var i = 0; i < l; i++) {
            if (!observers[i]) continue; // behalte ich für alle Fälle
            if (observers[i].cmdType === msg.type) {
                observers[i].handler(msg);
            }
        }
    }
    
    
    var commandRequestQueue = [];
    var currentRequest = null;
    
    function removeRequest(request) {
        commandRequestQueue.remove(request);
    }
    this.removeRequest = removeRequest;
    
    // scheduler function to schedule the CommandRequests on the Queue
    function startGetRequestScheduler() {
        if (!currentRequest && commandRequestQueue.length > 0) {
            // get the first request on the queue and remove it from the queue
            currentRequest = commandRequestQueue.shift();
        }
        if (currentRequest) {
            if (!currentRequest.isSent()) {
                send(currentRequest.jsonCmd);
                currentRequest.notifySent();
            }
            if (currentRequest.hasFailed()) {
                currentRequest = null;
            }
        }
        setTimeout(function(){startGetRequestScheduler();},actRate);
    }
    
    startGetRequestScheduler();
}
ServerConnection.DEBUG = false;













function NetworkManager() {
    var openServerConnections = [];
    var localIP = false;
    var scanning = false;
    var onScanReady = false;
    
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
    };
    
    this.scanManually = function(ip) {
        var s = new ServerConnection(ip, gameServerPort);
        setCookie("ip", ip, 1000);
        s.setOnOpen(function() {
            addServer(s);
            joinServer(s);
            s.setOnClose(function() {
                navigation.openFrames(welcomeFrame);
            });
        });
    };
    
    function checkNext(ipArray, isBasic, c) {
        if (!scanning || c > 255*255) return;
        var ip = String(ipArray[0]) + "." + String(ipArray[1]) + "." + String((Math.floor(ipArray[2])+256)%256) + "." + String(ipArray[3]);
        tryPing(ip);
        ipArray[3] = ipArray[3] + 1;
        if (ipArray[3] >= 255) {
            if (isBasic) {setTimeout(onScanReady,100);log("scan ready");return;}
            ipArray[3] = 0;
            var diff = ipArray[2] - localIP[2];
            ipArray[2] -= (diff * 2 + Math.signum(diff)/2);
            log(ip);
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
        log(ip);
        var s = new ServerConnection(ip, gameServerPort);
        s.setOnOpen(function(){addServer(s);});
    }
    
    this.setOnScanReady = function(f) {
        onScanReady = f;
    };
    
    function addServer(conn) {
        openServerConnections.push(conn);
    }
    
    this.getOpenServerConnections = function() {
        return openServerConnections;
    };
    
    this.popOpenConnection = function() {
        return openServerConnections.pop();
    };
    
    // kleines workaround um die lokale IP des Users zu ermitteln
    function updateLocalIP(){
        window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;   //compatibility for firefox and chrome
        if (!window.RTCPeerConnection) return;
        var pc = new RTCPeerConnection({iceServers:[]}); 
        pc.createDataChannel("");    //create a bogus data channel
        pc.createOffer(pc.setLocalDescription.bind(pc), uselessFunction);    // create offer and set local description
        pc.onicecandidate = function(ice){  //listen for candidate events
            if(!ice || !ice.candidate || !ice.candidate.candidate)  return;
            localIP = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/.exec(ice.candidate.candidate)[1];
            pc.onicecandidate = uselessFunction;
        };
    }
    this.updateLocalIP = updateLocalIP;
    
    this.getLocalIP = function() {
        return localIP;
    };
    
    this.getLocalIPSub = function() {
        var a = localIP.split(".");
        return a[0] + "." + a[1] + ".";
    };
}


