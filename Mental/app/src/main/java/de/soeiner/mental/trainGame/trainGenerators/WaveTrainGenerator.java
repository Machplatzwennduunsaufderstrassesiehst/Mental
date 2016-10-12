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
    private boolean waveRunning = false;
    private Blocker betweenWaveBlocker = new TimeoutBlocker(5000);
    public final EventDispatcher<BooleanEvent> playersWon = new EventDispatcher<>();

    public WaveTrainGenerator(TrainGameMode trainGameMode, int nPlayers, Integer[] availableMatchingIds, Integer[] availableStartTrackIds) {
        super(trainGameMode, nPlayers, availableMatchingIds, availableStartTrackIds);
    }

    public void setBetweenWaveBlocker(Blocker betweenWaveBlocker) {
        this.betweenWaveBlocker = betweenWaveBlocker;
    }

    private boolean goToNextWave() {
        if (hasNextWave()) {
            currentWave = getNextWave();
            HealthWithRestoreGameCondition waveCondition = new HealthWithRestoreGameCondition(trainGameMode, currentWave.getHealth(), 0, currentWave.getHealthNeededToWin());
            waveCondition.addListener(new EventListener<HealthLimitReachedEvent>() {
                @Override
                public void onEvent(HealthLimitReachedEvent event) {
                    if (event.isPositive()) {
                        waveRunning = false;
                        betweenWaveBlocker.block();
                        if (goToNextWave()) {
                            waveRunning = true;
                        }
                    }  else {
                        // negative health limit, players lost
                        playersWon.fireEvent(new BooleanEvent(false));
                    }
                }
            });
            return true;
        } else {
            // no waves left, so players won.
            playersWon.fireEvent(new BooleanEvent(true));
            return false;
        }
    }

    public void loop() {
        if (currentWave == null) goToNextWave();
        if (waveRunning) {
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
