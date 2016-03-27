var finalScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardBody = byID("scoreboardBody");
    var html = "";
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.scoreValue;
        html += "<tr><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td><td>"+e.overallScoreValue+"</td><td>"+e.playerLevel+"</td>";
        html += "<td><span class='lvlProgress'><span class='lvlProgressBar' style='width: " + e.playerLevelProgress*3/5 + "%;'></span></span></td></tr>";
    }
    scoreboardBody.innerHTML = html;
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

var scoreStringObserver = new Observer("score_string", function(msg) {
    setCookie("scoreString", msg.score_string, 1000);
    byID("scoreStringInput").value = msg.score_string;
    byID("scoreString").innerHTML = "Dein Punkte-Code: " + msg.score_string;
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
