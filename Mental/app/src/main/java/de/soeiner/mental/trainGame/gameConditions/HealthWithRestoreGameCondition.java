package de.soeiner.mental.trainGame.gameConditions;

import de.soeiner.mental.trainGame.events.HealthLimitReachedEvent;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Sven on 12.10.16.
 */
public class HealthWithRestoreGameCondition extends GameCondition<HealthLimitReachedEvent> {

    private int health;
    private int startHealth;
    private int negativeHealthLimit;
    private int positiveHealthLimit;

    public HealthWithRestoreGameCondition(TrainGameMode trainGameMode, int startHealth, int negativeHealthLimit, int positiveHealthLimit) {
        super(trainGameMode);
        this.health = this.startHealth = startHealth;
        this.negativeHealthLimit = negativeHealthLimit;
        this.positiveHealthLimit = positiveHealthLimit;
        trainGameMode.trainArrived.addListenerOnce(trainArrivedListener);
    }

    EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {
            if (event.isMatch()) {
                health++;
            } else {
                health--;
            }
            if (health <= negativeHealthLimit) {
                dispatchEvent(new HealthLimitReachedEvent(false));
                health = startHealth;
            } else if (health >= positiveHealthLimit) {
                dispatchEvent(new HealthLimitReachedEvent(true));
                health = startHealth;
            }
        }
    };

    public int getHealth() {
        return health;
    }

    @Override
    public void reset() {
        health = startHealth;
    }

    public void setHealth(int health) {
        this.startHealth = this.health = health;
    }

    public void setNegativeHealthLimit(int negativeHealthLimit) {
        this.negativeHealthLimit = negativeHealthLimit;
    }

    public void setPositiveHealthLimit(int positiveHealthLimit) {
        this.positiveHealthLimit = positiveHealthLimit;
    }
}
