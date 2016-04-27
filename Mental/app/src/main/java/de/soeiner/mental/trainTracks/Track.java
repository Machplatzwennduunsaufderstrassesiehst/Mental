package de.soeiner.mental.trainTracks;

/**
 * Created by Malte on 26.04.2016.
 */
public class Track extends TrainTrack {
    public Track(int value){
        this.setValue(value);
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
        return "track";
    }

    @Override
    public void setSuccessor(TrainTrack s){
        successor = s;
    }
}
