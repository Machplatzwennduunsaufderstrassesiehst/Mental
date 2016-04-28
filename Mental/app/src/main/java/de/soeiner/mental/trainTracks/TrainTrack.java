package de.soeiner.mental.trainTracks;

/**
 * Created by Malte on 26.04.2016.
 */
public abstract class TrainTrack {

    protected TrainTrack successor;
    protected TrainTrack predecessor;
    protected int id;
    protected int from;
    protected int to;
    protected int switchTo;
    protected int value = 0;

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

}