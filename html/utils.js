

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

function countdown() {
    var countDownId = "countdown";
    countdownValue -= 1;
    if (countdownValue < 0) {
        byID(countDownId).style.display = "none";
    } else {
        byID(countDownId).style.display = "inline";
        byID(countDownId).innerHTML = String(countdownValue) + "s";
    }
    setTimeout(function(){countdown();}, 1000);
}
