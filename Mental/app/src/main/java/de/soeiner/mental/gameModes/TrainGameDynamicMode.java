package de.soeiner.mental.gameModes;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.Wave;

/**
 * Created by Sven L. on 24.06.16.
 */
public class TrainGameDynamicMode extends TrainGameMode {

    private int nWaves = 10;

    public TrainGameDynamicMode(Game game) {
        super(game);
    }

    Wave[] initiateWaves() {
        Wave[] waves = new Wave[nWaves];
        for (int i = 0; i < nWaves; i++) {

        }
        return null;
    }
}
