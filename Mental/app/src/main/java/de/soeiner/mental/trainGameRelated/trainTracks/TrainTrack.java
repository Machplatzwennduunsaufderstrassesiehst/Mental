package de.soeiner.mental.trainGameRelated.trainTracks;

import de.soeiner.mental.util.JSONException;
import de.soeiner.mental.util.JSONObject;

/**
 * Created by Malte on 26.04.2016.
 */
public abstract class TrainTrack extends JSONObject { //TODO: set attributes

    protected TrainTrack successor;
    protected TrainTrack predecessor;
    public int id;
    protected int to;
    protected int value = 0;
    protected int[] coordinates = new int[2];

    public TrainTrack(int x, int y, int v, int id){
        coordinates[0] = x;
        coordinates[1] = y;
        this.id = id;
        this.value = v;
        try {
            this.put("trackType", this.getType());
            this.put("xpos", x);
            this.put("ypos", y);
            this.put("value", v);
            this.put("id", id);
        }catch (JSONException e){e.printStackTrace();}
    }
    public abstract String getType();

    public TrainTrack getSuccessor(){
        return successor;
    }
    public TrainTrack getPredecessor(){
        return predecessor;
    }
    public int getId(){
        return id;
    }

    public boolean hasSuccessor(){
        return !(successor == null);
    }

    public abstract void setSuccessor(TrainTrack s);
    public void setPredecessor(TrainTrack p){
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
    public void setValue(int v){
        value = v;
        try {
            this.put("value", v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int getValue(){
        return value;
    }
    public void setCoordinates(int x, int y){
        coordinates[0] = x;
        coordinates[1] = y;
    }
    public int getX(){return coordinates[0];}
    public int getY(){return coordinates[1];}

}