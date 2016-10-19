package de.soeiner.mental.trainGame.gameModes;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 16.09.2016.
 */
public class SuddenDeathWavesTrainGameMode extends StaticWavesTrainGameMode {

    EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {
            if (!event.isMatch()) {
                runState.setRunning(false);
                broadcastWaveCompleted(false, waveId, 0);
            }
        }
    };

    public SuddenDeathWavesTrainGameMode(final Game game) {
        super(game);
        trainArrived.addListenerOnce(trainArrivedListener);
    }

    @Override
    public String getName() {
        return "Sudden Death";
    }
}
