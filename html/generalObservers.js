

var gameStringObserver = new Observer("game_string", function(msg) {
    setCookie("gameString", msg.game_string, 1000);
    byID("gameStringInput").value = msg.game_string;
    byID("gameString").innerHTML = "Dein Spielstand: " + msg.game_string;
});

var timeLeftObserver = new Observer("time_left", function(msg) {
    countdownValue = msg.time;
});

