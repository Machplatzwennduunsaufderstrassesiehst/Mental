package de.soeiner.mental.gameModes.traingame;

import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.trainGameRelated.Train;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;

/**
 * Created by Malte on 15.09.2016.
 */
public class VersusTrainGameMode extends TrainGameMode {

    ArrayList<Player> teamRed = new ArrayList<>();
    ArrayList<Player> teamBlue = new ArrayList<>();
    ArrayList<Goal> teamRedGoals = new ArrayList<>();
    ArrayList<Goal> teamBlueGoals = new ArrayList<>();
    Object lock = new Object();


    public VersusTrainGameMode(Game game) {
        super(game);
    }

    int goalDestructionBonus = 50;

    @Override
    public void trainArrived(int trainId, Goal goal, boolean succsess) {
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

    private void broadcastGoalDestroyed(int goalId){
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendGoalDestroyed(goalId);
        }
    }

    @Override
    Wave[] initiateWaves() {
        return new Wave[0];
    }

    @Override
    public void extraPreparationsPreMap() {
        reward = 0;
        trainMapCreator.setSizeManually(game.activePlayers.size()*2);
    }

    @Override
    public void extraPreparationsMidMap(){
        for(int i = 0; i < goals.length; i++){
            if(i%2 == 0){
                teamRedGoals.add(goals[i]);
                goals[i].setColorId(1);
            }else{
                teamBlueGoals.add(goals[i]);
                goals[i].setColorId(2);
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
    @Override
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
    public String getGameModeString() {
        return "Versus";
    }

    public synchronized boolean playerAnswered(Player player, JSONObject answer) {
        if((teamRed.contains(player) && whosTurn) || (teamBlue.contains(player) && !whosTurn)) { //wenn der Spieler aus dem Team ist, das an der Reihe ist
            if (answer.has("switch")) {
                try {
                    for (Switch s : switches) {
                        if (s.getSwitchId() == answer.getInt("switch")) {
                            s.changeSwitch(answer.getInt("switchedTo"));
                            for (int i = 0; i < game.activePlayers.size(); i++) {
                                game.activePlayers.get(i).sendSwitchChange(s);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notify(); // benachrichtige loop()
                return true;
            }
        }
        return false;
    }
}
