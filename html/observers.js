
var playerWonObserver = new Observer("player_won", function(msg) {
    displayMessage(msg.playerName + " hat diese Runde gewonnen!");
    countdownValue = 0;
});

var exerciseObserver = new Observer("exercise", function(msg) {
    var ex = msg.exercise;
    byID("exercise").innerHTML = ex + " = ";
    byID("answer").placeholder = "?";
});

var timeLeftObserver = new Observer("time_left", function(msg) {
    countdownValue = msg.time;
});

var messageObserver = new Observer("message", function(msg){displayMessage(msg.message);});
