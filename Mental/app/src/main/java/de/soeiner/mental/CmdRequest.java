package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class CmdRequest {

    public static final String SEND_EXERCISE = "{type:'exercise'}";
    public static final String SEND_SCOREBOARD = "{type: 'scoreboard'}";
    public static final String SEND_GAMES = "{type: '_get_games_'}";
    public static final String SEND_PLAYER_WON = "{type: 'player_won'}";
    public static final String SEND_TIME_LEFT = "{type: 'time_left'}";
    public static final String SEND_MESSAGE = "{type: 'message'}"; // wird nun allgemein fuer player + broadcast "nachrichten" (human readable!) genutzt, man
    public static final String SEND_GAME_STRING = "{type: 'game_string'}";
    public static final String SEND_SHOP_ITEM_LIST = "{type: 'shop_item_list'}";
    public static final String SEND_SUGGESTIONS = "{type: 'suggestions'}";
    public static final String SEND_SHOW_SCOREBOARD = "{type: 'showScoreboard'}";


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



    public static JSONObject makeCmd(String json) {
        try {
            return new JSONObject(json);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static JSONObject makeResponseCmd(String type) {
        try {
            return new JSONObject("{type:'_"+type+"_'}");
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }
}
