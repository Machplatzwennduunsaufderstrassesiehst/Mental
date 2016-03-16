
"use strict";

var serverConnection = null;

// DO AFTER HTML LOADED
window.onload = function() {
    show("welcome");
    setDoOnEnter(function(){byID("connect").click();});
    countdown();
    /*var fontSize = window.screen.availHeight / 15;
    window.document.body.style.fontSize = String(fontSize) + "px";*/
    setTimeout(function() {
        byID("answerFormSubmit").parentElement.style.position = "absolute";
        byID("answerFormSubmit").parentElement.style.top = "-200px";
    }, 1000);
    byID("answer").onfocus = function(){byID("numpadTable").style.opacity = 0;};
    byID("answer").onblur = function(){byID("numpadTable").style.opacity = 1;};
    if (document.fullscreenEnabled || document.webkitFullscreenEnabled || 
        document.mozFullScreenEnabled || document.msFullscreenEnabled) {
        setTimeout(function(){document.requestFullScreen();}, 50);
        setTimeout(function(){document.requestFullscreen();}, 50);
    }
}


function openMainFrame() {
    show("mainFrame");
    setDoOnEnter(function(){sendAnswer();});
}

function openScoreboardFrame() {
    show("scoreboardFrame");
    setDoOnEnter(uselessFunction);
}

function numpad(n) {
    byID("answer").value += String(n);
}
    
function numpadDel() {
    var v = String(byID("answer").value);
    byID("answer").value = v.substring(0, v.length-1);
}
        
var answered = false;
function sendAnswer() {
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

function connect() {
    var ip = document.getElementById("ip").value;
    var name = document.getElementById("name").value;
    serverConnection = new ServerConnection(ip, 6382);
    openMainFrame();
    serverConnection.setOnOpen(function() {
        serverConnection.send(makeSetCmd("name", name));
        serverConnection.send(makeSimpleCmd("create", "name", name));
        serverConnection.send(makeSimpleCmd("join", "game_id", 0));
        
        serverConnection.addObserver(playerWonObserver);
        serverConnection.addObserver(exerciseObserver);
        serverConnection.addObserver(timeLeftObserver);
        serverConnection.addObserver(messageObserver);
    });
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
    
