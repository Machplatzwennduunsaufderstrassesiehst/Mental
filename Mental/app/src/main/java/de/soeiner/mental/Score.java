package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 16.02.16.
 */
public class Score extends JSONObject{

    public Score(String playerName, int score) {
        try {
            put("playerName", playerName);
            put("scoreValue", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerName(String playerName) {
        this.remove("playerName");
        try {
            this.put("playerName", playerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setScoreValue(int scoreValue) {
        this.remove("scoreValue");
        try {
            this.put("scoreValue", scoreValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
