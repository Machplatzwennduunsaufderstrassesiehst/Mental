package de.soeiner.mental.trainGame.tracks;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Malte on 28.04.2016.
 */
public class Goal extends TrainTrack {

    private int goalId = 0;
    private ArrayList<Integer> matchingIds = new ArrayList<>();
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

    public void addMatchingId(int x){
        if (!matchingIds.contains(x)) {
            matchingIds.add(x);
        }
        try {
            this.put("matchingIds", matchingIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer[] getMatchingIds() {
        return matchingIds.toArray(new Integer[matchingIds.size()]);
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
