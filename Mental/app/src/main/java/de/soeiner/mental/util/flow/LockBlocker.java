package de.soeiner.mental.util.flow;

/**
 * Created by Sven on 12.10.16.
 */
public class LockBlocker implements Blocker {

    protected final Object lock;

    public LockBlocker(Object lock) {
        this.lock = lock;
    }

    @Override
    public void block(long timeout) {
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void block() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
