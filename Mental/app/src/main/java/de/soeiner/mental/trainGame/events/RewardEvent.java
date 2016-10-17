package de.soeiner.mental.trainGame.events;

import org.json.JSONException;
import org.json.JSONObject;

import de.soeiner.mental.main.Player;
import de.soeiner.mental.util.event.Event;

/**
 * Created by Sven on 17.10.16.
 */
public class RewardEvent extends JSONObject implements Event{

    private Player player;
    private int pointsReward;
    private int moneyReward;

    public RewardEvent(Player player, int pointsReward, int moneyReward) {
        this.player = player;
        this.pointsReward = pointsReward;
        this.moneyReward = moneyReward;

        try {
            this.put("type", "reward");
            this.put("player", player);
            this.put("pointsReward", pointsReward);
            this.put("moneyReward", moneyReward);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getPointsReward() {
        return pointsReward;
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public Player getPlayer() {
        return player;
    }
}
