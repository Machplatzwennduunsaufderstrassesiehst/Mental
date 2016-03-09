

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
