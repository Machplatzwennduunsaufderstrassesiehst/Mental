package de.soeiner.mental.util.event;

/**
 * Created by Sven on 11.10.16.
 */
public interface EventListener<E extends Event> {
    void onEvent(E event);
}
