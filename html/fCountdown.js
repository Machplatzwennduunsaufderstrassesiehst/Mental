
var countdownFrame = new Frame("countdownFrame");

// OBSERVERS ===========================================================

var countdownObserver = new Observer("countdown", function(msg) {
    navigation.openFrames(countdownFrame);
    var onCountdown = function(e, value){
        e.style.opacity = 1;
        e.style.transitionDuration = 0.5;
        setTimeout(function(){e.style.opacity = 0;e.style.transitionDuration = 0.1;}, 500);
        if (value == 0) e.innerHTML = "GO!";
    }
    countdown("bigCountdown", msg.time, true, onCountdown, "#");
});
