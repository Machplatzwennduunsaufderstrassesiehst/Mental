


// OBSERVERS ===========================================================

var countdownObserver = new Observer("countdown", function(msg) {
    show("countdownFrame");
    countdownValue = msg.time;
    countDownId = "bigCountdown";
}
