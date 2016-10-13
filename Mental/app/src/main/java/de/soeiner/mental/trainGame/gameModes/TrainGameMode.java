package de.soeiner.mental.trainGame.gameModes;

import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.trainGame.mapCreators.PathBasedTrainMapCreator;
import de.soeiner.mental.trainGame.mapCreators.PathFinderTrainMapCreator;
import de.soeiner.mental.trainGame.mapCreators.TrainMapCreator;
import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.main.GameMode;
import de.soeiner.mental.trainGame.trainGenerators.TrainGenerator;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.events.TrainSpawnEvent;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.trainGame.trainTracks.Switch;
import de.soeiner.mental.trainGame.trainTracks.TrainTrack;
import de.soeiner.mental.util.event.EventDispatcher;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 14.09.2016.
 */
public abstract class TrainGameMode extends GameMode {

    public TrainTrack[][] trainMap;
    Switch[] switches;
    Goal[] goals;

    protected TrainMapCreator trainMapCreator;

    public EventDispatcher<TrainArrivedEvent> trainArrived = new EventDispatcher<>();
    public EventDispatcher<TrainSpawnEvent> trainSpawn = new EventDispatcher<>();

    public TrainGameMode(Game game) {
        super(game);
        needsConfirmation = false;

        trainArrived.addListener(new EventListener<TrainArrivedEvent>() {
            @Override
            public void onEvent(TrainArrivedEvent event) {
                broadcastTrainArrived(event.getTrain().getId(), event.getGoal(), event.isMatch());
            }
        });
    }

    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new PathBasedTrainMapCreator(game));
        compatibleExerciseCreators.add(new PathFinderTrainMapCreator(game));
    }

    public void prepareGame() {
        super.prepareGame();

        distributePlayers();
        trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        prepareMapCreation();
        game.broadcastMessage("Generating Map...");
        trainMapCreator.next(); // erstellt die neue map
        trainMap = trainMapCreator.getTrainMap();
        switches = getSwitches();
        goals = getGoals();
        prepareMap();
        trainMapCreator.updateExerciseObject();
        game.broadcastExercise(); // macht nichts außer die map an alle zu senden
        prepareGameStart();
    }

    //diese mehtoden sind jetzt nicht mehr abstract sonder müssen überschrieben werden

    /**
     * zusätzliches vorbereitungen wie das manuelle setzen der Spieleranzahl
     */
    public void prepareMapCreation() {
    }

    /**
     * zusätzliches vorbereitungen wie die farbgebung der goals
     */
    public void prepareMap() { //
        for (Goal goal : goals) {
            goal.setMatchingId(goal.getGoalId());
        }
    }

    /**
     * zusätzliches Vorbereitungen nach dem senden der map
     */
    public void prepareGameStart() {
        alignSwitchesInGUI();
    }

    /**
     * verteilen der Spieler auf activeplayers oder teams usw
     */
    public void distributePlayers() {
        addAllPlayersToActive();
    }

    protected Switch[] getSwitches() {
        ArrayList<TrainTrack> arrayList = trainMapCreator.scanSurroundings(0, 0, Math.max(trainMap.length, trainMap[0].length), TrainMapCreator.TrainTrackPredicates.containsSwitch);
        game.broadcastMessage("Num Switches: " + arrayList.size());
        return arrayList.toArray(new Switch[arrayList.size()]);
    }

    protected Goal[] getGoals() {
        ArrayList<TrainTrack> arrayList = trainMapCreator.scanSurroundings(0, 0, Math.max(trainMap.length, trainMap[0].length), TrainMapCreator.TrainTrackPredicates.containsGoal);
        game.broadcastMessage("Num Goals: " + arrayList.size());
        return arrayList.toArray(new Goal[arrayList.size()]);
    }

    protected Integer[] getAvailableMatchingIds() {
        ArrayList<Integer> availableMatchingIds = new ArrayList<>();
        for (Goal goal : goals) {
            int id = goal.getMatchingId();
            if (!availableMatchingIds.contains(id)) {
                availableMatchingIds.add(id);
            }
        }
        return availableMatchingIds.toArray(new Integer[availableMatchingIds.size()]);
    }

    // TODO TODO TODO get the list from trainMapCreator as soon as supported
    protected TrainTrack[] getAvailableStartTracks() {
        return new TrainTrack[]{getTrackById(getFirstTrackId())};
    }

    public TrainTrack getTrackById(int id) {
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap[i].length; j++) {
                if (trainMap[i][j] != null && trainMap[i][j].getId() == id) {
                    return trainMap[i][j];
                }
            }
        }
        throw new RuntimeException("getTrackById(), konnte keine Track mit id" + id + " finden");
    }

    public int getFirstTrackId() {
        return ((TrainMapCreator) game.exerciseCreator).getFirstTrackId();
    }

    // TODO
    /*protected void playersWon() {
        game.broadcastMessage("You Won! Reward: " + reward + "$");
        //game.broadcastMessage("und bekommen einen Bonus von " + reward + "$ !");
        giveReward(reward);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }*/

    public boolean playerAction(Player player, JSONObject actionData) {
        if (actionData.has("switch")) {
            try {
                for (Switch s : switches) {
                    if (s.getSwitchId() == actionData.getInt("switch")) {
                        s.changeSwitch(actionData.getInt("switchedTo"));
                        broadcastSwitchChange(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void countdown(int from) {
        for (int i = from; i > 0; i--) {
            game.broadcastMessage("" + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        game.broadcastMessage("GO!");
    }

    // TODO rewarding und ende des Spiels



    public Goal findGoalById(int id) {
        for (int i = 0; i < goals.length; i++) {
            if (goals[i].getGoalId() == id) return goals[i];
        }
        throw new Error("Goal nicht gefunden");
    }

    public void alignSwitchesInGUI() {
        for (int i = 0; i < switches.length; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcastSwitchChange(switches[i]);
        }
    }

    public void broadcastSwitchChange(Switch s) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendSwitchChange(s);
        }
    }

    public void broadcastNewTrain(JSONObject train) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendNewTrain(train);
        }
    }

    public void broadcastTrainDecision(int trainId, int switchId, int direction) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendTrainDecision(trainId, switchId, direction);
        }
    }

    public void broadcastTrainArrived(int trainId, Goal goal, boolean match) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendTrainArrived(trainId, goal.getGoalId(), match);
        }
    }

    protected void giveReward(int reward) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).getScore().updateScore(reward);
        }
    }

    // TODO das sollte natürlich keine Funktion von TrainGameMode sein
    public void newExercise() {
    }
}
