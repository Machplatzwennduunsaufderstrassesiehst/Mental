package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 21.04.2016.
 */
public class TrainGameMode extends GameMode {

    TrainTrack[][] trainMap;

    public TrainGameMode(Game game) {
        super(game);
    }

    @Override
    public void prepareGame() {
        super.prepareGame();
        for(int i = 0; i<game.joinedPlayers.size();i++){
            game.activePlayers.add(game.joinedPlayers.get(i));
        }
        trainMap = game.exerciseCreator.createTrainMap();
        game.broadcastTrainMap(trainMap);
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
        return "Train Game";
    }
}
