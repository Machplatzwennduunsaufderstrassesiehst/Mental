
/* global byID, uselessFunction, serverConnections, serverConnection, connections, navigation */

var lobbyFrame = new Frame("listGamesFrame");

lobbyFrame.setOnOpen(function() {
    byID("gamesList").innerHTML = "laden...";
    setDoOnEnter(function(){joinGame(0);});
    byID("disconnect").style.display = "inline";
    listAvailableGames();
});

// FUNCTIONALITY =======================================================

function listAvailableGames() {
    if (serverConnection === null) return;
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
            if (players === "") players = "keine";
            html += "<div style='padding-left: 5px;padding-right: 5px;' class='selectListItem' onclick='joinGame("+game.gameId+");'>";
            html += "<p>"+createIcon("account-login")+"Join: "+game.name+" auf "+serverConnection.host+" - Spieler: "+players+"</p>";
            html += "</div>";
            byID("gamesList").innerHTML += html;
        }
    });
}

function joinGame(gameId) {
    navigation.closeFrames(lobbyFrame);
    byID("toLobby").style.display = "inline";
    serverConnection.send(makeSimpleCmd("join", "gameId", gameId));
    showMsgBox("Warten auf Spieler...");
}

// OBSERVERS ===========================================================



