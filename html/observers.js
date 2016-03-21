var finalScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardBody = byID("scoreboardBody");
    scoreboardBody.innerHTML = "";
    
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.scoreValue;
        scoreboardBody.innerHTML += "<tr><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td><td>"+e.overallScoreValue+"</td><td>"+e.playerLevel+"</td></tr>";
    }
});

var reopenMainFrameObserver = new Observer("exercise", function(msg) {
    openMainFrame();
    setTimeout(openMainFrame, 1000); // to be save...
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
