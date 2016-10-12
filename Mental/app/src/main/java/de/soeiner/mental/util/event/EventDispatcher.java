package de.soeiner.mental.util.event;

import java.util.ArrayList;

/**
 * Created by Sven on 11.10.16.
 */
public class EventDispatcher<E extends Event> implements EventListener<E>{

    private ArrayList<EventListener<E>> listeners = new ArrayList<>();
    private final Object listenersArrayLock = new Object();

    public final void addListener(EventListener<E> listener) {
        if (listener == this) {
            throw new RuntimeException("I can't listen to myself!");
        }
        synchronized (listenersArrayLock) {
            listeners.add(listener);
        }
    }

    public final void addListenerOnce(EventListener<E> listener) {
        while (listeners.contains(listener)) {
            synchronized (listenersArrayLock) {
                listeners.remove(listener);
            }
        }
        addListener(listener);
    }

    public final void addSingleDispatchListener(final EventListener<E> listener) {
        addListener(new EventListener<E>() {

            @Override
            public void onEvent(E event) {
                listener.onEvent(event);
                removeListener(this);
            }
        });
    }

    public final boolean removeListener(EventListener<E> listener) {
        synchronized (listenersArrayLock) {
            return listeners.remove(listener);
        }
    }

    public final void dispatchEvent(final E event) {
        synchronized (listenersArrayLock) {
            for (final EventListener<E> listener : listeners) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEvent(event);
                    }
                }).start();
            }
        }
        System.out.println("Event fired: " + event.toString());
    }


    /**
     * EventDispatcher can of cause also be a listener and forward events
     * @param event event to be forwarded
     */
    @Override
    public void onEvent(E event) {
        dispatchEvent(event);
    }
}