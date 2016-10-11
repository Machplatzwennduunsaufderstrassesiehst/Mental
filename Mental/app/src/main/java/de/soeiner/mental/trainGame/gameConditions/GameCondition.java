package de.soeiner.mental.trainGame.gameConditions;

import de.soeiner.mental.trainGame.events.GameConditionEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.EventDispatcher;

/**
 * Created by Sven on 11.10.16.
 */
public abstract class GameCondition<E extends GameConditionEvent> extends EventDispatcher<E> {

    protected TrainGameMode trainGameMode;

    public GameCondition(TrainGameMode trainGameMode) {
        this.trainGameMode = trainGameMode;
    }

    public abstract void reset();

}
