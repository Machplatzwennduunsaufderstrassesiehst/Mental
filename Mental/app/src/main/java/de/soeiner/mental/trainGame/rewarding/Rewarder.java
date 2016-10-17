package de.soeiner.mental.trainGame.rewarding;

import de.soeiner.mental.main.Player;
import de.soeiner.mental.trainGame.events.RewardEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.trainGame.misc.GameThread;
import de.soeiner.mental.util.event.EventDispatcher;

/**
 * Created by Sven on 17.10.16.
 */
public abstract class Rewarder extends GameThread {

    public EventDispatcher<RewardEvent> rewardDispatcher = new EventDispatcher<>();

    public Rewarder(TrainGameMode trainGameMode) {
        super(trainGameMode);
    }

    protected void reward(RewardEvent event) {
        trainGameMode.broadcast(event);
        rewardDispatcher.dispatchEvent(event);
    }

    protected void rewardAll(RewardEvent event) {
        for (Player player : trainGameMode.getActivePlayers()) {
            reward(new RewardEvent(player, event.getPointsReward(), event.getMoneyReward()));
        }
    }

}
