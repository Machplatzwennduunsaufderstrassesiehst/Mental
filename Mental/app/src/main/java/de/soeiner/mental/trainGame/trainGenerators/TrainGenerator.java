package de.soeiner.mental.trainGame.trainGenerators;

import de.soeiner.mental.trainGame.Train;
import de.soeiner.mental.trainGame.events.TrainSpawnEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;

/**
 * Created by Sven on 11.10.16.
 */
public abstract class TrainGenerator implements Runnable {

    protected boolean running = false;

    int trainIdCounter = 0;

    protected TrainGameMode trainGameMode;
    protected int numPlayers;
    protected Integer[] availableMatchingIds;
    protected Integer[] availableStartTrackIds;

    public TrainGenerator(TrainGameMode trainGameMode, int numPlayers, Integer[] availableMatchingIds, Integer[] availableStartTrackIds) {
        this.trainGameMode = trainGameMode;
        this.numPlayers = numPlayers;
        this.availableMatchingIds = availableMatchingIds;
        this.availableStartTrackIds = availableStartTrackIds;
    }

    @Override
    public final void run() {
        while (running) {
            loop();
        }
    }

    public void start() {
        running = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() {
        running = false;
    }

    public Train newTrain(int matchingId, int speed, boolean bombTrain) {
        Train train = new Train(trainIdCounter++, matchingId, speed, trainGameMode, bombTrain);
        train.trainArrived.addListenerOnce(trainGameMode.trainArrived);
        trainGameMode.trainSpawn.fireEvent(new TrainSpawnEvent(train));
        return train;
    }

    public abstract void loop();

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
