
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
    var observers = [];

    window.addEventListener("load", function() {
        byID(id).style.transitionDuration = (transitionTime / 1000) + "s";
    });
    
    var open = false;

    this.setObservers = function() {
        for (var i in arguments) {
            observers.add(arguments[i]);
        }
    };
    
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
        for (var i = 0; i < observers.length; i++) {
            serverConnection.add(observers[i]);
        }
        onopen();    
    };
    
    var notifyClose = this.notifyClose = function() {
        if (!open) return;
        open = false;
        for (var i = 0; i < observers.length; i++) {
            serverConnection.remove(observers[i]);
        }
        onclose();
    };

    var smoothClose = this.smoothClose = function() {
        var element = byID(id);
        element.style.opacity = 0;
        element.style.display = "none";
    };

    var smoothOpen = this.smoothOpen = function() {
        var element = byID(id);
        element.style.display = "block";
        element.style.opacity = 0;
        setTimeout(function(){element.style.opacity = 1;},10);
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
