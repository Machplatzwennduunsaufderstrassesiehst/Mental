package de.soeiner.mental.gameFundamentals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.gameModes.GameMode;

/**
 * Created by Malte on 07.04.2016.
 */
public class Suggestion extends JSONObject {

    GameMode gameMode;
    ExerciseCreator exerciseCreator;
    ArrayList<Player> voters = new ArrayList<Player>();

    public Suggestion(GameMode g, ExerciseCreator e, int suggestionID) {
        gameMode = g;
        exerciseCreator = e;
        try {
            this.put("gameMode", g.getGameModeString());
            this.put("exerciseCreator", e.getName());
            this.put("suggestionID", suggestionID);
            this.put("votes", voters.size());
        } catch (JSONException s) {
            s.printStackTrace();
        }
        putName(g.getGameModeString() + " (" + e.getName() + ")");
    }

    public void upvote(Player p) {
        if (!voters.contains(p)) {
            voters.add(p);
            try {
                this.put("votes", voters.size());
            } catch (Exception e) {
            }
        }
    }

    public void downvote(Player p) {
        if (voters.contains(p)) {
            voters.remove(p);
            try {
                this.put("votes", voters.size());
            } catch (Exception e) {
            }
        }
    }

    public int getVotes() {
        return voters.size();
    }

    public void reset() {
        for (int i = 0; i < voters.size(); i++) {
            voters.remove(0);
        }
        try {
            this.put("votes", voters.size());
        } catch (Exception e) {
        }
    }

    public ArrayList<Player> getPlayers() {
        return voters;
    }

    public void putName(String suggestionName) {
        if (has("suggestionName")) this.remove("suggestionName");
        try {
            this.put("suggestionName", suggestionName);
        } catch (Exception e) {
        }
    }

    public boolean votersContain(Player p) { //vergleicht Spieler Objekt mit anderem Spielerobjekt
        if (voters.contains(p)) {
            return true;
        } else {
            return false;
        }
    }

    public void setHiglight(boolean yeORnaw) {
        if (has("highlight")) this.remove("highlight");
        try {
            this.put("highlight", yeORnaw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
