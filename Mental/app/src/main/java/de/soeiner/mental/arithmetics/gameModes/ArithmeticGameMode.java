package de.soeiner.mental.arithmetics.gameModes;

import org.json.JSONException;
import org.json.JSONObject;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;
import de.soeiner.mental.main.GameMode;
import de.soeiner.mental.trainGame.events.BooleanEvent;
import de.soeiner.mental.util.event.EventListener;

/**
 * Created by Malte on 14.09.2016.
 */
public abstract class ArithmeticGameMode extends GameMode {

    public int GAME_TIMEOUT = 0; //für pause zwischen den spielen mit siegerbildschirm
    public static final int EXERCISE_TIMEOUT = 30;

    public final Object answerTimeoutLock = new Object();

    public ArithmeticGameMode(Game game){
        super(game);
        this.runStateChanged.addListener(new EventListener<BooleanEvent>() {
            @Override
            public void onEvent(BooleanEvent event) {
                if (event.isPositive()) {
                    synchronized (answerTimeoutLock) {
                        answerTimeoutLock.notifyAll();
                    }
                }
            }
        });
    }

    @Override
    public void gameLoop() {
        while (isRunning()) {
            System.out.println("[ArithmeticGameMode.gameLoop()] while-Schleife anfang");
            if (game.activePlayers.size() == 0) { //wenn keine spieler mehr da sind
                setRunning(false);
                System.out.println("[ArithmeticGameMode.gameLoop()] game ended, no players left");
                return;
            } else {
                try {
                    System.out.println("[ArithmeticGameMode.gameLoop()] newExercise()");
                    newExercise();
                    System.out.println("[ArithmeticGameMode.gameLoop()] spawnNextTrain()");
                    loop();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("FEHLER IN GAMEMODE");
                }
            }

        }
    }

    public abstract void loop();

    public int getPoints() { //methode berechent punkte fürs lösen einer Aufgabe
        //jenachdem als wievielter der jeweilige spieler die richtige Antwort eraten hat
        int points = game.exerciseCreator.getDifficulty() * 3 / 2; // hab ich bisschen erhöht, da eine Runde ganz schön lange gedauert hat, wenn jeder mal ne Aufgabe löst
        for (int i = 0; i < getRank(); i++) {
            points = points / 2;
        }
        return points;
    }

    public int getRank() { //methode berechnet wie viele
        // Spieler die Aufgabe schon gelöst haben
        int rank = 0;
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            Player p = game.joinedPlayers.get(i);
            if (p.finished) {
                rank++;
            }
        }
        return rank;
    }

    public void exerciseTimeout() {
        doWaitTimeout(EXERCISE_TIMEOUT);
    }

    public void doWaitTimeout(int timeout) {
        try {
            JSONObject j = CmdRequest.makeCmd(CmdRequest.TIME_LEFT);
            j.put("time", timeout);
            for (int i = 0; i < game.activePlayers.size(); i++) {
                Player p = game.activePlayers.get(i);
                p.makePushRequest(new PushRequest(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        synchronized (answerTimeoutLock) {
            try {
                answerTimeoutLock.wait(timeout * 1000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void newExercise() {
        super.newExercise();
        System.out.println("gameMode.exerciseTimeout()");
        exerciseTimeout();
    }

    @Override
    public void openGUIFrame() {
        game.broadcastSendCountdown(3);
        super.openGUIFrame();
    }

    public void broadcastPlayerWon(String playerName, String gameModeString) { //wird nur aufgerufen wenn Spieler das Spiel gewonnen hat
        //dem scoreboard können nun auch der zweite und dritte platz entnommen werden
        for (int i = 0; i < game.joinedPlayers.size(); i++) {
            Player p = game.joinedPlayers.get(i);
            try {
                JSONObject j = CmdRequest.makeCmd(CmdRequest.PLAYER_WON);
                j.put("playerName", playerName);
                j.put("gameTimeout", GAME_TIMEOUT);
                j.put("gameMode", gameModeString);
                p.makePushRequest(new PushRequest(j));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
