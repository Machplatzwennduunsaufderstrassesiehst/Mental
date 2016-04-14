
function listSuggestions(suggestions) {
    var html = "";
    var scoreboardWidth = byID("scoreboard").clientWidth;
    for (var i = 0; i < suggestions.length; i++) {
        var suggestion = suggestions[i];
        html += "<div style='width:"+scoreboardWidth+"px;"+(suggestion.highlight ? "background-color: #aaa;" : "")+"' class='selectListItem' onclick='vote("+suggestion.suggestionID+");'>";
        html += "<span style='float:right;border-radius:0.5em;'>"+suggestion.votes+"</span>";
        html += "<span>"+suggestion.suggestionName+"</span>";
        html += "</div>";
    }
    byID("voting").innerHTML = html;
}

function vote(suggestionIndex) {
    serverConnection.send(makeSimpleCmd("vote", "suggestionID", suggestionIndex));
}

// OBSERVERS ===========================================================

var suggestionsObserver = new Observer("suggestions", function(msg) {
    var s = msg.suggestions;
    listSuggestions(s);
});

var updateScoreboardObserver = new Observer("scoreboard", function(msg) {
    var scoreboardBody = byID("scoreboardBody");
    var html = "";
    for (var i = 0; i < msg.scoreboard.length; i++) {
        var e = msg.scoreboard[i];
        var name = e.playerName;
        var score = e.scoreValue;
        html += "<tr style='"+(e.highlight ? "background-color: #e5ebff;" : "")+"'><td>"+(i+1)+"</td><td>"+name+"</td><td>"+score+"</td><td>"+e.playerLevel+"";
        html += "&nbsp;&nbsp;<span class='lvlProgress'><span class='lvlProgressBar' style='width: " + e.playerLevelProgress*3/5 + "%;"+(e.highlight ? "background-color: #ff3cbf;" : "")+"'></span></span></td></tr>";
    }
    scoreboardBody.innerHTML = html;
});

var showScoreboardObserver = new Observer("showScoreboard", function(msg) {
    openScoreboardFrame();
    serverConnection.addObserver(updateScoreboardObserver);
});

