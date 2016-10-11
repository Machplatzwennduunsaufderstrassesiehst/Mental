package de.soeiner.mental.util.event;

import java.util.ArrayList;

/**
 * Created by Sven on 11.10.16.
 */
public class EventDispatcher<E extends Event> {

    private ArrayList<EventListener<E>> listeners = new ArrayList<>();

    public void addListener(EventListener<E> listener) {
        listeners.add(listener);
    }

    public void addListenerOnce(EventListener<E> listener) {
        while (listeners.contains(listener)) {
            listeners.remove(listener);
        }
        addListener(listener);
    }

    public void addSingleDispatchListener(final EventListener<E> listener) {
        addListener(new EventListener<E>() {

            @Override
            public void onEvent(E event) {
                listener.onEvent(event);
                removeListener(this);
            }
        });
    }

    public boolean removeListener(EventListener<E> listener) {
        return listeners.remove(listener);
    }

    public void fireEvent(E event) {
        for (EventListener<E> listener : listeners) {
            listener.onEvent(event);
        }
    }

}