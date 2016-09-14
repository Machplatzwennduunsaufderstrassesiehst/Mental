package de.soeiner.mental.gameModes.traingame;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.Wave;

/**
 * Created by Sven L. on 24.06.16.
 */
public class Train_Dynamic extends Train_Classic {

    private int nWaves = 10;

    public Train_Dynamic(Game game) {
        super(game);
    }

    @Override
    public String getGameModeString() {
        return "Dynamic";
    }

    Wave[] initiateWaves() {
        Wave[] waves = new Wave[nWaves];
        for (int i = 0; i < nWaves; i++) {

        }
        return null;
    }
}
