package de.soeiner.mental.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;

/**
 * Created by Malte on 28.03.2016.
 */

public class ClassicGameMode extends GameMode {

    public ClassicGameMode(Game g) {
        super(g);
        minPlayers = 1;
    }

    public String getGameModeString() {
        return "Classic";
    }

    public void prepareGame() {
        super.prepareGame();
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
    }

    public void loop() {
    }

    public boolean playerAnswered(Player player, JSONObject answer) {
        boolean allFinished = true;
        Score s = player.getScore();
        synchronized (answerLock) {
            if (!player.finished) { // sonst kann man 2x mal punkte absahnen ;; spieler kriegt jetzt keine punkte mehr abgezogen für doppeltes antworten
                if (game.exerciseCreator.checkAnswer(answer)) {
                    s.updateScore(game.getPoints());
                    game.broadcastMessage(player.getName() + " hat die Aufgabe als " + (game.getRank() + 1) + ". gelöst!");
                    if (s.getScoreValue() > 100) {
                        gameIsRunning = false; // schleife in run() beenden
                        game.broadcastPlayerWon(player.getName(), getGameModeString());
                        answerLock.notify(); // hat einer gewonnen, muss das wait im game loop ebenfalls beendet werden.
                    }
                    player.finished = true;
                    for (int i = 0; i < game.activePlayers.size(); i++) {
                        Player p = game.activePlayers.get(i);
                        if (!p.finished) {
                            allFinished = false;
                        }
                    }
                    if (allFinished) {
                        answerLock.notify();
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

