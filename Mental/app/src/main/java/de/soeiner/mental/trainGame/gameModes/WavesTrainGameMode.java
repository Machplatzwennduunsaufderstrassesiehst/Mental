package de.soeiner.mental.trainGame.gameModes;

import org.json.JSONException;
import org.json.JSONObject;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.main.Game;
import de.soeiner.mental.trainGame.events.HealthLimitReachedEvent;
import de.soeiner.mental.trainGame.gameConditions.HealthWithRestoreGameCondition;
import de.soeiner.mental.trainGame.trains.TrainGenerator;
import de.soeiner.mental.trainGame.trains.TrainWaveGenerator;
import de.soeiner.mental.trainGame.trains.Wave;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.tracks.TrainTrack;
import de.soeiner.mental.util.event.EventListener;
import de.soeiner.mental.util.flow.Blocker;
import de.soeiner.mental.util.flow.EventBlocker;
import de.soeiner.mental.util.flow.TimeoutBlocker;

/**
 * Created by Malte on 21.04.2016.
 */
public abstract class WavesTrainGameMode extends TrainGameMode {

    protected Wave wave;
    private int waveCounter = 1;
    protected Blocker betweenWaveBlocker = new TimeoutBlocker(5000);

    private TrainGenerator trainGenerator;

    protected HealthWithRestoreGameCondition waveCondition;

    public WavesTrainGameMode(final Game game) {
        super(game);
    }

    @Override
    public void prepareGame() {
        super.prepareGame();
        resetWaveCounter();
        wave = getNextWave();
    }

    @Override
    public String getName() {
        return "Waves - Coop";
    }

    @Override
    public void gameLoop() {
        countdown(5);
        trainArrivedRewarder.runState.setRunning(true);
        while (runState.isRunning()) {
            trainArrivedRewarder.moneyReward = wave.getReward(); // TODO may be different from
            trainArrivedRewarder.pointsReward = wave.getReward();
            trainGenerator = new TrainWaveGenerator(this, wave, game.activePlayers.size(), getAvailableMatchingIds(), new TrainTrack[]{getTrackById(getFirstTrackId())});
            trainGenerator.runState.setRunning(true);
            waveCondition = new HealthWithRestoreGameCondition(this, wave.getHealth(), 0, wave.getHealthNeededToWin());
            waveCondition.addListener(new EventListener<HealthLimitReachedEvent>() {
                @Override
                public void onEvent(HealthLimitReachedEvent event) {
                    broadcastWaveCompleted(event.isPositive(), waveCounter++, 0); // TODO
                    if (!event.isPositive()) {
                        runState.setRunning(false);
                    }
                }
            });
            new EventBlocker<>(waveCondition.conditionMetOrAborted).block();
            trainGenerator.runState.setRunning(false);
            if (hasNextWave()) {
                System.out.println("blocker");
                betweenWaveBlocker.block();
                wave = getNextWave();
                System.out.println("got next wave");
            } else {
                System.out.println("Players won");
                runState.setRunning(false);
            }
        }
        System.out.println("stopping trainGenerator");
    }

    public boolean isWaveRunning() {
        return trainGenerator.runState.isRunning();
    }

    protected abstract boolean hasNextWave();

    protected abstract Wave getNextWave();

    protected abstract void resetWaveCounter();

    public void broadcastWaveCompleted(boolean success, int waveNumber, int reward) {
        try {
            JSONObject j = CmdRequest.makeCmd(CmdRequest.TRAIN_WAVE_COMPLETED);
            j.put("success", success);
            j.put("waveNo", waveNumber);
            j.put("reward", reward);
            broadcast(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
