package de.soeiner.mental.gameModes.traingame;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameModes.GameMode;

/**
 * Created by Malte on 14.09.2016.
 */
public abstract class TrainGame extends GameMode {
    public TrainGame(Game game) {
        super(game);
        type = "Train";
    }
}
