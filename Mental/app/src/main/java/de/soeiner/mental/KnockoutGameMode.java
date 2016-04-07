package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 28.03.2016.
 */
public class KnockoutGameMode extends GameMode{

    public KnockoutGameMode(Game g) {
        super(g);
        minPlayers = 2;
    }

    public String getGameModeString() {
        return "Knockout";
    }

    public void prepareGame() {
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
    }


    public void loop() {
        int index = 0;
        for(int i = 1; i<game.activePlayers.size();i++){
            if(game.activePlayers.get(i).getScore().getScoreValue() <= game.activePlayers.get(index).getScore().getScoreValue()){
                index = i;
            }
        }
        game.broadcastMessage(game.activePlayers.get(index).getName() + " wurde eleminiert!");
        game.activePlayers.remove(index);
        gameIsRunning = game.activePlayers.size() > 1;
        if(game.activePlayers.size() == 1){
            game.broadcastPlayerWon(game.activePlayers.get(0).getName(), "Knockout");
            gameIsRunning = false; //eig. nutzlos
        }
    }

    public void run() {


        for(int i = 0; i<game.joinedPlayers.size();i++){ //Spieler kriegen am Ende Scorepunkte
            Player p = game.joinedPlayers.get(i);
            Score s = p.getScore();
            s.updateScore(s.getScoreValue() * 20);
        }
         // die Liste der aktiven Spieler zurücksetzen

    }

    public boolean playerAnswered(Player player, int answer) {

        boolean allFinishedButOne = false;
        int z = 0;
        Score s = player.getScore();
        synchronized (this) {
            if(!player.finished) {
                if (game.exerciseCreator.checkAnswer(answer)) {
                    s.updateScore(1); //score gibt bei knockout die überlebten runden wieder
                    game.broadcastMessage(player.getName()+" hat die Aufgabe als "+(game.getRank()+1)+". gelöst!");
                    player.finished = true;
                    for (int i = 0; i < game.activePlayers.size(); i++) {
                        Player p = game.joinedPlayers.get(i);
                        if (p.finished) {
                            z++;
                        }
                    }
                    if(game.activePlayers.size() - z == 1){
                        allFinishedButOne = true;
                    }
                    if (allFinishedButOne) {
                        notify(); // beendet das wait in loop() vorzeitig wenn alle fertig sind
                    }
                    game.broadcastScoreboard();
                    return true;
                } else {
          //          if (s.getScoreValue() > 0) {
          //            s.updateScore(-1);
          //            broadcastScoreboard();
                    }
                    return false;
                }
            }
            return true;
        }
}
