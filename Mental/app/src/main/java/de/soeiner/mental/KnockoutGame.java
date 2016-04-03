package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 28.03.2016.
 */
public class KnockoutGame extends Game{

    private ArrayList<Player> activePlayers = new ArrayList<Player>();
    private int minPlayers = 2; //mindest Anzahl Spieler

    public KnockoutGame(ExerciseCreator exerciseCreator){
        super(exerciseCreator);
    }

    @Override
    protected String getGameModeString() {
        return "Knockout";
    }

    public void run() {
        int index = 0;
        while(joinedPlayers.size() < minPlayers){
            try{Thread.sleep(1000);}catch(Exception e){} //Warte auf genügend Spieler
        }
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            activePlayers.add(p);
        }
        while(activePlayers.size() > 1){
            broadcastExercise();
            exerciseCreator.increaseDifficulty();
            doWaitTimeout(EXERCISE_TIMEOUT * 100); // vllt eine eigene Konstante hierfür?
            for(int i = 0; i<activePlayers.size();i++){
                if(activePlayers.get(i).getScore().getScoreValue() <= activePlayers.get(index).getScore().getScoreValue()){
                    index = i;
                }
            }
            broadcastMessage(activePlayers.get(index).getName()+" wurde eleminiert!");
            activePlayers.remove(index);
        }
        broadcastPlayerWon(activePlayers.get(0).getName(), "Knockout");
        sendGameStrings();


        try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            Thread.sleep(GAME_TIMEOUT * 1000);
        } catch (InterruptedException e) {}

        for(int i = 0; i<joinedPlayers.size();i++){ //Spieler kriegen am Ende Scorepunkte
            Player p = joinedPlayers.get(i);
            Score s = p.getScore();
            s.updateScore(s.getScoreValue()*20);
            s.resetScoreValue();
        }
        activePlayers = new ArrayList<Player>(); // die Liste der aktiven Spieler zurücksetzen
    }

    public boolean playerAnswered(Player player, int answer) {

        boolean allFinishedButOne = false;
        int z = 0;
        Score s = player.getScore();
        synchronized (this) {
            if(!player.finished) {
                if (exerciseCreator.checkAnswer(answer)) {
                    s.updateScore(1); //score gibt bei knockout die überlebten runden wieder
                    broadcastMessage(player.getName()+" hat die Aufgabe als "+(getRank()+1)+". gelöst!");
                    player.finished = true;
                    for (int i = 0; i < activePlayers.size(); i++) {
                        Player p = joinedPlayers.get(i);
                        if (p.finished) {
                            z++;
                        }
                    }
                    if(activePlayers.size() - z == 1){
                        allFinishedButOne = true;
                    }
                    if (allFinishedButOne) {
                        notify(); // beendet das wait in loop() vorzeitig wenn alle fertig sind
                    }
                    broadcastScoreboard();
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

    public void leave(Player p) {
        joinedPlayers.remove(p);
        if(activePlayers.contains(p)){
            activePlayers.remove(p);
        }
        updateScoreBoardSize();
        broadcastMessage(p.getName() + " hat das Spiel verlassen.");
    }

    public void updateScoreBoardSize() {
        scoreboard = new Score[activePlayers.size()];
        for (int i = 0; i < activePlayers.size(); i++) {
            Score s = activePlayers.get(i).getScore();
            scoreboard[i] = s;
        }
        broadcastScoreboard();
    }
}

