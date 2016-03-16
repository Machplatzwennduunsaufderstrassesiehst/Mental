
var uselessFunction = function(){};

function closeAll() {
    var frames = window.document.getElementsByClassName("frame");
    for (var i = 0; i < frames.length; i++) {
        frames[i].style.display = "none";
    }
}

function byID(id) {
    return window.document.getElementById(id);
}

function show(id) {
    closeAll();
    byID(id).style.display = "block";
}

var countdownValue = 0;
var countDownId = "countdownHack";

function countdown() {
    if (countdownValue < 0) {
        byID(countDownId).style.display = "none";
    } else {
        byID(countDownId).style.display = "inline";
        byID(countDownId).innerHTML = String(countdownValue) + "s";
    }
    countdownValue -= 1;
    setTimeout(function(){countdown();}, 1000);
}


var doOnEnter = uselessFunction;

document.onkeydown = function(event) {
    if (event.keyCode == 13) { // enter key pressed
        doOnEnter();
    }
}

function setDoOnEnter(f) {
    doOnEnter = f;
}

function fullScreen(element) {
    if (element.requestFullscreen) {
        element.requestFullscreen();
    }
    else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    }
    else if (element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
    }
}
