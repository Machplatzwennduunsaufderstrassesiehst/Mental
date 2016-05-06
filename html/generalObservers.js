

function configureObservers() {
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(gameStringObserver);
    serverConnection.addObserver(showScoreboardObserver);
    serverConnection.addObserver(countdownObserver);
    serverConnection.addObserver(openMainFrameObserver);
    serverConnection.addObserver(playerStateObserver);
}




var gameStringObserver = new Observer("gameString", function(msg) {
    var gs = btoa(msg.gameString); // base64 encode
    setCookie("gameString", gs, 1000);
    byID("gameStringInput").value = gs;
    byID("gameString").innerHTML = "Dein Spielstand: " + gs;
});

var playerStateObserver = new Observer("scoreboard", function(msg) {
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var s = msg.scoreboard[i];
        if (s.highlight) { // the highlighted player is the user
            var copy = ["playerMoney", "playerName", "playerTitle", "playerLevel"];
            for (var i = 0; i < copy.length; i++) {
                player.set_(copy[i], s[copy[i]]);
            }
            break;
        }
    }
});

// opens game frames on exercise ---- TODO temporary solution??
var openMainFrameObserver = new Observer("exercise", function(msg) {
    //serverConnection.removeObserver(openMainFrameObserver);
    if (msg.exercise.type == "arithmetic") {
        navigation.openFrames(mainFrame);
    } else if (msg.exercise.type == "trainMap") {
        navigation.openFrames(trainGameMainFrame);
    }
});


