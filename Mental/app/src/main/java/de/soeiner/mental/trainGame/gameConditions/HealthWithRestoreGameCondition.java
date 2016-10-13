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
    private int negativeHealthLimit;
    private int positiveHealthLimit;

    public HealthWithRestoreGameCondition(TrainGameMode trainGameMode, int health, int negativeHealthLimit, int positiveHealthLimit) {
        super(trainGameMode);
        this.health = health;
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
            } else if (health >= positiveHealthLimit) {
                dispatchEvent(new HealthLimitReachedEvent(true));
            }
        }
    };

    public int getHealth() {
        return health;
    }

    @Override
    public void destructor() {
        trainGameMode.trainArrived.removeListener(trainArrivedListener);
    }
}
