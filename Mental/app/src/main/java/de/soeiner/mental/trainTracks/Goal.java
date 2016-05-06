package de.soeiner.mental.trainTracks;

/**
 * Created by Malte on 28.04.2016.
 */
public class Goal extends TrainTrack{

    private int goalId = 0;

    public Goal(int x, int y, int v) {
        super(x, y, v);
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }
    public int getGoalId(){
        return goalId;
    }

    @Override
    public int getFrom() {
        return 0;
    }

    @Override
    public int getTo() {
        return 0;
    }

    @Override
    public int getSwitchTo() {
        return 0;
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
