
/* global byID, serverConnection, player, navigation, lobbyFrame */

var wheelOfFortuneFrame = new Frame("wheelOfFortuneFrame");

wheelOfFortuneFrame.setOnOpen(function() {
    var maxWheelSize = byID("wheelOfFortuneFrame").clientWidth;
    var pin = wheelOfFortuneFrame.pin = byID("pin");
    var wheel = wheelOfFortuneFrame.wheel = byID("wheel");
    pin.style.transitionDuration="0s";
    /*if (wheel.clientWidth > maxWheelSize) {
        alert("test");
        var scale = maxWheelSize / wheel.clientWidth;
        wheel.style.transitionDuration = "0s";
        wheel.style.width = maxWheelSize;
        wheel.style.height = maxWheelSize;
        pin.style.width = pin.clientWidth * scale;
        pin.style.height = pin.clientHeight * scale;
    }*/
    var left = wheel.getClientRects()[0].left+(wheel.clientWidth/2)-(pin.clientWidth/2);
    var up = wheel.getClientRects()[0].top+(wheel.clientHeight/2)-(pin.clientHeight/2);
    pin.style.left= left+"px";
    pin.style.top = up+"px";
	
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
});

wheelOfFortuneFrame.setOnClose(function() {
    
});

// FUNCTIONALITY =======================================================
//wheelOfFortuneFrame.buySpin = function() {
	
function buySpin() {
    serverConnection.communicate({type:"buySpin"}, function(msg){
        if (msg.success) {
            backgroundColorAnimate("buySpinButton", "#afa");
            player.update_("playerMoney", -Number(msg.price));
        } else {
            backgroundColorAnimate("buySpinButton", "#faa");
        }
    });
}

function reconfigureSpinButton(text, accessable) {
    var b = byID("spinButton");
    var bText = byID("spinButtonText");
    bText.innerHTML = text;
    if(accessable){
        //b.classList.add("disabled");
        b["data-disabled"] = "true";
    }else{
        //b.classList.remove("disabled");
        b["data-disabled"] = "false";
    }
}


var x, n, a, speed, fps, breakingStartPoint, initSpeed;
function rotate(rounds, angle){ //in degrees
    n = 0;
    a = angle + 360 * rounds;
    fps = 144; // damit das auch auf deinem bildschirm läuft
    initSpeed = speed = 10; // is now degrees per frame
    breakingStartPoint = 0;
    x = wheelOfFortuneFrame.wheel;
    startAngleRotate();
}
function startAngleRotate() {
    n = n + (n + speed < a ? speed : a - n); // verhindert, dass wir über das Ziel hinausschießen
    
    x.style.transform="rotate(" + n + "deg)";
    x.style.webkitTransform="rotate(" + n + "deg)";
    x.style.OTransform="rotate(" + n + "deg)";
    x.style.MozTransform="rotate(" + n + "deg)";
    if (n>=a){
        winPrize(prize);
        reconfigureSpinButton("Spin the wheel!", false);
    }else{
        //speed = ((a - n) / a )+ 20;
        //speed = (a - ((a-n) + 4))%20
        //speed *= 1+((3590/a)*0.0005);
        if (n >= breakingStartPoint) {
            var breakingProgress = 1 - (a - n) / (a - breakingStartPoint);
            speed = initSpeed - Math.pow(breakingProgress, 0.5) * (initSpeed - 0.2);
        }
        if (speed <= 0.05) speed = 0.05;
        setTimeout(function(){startAngleRotate();}, 1000 / fps);
    }
}
 
var prize;
var canSpin;
var slices = 8;
     
function spin(){
    serverConnection.communicate({type:"spin"}, function(msg){
        if (msg.success) {
            backgroundColorAnimate("spinButton", "#afa");
            player.update_("playerSpins", -1);
            canSpin = true;
        } else {
            backgroundColorAnimate("spinButton", "#faa");
            canSpin = false;
        }
        if(canSpin){ 
            reconfigureSpinButton("Wheel is spinning...", true);
            var rounds = Math.floor(msg.angle/360);
            var degrees = msg.angle%360;
            prize = slices - 1 - Math.floor(degrees / (360 / slices));
            rotate(rounds, degrees);
        }
    });					     
}
function winPrize(prize){
    var slicePrizes = ["ANOTHER SPIN", "50 DOLLARS", "500 DOLLARS", "BAD LUCK!", "200 DOLLARS", "100 DOLLARS", "150 DOLLARS", "BAD LUCK!"];
    var slicePrizesInt = [-1, 50, 500, 0, 200, 100, 150, 0];
    if(slicePrizesInt[prize] === -1){
        player.update_("playerSpins", 1);
    }else{
        player.update_("playerMoney", slicePrizesInt[prize]);
    }
    byID("prizeTextField").innerHTML = slicePrizes[prize];
}


//OBSERVERS ============================================================
