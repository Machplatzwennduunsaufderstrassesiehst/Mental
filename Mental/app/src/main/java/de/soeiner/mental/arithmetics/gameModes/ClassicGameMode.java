package de.soeiner.mental.arithmetics.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;

/**
 * Created by Malte on 28.03.2016.
 */

public class ClassicGameMode extends ArithmeticGameMode {

    public ClassicGameMode(Game g) {
        super(g);
        minPlayers = 1;
    }

    public String getName() {
        return "Classic";
    }

    public void prepareGame() {
        super.prepareGame();
        addAllPlayersToActive();
    }

    public void loop() {
    }

    public boolean playerAction(Player player, JSONObject actionData) {
        boolean allFinished = true;
        Score s = player.getScore();
        synchronized (answerTimeoutLock) {
            if (!player.finished) { // sonst kann man 2x mal punkte absahnen ;; spieler kriegt jetzt keine punkte mehr abgezogen für doppeltes antworten
                if (game.exerciseCreator.checkAnswer(actionData)) {
                    s.updateScore(getPoints());
                    game.broadcastMessage(player.getName() + " hat die Aufgabe als " + (getRank() + 1) + ". gelöst!");
                    if (s.getScoreValue() > 100) {
                        running = false; // schleife in run() beenden
                        broadcastPlayerWon(player.getName(), getName());
                        answerTimeoutLock.notify(); // hat einer gewonnen, muss das wait im game loop ebenfalls beendet werden.
                    }
                    player.finished = true;
                    for (int i = 0; i < game.activePlayers.size(); i++) {
                        Player p = game.activePlayers.get(i);
                        if (!p.finished) {
                            allFinished = false;
                        }
                    }
                    if (allFinished) {
                        answerTimeoutLock.notify();
                    }
                    game.broadcastScoreboard();
                    return true;
                } else {
                    if (s.getScoreValue() > 0) {
                        s.updateScore(-1);
                        game.broadcastScoreboard();
                    }
                    return false;
                }
            }
            return true;
        }
    }
}

