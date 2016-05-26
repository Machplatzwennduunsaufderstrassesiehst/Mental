
/* global byID, serverConnection, player, navigation, lobbyFrame, PIXI */

var wheelOfFortuneFrame = new Frame("wheelOfFortuneFrame");

wheelOfFortuneFrame.setOnOpen(function() {
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
    wheelOfFortuneFrame.startGraphics();
});

wheelOfFortuneFrame.setOnClose(function() {
    wheelOfFortuneFrame.stopGraphics();
});

// FUNCTIONALITY =======================================================
(function(wheelOfFortuneFrame) {
    
    var graphics = new GameGraphics("wheelGraphics");
    var wheel = PIXI.Sprite.fromImage("graphics/wof/wheel.png");
    wheel.anchor = new PIXI.Point(0.5, 0.5);
    var wheelPin = PIXI.Sprite.fromImage("graphics/wof/pin.png");
    wheelPin.anchor = new PIXI.Point(0.5, 0.5);
    graphics.addEnvironment(wheel);
    graphics.addEnvironment(wheelPin);
    
    var spinButtonAccessable = true;
    
    function reconfigureSpinButton(text, accessable) {
        spinButtonAccessable = accessable;
        var b = byID("spinButton");
        var bText = byID("spinButtonText");
        bText.innerHTML = text;
        if(accessable){
            b.classList.remove("disabled");
        }else{
            b.classList.add("disabled");
        }
    }

    var n, a, speed, fps, breakingStartPoint, initSpeed;
    function rotate(rounds, angle){ //in degrees
        n = 0;
        a = angle + 360 * rounds;
        initSpeed = speed = 10; // is now degrees per frame
        breakingStartPoint = 0;
        startAngleRotate();
    }
    
    function startAngleRotate() {
        n = n + (n + speed < a ? speed : a - n); // verhindert, dass wir über das Ziel hinausschießen

        fps = graphics.getCurrentFPS();
        wheel.rotation = graphics.degreesToRadian(n);
        
        if (n>=a){
            endRotate();
        }else{
            if (n >= breakingStartPoint) {
                var breakingProgress = 1 - (a - n) / (a - breakingStartPoint);
                speed = initSpeed - Math.sqrt(breakingProgress) * (initSpeed - 0.15);
            }
            if (speed <= 0.05) speed = 0.05;
            setTimeout(function(){startAngleRotate();}, 1000 / fps);
        }
    }
    
    function endRotate() {
        winPrize(prize);
        reconfigureSpinButton("Spin the wheel!", true);
    }
    
    function buySpin() {
        serverConnection.communicate({type:"buySpin"}, function(msg){
            if (msg.success) {
                backgroundColorAnimate("buySpinButton", "#afa");
                player.update_("playerMoney", -Number(msg.price));
                player.update_("playerSpins", +1);
            } else {
                backgroundColorAnimate("buySpinButton", "#faa");
            }
        });
    }

    var prize;
    var canSpin;
    var slices = 8;

    function spin(){
        if (!spinButtonAccessable) return;
        spinButtonAccessable = false;
        serverConnection.communicate({type:"spin"}, function(msg){
            if (msg.success) {
                backgroundColorAnimate("spinButton", "#afa");
                player.update_("playerSpins", -1);
                canSpin = true;
                spinButtonAccessable = true;
            } else {
                backgroundColorAnimate("spinButton", "#faa");
                canSpin = false;
                spinButtonAccessable = false;
            }
            if(canSpin){ 
                reconfigureSpinButton("Wheel is spinning...", false);
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
    
    wheelOfFortuneFrame.buySpin = buySpin;
    wheelOfFortuneFrame.spin = spin;
    
    wheelOfFortuneFrame.startGraphics = function() {
        var wh = jQuery(window).height();
        var ww = jQuery(window).width();
        var renderSize = (wh > ww ? ww - 50 : wh / 2);
        
        graphics.resizeRenderer(renderSize, renderSize);
    
        wheel.width = wheel.height = renderSize;
        wheelPin.width = wheelPin.height = renderSize / 3;

        graphics.centerSprite(wheel);
        graphics.centerSprite(wheelPin);
        
        graphics.start();
    };
    wheelOfFortuneFrame.stopGraphics = function() {graphics.stop();};
    
})(wheelOfFortuneFrame);

