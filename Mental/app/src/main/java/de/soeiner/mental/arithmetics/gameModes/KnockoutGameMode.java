package de.soeiner.mental.arithmetics.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;

/**
 * Created by Malte on 28.03.2016.
 */
public class KnockoutGameMode extends ArithmeticGameMode {

    public KnockoutGameMode(Game g) {
        super(g);
        minPlayers = 2;
    }

    public String getName() {
        return "Knockout";
    }

    public void prepareGame() {
        super.prepareGame();
        addAllPlayersToActive();
    }

    public void loop() {

        if (game.activePlayers.size() == 1) {
            System.out.println("knockout gewonnen");
            running = false;
            for (int i = 0; i < game.joinedPlayers.size(); i++) { //Spieler kriegen am Ende Scorepunkte
                Player p = game.joinedPlayers.get(i);
                Score s = p.getScore();
                s.updateScore(s.getScoreValue() * 20);
            }
            game.broadcastScoreboard();
            broadcastPlayerWon(game.activePlayers.get(0).getName(), "Knockout");
        } else if (game.activePlayers.size() > 1) {
            int index = 0;
            for (int i = 1; i < game.activePlayers.size(); i++) {
                if (game.activePlayers.get(i).getScore().getScoreValue() <= game.activePlayers.get(index).getScore().getScoreValue()) {
                    index = i;
                }
            }
            game.broadcastMessage(game.activePlayers.get(index).getName() + " wurde eleminiert!");
            game.activePlayers.remove(index);
            running = game.activePlayers.size() > 1;
        }
    }


    public boolean playerAction(Player player, JSONObject actionData) {
        boolean allFinishedButOne = false;
        int z = 0;
        Score s = player.getScore();
        synchronized (answerTimeoutLock) {
            if (!player.finished && running) {
                if (game.exerciseCreator.checkAnswer(actionData)) {
                    s.updateScore(1); //score gibt bei knockout die überlebten runden wieder
                    game.broadcastMessage(player.getName() + " hat die Aufgabe als " + (getRank() + 1) + ". gelöst!");
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
                        answerTimeoutLock.notify();
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

