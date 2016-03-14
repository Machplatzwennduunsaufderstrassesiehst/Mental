package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by malte on 13.02.16.
 */
public class Game implements Runnable {

    private static ArrayList<Game> games;

    static {
        games = new ArrayList<Game>();
    }

    public static ArrayList<Game> getGames() {
        return games;
    }

    private String name = "";
    private String description = "";
    private ArrayList<Player> joinedPlayers;
    private int difficulty = 0;

    private int EXERCISE_TIMEOUT = 30;
    private int GAME_TIMEOUT = 30; //für pause zwischen den spielen mit siegerbildschirm

    String exercise = "";
    private int result = 0;
    private Score[] scoreboard;
    boolean gameIsLive;

    public Game(String name) {
        games.add(this);
        this.name = name;
        Thread t = new Thread(this);
        t.start();
        joinedPlayers = new ArrayList<Player>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void destroy() {
        games.remove(this);
    }

    public void updateScoreBoardSize() {
        scoreboard = new Score[joinedPlayers.size()];
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Score s = joinedPlayers.get(i).getScore();
            scoreboard[i] = s;
        }
        broadcastScoreboard();
    }

    public void broadcastScoreboard() {
        for (Player p : joinedPlayers) {
            p.sendScoreBoard(scoreboard);
        }
    }

    public void join(Player p) {
        if (!joinedPlayers.contains(p)) {
            joinedPlayers.add(p);
        }
        p.updateScore();
        p.sendExercise(exercise);
        updateScoreBoardSize();
    }

    public void leave(Player p) {
        joinedPlayers.remove(p);
        updateScoreBoardSize();
    }

    public void broadcastExercise() {
        exercise = createExercise();
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.finished = false;
            p.sendExercise(exercise);
        }

        //der folgende Code schickt allen spielern einen integer (hier 30) um
        // einen countdown starten zu können. Dann wird 30 Sekunden gewartet

        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TIME_LEFT);
        try {
            j.put("time", EXERCISE_TIMEOUT);
            for (int i = 0; i < joinedPlayers.size(); i++) {
                Player p = joinedPlayers.get(i);
                p.makePushRequest(new PushRequest(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public String createExercise(){

        System.out.println("createExercise() wurde aufgerufen");
        int temp;
        int a = (int) (Math.random() * 5 * difficulty/2)+1;
        int b = (int) (Math.random() * 5 * difficulty/2)+1;

        if(difficulty % 3 == 0){
            if(a < b){
                temp = a;
                a = b;
                b = temp;
            }
                result = a - b;
                return a+" - "+b;

        }else{
            if(difficulty % 5 == 0){
                while(a * b > 1000){
                    a = (int) (a/10);
                    b = (int) (b/10);
                }
                result = a * b;
                return a+" * "+b;

            }else {
                result = a + b;
                return a+" + "+b;
            }
        }
    }

    public boolean playerAnswered(Player player, int answer) {
        System.out.println("spieler hat geantwortet");
        boolean allFinished = true;
        Score s = player.getScore();
        synchronized (this) {
            if (answer == result && !player.finished) { // sonst kann man 2x mal punkte absahnen
                s.setScoreValue(s.getScoreValue() + getPoints());
                sendExerciseSolvedMessage(player.getName(), getRank());
                if(s.getScoreValue() > 100){
                    sendPlayerWon(player.getName());
                }
                player.finished = true;
                for (int i = 0; i < joinedPlayers.size(); i++) {
                    Player p = joinedPlayers.get(i);
                    if (!p.finished) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    notify(); // beendet das wait in loop() vorzeitig wenn alle fertig sind
                }
                broadcastScoreboard();
                return true;
                } else {
                  if(s.getScoreValue() > 0) {
                      s.setScoreValue(s.getScoreValue() - 1);
                      broadcastScoreboard();
                }
                return false;
            }
        }
    }

    private int getPoints(){ //methode berechent punkte fürs lösen einer Aufgabe
        //jenachdem als wievielter der jeweilige spieler die richtige Antwort eraten hat
        int points = difficulty;
        for(int i = 0; i<getRank();i++){
            points = points/2;
        }
        return points;
    }

    private int getRank(){ //methode berechnet wie viele
        // Spieler die Aufgabe schon gelöst haben
        int rank = 0;
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            if(p.finished == true){
                rank++;
            }
        }
        return rank;
    }

    public void sendExerciseSolvedMessage(String playerName, int rang) {
        String m = playerName+" hat die Aufgabe als "+(rang+1)+". gelöst!";
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_MESSAGE);
            try {
                j.put("message", m);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }

    public void sendPlayerWon(String playerName) { //wird nur aufgerufen wenn Spieler das Spiel gewonnen hat
        //dem scoreboard können nun auch der zweite und dritte platz entnommen werden
        gameIsLive = false;
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);

            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_PLAYER_WON);
            try {
                j.put("playerName", playerName);
                j.put("gameTimeout", GAME_TIMEOUT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }

    @Override
    public void run() {
        loop();
    }

    public void loop() {
        // jetzt gibt es hier so eine art game loop, der die Abfolge managed
        // vllt besser als das immer rekursiv aufzurufen wie ich das anfangs gemacht habe
        // spaeter dann vllt auch Match management?
        start:
        while(true) {
            difficulty = 0;
            while (joinedPlayers.size() == 0) { //Warten bis spieler das Spiel betreten hat
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
            gameIsLive = true;

            while (gameIsLive) {
                if (joinedPlayers.size() == 0) { //wenn keine spieler mehr da sin
                    continue start; //springe zurück in den Wartezustand
                } else {
                    broadcastExercise();
                    difficulty++;
                    synchronized (this) { // ist angefordert damit man wait oder notify nutzen kann
                        try {
                            wait(EXERCISE_TIMEOUT * 1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
                Thread.sleep(GAME_TIMEOUT * 1000);
            } catch (InterruptedException e) {
            }

            // punktestaende fuer alle Spieler zuruecksetzen
            for (int i = 0; i < joinedPlayers.size(); i++) {
                Player p = joinedPlayers.get(i);
                p.getScore().setScoreValue(0);
            }
        }
    }
}
