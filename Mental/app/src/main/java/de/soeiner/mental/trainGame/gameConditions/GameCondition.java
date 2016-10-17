package de.soeiner.mental.trainGame.gameConditions;

import de.soeiner.mental.trainGame.events.BooleanEvent;
import de.soeiner.mental.trainGame.events.GameConditionEvent;
import de.soeiner.mental.trainGame.events.RunStateChangedEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.Event;
import de.soeiner.mental.util.event.EventDispatcher;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Sven on 11.10.16.
 */
public abstract class GameCondition<E extends GameConditionEvent> extends EventDispatcher<E> {

    protected TrainGameMode trainGameMode;

    public EventDispatcher<Event> conditionMetOrAborted = new EventDispatcher<>();

    public GameCondition(TrainGameMode trainGameMode) {
        this.trainGameMode = trainGameMode;
        trainGameMode.runState.addSingleDispatchListener(new EventListener<RunStateChangedEvent>() {
            @Override
            public void onEvent(RunStateChangedEvent event) {
                if (!event.isPositive()) {
                    destroy();
                }
            }
        });
    }

    @Override
    public final void dispatchEvent(E event) {
        super.dispatchEvent(event);
        super.removeAllListeners();
        destroy();
    }

    private void destroy() {
        destructor();
        conditionMetOrAborted.dispatchEvent(new BooleanEvent(false));
    }

    /**
     * should remove this and its listeners from existing event dispatchers in order to free it up for Garbage Collection
     */
    public abstract void destructor();
}
