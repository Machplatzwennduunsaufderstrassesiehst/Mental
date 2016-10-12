package de.soeiner.mental.trainGame.gameModes;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.trainGame.trainGenerators.Wave;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 16.09.2016.
 */
public class SuddenDeathTrainGameMode extends WavesTrainGameMode {

    EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {
            if (!event.isMatch()) {
                setRunning(false);
            }
        }
    };

    public SuddenDeathTrainGameMode(final Game game) {
        super(game);
        trainArrived.addListenerOnce(trainArrivedListener);
    }

    @Override
    public String getName() {
        return "Sudden Death";
    }
}
