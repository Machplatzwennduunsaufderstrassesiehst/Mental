package de.soeiner.mental.trainGame.trainTracks;

/**
 * Created by Malte on 25.05.2016.
 */
public class Cross extends TrainTrack {

    public Cross(int x, int y, int v, int id) {
        super(x, y, v, id);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setSuccessor(TrainTrack s) {

    }
}
