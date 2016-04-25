package de.soeiner.mental.exerciseCreators;

import org.json.JSONArray;
import org.json.JSONObject;

import de.soeiner.mental.TrainTrack;
import de.soeiner.mental.communication.CmdRequest;

/**
 * Created by sven on 25.04.16.
 */
public class TrainMapCreator extends ExerciseCreator {

    public double getExpectedSolveTime() { return 0; }

    private TrainTrack[][] trainMap;

    @Override
    JSONObject createNext() {
        createTrainMap();
        JSONObject j = new JSONObject();
        try {
            j.put("type", "trainMap");
            j.put("trainMap", translateTrainMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public String getName() {
        return "Casual Train Map";
    }

    public TrainTrack[][] createTrainMap() { //TODO
        trainMap = new TrainTrack[10][10];
        return trainMap;
    }

    // hier kann man die TrainMap jetzt abrufen
    public TrainTrack[][] getTrainMap() {
        return trainMap;
    }

    private JSONArray translateTrainMap() {
        JSONArray trainJSONArray = new JSONArray(); //erstellt eigenes 2d JSON array
        try {
            for(int i = 0; i<trainMap.length;i++) {
                JSONArray temp = new JSONArray(trainMap[i]);
                trainJSONArray.put(temp);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return trainJSONArray;
    }
}
