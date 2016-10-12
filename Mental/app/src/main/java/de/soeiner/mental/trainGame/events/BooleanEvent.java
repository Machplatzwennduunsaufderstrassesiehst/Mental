package de.soeiner.mental.trainGame.events;

/**
 * Created by Sven on 12.10.16.
 */
public class BooleanEvent implements GameConditionEvent {

    private boolean positive;

    public BooleanEvent(boolean positive) {
        this.positive = positive;
    }

    public boolean isPositive() {
        return positive;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + positive + ")";
    }
}
