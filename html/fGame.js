
function numpad(n) {
    byID("answer").value += String(n);
}

function numpadDel() {
    var v = String(byID("answer").value);
    byID("answer").value = v.substring(0, v.length-1);
}
        
var alreadyAnswered = false;
function sendAnswer() {
    console.log("sendAnswer");
    if (alreadyAnswered) {return;}
    alreadyAnswered = true;
    console.log(alreadyAnswered);
    setTimeout(function(){alreadyAnswered = false;}, 100); // hier lieber ein Timeout, da es ja sein kann, dass keine Antwort vom Server kommt (dann waere diese Methode für immer gelockt!)
    var answer = byID("answer").value;
    serverConnection.communicate(makeSimpleCmd("answer", "answer", Number(answer)), function(msg) {
        if (msg.isCorrect) {
            byID("answer").style.backgroundColor = "#afa";
            byID("answer").placeholder = "Richtig!";
        } else {
            byID("answer").style.backgroundColor = "#faa";
            byID("answer").placeholder = "Falsch!";
            byID("answer").value = ""; // bei einer falschen Antwort wird das ergebnis gelöscht, bei einer richtigen Antwort bleibt das Ergebnis stehen, bis die nächste Aufgabe kommt
        }
        setTimeout(function(){
            byID("answer").style.backgroundColor = "#fff";
        }, 1000);
    });
}

// OBSERVERS ===========================================================

var playerWonObserver = new Observer("playerWon", function(msg) {
    countdownValue = Number(msg.gameTimeout);
    countDownId = "gameTimeoutCountdown";
});

var exerciseObserver = new Observer("exercise", function(msg) {
    var ex = msg.exercise;
    byID("exercise").innerHTML = ex + " = ";
    byID("answer").placeholder = "?";
    byID("answer").value = "";
});

var reopenMainFrameObserver = new Observer("showExercises", function(msg) {
    openMainFrame();
    setTimeout(openMainFrame, 1000); // to be save...
    serverConnection.removeObserver(updateScoreboardObserver);
    countDownId = "exerciseCountdown";
});

var beatbobObserver = new Observer("beatbob", function(msg) {
    
});

var messageObserver = new Observer("message", function(msg){displayMessage(msg.message);});
