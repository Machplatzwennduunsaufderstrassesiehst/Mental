
function showMsgBox(message, theme) {
    var t = theme || "b";
    var m = message || "Bitte Warten...";
    $.mobile.loading( 'show', {
        text: m,
        textVisible: true,
        theme: t,
        textonly: true,
        html: ""
    });
}

function unshowMsgBox() {
    $.mobile.loading( "hide" );
}

function Frame(id_) {
    var id = this.id = id_;
    var onopen = function(){};
    var onclose = function(){};
    var transitionTime = this.transitionTime = 300;
    var smoothLock = new Lock(); // lock for smoothClose and smoothOpen
    window.addEventListener("load", function() {
        byID(id).style.transitionDuration = (transitionTime / 1000) + "s";
    });
    
    var isOpen = false;
    
    this.setOnOpen  = function(func) {
        onopen = func;
    }
    
    this.setOnClose = function(func) {
        onclose = func;
    }
    
    var notifyOpen = this.notifyOpen = function() {
        if (isOpen) return;
        isOpen = true;
        onopen();    
    }
    
    var notifyClose = this.notifyClose = function() {
        if (!isOpen) return;
        isOpen = false;
        onclose();    
    }

    var smoothClose = this.smoothClose = function() {
        var element = byID(id);
        element.style.display = "none";
        element.style.opacity = 0;
        return;
        if (element.style.display == "none") return;
        if (!smoothLock.acquire(smoothClose)) return;
        console.log(element);
        console.log("close");
        element.setAttribute("data-old-style-display", element.style.display);
        element.style.opacity = 0;
        setTimeout(function(){element.style.display = "none";smoothLock.release();isVisible = false;}, transitionTime);
    }

    var smoothOpen = this.smoothOpen = function() {
        var element = byID(id);
        element.style.display = "block";
        element.style.opacity = 0;
        setTimeout(function(){element.style.opacity = 1;},50);
        return;
        if (!smoothLock.acquire(smoothOpen)) return;
        console.log(element);
        console.log("open");
        var d = element.getAttribute("data-old-style-display");
        if (d == null || d == "none" || d == "") d = "block";
        element.style.display = d;
        setTimeout(function(){element.style.opacity = 1;smoothLock.release();isVisible = true;}, transitionTime);
    }
    
    navigation.registerFrame(this);
}



function Navigation() {
    var frames = []; // list with <Frame> objects
    
    this.registerFrame = function(frame) {
        window.addEventListener("load", function() {
            var frameId = frame.id;
            frames.push(frame);
        });
    }
    
    var closeAll = this.closeAll = function() {
        closeFrames(frames);
        byID("disconnect").style.display = "none";
        byID("toLobby").style.display = "none";
    }
    
    var openFrames = this.openFrames = function() {
        if (arguments.length < 1) return;
        if (!arguments[0].id) arguments = arguments[0];
        closeAll();
        var frames_ = arguments; // (arguments.length ? arguments : [arguments]);
        for (i = 0; i < frames_.length; i++) {
            var f = frames_[i];
            f.smoothOpen();
            f.notifyOpen();
        }

        //if (isMobile()) hideAddressBar();
    }
    
    // takes frames as arguments
    var closeFrames = this.closeFrames = function() {
        if (arguments.length < 1) return;
        if (!arguments[0].id) arguments = arguments[0];
        for (i = 0; i < arguments.length; i++) {
            var f = arguments[i];
            f.notifyClose();
            f.smoothClose();
        }
    }
}
// man braucht davon nur eine instanz
var navigation = new Navigation();
