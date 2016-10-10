package de.soeiner.mental.trainGame.trainTracks;

import org.json.JSONException;

/**
 * Created by Malte on 28.04.2016.
 */
public class Goal extends TrainTrack {

    private int goalId = 0;
    private int matchingId = -1;
    private boolean destroyed = false;

    public Goal(int x, int y, int v, int id) {
        super(x, y, v, id);
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
        try {
            this.put("goalId", goalId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMatchingId(int x){
        matchingId = x;
        try {
            this.put("matchingId", matchingId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getMatchingId() {
        return matchingId;
    }

    public int getGoalId() {
        return goalId;
    }


    @Override
    public String getType() {
        return "goal";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        throw new RuntimeException("there is no successor to a goal");
    }

    public void destroy(){
        destroyed = true;
    }
    public boolean isDestroyed(){
        return destroyed;
    }
}
