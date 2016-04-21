package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 21.04.2016.
 */
public abstract class TrainTrack extends JSONObject {

    private TrainTrack[] successor;
    private TrainTrack[] predecessor;
    private int id;
    private int from;
    private int to;
    private int switchTo;

    public abstract int getFrom();
    public abstract int getTo();
    public abstract int getSwitchTo();
    public abstract String getType();

    public TrainTrack[] getSuccessor(){
        return successor;
    }
    public TrainTrack[] getPredecessor(){
        return predecessor;
    }
    public int getId(){
        return id;
    }

    public void setSuccessor(TrainTrack[] s){
        successor = s.clone();
    }
    public void setPredecessor(TrainTrack[] p){
        predecessor = p.clone();
    }
}

class Track extends TrainTrack {
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
        return null;
    }
}

class Switch extends TrainTrack {
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
        return null;
    }
}

