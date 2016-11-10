
/* global byID, netManager */

var welcomeFrame = new Frame("welcome");

welcomeFrame.setOnOpen(function() {
    setDoOnEnter(function(){netManager.scanManually(byID('ip').value);});
    netManager.abortScanning();
    setTimeout(listAvailableServers, 1000);
});

welcomeFrame.setOnClose(function () {
    netManager.abortScanning();
});

// FUNCTIONALITY =======================================================

function onServerJoinClick() {
    netManager.scanManually(byID('ip').value);
}

function listAvailableServers() {
    byID("serversList").innerHTML = "Suche Server...";
    netManager.scan(function() {
        byID("serversList").innerHTML = "";
        while (serverConnections.length > 0) {
            var c = serverConnections.pop();
            var serverListItem = document.createElement("div");
            serverListItem.classList.add("selectListItem");
            serverListItem.onclick = (function(conn){return function() {
                joinServer(conn);
            }})(c);
            byID("serversList").appendChild(serverListItem);
            serverListItem.innerHTML = createIcon("account-login") + "Join Server on " + c.host + "!";
        }
    });
}

function joinServer(connection) {
    for (var i = 0; i < serverConnections; i++) {
        if (serverConnections[i] !== connection) connections[i].close();
    }
    console.log(connection);
    serverConnection = connection;
    configureObservers();
    navigation.openFrames(lobbyFrame);
    var name = byID("name").value;
    var gameString_ = atob(byID("gameStringInput").value); // base64 decode
    setCookie("userName", name, 1000);
    serverConnection.send(makeSetCmd("name", name));
    serverConnection.send(makeSetCmd("gameString", gameString_));
}
