

/* global serverConnection, byID, player, navigation, mainFrame, scoreboardFrame, mainTrainGameFrame, countdownFrame */

function configureObservers() {
    serverConnection.addObserver(gameStringObserver);
    serverConnection.addObserver(showScoreboardObserver);
    serverConnection.addObserver(countdownObserver);
    serverConnection.addObserver(openMainFrameObserver);
    serverConnection.addObserver(playerStateObserver);
}

// GENERAL OR FRAME OPENING OBSERVERS =========================================================================

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
            var copy = ["playerMoney", "playerName", "playerTitle", "playerLevel", "playerSpins"];
            for (var i = 0; i < copy.length; i++) {
                player.set_(copy[i], s[copy[i]]);
            }
            break;
        }
    }
});

var openMainFrameObserver = new Observer("showExercises", function(msg) {
    if (msg.exerciseType === "arithmetic") {
        navigation.openFrames(mainFrame);
    } else if (msg.exerciseType === "trainMap") {
        navigation.openFrames(mainTrainGameFrame);
    }
});

var showScoreboardObserver = new Observer("showScoreboard", function(msg) {
    navigation.openFrames(scoreboardFrame);
});

var countdownObserver = new Observer("countdown", function(msg) {
    countdownFrame["countdownTime"] = msg.time;
    navigation.openFrames(countdownFrame);
});
