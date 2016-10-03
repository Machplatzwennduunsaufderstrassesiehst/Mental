package de.soeiner.mental.gameModes.arithmetics;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;
import de.soeiner.mental.gameModes.GameMode;

/**
 * Created by Malte on 28.03.2016.
 */
public class MA_Knockout extends Arithmetic {

    public MA_Knockout(Game g) {
        super(g);
        minPlayers = 2;
    }

    public String getGameModeString() {
        return "Knockout";
    }

    public void prepareGame() {
        super.prepareGame();
        addAllPlayersToActive();
    }

    public void loop() {

        if (game.activePlayers.size() == 1) {
            System.out.println("knockout gewonnen");
            gameIsRunning = false;
            for (int i = 0; i < game.joinedPlayers.size(); i++) { //Spieler kriegen am Ende Scorepunkte
                Player p = game.joinedPlayers.get(i);
                Score s = p.getScore();
                s.updateScore(s.getScoreValue() * 20);
            }
            game.broadcastScoreboard();
            game.broadcastPlayerWon(game.activePlayers.get(0).getName(), "MA_Knockout");
        } else if (game.activePlayers.size() > 1) {
            int index = 0;
            for (int i = 1; i < game.activePlayers.size(); i++) {
                if (game.activePlayers.get(i).getScore().getScoreValue() <= game.activePlayers.get(index).getScore().getScoreValue()) {
                    index = i;
                }
            }
            game.broadcastMessage(game.activePlayers.get(index).getName() + " wurde eleminiert!");
            game.activePlayers.remove(index);
            gameIsRunning = game.activePlayers.size() > 1;
        }
    }


    public boolean playerAnswered(Player player, JSONObject answer) {
        boolean allFinishedButOne = false;
        int z = 0;
        Score s = player.getScore();
        synchronized (answerLock) {
            if (!player.finished && gameIsRunning) {
                if (game.exerciseCreator.checkAnswer(answer)) {
                    s.updateScore(1); //score gibt bei knockout die überlebten runden wieder
                    game.broadcastMessage(player.getName() + " hat die Aufgabe als " + (game.getRank() + 1) + ". gelöst!");
                    player.finished = true;
                    for (int i = 0; i < game.activePlayers.size(); i++) {
                        Player p = game.joinedPlayers.get(i);
                        if (p.finished) {
                            z++;
                        }
                    }
                    if (game.activePlayers.size() - z <= 1) {
                        allFinishedButOne = true;
                    }
                    if (allFinishedButOne) {
                        answerLock.notify();
                    }
                    game.broadcastScoreboard();
                    return true;
                } else {
                    //          if (s.getScoreValue() > 0) {
                    //            s.updateScore(-1);
                    //            broadcastScoreboard();
                }
                return false;
            }
        }
        return true;
    }
}

