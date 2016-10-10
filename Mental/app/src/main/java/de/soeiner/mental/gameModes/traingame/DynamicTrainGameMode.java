package de.soeiner.mental.gameModes.traingame;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;

/**
 * Created by Sven L. on 24.06.16.
 */
public class DynamicTrainGameMode extends TrainGameMode {

    private int nWaves = 10;

    public DynamicTrainGameMode(Game game) {
        super(game);
    }

    @Override
    public void loop() {
        goThroughWaves();
    }

    @Override
    public String getName() {
        return "Dynamic";
    }

    @Override
    public void trainArrived(int trainId, Goal goal, boolean succsess) {

    }

    Wave[] initiateWaves() {
        Wave[] waves = new Wave[nWaves];
        for (int i = 0; i < nWaves; i++) {

        }
        return null;
    }

    @Override
    public void extraPreparationsPreMap() {

    }
    public void extraPreparationsPostMap() {

    }
}
