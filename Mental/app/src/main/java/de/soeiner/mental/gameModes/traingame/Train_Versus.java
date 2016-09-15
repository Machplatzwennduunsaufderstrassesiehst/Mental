package de.soeiner.mental.gameModes.traingame;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.trainGameRelated.Wave;

/**
 * Created by Malte on 15.09.2016.
 */
public class Train_Versus extends TrainGame{

    public Train_Versus(Game game) {
        super(game);
    }

    @Override
    public void trainArrived(int trainId, int goalId, boolean succsess) {

    }

    @Override
    Wave[] initiateWaves() {
        return new Wave[0];
    }

    @Override
    public void extraPreparations() {

    }

    @Override
    public void distributePlayers() {
        //einteilung der Spieler in zwei oder mehr teams TODO
    }

    @Override
    public void loop() {

    }

    @Override
    public boolean playerAnswered(Player player, JSONObject answer) {
        return false;
    }

    @Override
    public String getGameModeString() {
        return "Versus";
    }
}
