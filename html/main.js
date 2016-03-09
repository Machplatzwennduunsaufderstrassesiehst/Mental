
"use strict";

var name = "";
var serverConnection = null;

window.onload = function() {
    show("welcome");
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
    serverConnection.send(makeSimpleCmd("answer", "answer", Number(answer)));
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
    });
}


function openMainFrame() {
    show("mainFrame");
}
