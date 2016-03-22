package de.soeiner.mental;

import org.json.JSONArray;
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

    public static void addGame(Game g) {games.add(g);}
    public static ArrayList<Game> getGames() {
        return games;
    }
    public static JSONArray getGamesJSONArray() {
        try {
            JSONArray jsonGameArray = new JSONArray();
            for (int i = 0; i < games.size(); i++) {
                Game g = games.get(i);
                if (g == null) continue;
                JSONObject jsonGameObject = new JSONObject();
                jsonGameObject.put("game_id", i);
                jsonGameObject.put("name", g.getName());
                jsonGameObject.put("description", g.getDescription());
                jsonGameObject.put("players", new JSONArray(g.getScoreboard()));
                jsonGameArray.put(jsonGameObject);
            }
            return jsonGameArray;
        } catch (Exception e) {e.printStackTrace();}
        return new JSONArray();
    }

    private String name = "";
    private String description = "";
    private ArrayList<Player> joinedPlayers;

    private int EXERCISE_TIMEOUT = 30;
    private int GAME_TIMEOUT = 30; //für pause zwischen den spielen mit siegerbildschirm

    ExerciseCreator exerciseCreator;
    private Score[] scoreboard = new Score[0];
    private Score[] getScoreboard() {return scoreboard;}
    boolean gameIsLive;

    public Game(String name, ExerciseCreator exerciseCreator) {
        games.add(this);
        this.name = name;
        this.exerciseCreator = exerciseCreator;
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
        Score temp;
        for(int i = 0; i < scoreboard.length;i++){ //aufsteigendes Sortieren nach ScoreValue
            for(int j = 1; j < (scoreboard.length - i); j++){
                if(scoreboard[j-1].getScoreValue() < scoreboard[j].getScoreValue()){
                    temp = scoreboard[j];
                    scoreboard[j] = scoreboard[j-1];
                    scoreboard[j-1] = temp;
                }
            }
        }
        for (Player p : joinedPlayers) {
            p.sendScoreBoard(scoreboard);
        }
    }

    public void join(Player p) {
        if (!joinedPlayers.contains(p)) {
            joinedPlayers.add(p);
        }
       // p.updateScore();
        p.sendExercise(exerciseCreator.getExerciseString());
        updateScoreBoardSize();
        broadcastMessage(p.getName() + " ist beigetreten.");
    }

    public void leave(Player p) {
        joinedPlayers.remove(p);
        updateScoreBoardSize();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
    }

    public void sendScoreStrings() {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendScoreString();
        }
    }

    public void broadcastExercise() {
        exerciseCreator.createNext();
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.finished = false;
            p.sendExercise(exerciseCreator.getExerciseString());
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

    public boolean playerAnswered(Player player, int answer) {
        System.out.println("spieler hat geantwortet");
        boolean allFinished = true;
        Score s = player.getScore();
        synchronized (this) {
            if(!player.finished) { // sonst kann man 2x mal punkte absahnen ;; spieler kriegt jetzt keine punkte mehr abgezogen für doppeltes antworten
                if (exerciseCreator.checkAnswer(answer)) {
                    s.updateScore(getPoints());
                    broadcastMessage(player.getName()+" hat die Aufgabe als "+(getRank()+1)+". gelöst!");
                    if (s.getScoreValue() > 100) {
                        broadcastPlayerWon(player.getName());
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
                    if (s.getScoreValue() > 0) {
                        s.updateScore(-1);
                        broadcastScoreboard();
                    }
                    return false;
                }
            }
            return true;
        }
    }

    private int getPoints(){ //methode berechent punkte fürs lösen einer Aufgabe
        //jenachdem als wievielter der jeweilige spieler die richtige Antwort eraten hat
        int points = exerciseCreator.getDifficulty();
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

    public void broadcastMessage(String message) {

        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_MESSAGE);
            try {
                j.put("message", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }

    public void broadcastPlayerWon(String playerName) { //wird nur aufgerufen wenn Spieler das Spiel gewonnen hat
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
        start:
        while(true) {
            exerciseCreator.resetDifficulty();
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
                    exerciseCreator.increaseDifficulty();
                    synchronized (this) { // ist angefordert damit man wait oder notify nutzen kann
                        try {
                            wait(EXERCISE_TIMEOUT * 1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
                sendScoreStrings();
                Thread.sleep(GAME_TIMEOUT * 1000);
            } catch (InterruptedException e) {
            }

            // punktestaende fuer alle Spieler zuruecksetzen
            for (int i = 0; i < joinedPlayers.size(); i++) {
                Player p = joinedPlayers.get(i);
                p.getScore().resetScoreValue(); //reset
            }
        }
    }
}
