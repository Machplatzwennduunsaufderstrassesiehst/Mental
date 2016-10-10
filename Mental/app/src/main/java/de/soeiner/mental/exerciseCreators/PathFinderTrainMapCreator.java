package de.soeiner.mental.exerciseCreators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.trainGame.trainTracks.Switch;
import de.soeiner.mental.trainGame.trainTracks.Track;
import de.soeiner.mental.trainGame.trainTracks.TrainTrack;
import de.soeiner.mental.util.Pathfinder;

/**
 * Created by Sven on 28.08.16.
 */
public class PathFinderTrainMapCreator extends TrainMapCreator {

    private static int MAX_X_SIZE = 7;
    private static int NUM_START_TRACKS = 4;
    private static double MAP_RATIO = 3.0 / 2;

    private Game game;
    private int id;
    private int switchId;
    private TrainTrack firstTrack;
    private int numPlayers = 0;
    private int numGoals = 0;

    Pathfinder<TrainTrack> pathfinder;

    public PathFinderTrainMapCreator(Game game) {
        super();
        this.game = game;
        pathfinder = new Pathfinder<>();
    }

    @Override
    public void setGoalAmount(int goalAmount) {
        numGoals = goalAmount;
    }

    @Override
    TrainTrack[][] createTrainMap() {
        numPlayers = game.activePlayers.size();
        ArrayList<TrainTrack> protectedTrack = new ArrayList<>();
        id = 1;
        switchId = 0;
        int value = 1;
        int retry = 0;
        int x, y;
        if (numGoals <= 2) numGoals = (numPlayers * 5) / 3 + 3; // numGoals - 3 == numPlayers * 5/3 // (numGoals-3) * 3/5 ~= numPlayers
        System.out.println(numGoals);
        xMapSize = (numGoals - 3) / 2 + 4;
        if (xMapSize > MAX_X_SIZE) xMapSize = MAX_X_SIZE;
        yMapSize = (int) (xMapSize * MAP_RATIO);
        map = new TrainTrack[xMapSize][yMapSize];
        // put the start somewhere
        x = (int) (Math.random() * xMapSize);
        y = (int) (Math.random() * yMapSize);
        TrainTrack track = map[x][y] = firstTrack = new Track(x, y, value, nextId());
        // add some start track protected from further attachments
        int layedStartTrack = 0;
        while (layedStartTrack < NUM_START_TRACKS) {
            int[] v = randomDirection();
            x = track.getX() + v[0];
            y = track.getY() + v[1];
            if (isValid(x, y) && map[x][y] == null) {
                protectedTrack.add(track);
                track = map[x][y] = track.continueAsTrack(v, nextId());
                layedStartTrack++;
                retry = 0;
            } else {
                retry++;
                if (retry > 20) {
                    return createTrainMap();
                }
            }
        }
        // Place goals randomly on the map
        Goal[] goals = new Goal[numGoals];
        int numCompletelyRandomGoals = (int) (0.4 * numGoals);
        int depth = yMapSize;
        for (int i = 0 ; i < numGoals; i++) {
            retry = 0;
            while (true) {
                retry++;
                if (i < numCompletelyRandomGoals) {
                    depth = 0;
                } else {
                    if (retry > 15 && depth > 0) {
                        depth -= 1;
                    }
                }
                x = (int) (Math.random() * xMapSize);
                y = (int) (Math.random() * yMapSize);
                if (!testSurroundings(x, y, depth, TrainTrackPredicates.containsTrainTrack)) {
                    goals[i] = new Goal(x, y, i + 1, nextId());
                    goals[i].setGoalId(i);
                    map[x][y] = goals[i];
                    break;
                }
            }
        }
        // Now find always the next shortest junction to establish between a track and a goal
        ArrayList<Goal> connectedGoals = new ArrayList<>();
        while (connectedGoals.size() < goals.length) {
            Junction bestPossibleJunction = null;
            int i = 0;
            while (bestPossibleJunction == null && i <= yMapSize) {
                i += 1;
                for (Goal goal : goals) {
                    if (connectedGoals.contains(goal)) {
                        continue;
                    }
                    for (TrainTrack[] trainTracks : map) {
                        for (TrainTrack trainTrack : trainTracks) {
                            if (trainTrack != null && trainTrack instanceof Track && !protectedTrack.contains(trainTrack)) {
                                Junction junction = new Junction(goal, (Track) trainTrack);
                                if (bestPossibleJunction == null || junction.distanceSquare < bestPossibleJunction.distanceSquare) {
                                    bestPossibleJunction = junction;
                                }
                            }
                        }
                    }
                }
            }
            if (bestPossibleJunction == null) {
                //System.out.println("no next possible junction found :/");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return createTrainMap();
            } else {
                if (connect(bestPossibleJunction.track, bestPossibleJunction.goal)) {
                    connectedGoals.add(bestPossibleJunction.goal);
                } else {
                    //System.out.println("Retrying, no path could be found");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return createTrainMap();
                }
            }
        }
        //debugMapOutput();
        return map;
    }

    private class Junction {

        Goal goal;
        Track track;
        double distanceSquare;

        Junction(Goal goal, Track track) {
            this.goal = goal;
            this.track = track;
            distanceSquare = getDistanceSquare(goal.getX(), goal.getY(), track.getX(), track.getY());
        }

        @Override
        public String toString() {
            return "goal(" + goal.getX() + "," + goal.getY() + ") <--> track(" + track.getX() + "," + track.getY() + ")";
        }
    }

    private TrainTrack transformIntoAttachmentPoint(TrainTrack track) {
        TrainTrack newTrack;
        if (track.hasSuccessor()) {
            newTrack = new Switch(track.getX(), track.getY(), track.getValue(), track.getId());
            ((Switch) newTrack).setSwitchId(switchId++);
            track.getSuccessor().setPredecessor(newTrack);
            newTrack.setSuccessor(track.getSuccessor());
            track.getPredecessor().setSuccessor(newTrack);
            newTrack.setPredecessor(track.getPredecessor());
        } else {
            newTrack = track;
        }
        //System.out.println(newTrack);
        return newTrack;
    }

    private boolean connect(Track start, TrainTrack end) {
        //System.out.println("connect:  start(" + start.getX() + "," + start.getY() + ") <--> end(" + end.getX() + "," + end.getY() + ")");
        int x = start.getX();
        int y = start.getY();
        TrainTrack trainTrack = map[x][y] = transformIntoAttachmentPoint(start);
        pathfinder.setMap(map);
        Stack<int[]> path = pathfinder.searchPath(trainTrack, end);
        if (path == null) {
            return false;
        } else {
            Object[] array = path.toArray();
            int i = array.length - 1;
            while (i >= 0) {
                int[] position = (int[]) array[i];
                x = position[0];
                y = position[1];
                int[] vector = new int[]{x - trainTrack.getX(), y - trainTrack.getY()};
                map[x][y] = trainTrack = trainTrack.continueAsTrack(vector, nextId());
                trainTrack.setValue(end.getValue());
                i--;
                //debugMapOutput();
            }
            trainTrack.attachTrainTrack(end);
            return true;
        }
    }

    private double getDistanceSquare(int x, int y, int x2, int y2) {
        return (x - x2) * (x - x2) + (y - y2) * (y - y2);
    }

    private int nextId() {
        return id++;
    }

    @Override
    public int getFirstTrackId() {
        return firstTrack.getId();
    }

    @Override
    public String getName() {
        return "Path Finder";
    }

    @Override
    public ExerciseCreator copy() {
        return null;
    }


}
