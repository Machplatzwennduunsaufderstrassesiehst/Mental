package de.soeiner.mental.gameFundamentals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;
import de.soeiner.mental.exerciseCreators.TrainMapCreator;
import de.soeiner.mental.gameModes.ClassicGameMode;
import de.soeiner.mental.gameModes.GameMode;
import de.soeiner.mental.gameModes.TrainGameMode;

/**
 * Created by malte on 13.02.16.
 */
public class Game implements Runnable {

    public static ArrayList<Game> games;

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
                jsonGameObject.put("gameId", i);
                jsonGameObject.put("name", g.getName());
                jsonGameObject.put("description", g.getDescription());
                jsonGameObject.put("players", new JSONArray(g.getScoreboard()));
                jsonGameObject.put("gameIsRunning", g.gameMode.gameIsRunning);
                jsonGameArray.put(jsonGameObject);
            }
            return jsonGameArray;
        } catch (Exception e) {e.printStackTrace();}
        return new JSONArray();
    }

    public GameMode gameMode;
    public ExerciseCreator exerciseCreator = null;
    Voting voting;
    Object voteLock = new Object();
    String name;

    public String description = "";
    public Score[] scoreboard = new Score[0];
    public ArrayList<Player> joinedPlayers;
    public ArrayList<Player> activePlayers;
    public ArrayList<Player> spectators;

    public int GAME_TIMEOUT = 0; //für pause zwischen den spielen mit siegerbildschirm


    public Game() {
        games.add(this);
        joinedPlayers = new ArrayList<Player>();
        activePlayers = new ArrayList<Player>();
        spectators = new ArrayList<Player>();
        exerciseCreator = new SimpleMultExerciseCreator();
        gameMode = new ClassicGameMode(this);
        voting = new Voting(this);
        name = "Game";
        Thread t = new Thread(this);
        t.start();
    }

    public void setName(String n){
        name = n;
    }

    public String getGameModeString(){
        return gameMode.getGameModeString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Score[] getScoreboard() {return scoreboard;}

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
        updateScoreBoardSize();
        broadcastScoreboard();
        if(!gameMode.getGameIsRunning()) {
            broadcastShowScoreBoard();
            voting.broadcastSuggestions();
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
        if (!arePlayersInGame()) interruptGame();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
        gameMode.removePlayer(p);
    }

    public boolean arePlayersInGame() {
        return (activePlayers.size() >= 1);
    }

    public void interruptGame() {
        System.out.println("game interrupt");
        gameMode.gameIsRunning = false;
        synchronized (gameMode.answerLock) {
            gameMode.answerLock.notifyAll();
        }
    }

    public void sendGameStrings() {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendGameString();
        }
    }

    public void broadcastExercise() {
        System.out.println("broadcastExercise()");
        exerciseCreator.next();
        for (int i = 0; i < activePlayers.size(); i++) {
            Player p = activePlayers.get(i);
            p.finished = false;
            p.sendExercise(exerciseCreator.getExerciseObject());
        }
    }

    private void broadcastShopItemList(){
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            p.sendShopItemList(p.getShop().getShopItemList());
        }
    }

    /*
    public boolean playerAnswered(Player player, JSONObject answer){
        if(answer.has("value")){
            try {
                return gameMode.playerAnswered(player, answer.getInt("value"));
            }catch(Exception e){}
        }
        return false;
    }
    */

    public int getPoints(){ //methode berechent punkte fürs lösen einer Aufgabe
        //jenachdem als wievielter der jeweilige spieler die richtige Antwort eraten hat
        int points = exerciseCreator.getDifficulty() * 3 / 2; // hab ich bisschen erhöht, da eine Runde ganz schön lange gedauert hat, wenn jeder mal ne Aufgabe löst
        for(int i = 0; i<getRank();i++){
            points = points/2;
        }
        return points;
    }

    public int getRank(){ //methode berechnet wie viele
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

    public void broadcastPlayerWon(String playerName, String gameModeString) { //wird nur aufgerufen wenn Spieler das Spiel gewonnen hat
        //dem scoreboard können nun auch der zweite und dritte platz entnommen werden
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_PLAYER_WON);
            try {
                j.put("playerName", playerName);
                j.put("gameTimeout", GAME_TIMEOUT);
                j.put("gameMode", gameModeString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }

    public void broadcastSendCountdown(int time){
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_COUNTDOWN);
            try{
                j.put("time", time);
            }catch(Exception e){}

            p.makePushRequest(new PushRequest(j));
        }
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void broadcastShowScoreBoard(){
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendGameString();
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_SHOW_SCOREBOARD);
            p.makePushRequest(new PushRequest(j));
        }
        broadcastScoreboard();
    }

    public void broadcastShowExercises(){
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_SHOW_EXERCISES);
            try {
                j.put("exerciseType", exerciseCreator.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            p.makePushRequest(new PushRequest(j));
        }
    }


    private void roundTimeout(){
        /*try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            Thread.sleep(GAME_TIMEOUT * 1000); //VOTE_TIMEOUT
        } catch (InterruptedException e) {} */

        // punktestaende fuer alle Spieler zuruecksetzen
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
        }
        activePlayers = new ArrayList<Player>();
    }


    public void waitForPlayers(int players){
        this.gameMode = new ClassicGameMode(this);
        gameMode.minPlayers = players;
        gameMode.waitForPlayers();
    }

    @Override
    public void run() {
        System.out.println("run()");
        waitForPlayers(1);
        start:
        while(true) {
            broadcastShowScoreBoard();
            sendGameStrings();
            roundTimeout();
            voting.createGameModeSuggestions();
            synchronized (voteLock) {
                try {
                    voteLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            broadcastSendCountdown(3);
            System.out.println("Countdown sent");
            broadcastShowExercises();
            System.out.println("broadcastedShowExercise");
            gameMode.prepareGame();
            System.out.println("game prepared: " + gameMode.getGameModeString());

            while (gameMode.getGameIsRunning()) {
                System.out.println("[Game.run] while-Schleife anfang");
                if (activePlayers.size() == 0) { //wenn keine spieler mehr da sind
                    gameMode.gameIsRunning = false;
                    System.out.println("[Game.run] continue start, no players left");
                    continue start;
                } else {
                    try {
                        System.out.println("[Game.run] gameMode.newExercise()");
                        gameMode.newExercise();
                        System.out.println("[Game.run] gameMode.exerciseTimeout()");
                        gameMode.exerciseTimeout();
                        System.out.println("[Game.run] gameMode.loop()");
                        gameMode.loop();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("FEHLER IN GAMEMODE");
                    }
                }

            }
        }
    }
}
