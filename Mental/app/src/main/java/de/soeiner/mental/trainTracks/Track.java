package de.soeiner.mental.trainTracks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 26.04.2016.
 */
public class Track extends TrainTrack {

    public Track(int x, int y, int v) {
        super(x, y, v);
    }

    @Override
    public String getType() {
        return "track";
    }

    @Override
    public void setSuccessor(TrainTrack s){
        successor = s;
        JSONObject position = new JSONObject();
        try {
            position.put("xpos", s.getX());
            position.put("ypos", s.getY());
            this.put("succesorPosition", position);
        }catch(JSONException e){e.printStackTrace();}
    }
}
