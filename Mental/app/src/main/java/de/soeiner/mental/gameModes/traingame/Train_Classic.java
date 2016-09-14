package de.soeiner.mental.gameModes.traingame;

import org.json.JSONObject;

import de.soeiner.mental.exerciseCreators.PathBasedTrainMapCreator;
import de.soeiner.mental.exerciseCreators.PathFinderTrainMapCreator;
import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.gameModes.GameMode;
import de.soeiner.mental.trainGameRelated.Train;
import de.soeiner.mental.trainGameRelated.Wave;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;
import de.soeiner.mental.trainGameRelated.trainTracks.TrainTrack;

/**
 * Created by Malte on 21.04.2016.
 */
public class Train_Classic extends TrainGame {

    public TrainTrack[][] trainMap;
    Switch[] switches;
    Goal[] goals;
    Wave[] waves;
    boolean waveIsRunning;
    boolean waveSuccess;
    int health;
    int healthNeededToWin;
    int reward;
    int trainArrivedReward;

    public void initializeCompatibleExerciseCreators() {
        compatibleExerciseCreators.add(new PathBasedTrainMapCreator());
        compatibleExerciseCreators.add(new PathFinderTrainMapCreator(game));
    }

    public Train_Classic(Game game) {
        super(game);
        needsConfirmation = true;
    }

    @Override
    public String getGameModeString() {
        return "Classic";
    }

    @Override
    public void prepareGame() {
        super.prepareGame();
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            game.activePlayers.add(game.joinedPlayers.get(i));
        }
        TrainMapCreator trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        game.exerciseCreator.next(); // erstellt die neue map
        game.broadcastExercise(); // macht nichts außer die map an alle zu senden
        trainMap = trainMapCreator.getTrainMap(); //TODO, von player abhängig machen
        switches = getSwitches();
        goals = getGoals();
        waves = initiateWaves();
        /*for (int i = 0; i < switches.length; i++) {
            switches[i].setSwitchId(i);
        }*/
        reward = 100; //reward für beenden des Spiels
    }

    Wave[] initiateWaves() {
        Wave[] wellen = new Wave[7];
        //double minspeed, double maxspeed, trainspawnintervall, trainarrivedreward, health, healthnw, reward
        //wellen[0] = new Wave(6, 6, 100, 1, 99999, 999999, 25);
        //wellen[0] = new Wave(1.0, 1.0, 3500, 1, 10, 15, 25);
        wellen[0] = new Wave(1.1, 1.3, 2700, 2, 10, 25, 50);
        wellen[1] = new Wave(1.4, 1.8, 2200, 3, 10, 30, 100);
        wellen[2] = new Wave(1.7, 2.2, 1800, 4, 10, 35, 200);
        wellen[3] = new Wave(1.7, 2.5, 1600, 10, 10, 40, 300);
        wellen[4] = new Wave(1.5, 2.6, 1400, 10, 10, 50, 500);
        wellen[5] = new Wave(1.3, 2.6, 1300, 10, 10, 50, 750);
        wellen[6] = new Wave(1.0, 2.6, 1300, 10, 10, 50, 1000);
/*        int testhealth = 8;
        int testhealthNeededToWin = 17; // um schnell zur nächsten wave zu gelangen
        wellen[0] = new Wave(0.5, 0.5, 4000, 1, 10, testhealthNeededToWin, 25);
        wellen[1] = new Wave(1.0, 1.0, 4000, 2, 10, testhealthNeededToWin, 50);
        wellen[2] = new Wave(1.5, 1.5, 3500, 3, 10, testhealthNeededToWin, 100);
        wellen[3] = new Wave(2.0, 2.0, 3000, 4, 10, testhealthNeededToWin, 200);
        wellen[4] = new Wave(4.0, 4.0, 2000, 10, 10, testhealthNeededToWin, 500);*/
        return wellen;
    }

    @Override
    public void loop() {
        int destinationId = 0;
        int idcounter = 0;
        double speed = 0;
        for (int i = 0; i < waves.length && gameIsRunning; i++) {
            health = waves[i].getHealth();
            healthNeededToWin = waves[i].getHEALTH_NEEDED_TO_WIN();
            trainArrivedReward = waves[i].getTRAIN_ARRIVED_REWARD();
            waveIsRunning = true;
            while (waveIsRunning && gameIsRunning) {
                destinationId = (int) (Math.random() * goals.length); // da die goalId jetzt gleich der values sind und bei 1 starten, muss hier +1 stehen
                speed = Math.random() * (waves[i].getMAX_SPEED() - waves[i].getMIN_SPEED()) + waves[i].getMIN_SPEED();
                new Train(idcounter, destinationId, speed, this); //zug spawnen
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

    public void trainArrived(int trainId, int goalId, boolean succsess) {
        if (succsess) {
            //game.broadcastMessage("Zug hat sein Ziel erreicht!");
            health++;
            giveReward(trainArrivedReward);
        } else {
            //game.broadcastMessage("Zug hat das falsche Ziel erreicht :/");
            health--; //TODO TODO TODO TODO TODO
        }
        for (int i = 0; i < game.activePlayers.size(); i++) {
            if (succsess) {
                game.activePlayers.get(i).getScore().updateScore(trainArrivedReward);
            }
            game.activePlayers.get(i).sendTrainArrived(trainId, goalId, succsess);
        }
        if (health <= 0) { //Check for Wellen status
            waveSuccess = false;
            waveIsRunning = false;
            game.broadcastMessage("Spieler haben verloren !");
        }
        if (health >= healthNeededToWin) {
            waveSuccess = true;
            waveIsRunning = false;
        }
    }

    private void playersWon() {
        game.broadcastMessage("Spieler haben gewonnen!");
        game.broadcastMessage("und bekomen einen Bonus von " + reward + "$ !");
        giveReward(reward);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }

    private void giveReward(int reward) {
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

    @Override
    public void doWaitTimeout(int timeout) {
    } //es soll kein timeout stattfinden

    private Switch[] getSwitches() {
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

    private Goal[] getGoals() {
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

    //@Override
    public void newExercise() {
    }
}
