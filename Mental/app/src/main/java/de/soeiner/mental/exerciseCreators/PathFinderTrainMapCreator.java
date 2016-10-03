package de.soeiner.mental.exerciseCreators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;
import de.soeiner.mental.trainGameRelated.trainTracks.Track;
import de.soeiner.mental.trainGameRelated.trainTracks.TrainTrack;
import de.soeiner.mental.util.Pathfinder;

/**
 * Created by Sven on 28.08.16.
 */
public class PathFinderTrainMapCreator extends TrainMapCreator {

    private Game game;
    private int id;
    private int switchId;
    private TrainTrack firstTrack;
    private int numPlayers = 0;

    Pathfinder<TrainTrack> pathfinder;

    public PathFinderTrainMapCreator(Game game) {
        super();
        this.game = game;
        pathfinder = new Pathfinder<>();
    }

    @Override
    TrainTrack[][] createTrainMap() {
        if(numPlayers == 0) numPlayers = game.activePlayers.size();
        ArrayList<TrainTrack> protectedTrack = new ArrayList<>();
        id = 1;
        switchId = 0;
        int value = 1;
        int retry = 0;
        int x, y;
        int numGoals = (numPlayers * 4) / 3 + 3;
        int xMapSize = (numPlayers * 3) / 4 + 4;
        if (xMapSize > 7) xMapSize = 7;
        int yMapSize = (xMapSize * 11) / 7;
        map = new TrainTrack[xMapSize][yMapSize];
        // put the start somewhere
        x = (int) (Math.random() * xMapSize);
        y = (int) (Math.random() * yMapSize);
        TrainTrack track = map[x][y] = firstTrack = new Track(x, y, value, nextId());
        // add some start track protected from further attachments
        while (retry < 3) {
            int[] v = randomDirection();
            System.out.println(Arrays.toString(v));
            x = track.getX() + v[0];
            y = track.getY() + v[1];
            if (isValid(x, y) && map[x][y] == null) {
                protectedTrack.add(track);
                track = map[x][y] = track.continueAsTrack(v, nextId());
                retry++;
            }
        }
        int depth = 4;
        // Place goals randomly on the map
        Goal[] goals = new Goal[numGoals];
        for (int i = 0; i < goals.length; i++) {
            value = i + 1;
            retry = 0;
            while (true) {
                retry++;
                if (retry > 10 && depth > 0) {
                    depth -= 1;
                }
                x = (int) (Math.random() * xMapSize);
                y = (int) (Math.random() * yMapSize);
                if (!testSurroundings(x, y, depth, containsTrainTrack)) {
                    System.out.println("new Goal(" + x + "," + y + ")");
                    goals[i] = new Goal(x, y, value, nextId());
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
                System.out.println(i);
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
                System.out.println("no next possible junction found :/");
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
                    System.out.println("Retrying, no path could be found");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return createTrainMap();
                }
            }
        }
        ausgabe();
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
        System.out.println(newTrack);
        return newTrack;
    }

    private boolean connect(Track start, TrainTrack end) {
        System.out.println("connect:  start(" + start.getX() + "," + start.getY() + ") <--> end(" + end.getX() + "," + end.getY() + ")");
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
                ausgabe();
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
    public void setSizeManually(int players) {
        numPlayers = players;
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
