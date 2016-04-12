
var updateScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardBody = byID("scoreboardBody");
    var html = "";
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.scoreValue;
        html += "<tr><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td><td>"+e.playerLevel+"</td>";
        html += "<td><span class='lvlProgress'><span class='lvlProgressBar' style='width: " + e.playerLevelProgress*3/5 + "%;'></span></span></td></tr>";
    }
    scoreboardBody.innerHTML = html;
});

var reopenMainFrameObserver = new Observer("exercise", function(msg) {
    openMainFrame();
    setTimeout(openMainFrame, 1000); // to be save...
    serverConnection.removeObserver(reopenMainFrameObserver);
    serverConnection.removeObserver(updateScoreboardObserver);
    countDownId = "exerciseCountdown";
});

var showScoreboardObserver = new Observer("showScoreboard", function(msg) {
    openScoreboardFrame();
    serverConnection.addObserver(reopenMainFrameObserver);
    serverConnection.addObserver(updateScoreboardObserver);
});

var playerWonObserver = new Observer("player_won", function(msg) {
    countdownValue = Number(msg.gameTimeout);
    countDownId = "gameTimeoutCountdown";
});

var gameStringObserver = new Observer("game_string", function(msg) {
    setCookie("gameString", msg.game_string, 1000);
    byID("gameStringInput").value = msg.game_string;
    byID("gameString").innerHTML = "Dein Spielstand: " + msg.game_string;
});

var exerciseObserver = new Observer("exercise", function(msg) {
    var ex = msg.exercise;
    byID("exercise").innerHTML = ex + " = ";
    byID("answer").placeholder = "?";
    byID("answer").value = "";
});

var timeLeftObserver = new Observer("time_left", function(msg) {
    countdownValue = msg.time;
});

var suggestionsObserver = new Observer("suggestions", function(msg) {
    var s = msg.suggestions;
    listSuggestions(s);
});

var messageObserver = new Observer("message", function(msg){displayMessage(msg.message);});

