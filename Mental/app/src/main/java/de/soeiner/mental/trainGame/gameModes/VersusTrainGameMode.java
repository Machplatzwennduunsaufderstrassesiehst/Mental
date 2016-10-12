package de.soeiner.mental.trainGame.gameModes;

import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.trainGame.Train;
import de.soeiner.mental.trainGame.trainGenerators.TrainGenerator;
import de.soeiner.mental.trainGame.trainGenerators.Wave;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.trainGame.trainTracks.Switch;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 15.09.2016.
 */
public class VersusTrainGameMode extends TrainGameMode { // TODO

    ArrayList<Player> teamRed = new ArrayList<>();
    ArrayList<Player> teamBlue = new ArrayList<>();
    ArrayList<Goal> teamRedGoals = new ArrayList<>();
    ArrayList<Goal> teamBlueGoals = new ArrayList<>();
    Object lock = new Object();

    EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {
            Goal goal = event.getGoal();
            if(!goal.isDestroyed()) { //wenn das entsprechende Ziel noch nicht zerstört ist
                goal.destroy(); //zerstöre es
                broadcastGoalDestroyed(goal.getGoalId());
                if (teamRedGoals.contains(goal)) { // wenn das ziel zu team rot gehörte
                    for (Player p : teamBlue) {
                        p.getScore().updateScore(goalDestructionBonus); //award team blue
                    }
                } else { //sonst
                    for (Player p : teamRed) {
                        p.getScore().updateScore(goalDestructionBonus); //award team red
                    }
                }
            }

        }
    };


    public VersusTrainGameMode(Game game) {
        super(game);
        trainArrived.addListener(trainArrivedListener);
    }

    @Override
    public void gameLoop() {

    }

    @Override
    protected TrainGenerator createTrainGenerator() {
        return null;
    }

    int goalDestructionBonus = 50;

    private void broadcastGoalDestroyed(int goalId){
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendGoalDestroyed(goalId);
        }
    }

    @Override
    public void prepareMapCreation() {
        trainMapCreator.setGoalAmount(game.activePlayers.size() * 2);
    }

    @Override
    public void prepareMap(){
        for(int i = 0; i < goals.length; i++){
            if(i%2 == 0){
                teamRedGoals.add(goals[i]);
                goals[i].setMatchingId(1);
            }else{
                teamBlueGoals.add(goals[i]);
                goals[i].setMatchingId(2);
            }
        }
    }

    @Override
    public void distributePlayers() {     //einteilung der Spieler in zwei teams
        addAllPlayersToActive();
        for(int i = 0; i < game.joinedPlayers.size(); i++){
            if(i%2 == 0){
                teamRed.add(game.joinedPlayers.get(i));
            }else{
                teamBlue.add(game.joinedPlayers.get(i));
            }
        }
    }

    boolean whosTurn = true;  // true -> rot ist an der reihe, false -> blau ist an der Reihe
    public synchronized void loop() {
        int id = 0;
        while(!allGoalsDestroyed()){ //solange nicht alle Ziele eines der beiden Teams zerstört sind
            if(whosTurn){ whosTurn = false; }else{ whosTurn = true; } //alterniere wer an der Reihe ist
            try{ wait(10000); }catch(Exception e){ e.printStackTrace(); } //warte darauf das eines der beiden Teams seinen move macht
            new Train(id++, -1, 5.0, this, true); // schicke nächsten Zug
        }
    }

    private boolean allGoalsDestroyed(){
        boolean red = true;
        boolean blue = true;
        for(Goal g : teamRedGoals){
            if(!g.isDestroyed()) red = false;
        }
        for(Goal g : teamBlueGoals){
            if(!g.isDestroyed()) blue = false;
        }
        return red || blue;
    }

    @Override
    public String getName() {
        return "Versus";
    }

    public synchronized boolean playerAction(Player player, JSONObject actionData) {
        if((teamRed.contains(player) && whosTurn) || (teamBlue.contains(player) && !whosTurn)) { //wenn der Spieler aus dem Team ist, das an der Reihe ist
            if (actionData.has("switch")) {
                try {
                    for (Switch s : switches) {
                        if (s.getSwitchId() == actionData.getInt("switch")) {
                            s.changeSwitch(actionData.getInt("switchedTo"));
                            for (int i = 0; i < game.activePlayers.size(); i++) {
                                game.activePlayers.get(i).sendSwitchChange(s);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notify(); // benachrichtige spawnNextTrain()
                return true;
            }
        }
        return false;
    }
}
