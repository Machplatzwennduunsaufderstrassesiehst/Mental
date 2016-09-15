package de.soeiner.mental.gameModes.traingame;

import org.json.JSONObject;

import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameModes.GameMode;
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
    }

    public abstract void trainArrived(int trainId, int goalId, boolean succsess);
    abstract Wave[] initiateWaves();

    public void prepareGame() {
        super.prepareGame();
        distributePlayers();
        trainMapCreator = (TrainMapCreator) game.exerciseCreator;
        extraPreparations();
        game.exerciseCreator.next(); // erstellt die neue map
        game.broadcastExercise(); // macht nichts außer die map an alle zu senden
        trainMap = trainMapCreator.getTrainMap();
        switches = getSwitches();
        goals = getGoals();
        waves = initiateWaves();
        /*for (int i = 0; i < switches.length; i++) {
            switches[i].setSwitchId(i);
        }*/
    }

    public abstract void extraPreparations();
    public abstract void distributePlayers();
    //public void loop(){}

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
        game.broadcastMessage("Spieler haben gewonnen!");
        game.broadcastMessage("und bekomen einen Bonus von " + reward + "$ !");
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

    @Override
    public void doWaitTimeout(int timeout) {} //es soll kein timeout stattfinden
    //@Override
    public void newExercise() {}
}
