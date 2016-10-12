package de.soeiner.mental.trainGame.trainGenerators;

import de.soeiner.mental.trainGame.events.BooleanEvent;
import de.soeiner.mental.trainGame.events.HealthLimitReachedEvent;
import de.soeiner.mental.trainGame.gameConditions.HealthWithRestoreGameCondition;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.EventDispatcher;
import de.soeiner.mental.util.event.EventListener;
import de.soeiner.mental.util.flow.Blocker;
import de.soeiner.mental.util.flow.TimeoutBlocker;

/**
 * Created by Sven on 11.10.16.
 */
public abstract class WaveTrainGenerator extends TrainGenerator {

    protected Wave currentWave;
    protected int waveNumber = 0;
    private boolean waveRunning = false;
    private Blocker betweenWaveBlocker = new TimeoutBlocker(5000);

    public final EventDispatcher<BooleanEvent> playersWon = new EventDispatcher<>();
    public final EventDispatcher<BooleanEvent> waveCompleted = new EventDispatcher<>();

    HealthWithRestoreGameCondition waveCondition;

    public WaveTrainGenerator(final TrainGameMode trainGameMode, int nPlayers, Integer[] availableMatchingIds, Integer[] availableStartTrackIds) {
        super(trainGameMode, nPlayers, availableMatchingIds, availableStartTrackIds);
        waveCondition = new HealthWithRestoreGameCondition(trainGameMode, 10, 0, 20);
        waveCondition.addListener(new EventListener<HealthLimitReachedEvent>() {
            @Override
            public void onEvent(HealthLimitReachedEvent event) {
                waveRunning = false;
                if (event.isPositive()) {
                    waveCompleted.dispatchEvent(new BooleanEvent(true));
                    betweenWaveBlocker.block();
                    goToNextWave();
                }  else {
                    // negative health limit, players lost
                    waveCompleted.dispatchEvent(new BooleanEvent(false));
                    playersWon.dispatchEvent(new BooleanEvent(false));
                }
            }
        });
        trainGameMode.runStateChanged.addSingleDispatchListener(new EventListener<BooleanEvent>() {
            @Override
            public void onEvent(BooleanEvent event) {
                if (!event.isPositive()) {
                    playersWon.dispatchEvent(event);
                }
            }
        });
        waveCompleted.addListener(new EventListener<BooleanEvent>() {
            @Override
            public void onEvent(BooleanEvent event) {
                trainGameMode.broadcastWaveCompleted(event.isPositive(), waveNumber++, 0);
            }
        });
    }

    public void setBetweenWaveBlocker(Blocker betweenWaveBlocker) {
        this.betweenWaveBlocker = betweenWaveBlocker;
    }

    private boolean goToNextWave() {
        if (hasNextWave()) {
            currentWave = getNextWave();
            waveCondition.setHealth(currentWave.getHealth());
            waveCondition.setPositiveHealthLimit(currentWave.getHealthNeededToWin());
            return waveRunning = true;
        } else {
            // no waves left, so players won.
            playersWon.dispatchEvent(new BooleanEvent(true));
            return waveRunning = false;
        }
    }

    public void spawnNextTrain() {
        System.out.println("spawnNextTrain()");
        if (currentWave == null) goToNextWave();
        if (waveRunning) {
            System.out.println("Spawning train...");
            int matchingId = availableMatchingIds[(int) (Math.random() * availableMatchingIds.length)];
            int speed = (int) (Math.random() * (currentWave.getMaxSpeed() - currentWave.getMinSpeed()) + currentWave.getMinSpeed());
            newTrain(matchingId, speed, false);
            try {
                Thread.sleep(currentWave.getTrainSpawnInterval()); //warten
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isWaveRunning() {
        return waveRunning;
    }

    protected abstract boolean hasNextWave();

    protected abstract Wave getNextWave();
}
