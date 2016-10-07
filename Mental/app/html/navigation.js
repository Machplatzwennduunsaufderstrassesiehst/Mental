
/* global byID */

function showMsgBox(message, extra) {
    var m = byID("msgBox");
    while (m.classList.length > 0) m.classList.remove(m.classList[0]);
    m.classList.add("msgBox");
    if (extra) m.classList.add(extra);
    var pageHeight = byID("page_").clientHeight;
    var pageWidth = byID("page_").clientWidth;
    m.innerHTML = message;
    m.style.opacity = 0;
    m.style.display = "block";
    setTimeout(function(){
        m.style.top = (pageHeight / 2 - 100) + "px";
        m.style.left = (pageWidth / 2 - m.clientWidth / 2) + "px";
        m.style.opacity = 0.7;
    }, 10);
}

function unshowMsgBox() {
    var m = byID("msgBox");
    m.style.opacity = 0;
    setTimeout(function(){
        m.style.display = "none";
    }, 500);
}

function Frame(id_) {
    var id = this.id = id_;
    var onopen = function(){};
    var onclose = function(){};
    var transitionTime = this.transitionTime = 300;
    window.addEventListener("load", function() {
        byID(id).style.transitionDuration = (transitionTime / 1000) + "s";
    });
    
    var open = false;
    
    this.isOpen = function() {
        return open;
    };
    
    this.setOnOpen  = function(func) {
        onopen = func;
    };
    
    this.setOnClose = function(func) {
        onclose = func;
    };
    
    var notifyOpen = this.notifyOpen = function() {
        if (open) return;
        open = true;
        onopen();    
    };
    
    var notifyClose = this.notifyClose = function() {
        if (!open) return;
        open = false;
        onclose();
    };

    var smoothClose = this.smoothClose = function() {
        var element = byID(id);
        element.style.opacity = 0;
        element.style.display = "none";
        return;
        /*if (element.style.display == "none") return;
        if (!smoothLock.acquire(smoothClose)) return;
        console.log(element);
        console.log("close");
        element.setAttribute("data-old-style-display", element.style.display);
        element.style.opacity = 0;
        setTimeout(function(){element.style.display = "none";smoothLock.release();isVisible = false;}, transitionTime);*/
    };

    var smoothOpen = this.smoothOpen = function() {
        var element = byID(id);
        element.style.display = "block";
        element.style.opacity = 0;
        setTimeout(function(){element.style.opacity = 1;},10);
        return;
        /*if (!smoothLock.acquire(smoothOpen)) return;
        console.log(element);
        console.log("open");
        var d = element.getAttribute("data-old-style-display");
        if (d == null || d == "none" || d == "") d = "block";
        element.style.display = d;
        setTimeout(function(){element.style.opacity = 1;smoothLock.release();isVisible = true;}, transitionTime);*/
    };
    
    navigation.registerFrame(this);
}



function Navigation() {
    var frames = []; // list with <Frame> objects
    
    this.registerFrame = function(frame) {
        window.addEventListener("load", function() {
            var frameId = frame.id;
            frames.push(frame);
        });
    };
    
    // does not close the frames given by parameters
    var closeAll = this.closeAll = function() {
        closeFrames(frames);
    };
    
    var openFrames = this.openFrames = function() {
        if (arguments.length < 1) return;
        if (!arguments[0].id) arguments = arguments[0];
        closeAll();
        unshowMsgBox();
        var frames_ = arguments;
        for (var i = 0; i < frames_.length; i++) {
            var f = frames_[i];
            if (f.isOpen()) continue; 
            f.smoothOpen();
            f.notifyOpen();
        }
        setTimeout(function(){window.scrollTo(1,1);},0);
        //if (isMobile()) hideAddressBar();
    };
    
    // takes frames as arguments
    var closeFrames = this.closeFrames = function() {
        if (arguments.length < 1) return;
        if (!arguments[0].id) arguments = arguments[0];
        for (i = 0; i < arguments.length; i++) {
            var f = arguments[i];
            if (!f.isOpen()) continue;
            byID("disconnect").style.display = "none";
            byID("toLobby").style.display = "none";
            f.notifyClose();
            f.smoothClose();
        }
    };
}
// man braucht davon nur eine instanz
var navigation = new Navigation();
