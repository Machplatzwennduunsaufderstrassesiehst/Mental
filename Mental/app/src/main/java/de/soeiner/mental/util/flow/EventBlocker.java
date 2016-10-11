package de.soeiner.mental.util.flow;

import de.soeiner.mental.util.event.Event;
import de.soeiner.mental.util.event.EventDispatcher;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Sven on 12.10.16.
 */
public class EventBlocker<E extends Event> extends LockBlocker {

    public EventBlocker(EventDispatcher<E> eventDispatcher) {
        super(new Object());
        eventDispatcher.addSingleDispatchListener(new EventListener<E>() {
            @Override
            public void onEvent(E event) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
    }

}
