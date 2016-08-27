package de.soeiner.mental.trainGameRelated.trainTracks;

/**
 * Created by Malte on 28.04.2016.
 */
public class Goal extends TrainTrack {

    private int goalId = 0;

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
}
