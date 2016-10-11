package de.soeiner.mental.trainGame.events;

/**
 * Created by Sven on 12.10.16.
 */
public class HealthLimitReachedEvent extends BooleanEvent {

    public HealthLimitReachedEvent(boolean positive) {
        super(positive);
    }

}
