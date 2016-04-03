package de.soeiner.mental;

import java.util.ArrayList;

/**
 * Created by Malte on 02.04.2016.
 */
public class ArenaGame extends Game{

    private ArrayList<Player> activePlayers = new ArrayList<Player>();
    private ArrayList<Player> spectators = new ArrayList<Player>();
    private int bet;
    private int zaehler = 0;

    public ArenaGame(ExerciseCreator exerciseCreator){
        super(exerciseCreator);
    }

    protected String getGameModeString() {
        return "Arena";
    }

    private void agreeOnBet(){ //TODO: einsatz über kommunikation zwischen den beiden spielern bestimmen
        for(int einsatz = 100; einsatz >= 10; einsatz -= 10) {
            if (activePlayers.get(0).getShop().getMoney() >= einsatz && activePlayers.get(1).getShop().getMoney() >= einsatz) {
                bet = einsatz;
            }
        }
    }

    public void run() {
        int index = 0;
        while(joinedPlayers.size() < 2){
            try{Thread.sleep(1000);}catch(Exception e){} //Warte auf genügend Spieler
        }
        for(int i = 0; i<2;i++){
            Player p = joinedPlayers.get(i);
            activePlayers.add(p);
        }
        for(int i = 2; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            spectators.add(p);
        }

        agreeOnBet();
        broadcastMessage("Das Spiel startet mit einem Einsatz von "+bet+"$");


        start:
        while(activePlayers.size() == 2) {
            broadcastExercise();
            exerciseCreator.increaseDifficulty();
            doWaitTimeout(EXERCISE_TIMEOUT); // vllt eine eigene Konstante hierfür?
            zaehler++;

            if(zaehler > 5) {
                if (activePlayers.get(0).getScore().getScoreValue() == activePlayers.get(1).getScore().getScoreValue()) {
                    broadcastMessage("Es gibt einen Gleichstand, das Spiel geht weiter!");
                    continue;
                }
                if (activePlayers.get(0).getScore().getScoreValue() > activePlayers.get(1).getScore().getScoreValue()) {
                    broadcastPlayerWon(activePlayers.get(0).getName(), "Arena");
                    activePlayers.get(0).getShop().addMoney(bet);
                    activePlayers.get(1).getShop().addMoney(-bet);
                } else {
                    broadcastPlayerWon(activePlayers.get(1).getName(), "Arena");
                    activePlayers.get(1).getShop().addMoney(bet);
                    activePlayers.get(0).getShop().addMoney(-bet);
                }
            }
        }
        sendGameStrings();

        try { //Zeit für einen siegerbildschrim mit erster,zweiter,dritter platz ?
            Thread.sleep(GAME_TIMEOUT * 1000);
        } catch (InterruptedException e) {}
        for(int i = 0; i<joinedPlayers.size();i++){
            Player p = joinedPlayers.get(i);
            Score s = p.getScore();
            s.resetScoreValue();
        }
    }

    @Override
    public boolean playerAnswered(Player player, int answer) {

        if(activePlayers.contains(player)){
            if(exerciseCreator.checkAnswer(answer)){
                Score s = player.getScore();
                s.updateScore(10);
                broadcastMessage(player.getName()+" hat die "+zaehler+". Runde für sich entschieden!");
                notify();
            }
        }

        return false;
    }


    public void leave(Player p) {
        joinedPlayers.remove(p);
        if(activePlayers.contains(p)){
            activePlayers.remove(p);
        }
        if(spectators.contains(p)){
            spectators.remove(p);
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
