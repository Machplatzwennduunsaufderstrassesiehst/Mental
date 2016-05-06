package de.soeiner.mental.trainTracks;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Malte on 26.04.2016.
 */
public class Switch extends TrainTrack {

    ArrayList<TrainTrack> successors = new ArrayList<TrainTrack>();
    ArrayList<Boolean> activeSuccessors = new ArrayList<Boolean>();
    int switchId;

    public Switch(int x, int y, int v){
        super(x, y, v);
    }

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
        return "switch";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        if (!successors.contains(s)) {
            successors.add(s);
            activeSuccessors.add(false);
        }
        changeSwitch(); //switch gleich setzen
    }

    public void changeSwitch(){ //nimmt an, dass es zwei nachfolger gibt
        int active = 0;

        for(int i = 0; i<successors.size(); i++){ //aktiven nachfolger finden
            if(activeSuccessors.get(i).equals(true)){
                active = i;
            }
        }
        for(int i = 0; i<successors.size(); i++){ //alle nachfolger auf wahr setzen
            activeSuccessors.set(i, new Boolean(true));
        }
        activeSuccessors.set(active, new Boolean(false)); //vorher aktiven nachfolger auf false setzen
        for (int i = 0; i < successors.size(); i++) {
            if(activeSuccessors.get(i) == true){
                successor = successors.get(i);
            }
        }
    }

    public ArrayList getSuccessors(){
        return successors;
    }

    public void setSwitchId(int id){
        switchId = id;
    }

    public int getSwitchId(){
       return switchId;
    }
}
