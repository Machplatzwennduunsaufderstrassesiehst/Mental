package de.soeiner.mental.trainGame.gameModes;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.trainGame.trainGenerators.Wave;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 16.09.2016.
 */
public class SuddenDeathTrainGameMode extends TrainGameMode {

    EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {
            Goal goal = event.getGoal();
            boolean success = event.isMatch();
            int trainId = event.getTrain().getId();
            if (success) {
                health++;
                giveReward(trainArrivedReward);
            } else {
                waveSuccess = false;
                waveIsRunning = false;
            }
            for (int i = 0; i < game.activePlayers.size(); i++) {
                if (success) {
                    game.activePlayers.get(i).getScore().updateScore(trainArrivedReward);
                }
                game.activePlayers.get(i).sendTrainArrived(trainId, goal.getGoalId(), success);
            }
            if (health >= healthNeededToWin) {
                waveSuccess = true;
                waveIsRunning = false;
            }
        }
    };

    public SuddenDeathTrainGameMode(final Game game) {
        super(game);
        trainArrived.addListenerOnce(trainArrivedListener);
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
    public void prepareMapCreation() {
        reward = 500;
    }
    @Override
    public void prepareGameStart() {

    }

    @Override
    public void loop() {
        goThroughWaves();
    }

    @Override
    public String getName() {
        return "Sudden Death";
    }
}
