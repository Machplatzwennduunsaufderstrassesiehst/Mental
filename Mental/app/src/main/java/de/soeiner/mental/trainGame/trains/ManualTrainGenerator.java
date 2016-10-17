package de.soeiner.mental.trainGame.trains;

import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.trainGame.tracks.TrainTrack;
import de.soeiner.mental.util.flow.EventBlocker;

/**
 * Created by Sven on 13.10.16.
 */
public class ManualTrainGenerator extends TrainGenerator {
    public ManualTrainGenerator(TrainGameMode trainGameMode, int numPlayers, Integer[] availableMatchingIds, TrainTrack[] availableStartTracks) {
        super(trainGameMode, numPlayers, availableMatchingIds, availableStartTracks);
    }

    @Override
    public void spawnNextTrainLoop() {
        new EventBlocker<>(super.runState).block();
    }
}
