
"use strict";

var serverConnection = null;
var netManager = new NetworkManager();

// DO AFTER HTML LOADED
window.onload = function() {
    openWelcomeFrame();
    
    // netManager konfigurieren
    netManager.setOnScanReady(function(){setTimeout(listAvailableGames, 1000);});
    
    // versuche die letzten anmeldedaten und gameString aus den cookies zu lesen
    if (getCookie("userName") != "") byID("name").value = getCookie("userName");
    if (getCookie("ip") != "") byID("ip").value = getCookie("ip");
    if (getCookie("gameString") != "") byID("gameStringInput").value = getCookie("gameString");
    if (getCookie("gameString") != "") byID("gameString").innerHTML = "Alter Spielstand: " + getCookie("gameString");
    
    updateLocalIP();
    setDoOnEnter(function(){byID("connect").click();});
    setTimeout(function() {
        byID("answerFormSubmit").parentElement.style.position = "absolute";
        byID("answerFormSubmit").parentElement.style.top = "-200px";
    }, 1000);
    byID("answer").onfocus = function(){byID("numpadTable").style.opacity = 0;};
    byID("answer").onblur = function(){byID("numpadTable").style.opacity = 1;};
    
    countdown();
    
    byID("ip").onfocus = function(){if (byID("ip").value == "") byID("ip").value = netManager.getLocalIPSub();};
}

function updateLocalIP() {
	netManager.updateLocalIP();
	setTimeout(function(){byID("localIP").innerHTML = "Deine lokale IP: " + netManager.getLocalIP();},1000);
}

function openWelcomeFrame() {
    show("welcome");
    setDoOnEnter(function(){netManager.scanManually(byID('ip').value);openListGamesFrame();});
    byID("disconnect").style.display = "none";
    byID("leaveGame").style.display = "none";
}

function openMainFrame() {
    show("mainFrame");
    byID("answer").focus();
    setDoOnEnter(function(){sendAnswer();});
    byID("disconnect").style.display = "none";
    byID("leaveGame").style.display = "inline";
}

function openScoreboardFrame() {
    show("scoreboardFrame");
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
    byID("leaveGame").style.display = "none";
    byID("blurHack").focus();
    byID("voting").innerHTML = '<p>Das Voting f&uuml;r die n&auml;chste Runde started in <span id="gameTimeoutCountdown"></span>!</p>';
}

function openListGamesFrame() {
    show("listGamesFrame");
    byID("gamesList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
    byID("leaveGame").style.display = "none";
}

function openListServersFrame() {
    show("listServersFrame");
    byID("serverList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
    byID("leaveGame").style.display = "none";
}

function numpad(n) {
    byID("answer").value += String(n);
}

function numpadDel() {
    var v = String(byID("answer").value);
    byID("answer").value = v.substring(0, v.length-1);
}

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
        
var alreadyAnswered = false;
function sendAnswer() {
    console.log("sendAnswer");
    if (alreadyAnswered) {return;}
    alreadyAnswered = true;
    setTimeout(function(){alreadyAnswered = false;}, 1000); // hier lieber ein Timeout, da es ja sein kann, dass keine Antwort vom Server kommt (dann waere diese Methode für immer gelockt!)
    var answer = byID("answer").value;
    serverConnection.communicate(makeSimpleCmd("answer", "answer", Number(answer)), function(msg) {
        if (msg.isCorrect) {
            byID("answer").style.backgroundColor = "#afa";
            byID("answer").placeholder = "Richtig!";
        } else {
            byID("answer").style.backgroundColor = "#faa";
            byID("answer").placeholder = "Falsch!";
            byID("answer").value = ""; // bei einer falschen Antwort wird das ergebnis gelöscht, bei einer richtigen Antwort bleibt das Ergebnis stehen, bis die nächste Aufgabe kommt
        }
        setTimeout(function(){
            byID("answer").style.backgroundColor = "#fff";
        }, 1000);
    });
}

function leaveGame() {
    serverConnection.send(makeSimpleCmd("leave", "x", ""));
    openListGamesFrame();
    listAvailableGames();
}

function disconnect() {
    for (var i = 0; i < serverConnections.length; i++) {
        serverConnections[i].close();
    }
    openWelcomeFrame();
}

function listSuggestions(suggestions) {
    var html = "";
    var scoreboardWidth = byID("scoreboard").style.width;
    for (var i = 0; i < suggestions.length; i++) {
        var suggestion = suggestions[i];
        html += "<div style='width:"+scoreboardWidth+"px' class='selectListItem' onclick='vote("+suggestion.suggestionID+");'>";
        html += "<span style='float:right;border-radius:0.5em;'>"+suggestion.votes+"</span>";
        html += "<span>"+suggestion.suggestionName+"</span>";
        html += "</div>";
    }
    byID("voting").innerHTML = html;
}

function vote(suggestionIndex) {
    serverConnection.send(makeSimpleCmd("vote", "suggestionID", suggestionIndex));
}





function infoBox(message) {
    byID("infoboxContent").innerHTML = message;
    byID("infobox").style.display = "block";
    byID("infobox").style.top = "-4em";
    byID("infobox").style.top = "1em";
    //byID("infobox").style.opacity = 1;
    setTimeout(function() {
        //byID("infobox").style.opacity = 0;
        byID("infobox").style.top = "-4em";
    }, 2000);
    setTimeout(function() {
        byID("infobox").style.display = "none";
    }, 3500);
}
    
