

var wheelOfFortuneFrame = new Frame("wheelOfFortuneFrame");

shoppingFrame.setOnOpen(function() {
    //updateShopItems(); //update wheel inhalt ?
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
});

shoppingFrame.setOnClose(function() {
    byID("wheel").style.opacity = 0;
});

// FUNCTIONALITY =======================================================

var wof = {};
//wof.spin = function(a, b){}

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


var x, n=0, wheelINT
function rotateDIV(angle){ //in degrees
x=document.getElementById("wheel");
clearInterval(wheelINT);
wheelINT=setInterval("startRotate()",10);
}
function startRotate(){
	n=n+1
	x.style.transform="rotate(" + n + "deg)"
	x.style.webkitTransform="rotate(" + n + "deg)"
	x.style.OTransform="rotate(" + n + "deg)"
	x.style.MozTransform="rotate(" + n + "deg)"
	if (n==180 || n==360){
		clearInterval(wheelINT);
		if (n==360){n=0}
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
			reconfigureSpinButton("spin the wheel!", true);
		}
	 });					    
}
     winPrize(prize){
		 var slicePrizes = ["ANOTHER SPIN", "50 DOLLARS", "500 DOLLARS", "BAD LUCK!", "200 DOLLARS", "100 DOLLARS", "150 DOLLARS", "BAD LUCK!"];
		 var prizeTextField;
         prizeText.text = slicePrizes[prize];
     }
}


//OBSERVERS ============================================================
