

function configureObservers() {
    serverConnection.addObserver(playerWonObserver);
    serverConnection.addObserver(exerciseObserver);
    serverConnection.addObserver(timeLeftObserver);
    serverConnection.addObserver(messageObserver);
    serverConnection.addObserver(gameStringObserver);
    serverConnection.addObserver(suggestionsObserver);
    serverConnection.addObserver(showScoreboardObserver);
    serverConnection.addObserver(countdownObserver);
    serverConnection.addObserver(reopenMainFrameObserver);
    serverConnection.addObserver(playerStateObserver);
    serverConnection.addObserver(beatbobObserver);
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
            player = s;
            break;
        }
    }
});

