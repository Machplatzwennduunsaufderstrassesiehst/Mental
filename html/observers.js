

var msgIDCounter = 0;
function displayMessage(message) {
    var i = 0;
    var msgCD = byID("messageContainerDivision");
    var msgC = byID("messageContainer");
    msgCD.style.opacity = 1;
    slide(msgC, -1.45);
    var msgID = "msg" + msgIDCounter;
    msgC.innerHTML = "<span id='"+msgID+"'>" + message + "<br></span>" + msgC.innerHTML;
    setTimeout(function(){byID(msgID).style.opacity = 0;}, 5000);
    setTimeout(function(){msgC.removeChild(byID(msgID));
                if (msgC.innerHTML.length <= 3) msgCD.style.opacity = 0;}, 5500);
    msgIDCounter++;
}
var slide = function(msgC, value) {
    if (value >= 0) {return;}
    msgC.style.marginTop = String(value) + "em";
    value += 0.1;
    setTimeout(function(){slide(msgC, value);}, 25);
}

var finalScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardBody = byID("scoreboardBody");
    scoreboardBody.innerHTML = "";
    
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.scoreValue;
        scoreboardBody.innerHTML += "<tr><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td><td>"+e.overallScoreValue+"</td><td>"+e.playerLevel+"</td></tr>";
    }
    
    serverConnection.addObserver(reopenMainFrameObserver);
});

var reopenMainFrameObserver = new Observer("exercise", function(msg) {
    openMainFrame();
    setTimeout(openMainFrame, 1000);
    serverConnection.removeObserver(reopenMainFrameObserver);
    serverConnection.removeObserver(finalScoreboardObserver);
});

var playerWonObserver = new Observer("player_won", function(msg) {
    serverConnection.addObserver(finalScoreboardObserver);
    openScoreboardFrame();
    countdownValue = Number(msg.gameTimeout);
    countDownId = "gameTimeoutCountdown";
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
    countDownId = "exerciseCountdown";
});

var messageObserver = new Observer("message", function(msg){displayMessage(msg.message);});
