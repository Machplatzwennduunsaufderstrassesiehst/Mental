package de.soeiner.mental.util.flow;

/**
 * Created by Sven on 12.10.16.
 */
public interface Blocker {
    /**
     * block until some condition is met
     * @param timeout blocking timeout in milliseconds
     */
    void block(long timeout);

    /**
     * block until some condition is met
     */
    void block();
}
