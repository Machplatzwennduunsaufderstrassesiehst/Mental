package de.soeiner.mental.util.flow;

/**
 * Created by Sven on 12.10.16.
 */
public class TimeoutBlocker implements Blocker {

    private int timeout;

    public TimeoutBlocker(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void block(long timeout) {
        try {
            Thread.sleep(Math.min(timeout, this.timeout));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void block() {
        block(timeout);
    }
}
