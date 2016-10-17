package de.soeiner.mental.util.event;

import de.soeiner.mental.trainGame.events.RunStateChangedEvent;

/**
 * Created by Sven on 13.10.16.
 */
public class RunState extends EventDispatcher<RunStateChangedEvent> {

    private boolean running;

    public RunState() {

    }

    /**
     * this Runstate stops at the same time as the parent RunState
     * @param parent parent RunState
     */
    public RunState(final RunState parent) {
        parent.addListener(new EventListener<RunStateChangedEvent>() {
            @Override
            public void onEvent(RunStateChangedEvent event) {
                if (!event.isPositive()) {
                    RunState.this.setRunning(false);
                    parent.removeListener(this);
                }
            }
        });
    }

    public void setRunning(boolean running) {
        if (running != this.running) {
            dispatchEvent(new RunStateChangedEvent(running));
            if (running) {
                onStart();
            } else {
                onStop();
            }
        }
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public void onStart() {

    }

    public void onStop() {

    }
}
