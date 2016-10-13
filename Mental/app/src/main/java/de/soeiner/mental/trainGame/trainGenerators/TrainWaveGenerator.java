package de.soeiner.mental.trainGame.trainGenerators;

import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.trainGame.trainTracks.TrainTrack;

/**
 * Created by Sven on 13.10.16.
 */
public class TrainWaveGenerator extends TrainGenerator {

    private Wave wave;

    public TrainWaveGenerator(TrainGameMode trainGameMode, Wave wave, int numPlayers, Integer[] availableMatchingIds, TrainTrack[] availableStartTracks) {
        super(trainGameMode, numPlayers, availableMatchingIds, availableStartTracks);
        this.wave = wave;
    }

    @Override
    public void spawnNextTrainLoop() {
        System.out.println("Spawning train...");
        int matchingId = availableMatchingIds[(int) (Math.random() * availableMatchingIds.length)];
        TrainTrack startTrack = availableStartTracks[(int) (Math.random() * availableStartTracks.length)];
        int speed = (int) (Math.random() * (wave.getMaxSpeed() - wave.getMinSpeed()) + wave.getMinSpeed());
        newTrain(matchingId, speed, false, startTrack);
        System.out.println("train spawned");
        try {
            Thread.sleep(wave.getTrainSpawnInterval()); //warten
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
