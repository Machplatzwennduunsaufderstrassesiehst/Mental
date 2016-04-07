package de.soeiner.mental;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by malte on 13.02.16.
 */
public abstract class Game implements Runnable {

    protected static ArrayList<Game> games;

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

    GameMode gameMode;
    protected String description = "";
    protected ArrayList<Player> joinedPlayers;
    protected ArrayList<Player> activePlayers;

    protected int EXERCISE_TIMEOUT = 30;
    protected int GAME_TIMEOUT = 20; //für pause zwischen den spielen mit siegerbildschirm

    protected ExerciseCreator exerciseCreator;
    protected Score[] scoreboard = new Score[0];
    protected Score[] getScoreboard() {return scoreboard;}

    public Game(ExerciseCreator exerciseCreator) {
        games.add(this);
        this.exerciseCreator = exerciseCreator;
        joinedPlayers = new ArrayList<Player>();
        activePlayers = new ArrayList<Player>();
        Thread t = new Thread(this);
        t.start();
    }

    // zwingt alle erbenden klassen eine Game mode zu definieren
    protected abstract String getGameModeString();

    public String getName() {
        return exerciseCreator.getName() + " - " + getGameModeString();
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

    public void addPlayer(Player p) {
        for (Game g : Game.games) { // den Spieler aus anderen Spielen gegebenenfalls entfernen
            if (g.joinedPlayers.contains(p)) g.removePlayer(p);
        }
        joinedPlayers.add(p);
        p.sendExercise(exerciseCreator.getExerciseString());
        updateScoreBoardSize();
        broadcastMessage(p.getName() + " ist beigetreten.");
    }

    public void removePlayer(Player p) {
        joinedPlayers.remove(p);
        updateScoreBoardSize();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
        //TODO gameMode.removePlayer(p);
    }

    public void sendGameStrings() {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendGameString();
        }
    }

    public void broadcastExercise() {
        exerciseCreator.createNext();
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.finished = false;
            p.sendExercise(exerciseCreator.getExerciseString());
        }
    }

    private void broadcastShopItemList(){
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            p.sendShopItemList(p.getShop().getShopItemList());
        }
    }

    // ausgelagert timeout senden + wait()
    /**
     * @param timeout ist in sekunden!
     */
    public void doWaitTimeout (int timeout) {
        //der folgende Code schickt allen spielern einen integer (hier 30) um
        // einen countdown starten zu können. Dann wird 30 Sekunden gewartet
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TIME_LEFT);
        try {
            j.put("time", timeout);
            for (int i = 0; i < joinedPlayers.size(); i++) {
                Player p = joinedPlayers.get(i);
                p.makePushRequest(new PushRequest(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            try {
                this.wait(timeout * 1000);
            } catch (InterruptedException e) {}
        }
    }

    public abstract boolean playerAnswered(Player player, int answer);

    protected int getPoints(){ //methode berechent punkte fürs lösen einer Aufgabe
        //jenachdem als wievielter der jeweilige spieler die richtige Antwort eraten hat
        int points = exerciseCreator.getDifficulty() * 3 / 2; // hab ich bisschen erhöht, da eine Runde ganz schön lange gedauert hat, wenn jeder mal ne Aufgabe löst
        for(int i = 0; i<getRank();i++){
            points = points/2;
        }
        return points;
    }

    protected int getRank(){ //methode berechnet wie viele
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

    public void broadcastPlayerWon(String playerName, String gameMode) { //wird nur aufgerufen wenn Spieler das Spiel gewonnen hat
        //dem scoreboard können nun auch der zweite und dritte platz entnommen werden
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);

            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_PLAYER_WON);
            try {
                j.put("playerName", playerName);
                j.put("gameTimeout", GAME_TIMEOUT);
                j.put("gameMode", gameMode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }

    private void roundTimeout(){
        sendGameStrings();
        try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            Thread.sleep(GAME_TIMEOUT * 1000);
        } catch (InterruptedException e) {}

        // punktestaende fuer alle Spieler zuruecksetzen
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.getScore().resetScoreValue(); //reset
        }
    }

    public abstract void run();

    public void callVote(){ //Abstimmung für nächsten gamemode
        for(int i = 0;i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);

        }
    }
}
