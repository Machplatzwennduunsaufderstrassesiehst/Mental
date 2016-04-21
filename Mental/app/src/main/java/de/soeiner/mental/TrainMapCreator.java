package de.soeiner.mental;

/**
 * Created by Malte on 21.04.2016.
 */
public class TrainMapCreator extends ExerciseCreator{

    public double getExpectedSolveTime() { return 0; }
    public String create() {return null;}

    public String getName() {
        return "Casual Train Map";
    }

    @Override
    public TrainTrack[][] createTrainMap() { //TODO
       TrainTrack[][] map = new TrainTrack[10][10];
        return map;
    }
}