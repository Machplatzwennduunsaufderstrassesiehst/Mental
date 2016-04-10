package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Malte on 07.04.2016.
 */
public abstract class GameMode {

    ArrayList<ExerciseCreator> compatibleExerciseCreators = new ArrayList<>();

    protected static final int EXERCISE_TIMEOUT = 30;

    public boolean gameIsRunning;
    public int minPlayers = 2;
    protected Game game;

    protected final Object answerLock = new Object();

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

    public void exerciseTimeout() {
        doWaitTimeout(EXERCISE_TIMEOUT);
    }

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

    protected void doWaitTimeout (int timeout) {
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TIME_LEFT);
        try {
            j.put("time", timeout);
            for (int i = 0; i < game.activePlayers.size(); i++) {
                Player p = game.activePlayers.get(i);
                p.makePushRequest(new PushRequest(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        synchronized (answerLock) {
            try {
                answerLock.wait(timeout * 1000);
            } catch (InterruptedException e) {}
        }
    }
}
