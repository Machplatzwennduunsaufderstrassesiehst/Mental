package de.soeiner.mental.arithmetics.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;
import de.soeiner.mental.exerciseCreators.SquareMultExerciseCreator;
import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.main.Score;

/**
 * Created by Malte on 09.04.2016.
 */
public class SpeedGameMode extends ArithmeticGameMode { //Es empfiehlt sich vll. diesen Modus mit dem kleinem 1x1 zu paaren. Jup, siehe unten

    private static final int SPEED_EXERCISE_TIMEOUT = 5;

    public SpeedGameMode(Game g) {
        super(g);
        minPlayers = 1;
    }

    @Override
    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new SimpleMultExerciseCreator());
        compatibleExerciseCreators.add(new SquareMultExerciseCreator());
    }

    public String getName() {
        return "Speed";
    }

    public void prepareGame() {
        super.prepareGame();
        addAllPlayersToActive();
    }

    @Override
    public void exerciseTimeout() {
        doWaitTimeout(SPEED_EXERCISE_TIMEOUT);
    }

    public void loop() {
    }

    public boolean playerAction(Player player, JSONObject actionData) {
        Score s = player.getScore();
        synchronized (answerTimeoutLock) {
            if (game.exerciseCreator.checkAnswer(actionData)) {
                s.updateScore(getPoints());
                game.broadcastMessage(player.getName() + " hat die Aufgabe als 1. gelöst");
                if (s.getScoreValue() >= 100) {
                    setRunning(false);
                    broadcastPlayerWon(player.getName(), getName());
                    answerTimeoutLock.notify();
                }
                answerTimeoutLock.notify();
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
