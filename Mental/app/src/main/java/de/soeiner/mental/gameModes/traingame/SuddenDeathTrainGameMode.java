package de.soeiner.mental.gameModes.traingame;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;

/**
 * Created by Malte on 16.09.2016.
 */
public class SuddenDeathTrainGameMode extends TrainGameMode {

    public SuddenDeathTrainGameMode(Game game) {
        super(game);
    }

    Wave[] initiateWaves() {
        Wave[] wellen = new Wave[7];
        wellen[0] = new Wave(1.1, 1.3, 2700, 2, 3, 25, 50);
        wellen[1] = new Wave(1.4, 1.8, 2200, 3, 10, 30, 100);
        wellen[2] = new Wave(1.7, 2.2, 1800, 4, 10, 35, 200);
        wellen[3] = new Wave(1.7, 2.5, 1600, 10, 10, 40, 300);
        wellen[4] = new Wave(1.5, 2.6, 1400, 10, 10, 50, 500);
        wellen[5] = new Wave(1.3, 2.6, 1300, 10, 10, 50, 750);
        wellen[6] = new Wave(1.0, 2.6, 1300, 10, 10, 50, 1000);
        return wellen;
    }

    @Override
    public void trainArrived(int trainId, Goal goal, boolean succsess) {
        if (succsess) {
            health++;
            giveReward(trainArrivedReward);
        } else {
            waveSuccess = false;
            waveIsRunning = false;
        }
        for (int i = 0; i < game.activePlayers.size(); i++) {
            if (succsess) {
                game.activePlayers.get(i).getScore().updateScore(trainArrivedReward);
            }
            game.activePlayers.get(i).sendTrainArrived(trainId, goal.getGoalId(), succsess);
        }
        if (health >= healthNeededToWin) {
            waveSuccess = true;
            waveIsRunning = false;
        }
    }

    @Override
    public void extraPreparationsPreMap() {
        reward = 500;
    }
    @Override
    public void extraPreparationsPostMap() {

    }

    @Override
    public void loop() {
        goThroughWaves();
    }

    @Override
    public String getGameModeString() {
        return "Sudden Death";
    }
}
