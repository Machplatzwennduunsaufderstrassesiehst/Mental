package de.soeiner.mental.exerciseCreators;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import de.soeiner.mental.trainTracks.*;

/**
 * Created by sven on 25.04.16.
 */
public class TrainMapCreator extends ExerciseCreator {

    public double getExpectedSolveTime() { return 0; }

    private TrainTrack[][] trainMap;

    @Override
    public ExerciseCreator copy() {
        return new TrainMapCreator();
    }

    @Override
    JSONObject createNext() {
        createTrainMap();
        JSONObject j = new JSONObject();
        try {
            j.put("type", this.getType());
            j.put("trainMap", translateTrainMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public String getName() {
        return "Casual Train Map";
    }
    public String getType() {
        return "trainMap";
    }

    private final int[] xT = {1, 0, -1, 0};
    private final int[] yT = {0, 1, 0, -1};
    private final int[] xTP = {1, 1, 0, -1, -1, -1, 0, 1}; //precisley
    private final int[] yTP = {0, 1, 1, 1, 0, -1, -1, -1}; //precisley
    private int pathNumber = 0;
    private final int size = 11;
    private final int BLOCK_VALUE = 8;
    TrainTrack[][] map;
    private int x = 0;
    private int y = 0;
    private TrainTrack[] succesors;
    private TrainTrack[] predeccesors;


    public TrainTrack[][] createTrainMap(){
        x = 0;
        y = 0;
        pathNumber = 0;

        map = new TrainTrack[size][size];
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                map[i][j] = new Track(i, j, 0);
            }
        }
        map[1][1].setPredecessor(null);
        boolean continuePossible = true;
        boolean[] possibilities = new boolean[4];

        for(int i = 0; i<4; i++){ //ränder setzen
            for(int j = 0; j<size-1;j++){
                map[x][y] = new BlockedTrack(x, y, BLOCK_VALUE);
                x += xT[i];
                y += yT[i];
            }
        }
        x = 1;
        y = 1;
        map[x][y] = new Track(x, y, 1);
        int[] coordinates = new int[2];
        int z = 0;
        int counter = 0;
        while(pathNumber < 7){
            pathNumber++;
            if(pathNumber > 1) {
                coordinates = getStartingPoint();
                if (coordinates[0] == 1 && coordinates[1] == 1) {
                    pathNumber = 10;
                    continuePossible = false;
                    System.out.println("==== Map wird aufgrund starting point fail, frühzeitig fertiggestellt ====");
                } else {
                x = coordinates[0];
                y = coordinates[1];

                coordinates = getStartingPointConnection(); //anknüpfung an startpunkt finden
                    if (map[coordinates[0]][coordinates[1]].getType().equals("switch")) { //wenn der anknüpfpunkt bereits ein switch ist
                        map[coordinates[0]][coordinates[1]].setSuccessor(map[x][y]);
                    } else {
                        TrainTrack successorTemp = map[coordinates[0]][coordinates[1]].getSuccessor(); //aktueller
                        TrainTrack predecessorTemp = map[coordinates[0]][coordinates[1]].getPredecessor(); //aktueller
                        map[coordinates[0]][coordinates[1]] = new Switch(coordinates[0], coordinates[1], 9); //switch setzen
                        map[coordinates[0]][coordinates[1]].setPredecessor(predecessorTemp);
                        map[coordinates[0]][coordinates[1]].setSuccessor(successorTemp); //1. vorheriger weg //Swich, daher mehrere Succesor
                        map[coordinates[0]][coordinates[1]].setSuccessor(map[x][y]); //2. neuer abzweig
                        map[x][y].setPredecessor(map[coordinates[0]][coordinates[1]]);
                        // anknüpfung mit startpunkt (switch) verbinden
                    }
                    map[x][y].setValue(pathNumber);
                }
                continuePossible = true;
                z = 0;
            }
            while(continuePossible && z < size){
                //System.out.println(counter+++" "+pathNumber);
                for(int i = 0; i<4; i++){
                    if(map[x+xT[i]][y+yT[i]].getValue() == 0){
                        if(checkSurrounding(x+xT[i], y+yT[i])){
                            possibilities[i] = true;
                        }
                    }
                }
                continuePossible = false;
                for(int i = 0; i<4; i++){
                    if(possibilities[i] == true){
                        continuePossible = true;
                    }
                }
                redo:
                while(continuePossible){
                    for(int i = 0; i<4; i++){
                        if(possibilities[i] == true){
                            if(Math.random()*10 >= 9){
                                if(Math.random()*10 >= 9.8){
                                    for(int k = 0; k<2; k++){ //gerade strecken
                                        if(map[x+xT[i]][y+yT[i]].getValue() == 0){
                                            map[x][y].setSuccessor(map[x + xT[i]][y + yT[i]]);
                                            map[x+xT[i]][y+yT[i]].setPredecessor(map[x][y]);
                                            x += xT[i];
                                            y += yT[i];
                                            map[x][y].setValue(pathNumber);
                                            z++;
                                        }
                                    }
                                }
                                if(map[x+xT[i]][y+yT[i]].getValue() == 0){
                                    map[x][y].setSuccessor(map[x+xT[i]][y+yT[i]]);
                                    map[x+xT[i]][y+yT[i]].setPredecessor(map[x][y]);
                                    x += xT[i];
                                    y += yT[i];
                                    map[x][y].setValue(pathNumber);
                                    z++;
                                }
                                break redo;
                            }
                        }
                    }
                }
                for(int i = 0; i<4; i++){
                    if(possibilities[i] == true){
                        possibilities[i] = false;
                    }
                }
                //System.out.println("========================== Iteration nr. "+pathNumber);
                //ausgabe();
            }
        }
        int zId = 0;
        Switch s;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j].getType().equals("switch")) {
                    s = (Switch) map[i][j];
                    s.setSwitchId(zId);
                    zId++;
                }
            }
        }
        for(int i = 0; i<map.length; i++){
            for (int j = 0; j < map.length; j++) {
                if(map[i][j].getValue() != 0 && map[i][j].getType().equals("track") && map[i][j].getSuccessor() == null){ //goals werden identifiziert
                     map[i][j] = new Goal(i, j, map[i][j].getValue()); //und gesetzt
                }
            }
        }
        System.out.println("map: ");
        ausgabe();
        this.trainMap = map;
        return map;
    }

    private int[] getStartingPoint() {
        int AVOID_START_REGION_VALUE = 4;
        int[] coordinates = {1, 1};
        ArrayList<int[]> pussybilities = new ArrayList<int[]>();
        for(int i = 1; i<size-1; i++){
            for(int j = 1; j<size-1; j++){
                if(map[i][j].getValue() == 0 && checkSurroundingTarget(i, j, 1) && (checkSurroundingPreciselyTarget(i, j, 2) || checkSurroundingPreciselyTarget(i, j, 3)) && i+j > AVOID_START_REGION_VALUE && getStartingPointConnectionElement(i, j).hasSuccessor()) {
                    coordinates[0] = i;
                    coordinates[1] = j;
                    pussybilities.add(coordinates.clone());
                }
            }
        }
        if(!pussybilities.isEmpty()) {
            coordinates = pussybilities.get((int) (Math.random() * pussybilities.size()));
        }else if (coordinates[0] == 1 && coordinates[1] == 1) {
                System.out.println("getStartingPoint() nicht möglich");
                //ausgabe();
        }
        return coordinates;
    }

    private int[] getStartingPointConnection() {
        int[] coordinates = new int[2];
        for(int i = 0; i<4; i++){
            if(map[x+xT[i]][y+yT[i]].getValue() != 0 && map[x+xT[i]][y+yT[i]].getValue() != BLOCK_VALUE){
                coordinates[0] = (x+xT[i]);
                coordinates[1] = (y+yT[i]);
            }
        }
        return coordinates;
    }

    private TrainTrack getStartingPointConnectionElement(int x, int y) {
        int[] coordinates = new int[2];
        for(int i = 0; i<4; i++){
            if(map[x+xT[i]][y+yT[i]].getValue() != 0 && map[x+xT[i]][y+yT[i]].getValue() != BLOCK_VALUE){
                coordinates[0] = (x+xT[i]);
                coordinates[1] = (y+yT[i]);
            }
        }
        return map[coordinates[0]][coordinates[1]];
    }

    private boolean checkSurrounding(int x, int y){
        int z = 0;
        for(int i = 0; i<4;i++){
            if(!(map[x+xT[i]][y+yT[i]].getValue() == 0 || map[x+xT[i]][y+yT[i]].getValue() == BLOCK_VALUE)){
                z++;
            }
        }
        return !(z>=2);
    }

    private boolean checkSurroundingTarget(int x, int y, int target){
        int z = 0;
        for(int i = 0; i<4;i++){
            if(!(map[x+xT[i]][y+yT[i]].getValue() == 0 || map[x+xT[i]][y+yT[i]].getValue() == BLOCK_VALUE)){
                z++;
            }
        }
        return z == target;
    }

    private boolean checkSurroundingPreciselyTarget(int x, int y, int target){
        int z = 0;
        for(int i = 0; i<8;i++){
            if(!(map[x+xTP[i]][y+yTP[i]].getValue() == 0 || map[x+xTP[i]][y+yTP[i]].getValue() == BLOCK_VALUE)){
                z++;
            }
        }
        return z == target;
    }

    private void ausgabe(){
        for(int i = 0; i<map.length;i++){
            System.out.println("");
            for(int j = 0; j<map.length;j++){
                if(map[i][j].getValue() == 0){
                    System.out.print("  ");
                }else{
                    System.out.print(map[i][j].getValue()+" ");
                }
            }
        }
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
