package de.soeiner.mental.trainTracks;

/**
 * Created by Malte on 26.04.2016.
 */
public class BlockedTrack extends TrainTrack {


    public BlockedTrack(int x, int y, int v) {
        super(x, y, v);
    }

    @Override
    public String getType() {
        return "blocked";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        throw new RuntimeException("can't assign Succesor to blocked track");
    }
}