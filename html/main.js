

var serverConnection = null;
var netManager = new NetworkManager();
var player = {};

// DO AFTER HTML LOADED
window.onload = function() {
    byID("warning").style.display = "none";
    iconize();
    
    openWelcomeFrame();
    
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
}

function updateLocalIP() {
    netManager.updateLocalIP();
    setTimeout(function(){byID("localIP").innerHTML = "Deine lokale IP: " + netManager.getLocalIP();},1000);
}

function openWelcomeFrame() {
    show("welcome");
    setDoOnEnter(function(){netManager.scanManually(byID('ip').value);openListGamesFrame();});
    byID("disconnect").style.display = "none";
    byID("toLobby").style.display = "none";
}

function openMainFrame() {
    show("mainFrame");
    byID("answer").focus();
    setDoOnEnter(function(){sendAnswer();});
    byID("disconnect").style.display = "none";
    byID("toLobby").style.display = "inline";
}

function openScoreboardFrame() {
    show("scoreboardFrame");
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "none";
    byID("toLobby").style.display = "inline";
    byID("blurHack").focus();
    byID("voting").innerHTML = '<p>Voting starten... <span id="gameTimeoutCountdown"></span></p>';
}

function openListGamesFrame() {
    show("listGamesFrame");
    byID("gamesList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
    byID("toLobby").style.display = "none";
}

function openListServersFrame() {
    show("listServersFrame");
    byID("serverList").innerHTML = "laden...";
    setDoOnEnter(uselessFunction);
    byID("disconnect").style.display = "inline";
    byID("toLobby").style.display = "none";
}

function openShoppingFrame() {
    show("shoppingFrame");
    updateShopItems();
    byID("disconnect").style.display = "none";
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onlick = function(){byID("toLobby").onlick = oldonclick;openListGamesFrame();};
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
