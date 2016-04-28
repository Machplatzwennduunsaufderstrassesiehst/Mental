package de.soeiner.mental.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.trainTracks.TrainTrack;

/**
 * Created by Malte on 21.04.2016.
 */
public class TrainGameMode extends GameMode {

    TrainTrack[][] trainMap;

    protected void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new TrainMapCreator());
    }


    public TrainGameMode(Game game) {
        super(game);
    }

    @Override
    public void prepareGame() {
        super.prepareGame();
        for(int i = 0; i<game.joinedPlayers.size();i++){
            game.activePlayers.add(game.joinedPlayers.get(i));
        }
        game.broadcastExercise();
        TrainMapCreator trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        trainMap = trainMapCreator.getTrainMap();
    }

    @Override
    public void loop() {
        while(gameIsRunning){

        }

    }

    @Override
    public boolean playerAnswered(Player player, JSONObject answer) {
        return false;
    }

    @Override
    public String getGameModeString() {
        return "Train Game";
    }
}
