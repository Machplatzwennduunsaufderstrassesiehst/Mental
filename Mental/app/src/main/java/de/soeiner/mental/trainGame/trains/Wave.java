package de.soeiner.mental.trainGame.trains;

/**
 * Created by Malte on 25.05.2016.
 */
public class Wave {
    private double maxSpeed; // zurückgestellt
    private double minSpeed;
    private int trainSpawnInterval;//in milllisekunden
    private int trainArrivedReward;
    private int health;
    private int healthNeededToWin;
    private int reward;

    public Wave(double mins, double maxs, int tsi, int tar, int h, int hnw, int r) {
        maxSpeed = mins;
        minSpeed = maxs;
        trainSpawnInterval = tsi;
        trainArrivedReward = tar;
        health = h;
        healthNeededToWin = hnw;
        reward = r;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public int getHealth() {
        return health;
    }

    public int getHealthNeededToWin() {
        return healthNeededToWin;
    }

    public int getReward() {
        return reward;
    }

    public int getTrainArrivedReward() {
        return trainArrivedReward;
    }

    public int getTrainSpawnInterval() {
        return trainSpawnInterval;
    }
}
