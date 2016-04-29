package de.soeiner.mental.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.trainTracks.*;

/**
 * Created by Malte on 21.04.2016.
 */
public class TrainGameMode extends GameMode {

    TrainTrack[][] trainMap;
    Switch[] switches;
    Goal[] goals;
    String[] colors = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private final double MAX_SPEED = 3.0;
    private final double MIN_SPEED = 0.5;
    private int TRAIN_SPAWN_INTERVAL = 500; //in milllisekunden
    public void initializeCompatibleExerciseCreators() {
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
        TrainMapCreator trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        trainMap = trainMapCreator.getTrainMap();
        switches = getSwitches();
        goals = getGoals();
        for (int i = 0; i < switches.length; i++) {
            switches[i].setSwitchId(i);
        }
        for (int i = 0; i < goals.length; i++) {
            goals[i].setGoalId(i);
        }
        game.broadcastExercise();
    }

    private Switch[] getSwitches() {
        int z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap.length; j++) {
                if (trainMap[i][j].getType().equals("switch")) {
                    z++;
                }
            }
        }
        Switch[] s = new Switch[z];
        z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap.length; j++) {
                if (trainMap[i][j].getType().equals("switch")) {
                    s[z] = (Switch) trainMap[i][j]; //TODO possible breakpoint
                    z++;
                }
            }
        }
        return s;
    }
    private Goal[] getGoals() {
        int z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap.length; j++) {
                if (trainMap[i][j].getType().equals("goal")) {
                    z++;
                }
            }
        }
        Goal[] s = new Goal[z];
        z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap.length; j++) {
                if (trainMap[i][j].getType().equals("goal")) {
                    s[z] = (Goal) trainMap[i][j]; //TODO possible breakpoint
                    z++;
                }
            }
        }
        return s;
    }

    @Override
    public void loop() {
        int destinationId = 0;
        int idcounter = 0;
        double speed = 0;
        while(gameIsRunning){
            destinationId = (int) Math.random()*goals.length;
            speed = Math.random()*(MAX_SPEED-MIN_SPEED)+MIN_SPEED; //mindestens 0.5, maximal 3
            new Train(idcounter, colors[destinationId], destinationId, speed, this); //zug spawnen
            idcounter++;
            try{
                Thread.sleep(500); //waten
            }catch(Exception e){e.printStackTrace();}
        }

    }

    public boolean playerAnswered(Player player, JSONObject answer) {
        if(answer.has("switch")){
            try {
                switches[answer.getInt("switch")].changeSwitch();
            }catch (Exception e){e.printStackTrace();}
            return true;
        }
        return false;
    }

    public void trainArrived(){ //TODO
        //reward
        //nachtricht an client
        //sendTrainArrived(trainId)
    }

    @Override
    public String getGameModeString() {
        return "Train Game";
    }
}
