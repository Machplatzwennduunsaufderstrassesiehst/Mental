package de.soeiner.mental.trainGame.rewarding;

import de.soeiner.mental.main.Player;
import de.soeiner.mental.trainGame.events.RewardEvent;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Sven on 17.10.16.
 */
public class TrainArrivedRewarder extends Rewarder implements EventListener<TrainArrivedEvent> {

    public int pointsReward = 0;
    public int moneyReward = 0;

    public TrainArrivedRewarder(TrainGameMode trainGameMode) {
        super(trainGameMode);
        trainGameMode.trainArrived.addListener(this);
        System.out.println("TrainArrivedRewarder created");
    }

    @Override
    protected void destructor() {
        trainGameMode.trainArrived.removeListener(this);
        System.out.println("TrainArrivedRewarder destroyed");
    }

    @Override
    public void onEvent(TrainArrivedEvent event) {
        rewardAll(new RewardEvent(null, pointsReward, moneyReward));
    }
}
