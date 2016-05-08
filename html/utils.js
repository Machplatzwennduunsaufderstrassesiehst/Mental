
var uselessFunction = function(){};

var logContent_ = "";
if (window.console == undefined) {
    var console = window.console = new function() {
        this.log = function(s) {logContent_ += s;}
    }
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
    if (!element.hasAttribute("data-plain-background-color")) element.setAttribute("data-plain-background-color", element.style.backgroundColor);
    var plainColor = element.getAttribute("data-plain-background-color");
    element.style.transitionDuration = "0.2s";
    element.style.backgroundColor = color;
    setTimeout(function(){
        element.style.transitionDuration = "1s";
        element.style.backgroundColor = plainColor;
    }, fallbackTimeout);
}


Array.prototype.remove = function(element) {
    var index = this.indexOf(element);
    if (index < 0) return false; // element not found in array
    this.splice(index, 1);
    return true;
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

function byTag(id) {
    return window.document.getElementsByTagName(id);
}

function byClass(id) {
    return window.document.getElementsByClassName(id);
}

var exerciseCountdownValue = 0;

function countdown(countdownId, value, recall, onCountdown, template) {
    if (!template) template = "#s";
    var e = byID(countdownId);
    if (!e) {
        e = {style:{}};
        log("countdown: element not found: " + countdownId);
    }
    if (value < 0) {
        e.style.display = "none";
    } else {
        e.style.display = "inline";
        e.innerHTML = template.replace("#", String(value));
        if (recall) setTimeout(function(){countdown(countdownId, Number(value)-1, true, onCountdown, template);}, 1000);
        if (onCountdown) onCountdown(e, value);
    }
}

setInterval(function(){countdown("exerciseCountdown", exerciseCountdownValue, false);exerciseCountdownValue--;}, 1000);


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
