
/* global serverConnection, uselessFunction, byID */

var scoreboardFrame = new Frame("scoreboardFrame");

scoreboardFrame.setOnOpen(function() {
    unshowMsgBox();
    setDoOnEnter(uselessFunction);
    byID("toLobby").style.display = "inline";
    byID("voting").innerHTML = '<p>Voting starten... <span id="gameTimeoutCountdown"></span></p>';
    serverConnection.addObserver(updateScoreboardObserver);
    serverConnection.addObserver(suggestionsObserver);
});

scoreboardFrame.setOnClose(function() {
    serverConnection.removeObserver(updateScoreboardObserver);
    serverConnection.removeObserver(suggestionsObserver);
});

// FUNCTIONALITY =======================================================

function listSuggestions(suggestions) {
    var html = "<p class='suggestion' style='padding:6px;margin:0px auto;'>Was möchtest du als nächstes spielen?</p>";
    for (var i = 0; i < suggestions.length; i++) {
        var suggestion = suggestions[i];
        html += "<div style='"+(suggestion.highlight ? "background-color: #aaa;" : "")+"' class='selectListItem suggestion' onclick='vote("+suggestion.suggestionID+");'>";
        html += "<span style='margin:5px;float:right;border-radius:0.5em;'>"+suggestion.votes+"</span>";
        html += "<p style='margin:5px 23px 5px 5px;white-space: nowrap;overflow-x: hidden;'>"+suggestion.suggestionName+"</p>";
        html += "</div>";
    }
    byID("voting").innerHTML = html;
    
    adjustSuggestionListing();
}

function adjustSuggestionListing(width) {
    var suggestions = document.getElementsByClassName("suggestion");
    var scoreboardWidth = Number(byID("scoreboard").clientWidth) - 12;
    for (var i = 0; i < suggestions.length; i++) {
        var suggestion = suggestions[i];
        suggestion.style.width = scoreboardWidth + "px";
    }
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
        html += "<tr style='"+(e.highlight ? "background-color: #e5ebff;" : "")+"'><td>"+(i+1)+"</td> \
                <td><p style='margin:0px 0px -4px 0px;color:"+e.color+";'>"+name+"</p><span style='font-weight:bold;font-size:0.65em;float:left;'>&#060;"+e.playerTitle+"&#062;</span>\
                </td><td style='text-align:right;'>"+score+"</td><td style='text-align:right;'>"+e.playerLevel+"";
        html += "&nbsp;&nbsp;<span class='lvlProgress'><span class='lvlProgressBar' style='width: " + e.playerLevelProgress + "%;"+(e.highlight ? "background-color: #ff3cbf;" : "")+"'></span></span></td></tr>";
    }
    scoreboardBody.innerHTML = html;
    
    adjustSuggestionListing();
});

