package de.soeiner.mental.trainGameRelated.trainTracks;

import org.json.JSONException;

/**
 * Created by Malte on 28.04.2016.
 */
public class Goal extends TrainTrack {

    private int goalId = 0;
    private boolean destroyed = false;
    private int colorId = -1;

    public Goal(int x, int y, int v, int id) {
        super(x, y, v, id);
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
        try {
            if(colorId == -1) this.put("colorId", goalId);
            this.put("goalId", goalId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColorId(int x){
        colorId = x;
        try {
            this.put("colorId", colorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        throw new RuntimeException("there is no succesor to a goal");
    }

    public void destroy(){
        destroyed = true;
    }
    public boolean isDestroyed(){
        return destroyed;
    }
}
