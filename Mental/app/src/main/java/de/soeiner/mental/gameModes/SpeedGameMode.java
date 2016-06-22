package de.soeiner.mental.gameModes;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;
import de.soeiner.mental.exerciseCreators.SquareMultExerciseCreator;
import de.soeiner.mental.util.JSONObject;

/**
 * Created by Malte on 09.04.2016.
 */
public class SpeedGameMode extends GameMode { //Es empfiehlt sich vll. diesen Modus mit dem kleinem 1x1 zu paaren. Jup, siehe unten

    private static final int SPEED_EXERCISE_TIMEOUT = 5;

    public SpeedGameMode(Game g){
        super(g);
        minPlayers = 1;
    }

    @Override
    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new SimpleMultExerciseCreator());
        compatibleExerciseCreators.add(new SquareMultExerciseCreator());
    }

    public String getGameModeString() {
        return "Speed";
    }

    public void prepareGame() {
        super.prepareGame();
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
    }

    @Override
    public void exerciseTimeout() {
        doWaitTimeout(SPEED_EXERCISE_TIMEOUT);
    }

    public void loop() {}

    public boolean playerAnswered(Player player, JSONObject answer) {
        Score s = player.getScore();
        synchronized (answerLock) {
            if (game.exerciseCreator.checkAnswer(answer)) {
                s.updateScore(game.getPoints());
                game.broadcastMessage(player.getName() + " hat die Aufgabe als 1. gelöst");
                if (s.getScoreValue() >= 100) {
                    gameIsRunning = false; // schleife in run() beenden
                    game.broadcastPlayerWon(player.getName(), getGameModeString());
                    answerLock.notify();
                }
                answerLock.notify();
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
    }
}
