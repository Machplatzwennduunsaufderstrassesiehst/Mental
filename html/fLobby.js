
function listAvailableServers() {
    byID("serversList").innerHTML = "";
    while (serverConnections.length > 0) {
        var c = serverConnections.pop();
        var html = "";
        html += "<div class='selectListItem' onclick='joinServer(getConnectionByHost("+'"'+c.host+'"'+"));'>";
        html += "Trete dem Server auf " + c.host + " bei.";
        html += "</div>";
        byID("serversList").innerHTML += html;
    }
}

function listAvailableGames() {
    if (serverConnection == null) return;
    byID("gamesList").innerHTML = "";
    serverConnection.communicate(makeGetCmd("get_games"), function(msg) {
        for (var j = 0; j < msg.games.length; j++) {
            var html = "";
            var game = msg.games[j];
            var players = "";
            for (var k = 0; k < game.players.length; k++) {
                players += game.players[k].playerName;
                if (k < game.players.length-1) players += ", ";
            }
            html += "<div class='selectListItem' onclick='joinGame("+game.game_id+");'>";
            html += "<p>"+game.name+" auf "+serverConnection.host+" - Spieler: "+players+"</p>";
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
    openListGamesFrame();
    listAvailableGames();
}

function joinGame(gameId) {
    var name = byID("name").value;
    var gameString = byID("gameStringInput").value;
    setCookie("userName", name, 1000);
    serverConnection.send(makeSetCmd("name", name));
    serverConnection.send(makeSetCmd("game_string", gameString));
    serverConnection.send(makeSimpleCmd("join", "game_id", gameId));
    serverConnection.addObserver(playerWonObserver);
    serverConnection.addObserver(exerciseObserver);
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(messageObserver);
    serverConnection.addObserver(gameStringObserver);
    serverConnection.addObserver(suggestionsObserver);
    serverConnection.addObserver(showScoreboardObserver);
    setCookie("ip", serverConnection.host, 1000);
}

// OBSERVERS ===========================================================


