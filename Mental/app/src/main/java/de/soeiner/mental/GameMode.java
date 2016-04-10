package de.soeiner.mental;

/**
 * Created by Malte on 07.04.2016.
 */
public abstract class GameMode {
    boolean gameIsRunning;
    int minPlayers = 2;
    Game game;
    public void waitForPlayers() {
        while(game.joinedPlayers.size() < minPlayers){
            try{Thread.sleep(1000);}catch(Exception e){} //Warte auf genügend Spieler
        }
    }
    public abstract void loop();
    public void prepareGame(){
        resetGameMode();
    }
    public boolean getGameIsRunning(){ return gameIsRunning; }
    public abstract boolean playerAnswered(Player player, int answer);
    public abstract String getGameModeString();

    public GameMode(Game g){
        game = g;
    }

    public void resetGameMode(){
        gameIsRunning = true;
    }

}
