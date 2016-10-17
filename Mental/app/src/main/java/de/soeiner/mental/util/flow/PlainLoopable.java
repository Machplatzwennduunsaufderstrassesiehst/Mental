package de.soeiner.mental.util.flow;

/**
 * Created by Sven on 17.10.16.
 */
public class PlainLoopable implements Loopable {

    private Blocker blocker;

    public PlainLoopable(Blocker blocker) {
        this.blocker = blocker;
    }

    @Override
    public void loopedAction() {
        blocker.block();
    }
}
