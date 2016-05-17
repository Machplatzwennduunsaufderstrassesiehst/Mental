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
    int switchedTo = 0;

    public Switch(int x, int y, int v){
        super(x, y, v);
    }

    public int getSwitchedTo() {
        //return (int) (Math.random()*(successors.size()+0.99));
        return switchedTo;
    }

    @Override
    public String getType() {
        return "switch";
    }

    @Override
    public void setSuccessor(TrainTrack s) {
        if(s == null){
            System.out.println("Objekt ist null!!!! ://///");
        }
        if (!successors.contains(s)) {
            successors.add(s);
            activeSuccessors.add(false);
            JSONArray successorList = new JSONArray();
            try {
                for(int i = 0; i < successors.size(); i++){
                    //System.out.println("Typ an der Stelle "+i);//+" ist "+successors.get(i).getType());
                    JSONObject position = new JSONObject();
                    position.put("xpos", successors.get(i).getX());
                    position.put("ypos", successors.get(i).getY());
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
        for(int i = 0; i<successors.size(); i++){ //alle nachfolger auf falsch setzen
            activeSuccessors.set(i, new Boolean(false));
        }
        active = (active+1)%successors.size(); //index des nächsten Nachfolgers ermitteln
        activeSuccessors.set(active, new Boolean(true)); //nächsten nachfolger auf true setzen
        switchedTo = active; //für trains globale variable setzen
        try {
            this.put("switchedTo", switchedTo); //für map jsonobject setzen
        }catch(Exception e){e.printStackTrace();}
        this.successor = successors.get(active);
    }

    public ArrayList getSuccessors(){
        return successors;
    }

    public void setSwitchId(int id){
        switchId = id;
        try{
            this.put("switchId", id);
        }catch(Exception e){e.printStackTrace();}
    }

    public int getSwitchId(){
       return switchId;
    }
}
