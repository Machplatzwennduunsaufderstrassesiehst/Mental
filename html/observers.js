

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

var finalScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardTable = byID("scoreboard");
    scoreboardTable.innerHTML = "<tr><td>Pos.</td><td>Name</td><td>Score</td></tr>";
    
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.value;
        scoreboardTable.innerHTML += "<tr><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td></tr>";
    }
    
    serverConnection.addObserver(reopenMainFrameObserver);
});

var reopenMainFrameObserver = new Observer("exercise", function(msg) {
    openMainFrame();
    serverConnection.removeObserver(reopenMainFrameObserver);
}

var playerWonObserver = new Observer("player_won", function(msg) {
    displayMessage(msg.playerName + " hat diese Runde gewonnen!");
    openScoreboardFrame();
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
