

var serverConnection = null;
var netManager = new NetworkManager();
var player = {};

// DO AFTER HTML LOADED
window.onload = function() {
    byID("warning").style.display = "none";
    iconize();
    
    navigation.openFrames(welcomeFrame);
    
    // netManager konfigurieren
    netManager.setOnScanReady(function(){setTimeout(listAvailableGames, 1000);});
    
    // versuche die letzten anmeldedaten und gameString aus den cookies zu lesen
    if (getCookie("userName") != "") byID("name").value = getCookie("userName");
    if (getCookie("ip") != "") byID("ip").value = getCookie("ip");
    if (getCookie("gameString") != "") byID("gameStringInput").value = getCookie("gameString");
    if (getCookie("gameString") != "") byID("gameString").innerHTML = "Alter Spielstand: " + getCookie("gameString");
    
    setTimeout(function(){updateLocalIP();},100);
    setDoOnEnter(function(){byID("connect").click();});
    setTimeout(function() {
        byID("answerFormSubmit").parentElement.style.position = "absolute";
        byID("answerFormSubmit").parentElement.style.top = "-200px";
    }, 1000);
    byID("answer").onfocus = function(){byID("numpadTable").style.opacity = 0;};
    byID("answer").onblur = function(){byID("numpadTable").style.opacity = 1;};
    
    countdown();
    
    byID("ip").onfocus = function(){if (byID("ip").value == "") byID("ip").value = netManager.getLocalIPSub();};
    byID("ip").onkeyup = function(){
        if (byID("ip").value == "") setTimeout(function(){if (byID("ip").value == "") byID("ip").value = netManager.getLocalIPSub();}, 1000);
    }
}

function updateLocalIP() {
    netManager.updateLocalIP();
    setTimeout(function(){byID("localIP").innerHTML = "Deine lokale IP: " + netManager.getLocalIP();},1000);
}

function leaveGame() {
    serverConnection.send(makeSimpleCmd("leave", "x", ""));
    navigation.openFrames(lobbyFrame);
}

function disconnect() {
    for (var i = 0; i < serverConnections.length; i++) {
        serverConnections[i].close();
    }
    navigation.openFrames(welcomeFrame);
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
