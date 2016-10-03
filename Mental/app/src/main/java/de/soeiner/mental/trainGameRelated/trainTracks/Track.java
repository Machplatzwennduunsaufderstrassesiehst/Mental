package de.soeiner.mental.trainGameRelated.trainTracks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 26.04.2016.
 */
public class Track extends TrainTrack {

    public Track(int x, int y, int v, int id) {
        super(x, y, v, id);
    }

    @Override
    public String getType() {
        return "track";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        successor = s;
        JSONObject position = new JSONObject();
        try {
            position.put("xpos", s.getX()); // alt
            position.put("ypos", s.getY()); // alt
            this.put("successorId", s.id); // Alternative
            this.put("successorPosition", position); //alt
            System.out.println("successorId mit Wert " + s.id + " von Track mit Koordinaten (" + getX() + "|" + getY() + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getSuccessorId() {
        if (hasSuccessor()) {
            return this.successor.getId();
        } else {
            throw new RuntimeException("no successor to get Id from ");
        }
    }
}
