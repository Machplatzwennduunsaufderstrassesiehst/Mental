package de.soeiner.mental.arithmetics.gameModes;

import org.json.JSONObject;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.main.Score;
import de.soeiner.mental.util.Math2;

/**
 * Created by Malte on 09.04.2016.
 */
public class BeatBobGameMode extends ArithmeticGameMode {

    double bobSolveTime;
    double bobStartSolveTime;
    double playerHeadstart;
    double health;
    double status = 0;
    double exercisesSolved = 0;
    double upTime;
    double[] function;

    public BeatBobGameMode(Game g) {
        super(g);
    }

    public void prepareGame() {
        super.prepareGame();
        status = 0;
        addAllPlayersToActive();
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).exerciseCreator = game.exerciseCreator.copy();
            game.activePlayers.get(i).exerciseCreator.setDifficulty(10);
        }
        if (game.activePlayers.size() != 0) {
            bobSolveTime = (game.exerciseCreator.getExpectedSolveTime()) / game.activePlayers.size() + 1; //angenommen ein Spieler benötigt 10 sekunden um eine Aufgabe zu lösen
            bobStartSolveTime = bobSolveTime;
            health = 5 * game.activePlayers.size();
            playerHeadstart = game.exerciseCreator.getExpectedSolveTime();
        } else {
            running = false;
        }
        function = calculateSolveTimeFunction();
    }

    @Override
    public void newExercise() {
    }

    public void loop() {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            Player player = game.activePlayers.get(i);
            player.sendExercise(player.exerciseCreator.next());
        }
        try {
            Thread.sleep(calculateMilliSeconds(playerHeadstart));
            upTime += playerHeadstart;
            while (running) {
                System.out.println("[BeatBob.loop]");
                if (game.activePlayers.size() == 0) {
                    running = false;
                }
                for (double i = 0; (i <= bobSolveTime * 10) && running; i++) {
                    System.out.println("[BeatBob.loop] for-Schleife");
                    bobSolveTime = balanceBob();
                    Thread.sleep(100);
                    upTime += 0.1;
                }
                Thread.sleep(calculateMilliSeconds(bobSolveTime - ((int) bobSolveTime))); //zeit nach dem Komma
                updateStatus(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("BOB FAILED");
        }
    }

    public boolean playerAction(Player player, JSONObject actionData) {
        Score s = player.getScore();
        if (player.exerciseCreator.checkAnswer(actionData)) {
            exercisesSolved++;
            s.updateScore(5);
            player.exerciseCreator.next();
            player.sendExercise(player.exerciseCreator.getExerciseObject());
            updateStatus(1);
            game.broadcastMessage(player.getName() + " hat einen Punkt gewonnen");
            synchronized (answerTimeoutLock) {
                answerTimeoutLock.notify();
            }
            game.broadcastScoreboard();
            return true;
        } else {
            player.getScore().setPointsGained(-1);
            if (s.getScoreValue() > 0) {
                s.updateScore(-1);
                game.broadcastScoreboard();
            }
            return false;
        }
    }

    private void updateStatus(int plus) {
        status += plus;
        broadcastStatus();
        checkObjective();
    }

    public void checkObjective() {
        synchronized (answerTimeoutLock) {
            if (status >= health) { //wenn bob tot ist
                giveReward();
                running = false; // schleife in run() beenden
                broadcastPlayerWon("die Spieler", getName());
                answerTimeoutLock.notify();
            }
            if (status <= -health) { //wenn spieler tot sind
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
                running = false; // schleife in run() beenden
                game.broadcastMessage("Bob hat gewonnen");
                broadcastPlayerWon("Bob", getName());
                answerTimeoutLock.notify();
            }
        }
    }


    public String getName() {
        return "Beat Bob";
    }

    private void giveReward() { //TODO
        game.broadcastMessage("Bob got rekt!");
        for (int i = 0; i < game.activePlayers.size(); i++) {
            game.activePlayers.get(i).getShop().addMoney(50);
        }
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }

    @Override
    public void removePlayer(Player p) {
        function = calculateSolveTimeFunction();
        checkObjective();
        if (game.activePlayers.size() == 0) {
            running = false;
        }
    }

    public void broadcastStatus() {
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            Player p = game.joinedPlayers.get(i);
            p.sendBeatBobStatus(calculatePercentage());
        }
        //game.broadcastMessage(status+" ; "+bobSolveTime);
    }

    private double calculatePercentage() {
        if (status > 0) {
            return status / health; // die anzeige kommt mit 300% nicht so gut an Xd
        }
        if (status < 0) {
            return status / health;
        }
        if (status == 0) {
            return 0;
        }
        return 0;
    }

    public int getIndex(Player p) { //gibt den index eines spielers in der aktiven liste zurück

        for (int i = 0; i < game.activePlayers.size(); i++) {
            if (game.activePlayers.get(i).equals(p)) {
                return i;
            }
        }
        return -1;
    }

    public void exerciseTimeout() {
    }

    //============================ KI

    private double balanceBob() {
        bobSolveTime = calculateSolveTime();
        return calculateSolveTime();
    }

    private double calculateSolveTime() {
        //System.out.println(solvetimeBaseFunction() +" "+ (-calculateRating() * solvetimeBaseFunction()));
        return solvetimeBaseFunction() + -calculateRating() * solvetimeBaseFunction();
    }

    private double solvetimeBaseFunction() {

        int value = 0;

        for (int i = 0; i < function.length; i++) {
            value += function[i] * Math.pow(status, 5 - i);
        }
        return value;
    }

    private double[] calculateSolveTimeFunction() {
        double baseTime = bobStartSolveTime;
        double[] f = {0.0, -health, health}; //x
        double[] b = {baseTime, 2 * baseTime, 0.3 * baseTime, 0, 0, 0};
        double[][] A = new double[6][6]; //gleichungen

        for (int i = 0; i < 3; i++) {
            for (double j = 5.0; j >= 0; j--) {
                A[i][5 - (int) j] = Math.pow(f[i], j);
            }
            System.out.println();
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 5; j > 1; j--) {
                A[3 + i][5 - (int) j] = (j) * Math.pow(f[i], j);
            }
            A[3 + i][4] = 1.0;
            A[3 + i][5] = 0;
        }
        return Math2.lsolve(A, b);
    }

    private double calculateBaseRep() {
        return exercisesSolved / (upTime * game.activePlayers.size() / game.exerciseCreator.getExpectedSolveTime());
    }

    private double calculateRating() {
        double baseRep = calculateBaseRep();
        if (baseRep > 1) {
            if (baseRep % 0.5 == 0) {
                return 0.5;
            }
            if (baseRep - 1.0 > 0.5) {
                return 0.5;
            }
            return ((baseRep - 1.0) % 0.5);
        }
        if (baseRep == 1) {
            return 0;
        }
        if (baseRep < 1) {
            if (baseRep % 0.5 == 0) {
                return -0.5;
            }
            if (1 - baseRep > 0.5) {
                return -0.5;
            }
            return -((1.0 - baseRep) % 0.5);
        }
        return 0.0;
    }

    private int calculateMilliSeconds(double SolveTime) {
        return (int) (1000 * SolveTime);
    }
}
