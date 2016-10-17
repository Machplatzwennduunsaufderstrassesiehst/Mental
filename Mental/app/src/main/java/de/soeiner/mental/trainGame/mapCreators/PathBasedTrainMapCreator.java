package de.soeiner.mental.trainGame.mapCreators;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.main.Game;
import de.soeiner.mental.trainGame.tracks.BlockedTrack;
import de.soeiner.mental.trainGame.tracks.Goal;
import de.soeiner.mental.trainGame.tracks.Switch;
import de.soeiner.mental.trainGame.tracks.Track;
import de.soeiner.mental.trainGame.tracks.TrainTrack;

/**
 * Created by Malte on 27.08.16.
 */
public class PathBasedTrainMapCreator extends TrainMapCreator {

    public double getExpectedSolveTime() {
        return 0;
    }

    @Override
    public ExerciseCreator copy() {
        return new PathBasedTrainMapCreator(game);
    }

    public String getName() {
        return "Square Map";
    }

    private final int[] xT = {1, 0, -1, 0};
    private final int[] yT = {0, 1, 0, -1};
    private final int[] xTP = {1, 1, 0, -1, -1, -1, 0, 1}; //precisley
    private final int[] yTP = {0, 1, 1, 1, 0, -1, -1, -1}; //precisley
    private int pathNumber = 0;
    private Game game;
    private int size = 0;
    private int numgoals = 7;
    private final int BLOCK_VALUE = 51;
    private int x = 0;
    private int y = 0;
    private TrainTrack[] succesors;
    private TrainTrack[] predeccesors;
    private int id;

    @Override
    public int getFirstTrackId() {
        if (map == null) {
            throw new RuntimeException("getFirstTrackId called before createTrainMap");
        }
        return map[0][0].getId();
    }

    public void setSizeManually(int players) {
        int s = 4;
        size = 8;
        for (int i = s - players + 1; i < s; i++) {
            if (i > 1) {
                size += i;
            } else {
                size++;
            }
        }
        //size = s*(s+1)/2 - ((s-players)*(s+1-players)/2);
        if (size < 0) size = 100;
        numgoals = players * 3;
    }

    @Override
    public void setGoalAmount(int goalAmount) {
        numgoals = goalAmount;
    }

    private void setSizeVersus(int players) {
        size = players * 5 * 2;
        numgoals = players * 3 * 2;
    }

    public PathBasedTrainMapCreator(Game game) {
        super();
        this.game = game;
    }

    TrainTrack[][] createTrainMap() {

        if (size == 0) setSizeManually(game.activePlayers.size());
        x = 0;
        y = 0;
        pathNumber = 0;
        id = 1;

        map = new TrainTrack[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = new Track(i, j, 0, id++);
            }
        }
        map[1][1].setPredecessor(null);
        boolean continuePossible = true;
        boolean[] possibilities = new boolean[4];

