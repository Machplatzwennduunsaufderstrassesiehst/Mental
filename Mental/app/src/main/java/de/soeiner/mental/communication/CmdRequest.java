package de.soeiner.mental.communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class CmdRequest {

    public static final String EXERCISE = "{type:'exercise'}";
    public static final String SCOREBOARD = "{type: 'scoreboard'}";
    public static final String GAMES = "{type: '_getGames_'}";
    public static final String PLAYER_WON = "{type: 'playerWon'}";
    public static final String TIME_LEFT = "{type: 'timeLeft'}";
    public static final String MESSAGE = "{type: 'message'}"; // wird nun allgemein fuer player + broadcast "nachrichten" (human readable!) genutzt
    public static final String GAME_STRING = "{type: 'gameString'}";
    public static final String SHOP_ITEM_LIST = "{type: 'shopItemList'}";
    public static final String SUGGESTIONS = "{type: 'suggestions'}";
    public static final String SHOW_SCOREBOARD = "{type: 'showScoreboard'}";
    public static final String SHOW_EXERCISES = "{type: 'showExercises'}";
    public static final String COUNTDOWN = "{type: 'countdown'}";
    public static final String BEATBOB = "{type: 'beatbob'}";
    public static final String NEWTRAIN = "{type: 'newTrain'}";
    public static final String SWITCHCHANGE = "{type: 'switchChange'}";
    public static final String TRAINDECISION = "{type: 'trainDecision'}";
    public static final String TRAIN_ARRIVED = "{type: 'trainArrived'}";
    public static final String TRAIN_WAVE_COMPLETED = "{type: 'trainWaveCompleted'}";
    public static final String GOAL_DESTROYED = "{type: 'goalDestroyed'}";

    private JSONObject cmd;

    public CmdRequest (JSONObject cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd.toString();
    }

    public JSONObject getJSONObject() {
        return cmd;
    }



    public static JSONObject makeCmd(String json) throws JSONException {
        return new JSONObject(json);
    }

    public static JSONObject makeResponseCmd(String type) throws JSONException {
        return makeCmd("{type:'_"+type+"_'}");
    }
}
