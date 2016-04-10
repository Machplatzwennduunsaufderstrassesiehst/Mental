package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 07.04.2016.
 */
public abstract class GameMode {

    ArrayList<ExerciseCreator> compatibleExerciseCreators = new ArrayList<>();

    public boolean gameIsRunning;
    public int minPlayers = 2;
    protected Game game;

    public GameMode(Game game){
        this.game = game;
        initializeCompatibleExerciseCreators();
    }

    protected void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(MultExerciseCreator.getUniqueInstance());
        compatibleExerciseCreators.add(MixedExerciseCreator.getUniqueInstance());
        compatibleExerciseCreators.add(SimpleMultExerciseCreator.getUniqueInstance());
    }

    public void waitForPlayers() {
        while(game.joinedPlayers.size() < minPlayers){
            try{Thread.sleep(1000);}catch(Exception e){} //Warte auf genügend Spieler
        }
    }
    public abstract void loop();
    public void prepareGame(){
        resetGameMode();
    }
    public boolean getGameIsRunning(){ return gameIsRunning; }
    public abstract boolean playerAnswered(Player player, int answer);
    public abstract String getGameModeString();

    public void resetGameMode(){
        gameIsRunning = true;
    }

    public ArrayList<ExerciseCreator> getCompatibleExerciseCreators() {
        return compatibleExerciseCreators;
    }
}
