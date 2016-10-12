package de.soeiner.mental.main;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;

/**
 * Created by Malte on 07.04.2016.
 */
public class VotingSuggestion extends JSONObject {

    GameMode gameMode;
    ExerciseCreator exerciseCreator;
    ArrayList<Player> voters = new ArrayList<Player>();

    public VotingSuggestion(GameMode g, ExerciseCreator e, int suggestionID) {
        gameMode = g;
        exerciseCreator = e;
        try {
            this.put("gameMode", g.getName());
            this.put("exerciseCreator", e.getName());
            this.put("suggestionID", suggestionID);
            this.put("votes", voters.size());
        } catch (JSONException s) {
            s.printStackTrace();
        }
        putName(g.getName() + " (" + e.getName() + ")");
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
