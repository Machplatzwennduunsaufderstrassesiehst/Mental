package de.soeiner.mental.exerciseCreators;

import org.json.JSONArray;
import org.json.JSONObject;
import de.soeiner.mental.trainGameRelated.trainTracks.TrainTrack;

/**
 * Created by Sven on 25.04.16.
 */
public abstract class TrainMapCreator extends ExerciseCreator {

    TrainTrack[][] map;

    @Override
    public double getExpectedSolveTime() {
        return 0;
    }

    @Override
    public String getType() {
        return "trainMap";
    }

    @Override
    JSONObject createNext() {
        createTrainMap();
        JSONObject j = new JSONObject();
        try {
            j.put("type", this.getType());
            j.put("trainMap", translateTrainMap());
            j.put("firstTrackId", getFirstTrackId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    // hier kann man die TrainMap jetzt abrufen
    public TrainTrack[][] getTrainMap() {
        return map;
    }

    private JSONArray translateTrainMap() {
        JSONArray trainJSONArray = new JSONArray(); //erstellt eigenes 2d JSON array
        try {
            for (int i = 0; i < map.length; i++) {
                JSONArray temp = new JSONArray(map[i]);
                trainJSONArray.put(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainJSONArray;
    }

    void ausgabe() {
        for (int i = 0; i < map.length; i++) {
            System.out.println("");
            for (int j = 0; j < map.length; j++) {
                if (map[j][i].getValue() == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print(map[j][i].getValue() + " ");
                }
            }
        }
    }

    abstract TrainTrack[][] createTrainMap();

    public abstract int getFirstTrackId();
}
