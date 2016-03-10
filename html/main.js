
"use strict";

var name = "";
var serverConnection = null;

window.onload = function() {
    show("welcome");
    countdown();
}

function nameChanged() {
    name = this.value;
}

function numpad(n) {
    byID("answer").value += String(n);
}
    
function numpadDel() {
    var v = String(byID("answer").value);
    byID("answer").value = v.substring(0, v.length-1);
} 

function sendAnswer() {
    var answer = byID("answer").value;
    serverConnection.communicate(makeSimpleCmd("answer", "answer", Number(answer)), function(msg) {
            if (msg.isCorrect) {
                byID("answer").style.backgroundColor = "#dfd";
            } else {
                byID("answer").style.backgroundColor = "#fdd";
            }
            setTimeout(function(){
                byID("answer").style.backgroundColor = "#fff";
            }, 1000);
        });
    byID("answer").value = "";
}

function connect() {
    var ip = document.getElementById("ip").value;
    var name = document.getElementById("name").value;
    serverConnection = new ServerConnection(ip, 6382);
    openMainFrame();
    serverConnection.setOnOpen(function() {
        serverConnection.send(makeSetCmd("name", name));
        serverConnection.send(makeSimpleCmd("create", "name", name));
        serverConnection.send(makeSimpleCmd("join", "game_id", 0));
        
        serverConnection.addObserver(new Observer("player_won", function(msg) {
            var s = msg.playerName + " hat die Aufgabe gel&ouml;st!!";
            infoBox(s);
        }));
        serverConnection.addObserver(new Observer("exercise", function(msg) {
            var ex = msg.exercise;
            byID("exercise").innerHTML = ex + " ?";
        }));
        serverConnection.addObserver(new Observer("time_left", function(msg) {
            countdownValue = msg.time;
        }));
    });
}


function openMainFrame() {
    show("mainFrame");
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


