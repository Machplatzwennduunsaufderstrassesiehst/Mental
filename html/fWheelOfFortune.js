
var wheelOfFortuneFrame = new Frame("wheelOfFortuneFrame");

wheelOfFortuneFrame.setOnOpen(function() {
	var pin = byID("pin");
	var w = byID("wheel");
	//w.style.reset(); //rotate zurücksetzen
	pin.style.transitionDuration="0s";
	var left = w.getClientRects()[0].left+(w.clientWidth/2)-(pin.clientWidth/2);
	var up = w.getClientRects()[0].top+(w.clientHeight/2)-(pin.clientHeight/2);
	pin.style.left= left+"px";
	pin.style.top = up+"px";
	
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
});

wheelOfFortuneFrame.setOnClose(function() {
		pin.style.left= 0+"px";
	pin.style.top = 0+"px";
});

// FUNCTIONALITY =======================================================
//wheelOfFortuneFrame.buySpin = function() {
	
function buySpin() {
    serverConnection.communicate({type:"buySpin"}, function(msg){
        if (msg.success) {
            backgroundColorAnimate("buySpinButton", "#afa");
            player.update_("playerMoney", -msg.price);
        } else {
            backgroundColorAnimate("buySpinButton", "#faa");
        }
    });
}

function reconfigureSpinButton(text, accessable) {
    var b = byID("spinButton");
    b.innerHTML = text;
	if(accessable){
		b.classList.add("disabled");
		b.disabled = true;
	}else{
		b.classList.remove("disabled");
		b.disabled = false;
	}
}


var x, n=0, a = 0, speed = 5, max;
function rotate(rounds, angle){ //in degrees
    n = 0;
    a = angle;
    speed = 5;
    max = 360 * rounds;
    x=document.getElementById("wheel");
    startRoundRotate();
}
function startRoundRotate(){
	n=n+5;
	x.style.transform="rotate(" + n + "deg)";
	x.style.webkitTransform="rotate(" + n + "deg)";
	x.style.OTransform="rotate(" + n + "deg)";
	x.style.MozTransform="rotate(" + n + "deg)";
	if (n>=max){
		n = 0;
		startAngleRotate();
	}else{
		setTimeout(function(){startRoundRotate();}, 20);
	}
}
function startAngleRotate(){
	n=n+1;
	x.style.transform="rotate(" + n + "deg)";
	x.style.webkitTransform="rotate(" + n + "deg)";
	x.style.OTransform="rotate(" + n + "deg)";
	x.style.MozTransform="rotate(" + n + "deg)";
	if (n>=a){
		winPrize(prize);
		//reconfigureSpinButton("spin the wheel!", false);
	}else{
		//speed = ((a - n) / a )+ 20;
		//speed = (a - ((a-n) + 4))%20
		speed *= 1+((3590/a)*0.0005);
		setTimeout(function(){startAngleRotate();}, speed);
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
			//reconfigureSpinButton("wheel is spinning...", true);
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
	if(slicePrizesInt[prize] == -1){
            player.update_("playerSpins", 1);
        }else{
            player.update_("playerMoney", slicePrizesInt[prize]);
        }
	byID("prizeTextField").innerHTML = slicePrizes[prize];
}


//OBSERVERS ============================================================
