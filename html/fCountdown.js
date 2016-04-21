
var countdownFrame = new Frame("countdownFrame");

// OBSERVERS ===========================================================

var countdownObserver = new Observer("countdown", function(msg) {
    navigation.openFrames(countdownFrame);
    var onCountdown = function(e, value){
        e.style.fontSize="8em";
        e.style.opacity = 1;
        e.style.transitionDuration = 0.5;
        setTimeout(function(){e.style.opacity = 0;e.style.transitionDuration = 0.1;}, 500);
        if (value == 0) {e.innerHTML = "GO!";e.style.paddingTop="-1em";e.style.fontSize="10em";};
    }
    countdown("bigCountdown", msg.time, true, onCountdown, "#");
});
