package de.soeiner.mental.trainTracks;

import java.util.ArrayList;

/**
 * Created by Malte on 26.04.2016.
 */
public class Switch extends TrainTrack {

    ArrayList<TrainTrack> successors = new ArrayList<TrainTrack>();
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
        return "switch";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        if (!successors.contains(s)) {
            successors.add(s);
        }
    }

    public ArrayList getSuccessors(){
        return successors;
    }
}
