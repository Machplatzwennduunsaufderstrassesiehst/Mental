package de.soeiner.mental.util.event;

import de.soeiner.mental.trainGame.events.RunStateChangedEvent;

/**
 * Created by Sven on 13.10.16.
 */
public class RunState extends EventDispatcher<RunStateChangedEvent> {

    private boolean running;

    public void setRunning(boolean running) {
        if (running != this.running) {
            dispatchEvent(new RunStateChangedEvent(running));
        }
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }
}
