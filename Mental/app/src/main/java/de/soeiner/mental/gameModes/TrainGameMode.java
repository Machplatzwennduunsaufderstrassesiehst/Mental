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

    private int HEALTH_NEEDED_TO_WIN = 50;
    private int REWARD = 100;
    TrainTrack[][] trainMap;
    Switch[] switches;
    Goal[] goals;
    String[] colors = {"#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000", "#000000"};
    private final double MAX_SPEED = 3.0; // 3.0
    private final double MIN_SPEED = 0.5;
    private int TRAIN_SPAWN_INTERVAL = 3000; //3000 //in milllisekunden
    private final int TRAIN_ARRIVED_REWARD = 10;
    private int health;

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
        game.exerciseCreator.next(); // erstellt die neue map
        game.broadcastExercise(); // macht nichts außer die map an alle zu senden
        trainMap = trainMapCreator.getTrainMap(); //TODO, von player abhängig machen
        switches = getSwitches();
        goals = getGoals();
        for (int i = 0; i < switches.length; i++) {
            switches[i].setSwitchId(i);
        }
        for (int i = 0; i < goals.length; i++) {
            goals[i].setGoalId(i);
        }
        health = 10; //TODO, von player abhängig machen
    }

    @Override
    public void loop() {
        int destinationId = 0;
        int idcounter = 0;
        double speed = 0;
        while(gameIsRunning){
            destinationId = (int) (Math.random()*goals.length);
            speed = Math.random()*(MAX_SPEED-MIN_SPEED)+MIN_SPEED; //mindestens 0.5, maximal 3
            new Train(idcounter, colors[destinationId], destinationId, speed, this); //zug spawnen
            idcounter++;
            try{
                Thread.sleep(TRAIN_SPAWN_INTERVAL); //warten
            }catch(Exception e){e.printStackTrace();}
        }
    }

    public boolean playerAnswered(Player player, JSONObject answer) {
        if(answer.has("switch")){
            try {
                switches[answer.getInt("switch")].changeSwitch();
                for (int i = 0; i < game.activePlayers.size(); i++) {
                    game.activePlayers.get(i).sendSwitchChange(switches[answer.getInt("switch")]);
                }
            }catch (Exception e){e.printStackTrace();}
            return true;
        }
        return false;
    }

    public void trainArrived(int trainId, int goalId, boolean succsess){
        if(succsess){
            game.broadcastMessage("Zug hat sein Ziel erreicht!");
            health++;
            giveReward(5);
        }else{
            game.broadcastMessage("Zug hat das falsche Ziel erreicht :/");
            health--;
        }
        for(int i = 0; i<game.activePlayers.size();i++){
            if(succsess) {
                game.activePlayers.get(i).getScore().updateScore(TRAIN_ARRIVED_REWARD);
            }
            game.activePlayers.get(i).sendTrainArrived(trainId, goalId, succsess);
        }
         // TODO auskommentieren wenn testphase vorrüber
        /*
        if(health <= 0){
            gameIsRunning = false;
            game.broadcastMessage("Spieler haben verloren !");
        }
        if(health >= HEALTH_NEEDED_TO_WIN){
            gameIsRunning = false;
            playersWon();
        }
        */

    }

    private void playersWon(){
        game.broadcastMessage("Spieler haben gewonnen!");
        game.broadcastMessage("und bekomen einen Bonus von "+REWARD+"$ !");
        giveReward(REWARD);
        try {
            Thread.sleep(3000);
        }catch(Exception e){}
    }

    private void giveReward(int reward){
        for(int i = 0; i<game.activePlayers.size();i++){
            game.activePlayers.get(i).getScore().updateScore(reward);
        }
    }


    public void broadcastNewTrain(JSONObject train){
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendNewTrain(train);
        }
    }

    @Override
    public String getGameModeString() {
        return "Train Game";
    }

    public void broadcastTrainDecision(int trainId, int switchId, int direction){
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendTrainDecision(trainId, switchId, direction);
        }
    }
    @Override
    public void doWaitTimeout (int timeout){} //es soll kein timeout stattfinden

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
                    s[z] = (Switch) trainMap[i][j];
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
                    s[z] = (Goal) trainMap[i][j];
                    z++;
                }
            }
        }
        return s;
    }

    //@Override
    public void newExercise() {}
}
