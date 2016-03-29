
"use strict";

var serverConnection = null;
var netManager = new NetworkManager();
var navi = new Navigation();

// DO AFTER HTML LOADED
window.onload = function() {
    openWelcomeFrame();
    
    // netManager konfigurieren
    netManager.setOnScanReady(function(){setTimeout(listAvailableGames, 1000);});
    
    // versuche die letzten anmeldedaten und scoreString aus den cookies zu lesen
    if (getCookie("userName") != "") byID("name").value = getCookie("userName");
    if (getCookie("ip") != "") byID("ip").value = getCookie("ip");
    if (getCookie("scoreString") != "") byID("scoreStringInput").value = getCookie("scoreString");
    if (getCookie("scoreString") != "") byID("scoreString").innerHTML = "Dein Punkte-Code: " + getCookie("scoreString");
    
    setTimeout(function(){byID("localIP").innerHTML = "Deine lokale IP: " + netManager.getLocalIP();},1000);
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

function openWelcomeFrame() {
    navi.clearHistory();
    navi.navigate("welcome");
    setDoOnEnter(function(){netManager.scanManually(byID('ip').value);openListGamesFrame();});
}

function openMainFrame() {
    navi.show("mainFrame");
    navi.clearHistory();
    setDoOnEnter(function(){sendAnswer();});
}

function openScoreboardFrame() {
    navi.show("scoreboardFrame");
    setDoOnEnter(uselessFunction);
    serverConnection.addObserver(reopenMainFrameObserver);
}

function openListGamesFrame() {
    navi.navigate("listGamesFrame");
    byID("gamesList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
}

function numpad(n) {
    byID("answer").value += String(n);
}
    
function numpadDel() {
    var v = String(byID("answer").value);
    byID("answer").value = v.substring(0, v.length-1);
}

// called once for every open connection stored in the NetworkManager
function listAvailableGames() {
    byID("gamesList").innerHTML = "";
    var connection = netManager.popOpenConnection();
    connection.communicate(makeGetCmd("get_games"), function(msg) {
        for (var j = 0; j < msg.games.length; j++) {
            var html = "";
            var game = msg.games[j];
            var players = "";
            for (var k = 0; k < game.players.length; k++) {
                players += game.players[k].playerName;
                if (k < game.players.length-1) players += ", ";
            }
            html += "<div class='gameListItem' onclick='joinGame(getConnectionByHost("+'"'+connection.host+'"'+"), "+game.game_id+");'>";
            html += "<p>"+game.name+" auf "+connection.host+" - Spieler: "+players+"</p>"
            html += "</div>";
            byID("gamesList").innerHTML += html;
        }
        if (netManager.getOpenServerConnections().length > 0) listAvailableGames();
    });
}

function joinGame(connection, gameId) {
    var name = byID("name").value;
    var scoreString = byID("scoreStringInput").value;
    setCookie("userName", name, 1000);
    for (var i = 0; i < serverConnections; i++) {
        if (serverConnections[i] != connection) connections[i].close();
    }
    serverConnection = connection;
    serverConnection.send(makeSetCmd("name", name));
    serverConnection.send(makeSetCmd("score_string", scoreString));
    serverConnection.send(makeSimpleCmd("join", "game_id", gameId));
    serverConnection.addObserver(playerWonObserver);
    serverConnection.addObserver(exerciseObserver);
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(messageObserver);
    serverConnection.addObserver(scoreStringObserver);
    setCookie("ip", connection.host, 1000);
    byID("back").style.display = "none";
    byID("disconnect").style.display = "inline";
    openMainFrame();
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

function disconnect() {
    for (var i = 0; i < serverConnections.length; i++) {
        serverConnections[i].close();
    }
    byID("disconnect").style.display = "none";
    byID("back").style.display = "inline";
    openWelcomeFrame();
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
    
