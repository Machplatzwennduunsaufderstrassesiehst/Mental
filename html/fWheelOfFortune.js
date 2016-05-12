
var wheelOfFortuneFrame = new Frame("wheelOfFortuneFrame");

wheelOfFortuneFrame.setOnOpen(function() {
	var pin = byID("pin");
	var w = byID("wheel");
	pin.style.transitionDuration="0s";
	var left = w.getClientRects()[0].left+(w.clientWidth/2)-(pin.clientWidth/2);
	var up = w.getClientRects()[0].top+(w.clientHeight/2)-(pin.clientHeight/2);
	pin.style.left= left+"px";
	pin.style.top = up+"px";
	
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
});

wheelOfFortuneFrame.setOnClose(function() {});

// FUNCTIONALITY =======================================================
//wheelOfFortuneFrame.buySpin = function() {
	
function buySpin() {
    serverConnection.communicate({type:"buySpin"}, function(msg){
        if (msg.success) {
            backgroundColorAnimate("buySpinButton", "#afa");
            player.update_("money", -msg.price);
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


var x, n=0, a = 0, speed = 5, max
function rotateDIV(rounds, angle){ //in degrees
a = angle;
x=document.getElementById("wheel");
max = 360 * rounds;
startRoundRotate();
//setTimeout(function(){startAngleRotate();},2*360*rounds);

}
function startRoundRotate(){
	n=n+1
	x.style.transform="rotate(" + n + "deg)"
	x.style.webkitTransform="rotate(" + n + "deg)"
	x.style.OTransform="rotate(" + n + "deg)"
	x.style.MozTransform="rotate(" + n + "deg)"
	if (n==max){
		n = 0
	}else{
		setTimeout(function(){startRoundRotate();}, 2);
	}
}
function startAngleRotate(){
	n=n+1
	x.style.transform="rotate(" + n + "deg)"
	x.style.webkitTransform="rotate(" + n + "deg)"
	x.style.OTransform="rotate(" + n + "deg)"
	x.style.MozTransform="rotate(" + n + "deg)"
	if (n==a){
		
	}else{
		//speed = ((a - n) / a )+ 30;
		//speed = (a - ((a-n) + 4))%20
		//speed *= 1.04;
		setTimeout(function(){startAngleRotate();}, speed);
	}
}
 
     
function spin(){
	var prize;
	var canSpin;
	var slices = 8;
	serverConnection.communicate({type:"spin"}, function(msg){
		if (msg.success) {
			backgroundColorAnimate("spinButton", "#afa");
			player.update_("spins", -1);
			canSpin = true;
		} else {
			backgroundColorAnimate("spinButton", "#faa");
			canSpin = false;
		}
		if(canSpin){
			reconfigureSpinButton("wheel is spinning...", false);
			var rounds = Math.floor(Math.random()*3)+2;
			var degrees = Math.floor(Math.random()*360);
			prize = slices - 1 - Math.floor(degrees / (360 / slices));
			rotateDIV((rounds*360)+degrees);
			winPrize(prize);
			setTimeout(function(){reconfigureSpinButton("spin the wheel!", true);}, 1000); //TODO richtige zeit rausfinden
		}
	 });					    
}
function winPrize(prize){
	var slicePrizes = ["ANOTHER SPIN", "50 DOLLARS", "500 DOLLARS", "BAD LUCK!", "200 DOLLARS", "100 DOLLARS", "150 DOLLARS", "BAD LUCK!"];
	byID("prizeTextField").innerHTML = slicePrizes[prize];
}


//OBSERVERS ============================================================
