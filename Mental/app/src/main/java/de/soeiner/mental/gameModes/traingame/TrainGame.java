package de.soeiner.mental.gameModes.traingame;

import org.json.JSONObject;

import de.soeiner.mental.exerciseCreators.PathBasedTrainMapCreator;
import de.soeiner.mental.exerciseCreators.PathFinderTrainMapCreator;
import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameModes.GameMode;
import de.soeiner.mental.trainGameRelated.Train;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;
import de.soeiner.mental.trainGameRelated.trainTracks.TrainTrack;

/**
 * Created by Malte on 14.09.2016.
 */
public abstract class TrainGame extends GameMode {
    public TrainGame(Game game) {
        super(game);
        type = "Train";
        needsConfirmation = true;
    }

    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new PathBasedTrainMapCreator(game));
        compatibleExerciseCreators.add(new PathFinderTrainMapCreator(game));
    }

    public abstract void trainArrived(int trainId, Goal goal, boolean succsess);
    abstract Wave[] initiateWaves();

    public void prepareGame() {
        super.prepareGame();
        distributePlayers();
        trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        extraPreparationsPreMap();
        game.exerciseCreator.next(); // erstellt die neue map
        trainMap = trainMapCreator.getTrainMap();
        switches = getSwitches();
        goals = getGoals();
        waves = initiateWaves();
        extraPreparationsMidMap();
        game.broadcastExercise(); // macht nichts außer die map an alle zu senden
        extraPreparationsPostMap();
        /*for (int i = 0; i < switches.length; i++) {
            switches[i].setSwitchId(i);
        }*/
    }

    //diese mehtoden sind jetzt nicht mehr abstract sonder müssen überschrieben werden
    public void extraPreparationsPreMap(){} //zusätzliches vorbereitungen wie das manuelle setzen der Spieleranzahl
    public void extraPreparationsMidMap(){} //zusätzliches vorbereitungen wie die farbgebung der goals
    public void extraPreparationsPostMap(){ checkSwitches(); } //zusätzliches vorbereitungen nach dem Sender der map
    public void distributePlayers() { //verteilen der Spieler auf activeplayers oder teams usw
        addAllPlayersToActive();
    }

    //public void loop(){}

    public TrainTrack[][] trainMap;
    Switch[] switches;
    Goal[] goals;
    Wave[] waves;
    public boolean waveIsRunning;
    public boolean waveSuccess;
    int health;
    int healthNeededToWin;
    int reward;
    int trainArrivedReward;
    TrainMapCreator trainMapCreator;

    protected Switch[] getSwitches() {
        int z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap[i].length; j++) {
                if (trainMap[i][j] != null && trainMap[i][j].getType().equals("switch")) {
                    z++;
                }
            }
        }
        Switch[] s = new Switch[z];
        z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap[i].length; j++) {
                if (trainMap[i][j] != null && trainMap[i][j].getType().equals("switch")) {
                    s[z] = (Switch) trainMap[i][j];
                    z++;
                }
            }
        }
        return s;
    }

    protected Goal[] getGoals() {
        int z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap[i].length; j++) {
                if (trainMap[i][j] != null && trainMap[i][j].getType().equals("goal")) {
                    z++;
                }
            }
        }
        Goal[] s = new Goal[z];
        z = 0;
        for (int i = 0; i < trainMap.length; i++) {
            for (int j = 0; j < trainMap[i].length; j++) {
                if (trainMap[i][j] != null && trainMap[i][j].getType().equals("goal")) {
                    s[z] = (Goal) trainMap[i][j];
                    z++;
                }
            }
        }
        return s;
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

    protected void giveReward(int reward) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).getScore().updateScore(reward);
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

    public void broadcastWaveCompleted(boolean success, int waveNo, int reward) {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendWaveCompleted(success, (waveNo + 1), reward);
        }
    }

    protected void playersWon() {
        game.broadcastMessage("You Won! Reward: " + reward + "$");
        //game.broadcastMessage("und bekommen einen Bonus von " + reward + "$ !");
        giveReward(reward);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }

    public boolean playerAnswered(Player player, JSONObject answer) {
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
            return true;
        }
        return false;
    }

    public abstract void loop();

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

    protected void goThroughWaves(){
        int destinationId = 0;
        int idcounter = 0;
        double speed = 0;
        countdown(5);
        for (int i = 0; i < waves.length && gameIsRunning; i++) {
            health = waves[i].getHealth();
            healthNeededToWin = waves[i].getHEALTH_NEEDED_TO_WIN();
            trainArrivedReward = waves[i].getTRAIN_ARRIVED_REWARD();
            waveIsRunning = true;
            while (waveIsRunning && gameIsRunning) {
                destinationId = (int) (Math.random() * goals.length); // da die goalId jetzt gleich der values sind und bei 1 starten, muss hier +1 stehen
                speed = Math.random() * (waves[i].getMAX_SPEED() - waves[i].getMIN_SPEED()) + waves[i].getMIN_SPEED();
                new Train(idcounter, destinationId, speed, this, false); //zug spawnen
                idcounter++;
                try {
                    Thread.sleep(waves[i].getTRAIN_SPAWN_INTERVAL()); //warten
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (waveSuccess) {
                giveReward(waves[i].getREWARD());
                broadcastWaveCompleted(true, i, waves[i].getREWARD());
                System.out.println("welle " + i + " erfolgreich abgeschlossen!");
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                broadcastWaveCompleted(false, i, waves[i].getREWARD());
                gameIsRunning = false;
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            if (i == waves.length - 1) {
                playersWon();
                gameIsRunning = false;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Goal findGoalById(int id){
        for (int i = 0; i < goals.length; i++) {
            if(goals[i].getGoalId() == id) return goals[i];
        }
        throw new Error("Goal nicht gefunden");
    }

    public void checkSwitches(){
        for (int i = 0; i < switches.length; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcastSwitchChange(switches[i]);
        }
    }

    public void broadcastSwitchChange(Switch s){
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).sendSwitchChange(s);
        }
    }

    @Override
    public void doWaitTimeout(int timeout) {} //es soll kein timeout stattfinden
    //@Override
    public void newExercise() {}
}
