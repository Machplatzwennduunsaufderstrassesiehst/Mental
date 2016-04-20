
var uselessFunction = function(){};

var logContent_ = "";
var console = console || new function() {
    this.log = function(s) {logContent_ += s;}
}

function log(s) {
    if (DEBUG) {
        console.log(s);
        if (DEBUG_ONPAGE) {
            byID("console").style.display = "block;";
            byID("console").innerHTML += s + "<br>";
        }
        if (DEBUG_ALERT) {
            alert(s);
        }
    }
}

function updateDataFields(key, value) {
    var spans = document.getElementsByTagName("span");
    for (var i = 0; i < spans.length; i++) {
        var field = spans[i];
        if (field.hasAttribute("data-field-key")) {
            if (key == field.getAttribute("data-field-key")) {
                field.innerHTML = value;
            }
        }
    }
}

function backgroundColorAnimate(id, color, fallbackTimeout) {
    if (fallbackTimeout == undefined) fallbackTimeout = 1000;
    var element = byID(id);
    var oldColor = element.style.backgroundColor;
    element.style.backgroundColor = color;
    setTimeout(function(){
        element.style.backgroundColor = oldColor;
    }, fallbackTimeout);
}

function createIcon(key, size, offsetpx) {
    if (size == undefined) size = 2;
    var size_;
    if (size < 2) {
        size_ = "";
    } else {
        size_ = "-" + size + "x";
    }
    if (offsetpx != undefined) size = offsetpx;
    var html = '<img style="margin-bottom:-'+size+'px;" src="graphics/icons/open-iconic-master/png/'+key+size_+'.png" ';
    html += 'alt="'+key+'">&nbsp;';
    return html;
}

function iconize() {
    var icons = document.getElementsByTagName("span");
    for (var i = 0; i < icons.length; i++) {
        var icon = icons[i];
        if (!icon.hasAttribute("data-icon")) continue;
        icon.innerHTML = createIcon(icon.getAttribute("data-icon"));
    }
}

String.prototype.capitalize = function(){
    var self = this.split('');
    for( var i=0; i < self.length; i++ ){
        if( /^[a-zA-ZäöüßÄÖÜ]+$/.test(self[i]) ){
            self[i] = self[i].toUpperCase();
            break;
        }
    }
    return self.join('');
}

function byID(id) {
    return window.document.getElementById(id);
}

function Frame(id_) {
    var id = this.id = id_;
    var onopen = function(){};
    var onclose = function(){};
    var transitionTime = this.transitionTime = 200;
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

// hat wieder einen Sinn
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

        if (isMobile()) hideAddressBar();
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

/*
function closeAll() {
    var frames = window.document.getElementsByClassName("frame");
    for (var i = 0; i < frames.length; i++) {
        frames[i].style.display = "none";
    }
}

function show() {
    if (arguments[0].callee) arguments = arguments[0];
    closeAll();
    for (i = 0; i < arguments.length; i++) {
        byID(arguments[i]).style.opacity = 0;
        byID(arguments[i]).style.display = "block";
        byID(arguments[i]).style.opacity = 1;
    }

    if (isMobile()) hideAddressBar();
}
*/

var countdownValue = 0;
var countDownId = "countdownHack";

function countdown() {
    setTimeout(function(){countdown();}, 1000);
    if (!byID(countDownId)) return;
    if (countdownValue < 0) {
        byID(countDownId).style.display = "none";
    } else {
        byID(countDownId).style.display = "inline";
        byID(countDownId).innerHTML = String(countdownValue) + "s";
    }
    countdownValue -= 1;
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

var msgIDCounter = 0;
function displayMessage(message) {
    var i = 0;
    var msgCD = byID("messageContainerDivision");
    var msgC = byID("messageContainer");
    msgCD.style.opacity = 1;
    slide(msgC, -1.45);
    var msgID = "msg" + msgIDCounter;
    msgC.innerHTML = "<span id='"+msgID+"'>" + message + "<br></span>" + msgC.innerHTML;
    setTimeout(function(){byID(msgID).style.opacity = 0;if (msgC.children.length <= 1) msgCD.style.opacity = 0;}, 5000);
    setTimeout(function(){msgC.removeChild(byID(msgID));}, 5500);
    msgIDCounter++;
}
var slide = function(msgC, value) {
    if (value >= 0) {return;}
    msgC.style.marginTop = String(value) + "em";
    value += 0.1;
    setTimeout(function(){slide(msgC, value);}, 25);
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

function hideAddressBar(){
  if(document.documentElement.scrollHeight<window.outerHeight/window.devicePixelRatio)
    document.documentElement.style.height=(window.outerHeight/window.devicePixelRatio)+'px';
  setTimeout(function(){window.scrollTo(1,1)},0);
}
window.addEventListener("load",function(){hideAddressBar();});
window.addEventListener("orientationchange",hideAddressBar);

Math.signum = function(a) {
    if (a > 0) {
        return 1;
    } else if (a < 0) {
        return -1;
    }
    return 1;
}

function blur() {byID("blurHack").focus();}

function Lock() {
    var acquired = false;
    var onrelease = [];
    
    var acquire = this.acquire = function(doOnRelease) {
        if (acquired) {
            if (doOnRelease) onrelease.push(doOnRelease);
            return false;
        } else {
            acquired = true;
            return true;
        }
    }
    
    var release = this.release = function() {
        if (acquired) {
            acquired = false;
            while (onrelease.length > 0) {
                var queued = onrelease.shift();
                console.log(queued);
                queued();
            }
            onrelease = [];
            return true;
        } else {
            return false;
        }
    }
}

window.isMobile = function() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
}
