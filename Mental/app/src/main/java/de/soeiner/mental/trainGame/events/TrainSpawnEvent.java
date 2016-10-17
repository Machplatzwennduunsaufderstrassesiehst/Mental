package de.soeiner.mental.trainGame.events;

import de.soeiner.mental.trainGame.trains.Train;
import de.soeiner.mental.util.event.Event;

/**
 * Created by Sven on 12.10.16.
 */
public class TrainSpawnEvent implements Event {

    private Train train;

    public TrainSpawnEvent(Train train) {
        this.train = train;
    }

    public Train getTrain() {
        return train;
    }
}
