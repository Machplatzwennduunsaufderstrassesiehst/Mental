package de.soeiner.mental.trainGame.mapCreators;

import com.android.internal.util.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.trainGame.tracks.Goal;
import de.soeiner.mental.trainGame.tracks.Switch;
import de.soeiner.mental.trainGame.tracks.Track;
import de.soeiner.mental.trainGame.tracks.TrainTrack;

/**
 * Created by Sven on 25.04.16.
 */
public abstract class TrainMapCreator extends ExerciseCreator {

    TrainTrack[][] map;

    protected int xMapSize;
    protected int yMapSize;

    @Override
    public double getExpectedSolveTime() {
        return 0;
    }

    @Override
    public String getType() {
        return "trainMap";
    }

    @Override
    protected JSONObject createNext() {
        createTrainMap();
        return updateExerciseObject();
    }

    public JSONObject updateExerciseObject() {
        exerciseObject = new JSONObject();
        try {
            exerciseObject.put("type", this.getType());
            exerciseObject.put("trainMap", toJSONArray());
            exerciseObject.put("firstTrackId", getFirstTrackId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exerciseObject;
    }

    // hier kann man die TrainMap jetzt abrufen
    public TrainTrack[][] getTrainMap() {
        assertMapCreated();
        return map;
    }

    private JSONArray toJSONArray() {
        assertMapCreated();
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

    protected void debugMapOutput() {
        assertMapCreated();
        System.out.println("__________________________");
        for (int i = 0; i < map.length; i++) {
            System.out.println("");
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    System.out.print("  ");
                } else if (map[i][j].getValue() == 0) {
                    System.out.print("0 ");
                } else {
                    System.out.print(map[i][j].getValue() + " ");
                }
            }
        }
        System.out.println();
        System.out.println("__________________________");
    }

    protected void assertMapCreated() {
        if (map == null) {
            throw new RuntimeException("Map not created yet, cannot continue.");
        }
    }

    protected boolean isValid(int x, int y) {
        assertMapCreated();
        if (!(x >= 0 && x < map.length)) {
            return false;
        }
        if (!(y >= 0 && y < map[x].length)) {
            return false;
        }
        return true;
    }

    String coords(int x, int y) {
        return "(" + x + "|" + y + ")";
    }

    /**
     * finds trainTrack in the radius of depth if the predicate applies for it
     *
     * @param x         x
     * @param y         y
     * @param depth     the radius around x, y to be investigated
     * @param predicate predicate
     * @return list of found trainTrack, if predicate does apply for it in the radius specified by depth
     */
    public ArrayList<TrainTrack> scanSurroundings(int x, int y, int depth, Predicate<TrainTrack> predicate) {
        assertMapCreated();
        if (depth < 0) throw new AssertionError();
        if (x < 0) throw new AssertionError();
        if (y < 0) throw new AssertionError();
        ArrayList<TrainTrack> surroundings = new ArrayList<>();
        //System.out.print("center: " + coords(x, y) + ";    ");
        for (int i = x - depth; i <= x + depth; i++) {
            for (int j = y - depth; j <= y + depth; j++) {
                //System.out.print(coords(i, j) + ", ");
                if (isValid(i, j) && predicate.apply(map[i][j])) {
                    surroundings.add(map[i][j]);
                }
            }
        }
        //System.out.println();
        return surroundings;
    }

    public boolean testSurroundings(int x, int y, int depth, Predicate<TrainTrack> predicate) {
        return scanSurroundings(x, y, depth, predicate).size() != 0;
    }

    public static class TrainTrackPredicates {

        public static final Predicate<TrainTrack> containsNull = new Predicate<TrainTrack>() {
            @Override
            public boolean apply(TrainTrack trainTrack) {
                return trainTrack == null;
            }
        };

        public static final Predicate<TrainTrack> containsTrainTrack = new Predicate<TrainTrack>() {
            @Override
            public boolean apply(TrainTrack trainTrack) {
                return trainTrack != null;
            }
        };

        public static final Predicate<TrainTrack> containsTrack = new Predicate<TrainTrack>() {
            @Override
            public boolean apply(TrainTrack trainTrack) {
                return trainTrack instanceof Track;
            }
        };

        public static final Predicate<TrainTrack> containsSwitch = new Predicate<TrainTrack>() {
            @Override
            public boolean apply(TrainTrack trainTrack) {
                return trainTrack instanceof Switch;
            }
        };

        public static final Predicate<TrainTrack> containsGoal = new Predicate<TrainTrack>() {
            @Override
            public boolean apply(TrainTrack trainTrack) {
                return trainTrack instanceof Goal;
            }
        };

    }

    protected int[] randomDirection() {
        int[] v = new int[2];
        v[0] = (int) (Math.random() * 3) - 1;
        v[1] = (int) (Math.random() * 3) - 1;
        if (Math.abs(v[0] + v[1]) != 1) {
            if (Math.random() > 0.5) {
                v[0] = 0;
            } else {
                v[1] = 0;
            }
        }
        return v;
    }

    protected int[] vectorAdd(int[] u, int[] v) {
        return new int[]{u[0] + v[0], u[1] + v[1]};
    }

    protected TrainTrack getTrainTrack(int[] p) {
        return map[p[0]][p[1]];
    }

    abstract TrainTrack[][] createTrainMap();

    public abstract int getFirstTrackId();

    public abstract void setGoalAmount(int goalAmount);

    public int getXMapSize() {
        return xMapSize;
    }

    public int getYMapSize() {
        return yMapSize;
    }
}
