package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 28.03.2016.
 */
public class Knockout extends Game{

    static String gameMode = "Knockout";
    private ArrayList<Player> activePlayers;// = new ArrayList<Player>();
    private int minPlayers = 2; //mindest Anzahl Spieler

    public Knockout(String name, ExerciseCreator exerciseCreator){
        super(name, new MixedExerciseCreator2(30));
    }

    public void run() {
        int index = 0;
        while(joinedPlayers.size() < minPlayers){
            try{Thread.sleep(100);}catch(Exception e){} //Warte auf genügend Spieler
        }
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            activePlayers.add(p);
        }
        while(activePlayers.size() > 1){
            broadcastExercise();
            exerciseCreator.increaseDifficulty();
            synchronized (this) { // ist angefordert damit man wait oder notify nutzen kann
                try {
                    wait(EXERCISE_TIMEOUT * 100000);
                } catch (InterruptedException e) {
                }
            }
            for(int i = 0; i<activePlayers.size();i++){
                if(activePlayers.get(i).getScore().getScoreValue() <= activePlayers.get(index).getScore().getScoreValue()){
                    index = i;
                }
            }
            broadcastMessage(activePlayers.get(index).getName()+" wurde eleminiert!");
            activePlayers.remove(index);
        }
        broadcastPlayerWon(activePlayers.get(0).getName(), gameMode);
        try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            sendScoreStrings();
            Thread.sleep(GAME_TIMEOUT * 1000);
        } catch (InterruptedException e) {
        }
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
    }

