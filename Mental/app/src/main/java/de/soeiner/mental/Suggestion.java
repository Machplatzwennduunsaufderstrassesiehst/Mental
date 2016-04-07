package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Malte on 07.04.2016.
 */
public class Suggestion extends JSONObject{

    GameMode gameMode;
    ExerciseCreator exerciseCreator;
    ArrayList<Player> voters = new ArrayList<Player>();

    public Suggestion(GameMode g, ExerciseCreator e, int suggestionID){
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
    }

    public void upvote(Player p){
        if(!voters.contains(p)) {
            voters.add(p);
        }
    }

    public void downvote(Player p){
        if(voters.contains(p)) {
            voters.remove(p);
        }
    }

    public int getVotes(){
        return voters.size();
    }
    public void reset(){
        voters = new ArrayList<Player>();
    }
    public ArrayList<Player> getPlayers(){
        return voters;
    }

}
