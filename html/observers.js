

var msgIDCounter = 0;
function displayMessage(message) {
    var i = 0;
    var msgC = byID("messageContainer");
    slide(msgC, -1.5);
    var msgID = "msg" + msgIDCounter;
    msgC.innerHTML = "<span id='"+msgID+"'>" + message + "</span><br>" + msgC.innerHTML;
    setTimeout(function(){byID(msgID).style.opacity = 0;}, 5000);
    setTimeout(function(){msgC.removeChild(byID(msgID));}, 5500);
    msgIDCounter++;
}
var slide = function(msgC, value){
    if (value >= 0) {return;}
    msgC.style.marginTop = String(value) + "em";
    value += 0.1;
    setTimeout(function(){slide(msgC, value);}, 25);
}



var playerWonObserver = new Observer("player_won", function(msg) {
    displayMessage(msg.playerName + " hat diese Runde gewonnen!");
    countdownValue = 0;
});


var exerciseResultSize = 0;
var exerciseObserver = new Observer("exercise", function(msg) {
    var ex = msg.exercise;
    byID("exercise").innerHTML = ex + " = ";
    byID("answer").placeholder = "?";
    byID("answer").value = "";
    exerciseResultSize = msg.length;
});

var timeLeftObserver = new Observer("time_left", function(msg) {
    countdownValue = msg.time;
});

var messageObserver = new Observer("message", function(msg){displayMessage(msg.message);});
