
var lobbyFrame = new Frame("listGamesFrame");
var serverLobbyFrame = new Frame("listServersFrame");

lobbyFrame.setOnOpen(function() {
    byID("gamesList").innerHTML = "laden...";
    setDoOnEnter(function(){joinGame(0);});
    byID("disconnect").style.display = "inline";
    listAvailableGames();
});

serverLobbyFrame.setOnOpen(function() {
    byID("serverList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
});

// FUNCTIONALITY =======================================================

function listAvailableServers() {
    byID("serversList").innerHTML = "";
    while (serverConnections.length > 0) {
        var c = serverConnections.pop();
        var html = "";
        html += "<div class='selectListItem btn' onclick='joinServer(getConnectionByHost("+'"'+c.host+'"'+"));'>";
        html += "Trete Server auf " + c.host + " bei.";
        html += "</div>";
        byID("serversList").innerHTML += html;
    }
}

function listAvailableGames() {
    if (serverConnection == null) return;
    byID("gamesList").innerHTML = "";
    serverConnection.communicate(makeGetCmd("getGames"), function(msg) {
        for (var j = 0; j < msg.games.length; j++) {
            var html = "";
            var game = msg.games[j];
            var players = "";
            for (var k = 0; k < game.players.length; k++) {
                players += game.players[k].playerName;
                if (k < game.players.length-1) players += ", ";
            }
            if (players == "") players = "keine";
            html += "<div style='padding-left: 5px;padding-right: 5px;' class='selectListItem' onclick='joinGame("+game.gameId+");'>";
            html += "<p>"+createIcon("account-login")+"Joinen: "+game.name+" auf "+serverConnection.host+" - Spieler: "+players+"</p>";
            html += "</div>";
            byID("gamesList").innerHTML += html;
        }
    });
}

function joinServer(connection) {
    for (var i = 0; i < serverConnections; i++) {
        if (serverConnections[i] != connection) connections[i].close();
    }
    serverConnection = connection;
    configureObservers();
    navigation.openFrames(lobbyFrame);
    listAvailableGames();
    var name = byID("name").value;
    var gameString_ = atob(byID("gameStringInput").value); // base64 decode
    setCookie("userName", name, 1000);
    serverConnection.send(makeSetCmd("name", name));
    serverConnection.send(makeSetCmd("gameString", gameString_));
}

function joinGame(gameId) {
    navigation.closeFrames(lobbyFrame);
    byID("toLobby").style.display = "inline";
    serverConnection.send(makeSimpleCmd("join", "gameId", gameId));
    showMsgBox("Warten auf andere Spieler...");
}

// OBSERVERS ===========================================================



