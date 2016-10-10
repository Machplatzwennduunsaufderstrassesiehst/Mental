package de.soeiner.mental.communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class CmdRequest {

    public static final String SEND_EXERCISE = "{type:'exercise'}";
    public static final String SEND_SCOREBOARD = "{type: 'scoreboard'}";
    public static final String SEND_GAMES = "{type: '_getGames_'}";
    public static final String SEND_PLAYER_WON = "{type: 'playerWon'}";
    public static final String SEND_TIME_LEFT = "{type: 'timeLeft'}";
    public static final String SEND_MESSAGE = "{type: 'message'}"; // wird nun allgemein fuer player + broadcast "nachrichten" (human readable!) genutzt
    public static final String SEND_GAME_STRING = "{type: 'gameString'}";
    public static final String SEND_SHOP_ITEM_LIST = "{type: 'shopItemList'}";
    public static final String SEND_SUGGESTIONS = "{type: 'suggestions'}";
    public static final String SEND_SHOW_SCOREBOARD = "{type: 'showScoreboard'}";
    public static final String SEND_SHOW_EXERCISES = "{type: 'showExercises'}";
    public static final String SEND_COUNTDOWN = "{type: 'countdown'}";
    public static final String SEND_BEATBOB = "{type: 'beatbob'}";
    public static final String SEND_NEWTRAIN = "{type: 'newTrain'}";
    public static final String SEND_SWITCHCHANGE = "{type: 'switchChange'}";
    public static final String SEND_TRAINDECISION = "{type: 'trainDecision'}";
    public static final String SEND_TRAIN_ARRIVED = "{type: 'trainArrived'}";
    public static final String SEND_TRAIN_WAVE_COMPLETED = "{type: 'trainWaveCompleted'}";
    public static final String SEND_GOAL_DESTROYED = "{type: 'goalDestroyed'}";

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
