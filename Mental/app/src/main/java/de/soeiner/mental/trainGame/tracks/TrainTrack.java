package de.soeiner.mental.trainGame.tracks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 26.04.2016.
 */
public abstract class TrainTrack extends JSONObject { //TODO: set attributes

    protected TrainTrack successor;
    protected TrainTrack predecessor;
    protected int id;
    protected int to;
    protected int value = 0;
    protected int[] coordinates = new int[2];

    public TrainTrack(int x, int y, int v, int id) {
        setCoordinates(x, y);
        this.id = id;
        this.value = v;
        try {
            this.put("trackType", this.getType());
            this.put("value", v);
            this.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Created: " + this);
    }

    public abstract String getType();

    public TrainTrack getSuccessor() {
        return successor;
    }

    public TrainTrack getPredecessor() {
        return predecessor;
    }

    public int getId() {
        return id;
    }

    public boolean hasSuccessor() {
        return !(successor == null);
    }

    public boolean hasPredecessor() {
        return predecessor != null;
    }

    public abstract void setSuccessor(TrainTrack s);

    public void setPredecessor(TrainTrack p) {
        predecessor = p;
        /*
        JSONObject position = new JSONObject();
        try {
            position.put("xpos", p.getX());
            position.put("ypos", p.getY());
            this.put("predecessorPosition", position);
        }catch(JSONException e){e.printStackTrace();}
        */
    }

    public void setValue(int v) {
        value = v;
        try {
            this.put("value", v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getValue() {
        return value;
    }

    public void setCoordinates(int x, int y) {
        coordinates[0] = x;
        coordinates[1] = y;
        try {
            this.put("xpos", x);
            this.put("ypos", y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return coordinates[0];
    }

    public int getY() {
        return coordinates[1];
    }

    public <T extends TrainTrack> T attachTrainTrack(T track) {
        this.setSuccessor(track);
        track.setPredecessor(this);
        track.setValue(this.getValue());
        return track;
    }

    public Track continueAsTrack(int[] v, int id) {
        Track track = new Track(coordinates[0] + v[0], coordinates[1] + v[1], value, id);
        return attachTrainTrack(track);
    }

    public Switch continueAsSwitch(int[] v, int id) {
        Switch s = new Switch(coordinates[0] + v[0], coordinates[1] + v[1], value, id);
        return attachTrainTrack(s);
    }

    public Goal continueAsGoal(int[] v, int id) {
        Goal g = new Goal(coordinates[0] + v[0], coordinates[1] + v[1], value, id);
        return attachTrainTrack(g);
    }

    @Override
    public String toString() {
        return this.getType() + " ("+coordinates[0]+"|"+coordinates[1]+") (id: "+id+", value: "+value+")";
    }
}