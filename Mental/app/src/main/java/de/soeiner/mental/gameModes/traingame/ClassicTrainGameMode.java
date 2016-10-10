package de.soeiner.mental.gameModes.traingame;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;

/**
 * Created by Malte on 21.04.2016.
 */
public class ClassicTrainGameMode extends TrainGameMode {

    public ClassicTrainGameMode(Game game) {
        super(game);
    }

    @Override
    public String getGameModeString() {
        return "Classic Coop";
    }

    @Override
    public void extraPreparationsPreMap() {
        // trainMapCreator.setSizeManually(2); // wieso war hier 2 hard gecoded?
        reward = 100;
    }

    @Override
    public void extraPreparationsPostMap() {

    }

    Wave[] initiateWaves() {
        Wave[] wellen = new Wave[7];
        //double minspeed, double maxspeed, trainspawnintervall, trainarrivedreward, health, healthnw, reward
        //wellen[0] = new Wave(6, 6, 100, 1, 99999, 999999, 25);
        //wellen[0] = new Wave(1.0, 1.0, 3500, 1, 10, 15, 25);
        wellen[0] = new Wave(1.1, 1.3, 2700, 2, 3, 25, 50);
        wellen[1] = new Wave(1.4, 1.5, 2100, 3, 10, 30, 100);
        wellen[2] = new Wave(1.7, 1.5, 1800, 4, 10, 35, 200);
        wellen[3] = new Wave(1.7, 1.5, 1600, 10, 10, 40, 300);
        wellen[4] = new Wave(1.4, 1.6, 1400, 10, 10, 50, 500);
        wellen[5] = new Wave(1.1, 1.7, 1200, 10, 10, 50, 750);
        wellen[6] = new Wave(0.7, 1.7, 1000, 10, 10, 50, 1000);
/*        int testhealth = 8;
        int testhealthNeededToWin = 17; // um schnell zur nächsten wave zu gelangen
        wellen[0] = new Wave(0.5, 0.5, 4000, 1, 10, testhealthNeededToWin, 25);
        wellen[1] = new Wave(1.0, 1.0, 4000, 2, 10, testhealthNeededToWin, 50);
        wellen[2] = new Wave(1.5, 1.5, 3500, 3, 10, testhealthNeededToWin, 100);
        wellen[3] = new Wave(2.0, 2.0, 3000, 4, 10, testhealthNeededToWin, 200);
        wellen[4] = new Wave(4.0, 4.0, 2000, 10, 10, testhealthNeededToWin, 500);*/
        return wellen;
    }

    @Override
    public void loop(){
        goThroughWaves();
    }

    public void trainArrived(int trainId, Goal goal, boolean succsess) {
        if (succsess) {
            //game.broadcastMessage("Zug hat sein Ziel erreicht!");
            health++;
            giveReward(trainArrivedReward);
        } else {
            //game.broadcastMessage("Zug hat das falsche Ziel erreicht :/");
            health--; //TODO TODO TODO TODO TODO
        }
        for (int i = 0; i < game.activePlayers.size(); i++) {
            if (succsess) {
                game.activePlayers.get(i).getScore().updateScore(trainArrivedReward);
            }
            game.activePlayers.get(i).sendTrainArrived(trainId, goal.getGoalId(), succsess);
        }
        if (health <= 0) { //Check for Wellen status
            waveSuccess = false;
            waveIsRunning = false;
        }
        if (health >= healthNeededToWin) {
            waveSuccess = true;
            waveIsRunning = false;
        }
    }
}
