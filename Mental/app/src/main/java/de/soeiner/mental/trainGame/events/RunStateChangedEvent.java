package de.soeiner.mental.trainGame.events;

/**
 * Created by Sven on 13.10.16.
 */
public class RunStateChangedEvent extends BooleanEvent {
    public RunStateChangedEvent(boolean positive) {
        super(positive);
    }
}
