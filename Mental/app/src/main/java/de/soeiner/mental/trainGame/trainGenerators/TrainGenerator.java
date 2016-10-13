package de.soeiner.mental.trainGame.trainGenerators;

import de.soeiner.mental.trainGame.Train;
import de.soeiner.mental.trainGame.events.BooleanEvent;
import de.soeiner.mental.trainGame.events.RunStateChangedEvent;
import de.soeiner.mental.trainGame.events.TrainSpawnEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.trainGame.trainTracks.TrainTrack;
import de.soeiner.mental.util.event.EventListener;
import de.soeiner.mental.util.event.RunState;

/**
 * Created by Sven on 11.10.16.
 */
public abstract class TrainGenerator implements Runnable {

    public final RunState runState = new RunState();

    int trainIdCounter = 0;

    protected TrainGameMode trainGameMode;
    protected int numPlayers;
    protected Integer[] availableMatchingIds;
    protected TrainTrack[] availableStartTracks;

    public TrainGenerator(TrainGameMode trainGameMode, int numPlayers, Integer[] availableMatchingIds, TrainTrack[] availableStartTracks) {
        this.trainGameMode = trainGameMode;
        this.numPlayers = numPlayers;
        this.availableMatchingIds = availableMatchingIds;
        this.availableStartTracks = availableStartTracks;

        runState.addListener(new EventListener<RunStateChangedEvent>() {
            @Override
            public void onEvent(RunStateChangedEvent event) {
                if (event.isPositive()) {
                    start();
                } else {
                    stop();
                }
            }
        });
    }

    @Override
    public final void run() {
        while (runState.isRunning()) {
            System.out.println("[TrainGenerator.run()] spawnNextTrainLoop()");
            spawnNextTrainLoop();
            System.out.println("[TrainGenerator.run()] spawned next train");
        }
    }

    private void start() {
        runState.setRunning(true);
        Thread t = new Thread(this);
        t.start();

        trainGameMode.runState.addSingleDispatchListener(runState);
    }

    private void stop() {
        trainGameMode.runState.removeListener(runState);
    }

    public Train newTrain(int matchingId, int speed, boolean bombTrain, TrainTrack startTrack) {
        Train train = new Train(trainIdCounter++, matchingId, speed, trainGameMode, bombTrain, startTrack);
        train.trainArrived.addListenerOnce(trainGameMode.trainArrived);
        trainGameMode.trainSpawn.dispatchEvent(new TrainSpawnEvent(train));
        return train;
    }

    public abstract void spawnNextTrainLoop();

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
