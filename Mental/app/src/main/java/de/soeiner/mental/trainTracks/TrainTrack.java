package de.soeiner.mental.trainTracks;

import org.json.JSONObject;

/**
 * Created by Malte on 26.04.2016.
 */
public abstract class TrainTrack extends JSONObject{ //TODO: set attributes

    protected TrainTrack successor;
    protected TrainTrack predecessor;
    protected int id;
    protected int from;
    protected int to;
    protected int switchTo;
    protected int value = 0;
    protected int[] coordinates = new int[2];

    public TrainTrack(int x, int y, int v){
        coordinates[0] = x;
        coordinates[1] = y;
        setValue(v);
    }

    public abstract int getFrom();
    public abstract int getTo();
    public abstract int getSwitchTo();
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

    public abstract void setSuccessor(TrainTrack s);
    public void setPredecessor(TrainTrack p){
        predecessor = p;
    }
    public void setValue(int v){
        value = v;
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