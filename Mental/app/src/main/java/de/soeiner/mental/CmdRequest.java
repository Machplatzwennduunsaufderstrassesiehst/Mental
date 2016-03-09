package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class CmdRequest {

    public static final String GET_POINTS = "{type:'get', get:'points'}";
    public static final String SEND_EXERCISE = "{type:'exercise'}";
    public static final String SEND_SCOREBOARD = "{type: 'scoreboard'}";
    public static final String SEND_GAMES = "{type: '_get_games_'}";
    public static final String SEND_PLAYER_WON = "{type: 'player_won'}";
    public static final String SEND_ANSWER_FEEDBACK = "{type: '_answer_'}";
    public static final String SEND_TIME_LEFT = "{type: 'time_left'}";

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



    public static JSONObject makeCmd(String s) {
        try {
            return new JSONObject(s);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }
}
