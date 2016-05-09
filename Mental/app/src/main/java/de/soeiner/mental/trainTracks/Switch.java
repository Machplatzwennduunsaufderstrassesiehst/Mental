package de.soeiner.mental.trainTracks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.gameModes.Train;

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
            JSONArray successorList = new JSONArray();
            try {
                for(int i = 0; i < successors.size(); i++){
                    TrainTrack temp =  successors.get(i);
                    JSONObject position = new JSONObject();
                    position.put("xpos", temp.getX());
                    position.put("ypos", temp.getY());
                    successorList.put(i, position);
                }
                this.put("successorList", successorList);
            }catch(JSONException e){e.printStackTrace();}
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
        try {
            this.put("switchedTo", active);
        }catch(Exception e){e.printStackTrace();}
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
