package de.soeiner.mental.gameFundamentals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.exerciseCreators.SimpleMultExerciseCreator;
import de.soeiner.mental.arithmetics.gameModes.ClassicGameMode;

/**
 * Created by malte on 13.02.16.
 */
public class Game implements Runnable {

    private boolean running = false;

    public GameMode gameMode;
    public ExerciseCreator exerciseCreator = null;
    Voting voting;
    final Object voteLock = new Object();
    String name;

    public String description = "";
    public Score[] scoreboard = new Score[0];
    public ArrayList<Player> joinedPlayers;
    public ArrayList<Player> activePlayers;
    public ArrayList<Player> spectators;

    public static ArrayList<Game> games;

    static {
        games = new ArrayList<Game>();
    }

    public static void addGame(Game g) {
        games.add(g);
    }

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
                jsonGameObject.put("gameIsRunning", g.gameMode.isRunning());
                jsonGameArray.put(jsonGameObject);
            }
            return jsonGameArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public Game() {
        games.add(this);
        name = "Game";
        joinedPlayers = new ArrayList<Player>();
        activePlayers = new ArrayList<Player>();
        spectators = new ArrayList<Player>();
        exerciseCreator = new SimpleMultExerciseCreator();
        gameMode = new ClassicGameMode(this);
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }

    public void start() {
        running = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() {
        running = false;
        synchronized (voteLock) {
            voteLock.notifyAll();
        }
    }

    public void setName(String n) {
        name = n;
    }

    public String getGameModeString() {
        return gameMode.getName();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Score[] getScoreboard() {
        return scoreboard;
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
        for (int i = 0; i < scoreboard.length; i++) { //aufsteigendes Sortieren nach ScoreValue
            for (int j = 1; j < (scoreboard.length - i); j++) {
                if (scoreboard[j - 1].getScoreValue() < scoreboard[j].getScoreValue()) {
                    temp = scoreboard[j];
                    scoreboard[j] = scoreboard[j - 1];
                    scoreboard[j - 1] = temp;
                }
            }
        }
        for (Player p : joinedPlayers) {
            p.sendScoreBoard(scoreboard);
        }
    }

    public void addPlayer(Player p) {
        for (Game g : Game.getGames()) { // den Spieler aus anderen Spielen gegebenenfalls entfernen
            if (g.joinedPlayers.contains(p)) g.removePlayer(p);
        }
        joinedPlayers.add(p);
        updateScoreBoardSize();
        broadcastScoreboard();
        if (!gameMode.isRunning()) {
            broadcastShowScoreBoard();
            voting.broadcastSuggestions();
        }
        broadcastMessage(p.getName() + " ist beigetreten.");
    }

    public void removePlayer(Player p) {
        joinedPlayers.remove(p);
        if (activePlayers.contains(p)) {
            activePlayers.remove(p);
        }
        if (spectators.contains(p)) {
            spectators.remove(p);
        }
        if(gameMode.isRunning()){ //falls gerade ein game läuft
            if(activePlayers.size() < gameMode.minPlayers){
                gameMode.setRunning(false);
            }
        }
        updateScoreBoardSize();
        voting.checkForCompletion();
        if (!arePlayersInGame()) interrupt();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
        gameMode.removePlayer(p);
    }

    public boolean arePlayersInGame() {
        return (activePlayers.size() >= 1);
    }

    public void interrupt() {
        System.out.println("game interrupt");
        gameMode.setRunning(false);
    }

    public void sendGameStrings() {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendGameString();
        }
    }

    /**
     * does only send the exercise to all players - not create !
     */
    public void broadcastExercise() {
        System.out.println("broadcastExercise()");
        for (int i = 0; i < activePlayers.size(); i++) {
            Player p = activePlayers.get(i);
            p.finished = false;
            p.sendExercise(exerciseCreator.getExerciseObject());
        }
    }

    public void broadcastMessage(String message) {

        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            try {
                JSONObject j = CmdRequest.makeCmd(CmdRequest.MESSAGE);
                j.put("message", message);
                p.makePushRequest(new PushRequest(j));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastSendCountdown(int time) {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            try {
                JSONObject j = CmdRequest.makeCmd(CmdRequest.COUNTDOWN);
                j.put("time", time);
                p.makePushRequest(new PushRequest(j));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void broadcastShowScoreBoard() {
        for (int i = 0; i < joinedPlayers.size(); i++) {
            Player p = joinedPlayers.get(i);
            p.sendGameString();
            try {
                JSONObject j = CmdRequest.makeCmd(CmdRequest.SHOW_SCOREBOARD);
                p.makePushRequest(new PushRequest(j));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        broadcastScoreboard();
    }

    public void waitForPlayers(int players) {
        this.gameMode = new ClassicGameMode(this);
        gameMode.minPlayers = players;
        gameMode.waitForPlayers();
    }

    public void confirm() {
        confirmed++;
    }

    int confirmed = 0;

    private void waitForConfirmations(int maxWaitTimeoutSeconds) {
        int z = 0;
        confirmed = 0;
        while (confirmed < activePlayers.size() && z < maxWaitTimeoutSeconds * 10) {
            try {
                Thread.sleep(100);
                z++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("run()");
        waitForPlayers(1);
        start:
        while (running) {
            broadcastShowScoreBoard();
            sendGameStrings();
            voting.createGameModeSuggestions();
            synchronized (voteLock) {
                try {
                    voteLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            gameMode.openGUIFrame();
            System.out.println("openGUIFrame");
            gameMode.prepareGame();
            System.out.println("game prepared: " + gameMode.getName());
            if (gameMode.needsConfirmation) {
                waitForConfirmations(30);
            }
            while (gameMode.isRunning()) {
                System.out.println("[Game.run] while-Schleife anfang");
                if (activePlayers.size() == 0) { //wenn keine spieler mehr da sind
                    gameMode.setRunning(false);
                    System.out.println("[Game.run] continue start, no players left");
                    continue start;
                } else {
                    try {
                        System.out.println("[Game.run] gameMode.newExercise()");
                        gameMode.newExercise();
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
