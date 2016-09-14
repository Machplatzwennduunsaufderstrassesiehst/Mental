package de.soeiner.mental.gameModes.arithmetics;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameModes.GameMode;

/**
 * Created by Malte on 14.09.2016.
 */
public abstract class Arithmetic extends GameMode {
    public Arithmetic(Game game){
        super(game);
        type = "MA";
    }
}
