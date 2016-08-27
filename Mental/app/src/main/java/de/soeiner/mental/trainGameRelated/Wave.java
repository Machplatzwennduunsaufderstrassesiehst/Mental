package de.soeiner.mental.trainGameRelated;

/**
 * Created by Malte on 25.05.2016.
 */
public class Wave {
    private double MAX_SPEED; // zurückgestellt
    private double MIN_SPEED;
    private int TRAIN_SPAWN_INTERVAL;//in milllisekunden
    private int TRAIN_ARRIVED_REWARD;
    private int health;
    private int HEALTH_NEEDED_TO_WIN;
    private int REWARD;

    public Wave(double mins, double maxs, int tsi, int tar, int h, int hnw, int r) {
        MAX_SPEED = mins;
        MIN_SPEED = maxs;
        TRAIN_SPAWN_INTERVAL = tsi;
        TRAIN_ARRIVED_REWARD = tar;
        health = h;
        HEALTH_NEEDED_TO_WIN = hnw;
        REWARD = r;
    }

    public double getMAX_SPEED() {
        return MAX_SPEED;
    }

    public double getMIN_SPEED() {
        return MIN_SPEED;
    }

    public int getHealth() {
        return health;
    }

    public int getHEALTH_NEEDED_TO_WIN() {
        return HEALTH_NEEDED_TO_WIN;
    }

    public int getREWARD() {
        return REWARD;
    }

    public int getTRAIN_ARRIVED_REWARD() {
        return TRAIN_ARRIVED_REWARD;
    }

    public int getTRAIN_SPAWN_INTERVAL() {
        return TRAIN_SPAWN_INTERVAL;
    }
}