        for (int i = 0; i < 4; i++) { //ränder setzen
            for (int j = 0; j < size - 1; j++) {
                map[x][y] = new BlockedTrack(x, y, BLOCK_VALUE, -1);
                x += xT[i];
                y += yT[i];
            }
        }
        x = 1;
        y = 1;
        //id = 1;
        map[x][y] = new Track(x, y, 1, id++);
        int[] coordinates = new int[2];
        int z = 0;
        int counter = 0;
        while (pathNumber < numgoals) {
            pathNumber++;
            if (pathNumber > 1) {
                coordinates = getStartingPoint();
                if (coordinates[0] == 1 && coordinates[1] == 1) {
                    pathNumber = BLOCK_VALUE + 1;
                    continuePossible = false;
                    System.out.println("==== Map wird aufgrund starting point fail, frühzeitig fertiggestellt ====");
                } else {
                    x = coordinates[0];
                    y = coordinates[1];

                    coordinates = getStartingPointConnection(); //anknüpfung an startpunkt finden
                    if (map[coordinates[0]][coordinates[1]].getType().equals("switch")) { //wenn der anknüpfpunkt bereits ein switch ist
                        map[coordinates[0]][coordinates[1]].setSuccessor(map[x][y]);
                        map[x][y].setPredecessor(map[coordinates[0]][coordinates[1]]);
                    } else {
                        TrainTrack successorTemp = map[coordinates[0]][coordinates[1]].getSuccessor(); //aktueller
                        TrainTrack predecessorTemp = map[coordinates[0]][coordinates[1]].getPredecessor(); //aktueller
                        map[coordinates[0]][coordinates[1]] = new Switch(coordinates[0], coordinates[1], -1, id++); //switch setzen
                        predecessorTemp.setSuccessor(map[coordinates[0]][coordinates[1]]); //TODO
                        map[coordinates[0]][coordinates[1]].setPredecessor(predecessorTemp);
                        map[coordinates[0]][coordinates[1]].setSuccessor(successorTemp); //1. vorheriger weg //Swich, daher mehrere Succesor
                        successorTemp.setPredecessor(map[coordinates[0]][coordinates[1]]);
                        map[coordinates[0]][coordinates[1]].setSuccessor(map[x][y]); //2. neuer abzweig
                        map[x][y].setPredecessor(map[coordinates[0]][coordinates[1]]);

                        // anknüpfung mit startpunkt (switch) verbinden
                    }
                    map[x][y].setValue(pathNumber);
                }
                continuePossible = true;
                z = 0;
            }
            while (continuePossible && z < size) {
                //System.out.println(counter+++" "+pathNumber);
                for (int i = 0; i < 4; i++) {
                    if (map[x + xT[i]][y + yT[i]].getValue() == 0) {
                        if (checkSurrounding(x + xT[i], y + yT[i])) {
                            possibilities[i] = true;
                        }
                    }
                }
                continuePossible = false;
                for (int i = 0; i < 4; i++) {
                    if (possibilities[i] == true) {
                        continuePossible = true;
                    }
                }
                redo:
                while (continuePossible) {
                    for (int i = 0; i < 4; i++) {
                        if (possibilities[i] == true) {
                            if (Math.random() * 10 >= 9) {
                                if (Math.random() * 10 >= 9.8) {
                                    for (int k = 0; k < 2; k++) { //gerade strecken
                                        if (map[x + xT[i]][y + yT[i]].getValue() == 0) {
                                            map[x][y].setSuccessor(map[x + xT[i]][y + yT[i]]);
                                            map[x + xT[i]][y + yT[i]].setPredecessor(map[x][y]);
                                            x += xT[i];
                                            y += yT[i];
                                            map[x][y].setValue(pathNumber);
                                            z++;
                                        }
                                    }
                                }
                                if (map[x + xT[i]][y + yT[i]].getValue() == 0) {
                                    map[x][y].setSuccessor(map[x + xT[i]][y + yT[i]]);
                                    map[x + xT[i]][y + yT[i]].setPredecessor(map[x][y]);
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
                for (int i = 0; i < 4; i++) {
                    if (possibilities[i] == true) {
                        possibilities[i] = false;
                    }
                }
                System.out.println("========================== Iteration nr. " + pathNumber);
                debugMapOutput();
            }
        }
        int zId = 0;
        Switch s;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j].getType().equals("switch")) {
                    s = (Switch) map[i][j];
                    map[i][j].getPredecessor().setSuccessor(map[i][j]); //experimentell
                    s.setSwitchId(zId);
                    zId++;
                }
            }
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j].getValue() != 0 && map[i][j].getType().equals("track") && map[i][j].getSuccessor() == null) { //goals werden identifiziert
                    TrainTrack predeccessorTemp = map[i][j].getPredecessor();
                    Goal goal = new Goal(i, j, map[i][j].getValue(), id++); //und gesetzt
                    goal.setGoalId(map[i][j].getValue() - 1);
                    map[i][j] = goal;
                    System.out.println("i: " + i + ", j: " + j + " mit predeccessor == null : " + (predeccessorTemp == null));
                    if (predeccessorTemp != null) {
                        predeccessorTemp.setSuccessor(map[i][j]);
                        map[i][j].setPredecessor(predeccessorTemp);
                        if (predeccessorTemp.getType().equals("switch")) {
                            System.out.println("switch vor goal");
                        } else {
                            System.out.println("track vor switch");
                        }
                    } else {
                        System.out.println("======================================== null vor switch");
                        debugMapOutput();
                        //
                        //keine längerfristige Lösung
                        System.out.println("beginne map creation prozess von vorne"); //quick fix
                        int ldot = 4;
                        /* nur bildschirm output */
                        try {
                            for (int g = 1; g < ldot; g++) {
                                System.out.println("");
                                for (int l = ldot - g; l < ldot; l++) {
                                    System.out.print(".");
                                }
                            }
                        } catch (Exception e) {
                        }//der ist mit absicht leer !!}
                        createTrainMap();
                        return null;
                        //keine längerfristige Lösung
                        //
                    }
                }
            }
        }
        System.out.println("map: ");
        debugMapOutput();
        // Blocked Tracks rund um die Map entfernen
        TrainTrack[][] finalMap = new TrainTrack[size - 2][size - 2];
        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {
                TrainTrack trainTrack = map[i][j];
                trainTrack.setCoordinates(trainTrack.getX() - 1, trainTrack.getY() - 1);
                finalMap[i - 1][j - 1] = trainTrack;
            }
        }
        this.map = finalMap;
        debugMapOutput();
        return this.map;
    }

    private int[] getStartingPoint() {
        int AVOID_START_REGION_VALUE = 4;
        int[] coordinates = {1, 1};
        ArrayList<int[]> pussybilities = new ArrayList<int[]>();
        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {
                if (map[i][j].getValue() == 0 && checkSurroundingTarget(i, j, 1) && (checkSurroundingPreciselyTarget(i, j, 2) || checkSurroundingPreciselyTarget(i, j, 3)) && i + j > AVOID_START_REGION_VALUE && getStartingPointConnectionElement(i, j).hasSuccessor()) {
                    coordinates[0] = i;
                    coordinates[1] = j;
                    pussybilities.add(coordinates.clone());
                }
            }
        }
        if (!pussybilities.isEmpty()) {
            coordinates = pussybilities.get((int) (Math.random() * pussybilities.size()));
        } else if (coordinates[0] == 1 && coordinates[1] == 1) {
            System.out.println("getStartingPoint() nicht möglich");
            //debugMapOutput();
        }
        return coordinates;
    }

    private int[] getStartingPointConnection() {
        int[] coordinates = new int[2];
        for (int i = 0; i < 4; i++) {
            if (map[x + xT[i]][y + yT[i]].getValue() != 0 && map[x + xT[i]][y + yT[i]].getValue() != BLOCK_VALUE) {
                coordinates[0] = (x + xT[i]);
                coordinates[1] = (y + yT[i]);
            }
        }
        return coordinates;
    }

    private TrainTrack getStartingPointConnectionElement(int x, int y) {
        int[] coordinates = new int[2];
        for (int i = 0; i < 4; i++) {
            if (map[x + xT[i]][y + yT[i]].getValue() != 0 && map[x + xT[i]][y + yT[i]].getValue() != BLOCK_VALUE) {
                coordinates[0] = (x + xT[i]);
                coordinates[1] = (y + yT[i]);
            }
        }
        return map[coordinates[0]][coordinates[1]];
    }

    private boolean checkSurrounding(int x, int y) {
        int z = 0;
        for (int i = 0; i < 4; i++) {
            if (!(map[x + xT[i]][y + yT[i]].getValue() == 0 || map[x + xT[i]][y + yT[i]].getValue() == BLOCK_VALUE)) {
                z++;
            }
        }
        return !(z >= 2);
    }

    private boolean checkSurroundingTarget(int x, int y, int target) {
        int z = 0;
        for (int i = 0; i < 4; i++) {
            if (!(map[x + xT[i]][y + yT[i]].getValue() == 0 || map[x + xT[i]][y + yT[i]].getValue() == BLOCK_VALUE)) {
                z++;
            }
        }
        return z == target;
    }

    private boolean checkSurroundingPreciselyTarget(int x, int y, int target) {
        int z = 0;
        for (int i = 0; i < 8; i++) {
            if (!(map[x + xTP[i]][y + yTP[i]].getValue() == 0 || map[x + xTP[i]][y + yTP[i]].getValue() == BLOCK_VALUE)) {
                z++;
            }
        }
        return z == target;
    }

}
