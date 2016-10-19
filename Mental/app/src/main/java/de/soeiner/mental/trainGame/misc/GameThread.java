package de.soeiner.mental.trainGame.misc;

import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.RunState;
import de.soeiner.mental.util.flow.EventBlocker;
import de.soeiner.mental.util.flow.Loopable;
import de.soeiner.mental.util.flow.PlainLoopable;

/**
 * Created by Sven on 17.10.16.
 */
public abstract class GameThread implements Runnable {

    protected TrainGameMode trainGameMode;
    public final RunState runState;
    private Loopable loopable;

    public GameThread(final TrainGameMode trainGameMode) {
        this.trainGameMode = trainGameMode;

        runState = new RunState(trainGameMode.runState) {
            @Override
            public void onStart() {
                new Thread(GameThread.this).start();
            }

            public void onStop() {
                destructor();
            }

        };

        loopable = new PlainLoopable(new EventBlocker<>(runState));
    }

    /**
     * @param loopable must take care of the logic and also sleep between actions
     */
    public void setLoopable(Loopable loopable) {
        this.loopable = loopable;
    }

    @Override
    public final void run() {
        while (runState.isRunning()) {
            loopable.loopedAction();
        }
    }

    /**
     * remove listeners and free up object for GC
     */
    protected abstract void destructor();
}
