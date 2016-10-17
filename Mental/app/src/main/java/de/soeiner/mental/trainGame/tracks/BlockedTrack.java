package de.soeiner.mental.trainGame.tracks;

/**
 * Created by Malte on 26.04.2016.
 */
public class BlockedTrack extends TrainTrack {


    public BlockedTrack(int x, int y, int v, int id) {
        super(x, y, v, id);
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