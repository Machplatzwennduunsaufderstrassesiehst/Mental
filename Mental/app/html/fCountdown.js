
/* global navigation */

var countdownFrame = new Frame("countdownFrame");

countdownFrame.setOnOpen(function() {
    function onCountdown(e, value){
        e.style.fontSize="8em";
        e.style.opacity = 1;
        e.style.transitionDuration = 0.5;
        setTimeout(function(){e.style.opacity = 0;e.style.transitionDuration = 0.1;}, 500);
        if (value === 0) {e.innerHTML = "GO!";e.style.paddingTop="-1em";e.style.fontSize="10em";};
    }
    countdown("bigCountdown", countdownFrame["countdownTime"], true, onCountdown, "#");
});
// OBSERVERS ===========================================================
