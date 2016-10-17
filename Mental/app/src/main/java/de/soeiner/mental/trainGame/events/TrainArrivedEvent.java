package de.soeiner.mental.trainGame.events;

import de.soeiner.mental.trainGame.trains.Train;
import de.soeiner.mental.trainGame.tracks.Goal;
import de.soeiner.mental.util.event.Event;

/**
 * Created by Sven on 11.10.16.
 */
public class TrainArrivedEvent implements Event {

    private Train train;
    private boolean match;
    private Goal goal;

    public TrainArrivedEvent(Train train, Goal goal, boolean match) {
        this.train = train;
        this.match = match;
        this.goal = goal;
    }

    public Train getTrain() {
        return train;
    }

    public Goal getGoal() {
        return goal;
    }

    public boolean isMatch() {
        return match;
    }
}
