package de.soeiner.mental.trainGame.trainGenerators;

import de.soeiner.mental.trainGame.gameModes.TrainGameMode;

/**
 * Created by Sven on 12.10.16.
 */
public class StaticWaveTrainGenerator extends WaveTrainGenerator {

    protected Wave[] waves;
    protected int waveId;

    public StaticWaveTrainGenerator(TrainGameMode trainGameMode, int nPlayers, Integer[] availableMatchingIds, Integer[] availableStartTrackIds) {
        super(trainGameMode, nPlayers, availableMatchingIds, availableStartTrackIds);
        initiateWaves();
    }


    Wave[] initiateWaves() {
        waves = new Wave[7];
        waveId = 0;
        //double minspeed, double maxspeed, trainspawnintervall, trainarrivedreward, health, healthnw, reward
        //waves[0] = new Wave(6, 6, 100, 1, 99999, 999999, 25);
        //waves[0] = new Wave(1.0, 1.0, 3500, 1, 10, 15, 25);
        waves[0] = new Wave(1.1, 1.3, 2700, 2, 3, 25, 50);
        waves[1] = new Wave(1.4, 1.5, 2100, 3, 10, 30, 100);
        waves[2] = new Wave(1.7, 1.5, 1800, 4, 10, 35, 200);
        waves[3] = new Wave(1.7, 1.5, 1600, 10, 10, 40, 300);
        waves[4] = new Wave(1.4, 1.6, 1400, 10, 10, 50, 500);
        waves[5] = new Wave(1.1, 1.7, 1200, 10, 10, 50, 750);
        waves[6] = new Wave(0.7, 1.7, 1000, 10, 10, 50, 1000);
        /*        int testhealth = 8;
        int testhealthNeededToWin = 17; // um schnell zur nächsten wave zu gelangen
        waves[0] = new Wave(0.5, 0.5, 4000, 1, 10, testhealthNeededToWin, 25);
        waves[1] = new Wave(1.0, 1.0, 4000, 2, 10, testhealthNeededToWin, 50);
        waves[2] = new Wave(1.5, 1.5, 3500, 3, 10, testhealthNeededToWin, 100);
        waves[3] = new Wave(2.0, 2.0, 3000, 4, 10, testhealthNeededToWin, 200);
        waves[4] = new Wave(4.0, 4.0, 2000, 10, 10, testhealthNeededToWin, 500);*/
        return waves;
    }

    @Override
    protected boolean hasNextWave() {
        return waveId < waves.length;
    }

    @Override
    protected Wave getNextWave() {
        return waves[waveId++]; /* post increment !! */
    }
}
