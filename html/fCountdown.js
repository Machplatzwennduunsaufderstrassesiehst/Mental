
var countdownFrame = new Frame("countdownFrame");

// OBSERVERS ===========================================================

var countdownObserver = new Observer("countdown", function(msg) {
    navigation.openFrames(countdownFrame);
    countdownValue = msg.time;
    countDownId = "bigCountdown";
});
