package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 07.04.2016.
 */
public class Suggestion extends JSONObject{

    GameMode gameMode;
    ExerciseCreator exerciseCreator;

    public Suggestion(GameMode g, ExerciseCreator e, int suggestionID){
        gameMode = g;
        exerciseCreator = e;
        try {
            this.put("gameMode", g.getGameModeString());
            this.put("exerciseCreator", e.getName());
            this.put("suggestionID", suggestionID);
        } catch (JSONException s) {
            s.printStackTrace();
        }
    }
}
