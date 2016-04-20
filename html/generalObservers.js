

function configureObservers() {
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(gameStringObserver);
    serverConnection.addObserver(showScoreboardObserver);
    serverConnection.addObserver(countdownObserver);
    serverConnection.addObserver(reopenMainFrameObserver);
    serverConnection.addObserver(playerStateObserver);
}




var gameStringObserver = new Observer("gameString", function(msg) {
    setCookie("gameString", msg.gameString, 1000);
    byID("gameStringInput").value = msg.gameString;
    byID("gameString").innerHTML = "Dein Spielstand: " + msg.gameString;
});

var timeLeftObserver = new Observer("timeLeft", function(msg) {
    countdownValue = msg.time;
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
