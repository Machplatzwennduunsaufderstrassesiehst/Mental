package de.soeiner.mental.trainTracks;

/**
 * Created by Malte on 26.04.2016.
 */
public abstract class TrainTrack {

    private TrainTrack successor;
    private TrainTrack predecessor;
    private int id;
    private int from;
    private int to;
    private int switchTo;
    private int value = 0;

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

    public void setSuccessor(TrainTrack s){
        successor = s;
    }
    public void setPredecessor(TrainTrack p){
        predecessor = p;
    }
    public void setValue(int v){
        value = v;
    }
    public int getValue(){
        return value;
    }

}