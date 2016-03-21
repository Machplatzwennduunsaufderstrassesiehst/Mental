
"use strict";

var serverConnection = null;
var netManager = new NetworkManager();
var navi = new Navigation();

// DO AFTER HTML LOADED
window.onload = function() {
    navi.navigate("welcome");
    
    // netManager konfigurieren
    netManager.setOnScanReady(function(){setTimeout(listAvailableGames, 1000);});
    
    // versuche die letzten anmeldedaten aus den cookies zu lesen
    if (getCookie("userName") != "") byID("name").value = getCookie("userName");
    if (getCookie("ip") != "") byID("ip").value = getCookie("ip");
    
    setTimeout(function(){byID("localIP").innerHTML = "Deine lokale IP: " + netManager.getLocalIP();},1000);
    setDoOnEnter(function(){byID("connect").click();});
    setTimeout(function() {
        byID("answerFormSubmit").parentElement.style.position = "absolute";
        byID("answerFormSubmit").parentElement.style.top = "-200px";
    }, 1000);
    byID("answer").onfocus = function(){byID("numpadTable").style.opacity = 0;};
    byID("answer").onblur = function(){byID("numpadTable").style.opacity = 1;};
    
    countdown();
    
    byID("ip").onkeyup = function(){if (byID("ip").value == "") byID("ip").value = netManager.getLocalIPSub();};
    byID("ip").onfocus = byID("ip").onkeyup;
}


function openMainFrame() {
    navi.show("mainFrame");
    setDoOnEnter(function(){sendAnswer();});
}

function openScoreboardFrame() {
    navi.show("scoreboardFrame");
    setDoOnEnter(uselessFunction);
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

function listAvailableGames() {
    var openConnections = netManager.getOpenServerConnections();
    byID("gamesList").innerHTML = "";
    for (var i = 0; i < openConnections.length; i++) {
        var conn = openConnections[i];
        conn.communicate(makeGetCmd("get_games"), function(msg) {
            for (var j = 0; j < msg.games.length; j++) {
                var html = "";
                var game = msg.games[j];
                var players = "";
                for (var k = 0; k < game.players.length; k++) {
                    player += game.players[k].playerName;
                    if (k < game.players.length-1) player += ", ";
                }
                html += "<div class='gameListItem' id='game"+conn.host+""+game.game_id+"'>";
                html += "<p>"+game.name+" on "+conn.host+" - Players: "+"</p>"
                html += "</div>";
                byID("gamesList").innerHTML += html;
                byID("game"+conn.host+""+game.game_id).onclick = function() {
                    joinGame(conn, game.game_id);
                }
                //setTimeout(new function(conn, game){byID("game"+conn.host+""+game.game_id).onclick = function() {joinGame(conn, game.game_id);}}(conn, game), 20);
            }
        });
    }
}

function joinGame(connection, gameId) {
    var name = document.getElementById("name").value;
    setCookie("userName", name, 1000);
    if (isMobile()) fullScreen(byID("page_"));
    serverConnection = connection;
    serverConnection.send(makeSimpleCmd("join", "game_id", gameId));
    serverConnection.send(makeSetCmd("name", name));
    serverConnection.addObserver(playerWonObserver);
    serverConnection.addObserver(exerciseObserver);
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(messageObserver);
    setCookie("ip", connection.host, 1000);
    byID("back").style.display = "none";
    byID("disconnect").style.display = "inline";
    openMainFrame();
}
        
var answered = false;
function sendAnswer() {
    console.log("sendAnswer");
    if (answered) {return;}
    answered = true;
    var answer = byID("answer").value;
    serverConnection.communicate(makeSimpleCmd("answer", "answer", Number(answer)), function(msg) {
            answered = false;
            if (msg.isCorrect) {
                byID("answer").style.backgroundColor = "#dfd";
                byID("answer").placeholder = "Richtig!";
            } else {
                byID("answer").style.backgroundColor = "#fdd";
                byID("answer").placeholder = "Falsch!";
            }
            setTimeout(function(){
                byID("answer").style.backgroundColor = "#fff";
            }, 1000);
        });
    byID("answer").value = "";
}

function disconnect() {
    if (serverConnection) {
        serverConnection.close();
    }
    byID("disconnect").style.display = "none";
    byID("back").style.display = "inline";
    navi.show("welcome");
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
    
