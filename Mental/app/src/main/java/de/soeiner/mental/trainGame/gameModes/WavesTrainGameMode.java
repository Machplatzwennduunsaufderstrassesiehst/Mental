package de.soeiner.mental.trainGame.gameModes;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.trainGame.events.BooleanEvent;
import de.soeiner.mental.trainGame.gameConditions.HealthWithRestoreGameCondition;
import de.soeiner.mental.trainGame.trainGenerators.StaticWaveTrainGenerator;
import de.soeiner.mental.trainGame.trainGenerators.TrainGenerator;
import de.soeiner.mental.trainGame.trainGenerators.Wave;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.trainGenerators.WaveTrainGenerator;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.util.event.EventListener;
import de.soeiner.mental.util.flow.EventBlocker;

/**
 * Created by Malte on 21.04.2016.
 */
public class WavesTrainGameMode extends TrainGameMode {

    protected StaticWaveTrainGenerator trainGenerator;

    public WavesTrainGameMode(final Game game) {
        super(game);
        super.trainArrived.addListenerOnce(trainArrivedListener);
    }

    @Override
    public void prepareGame() {
        super.prepareGame();
    }

    @Override
    protected TrainGenerator createTrainGenerator() {
        return trainGenerator = new StaticWaveTrainGenerator(this, game.activePlayers.size(), getAvailableMatchingIds(), new Integer[]{getFirstTrackId()});
    }

    @Override
    public String getName() {
        return "Waves - Coop";
    }

    @Override
    public void gameLoop() {
        countdown(5);
        System.out.println("countdown ended");
        setRunning(true);
        System.out.println("set running to true");
        trainGenerator.start();
        System.out.println("started trainGenerator");
        new EventBlocker<>(trainGenerator.playersWon).block();
        System.out.println("stopping trainGenerator");
        trainGenerator.stop();
        setRunning(false);
    }

    private EventListener<TrainArrivedEvent> trainArrivedListener = new EventListener<TrainArrivedEvent>() {
        @Override
        public void onEvent(TrainArrivedEvent event) {

        }
    };
}
