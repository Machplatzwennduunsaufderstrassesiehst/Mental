package de.soeiner.mental.gameModes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.exerciseCreators.MixedExerciseCreator;
import de.soeiner.mental.exerciseCreators.MultExerciseCreator;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;

/**
 * Created by Malte on 07.04.2016.
 */
public abstract class GameMode {

    ArrayList<ExerciseCreator> compatibleExerciseCreators = new ArrayList<>();

    public static final int EXERCISE_TIMEOUT = 30;

    public boolean gameIsRunning;
    public int minPlayers = 2;
    public boolean needsConfirmation = false;
    public Game game;

    public final Object answerLock = new Object();

    public GameMode(Game game){
        this.game = game;
        initializeCompatibleExerciseCreators();
    }

    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new MultExerciseCreator());
        compatibleExerciseCreators.add(new MixedExerciseCreator());
        compatibleExerciseCreators.add(new SimpleMultExerciseCreator());
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
        game.exerciseCreator.resetDifficulty();
    }

    public boolean getGameIsRunning(){ return gameIsRunning; }
    public abstract boolean playerAnswered(Player player, JSONObject answer);
    public abstract String getGameModeString();

    public void resetGameMode(){
        gameIsRunning = true;
        for (Player joinedPlayer : game.joinedPlayers) {
            joinedPlayer.getScore().resetScoreValue();
        }
    }

    public ArrayList<ExerciseCreator> getCompatibleExerciseCreators() {
        return compatibleExerciseCreators;
    }

    public void doWaitTimeout (int timeout) {
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


    public void newExercise(){
        game.exerciseCreator.next(); // erstellt neue aufgabe
        game.broadcastExercise(); // sendet aufgabe an alle spieler
        game.exerciseCreator.increaseDifficulty();
    }

    public void removePlayer(Player p) {}
}
