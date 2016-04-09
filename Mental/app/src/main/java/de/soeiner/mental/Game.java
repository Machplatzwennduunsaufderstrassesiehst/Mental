package de.soeiner.mental;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by malte on 13.02.16.
 */
public class Game implements Runnable {

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
    ExerciseCreator exerciseCreator = null;
    Voting voting;

    public String description = "";
    public Score[] scoreboard = new Score[0];
    public ArrayList<Player> joinedPlayers;
    public ArrayList<Player> activePlayers;
    public ArrayList<Player> spectators;

    public int EXERCISE_TIMEOUT = 30;
    public int GAME_TIMEOUT = 2; //für pause zwischen den spielen mit siegerbildschirm



    public Game() {
        games.add(this);
        joinedPlayers = new ArrayList<Player>();
        activePlayers = new ArrayList<Player>();
        spectators = new ArrayList<Player>();
        exerciseCreator = new SimpleMultExerciseCreator();
        voting = new Voting(this);
        gameMode = new ClassicGameMode(this);
        Thread t = new Thread(this);
        t.start();
    }


    protected String getGameModeString(){
        return gameMode.getGameModeString();
    }

    public String getName() {
        return "Mental Gamerino";//exerciseCreator.getName() + " - " + getGameModeString();
    }

    public String getDescription() {
        return description;
    }

    public Score[] getScoreboard() {return scoreboard;}

    public void destroy() {
        games.remove(this);
    }

    /*
    public void updateScoreBoardSize() {
        scoreboard = new Score[activePlayers.size()];
        for (int i = 0; i < activePlayers.size(); i++) {
            Score s = activePlayers.get(i).getScore();
            scoreboard[i] = s;
        }
        broadcastScoreboard();
    }
    */

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
        updateScoreBoardSize();
        broadcastScoreboard();
        if(!gameMode.gameIsRunning) {
            broadcastShowScoreBoard();
            voting.callVote();
        }
        broadcastMessage(p.getName() + " ist beigetreten.");
    }

    public void removePlayer(Player p) {
        joinedPlayers.remove(p);
        if(activePlayers.contains(p)){
            activePlayers.remove(p);
        }
        if(spectators.contains(p)){
            spectators.remove(p);
        }
        updateScoreBoardSize();
        voting.checkForCompletion();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
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

    public boolean playerAnswered(Player player, int answer){
        return gameMode.playerAnswered(player, answer);
    }

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
        broadcastShowScoreBoard();
    }

    public void broadcastShowScoreBoard(){
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_SHOW_SCOREBOARD);
            p.makePushRequest(new PushRequest(j));
        }
        broadcastScoreboard();
    }

    private void roundTimeout(){
        sendGameStrings();
        try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            Thread.sleep(GAME_TIMEOUT * 1000); //VOTE_TIMEOUT
        } catch (InterruptedException e) {}

        // punktestaende fuer alle Spieler zuruecksetzen
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.getScore().resetScoreValue(); //reset
        }
        activePlayers = new ArrayList<Player>();
    }
    private void broadcastAndIncrease(){
        broadcastExercise();
        exerciseCreator.increaseDifficulty();
        doWaitTimeout(EXERCISE_TIMEOUT); // das senden der restzeit sowie das warten selbst ist jetzt von broadcastExercise nach hier übertragen
    }

    Object voteLock = new Object();

    @Override
    public void run() {
        //gameMode.waitForPlayers();
        start:
        while(true) {
            broadcastShowScoreBoard();
            roundTimeout();
            voting.createGameModeSuggestions();
            synchronized (voteLock) {
                try {
                    voteLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //gameMode.waitForPlayers();
            exerciseCreator.resetDifficulty();
            gameMode.prepareGame();

            while (gameMode.getGameIsRunning()) {
                if (joinedPlayers.size() == 0) { //wenn keine spieler mehr da sind
                    continue start;
                } else {
                    broadcastAndIncrease();
                    gameMode.loop();
                }

            }
        }
    }
}
