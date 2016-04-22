package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 09.04.2016.
 */
public class BeatBobGameMode extends GameMode {

    double bobSolveTime;
    double bobStartSolveTime;
    double playerHeadstart;
    double health;
    double status = 0;
    double exercisesSolved = 0;
    double upTime;
    double[] function;

    public BeatBobGameMode(Game g){
        super(g);
    }

    public void prepareGame() {
        super.prepareGame();
        status = 0;
        game.individualExercises = true;
        for(int i = 0; i<game.joinedPlayers.size();i++) {
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
        for (int i = 0; i < game.activePlayers.size(); i++) {
            if(game.exerciseCreator instanceof SimpleMultExerciseCreator) {
                game.activePlayers.get(i).exerciseCreator = new SimpleMultExerciseCreator();
            }else if (game.exerciseCreator instanceof MultExerciseCreator) {
                game.activePlayers.get(i).exerciseCreator = new MultExerciseCreator();
            } else if (game.exerciseCreator instanceof MixedExerciseCreator) {
                game.activePlayers.get(i).exerciseCreator = new MixedExerciseCreator();
            }else{
                game.activePlayers.get(i).exerciseCreator = new SimpleMultExerciseCreator();
            }
            game.activePlayers.get(i).exerciseCreator.setDifficulty(10);
        }
        if(game.activePlayers.size() != 0) {
            bobSolveTime = (game.exerciseCreator.getExpectedSolveTime()) / game.activePlayers.size()+1; //angenommen ein Spieler benötigt 10 sekunden um eine Aufgabe zu lösen
            bobStartSolveTime = bobSolveTime;
            health = 5 * game.activePlayers.size();
            playerHeadstart = game.exerciseCreator.getExpectedSolveTime();
        }else{
            gameIsRunning = false;
        }
        function = calculateSolveTimeFunction();
    }

    public void loop() {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            Player player = game.activePlayers.get(i);
            player.sendExercise(player.exerciseCreator.createNext());
        }
        try {
            Thread.sleep(calculateMilliSeconds(playerHeadstart));
            upTime += playerHeadstart;
            while(gameIsRunning){
                if(game.activePlayers.size() == 0){gameIsRunning = false; }
                for (double i = 0; (i <= bobSolveTime*10) && gameIsRunning;i++) {
                    bobSolveTime = balanceBob();
                    Thread.sleep(100);
                    upTime += 0.1;
                }
                Thread.sleep(calculateMilliSeconds(bobSolveTime -((int) bobSolveTime))); //zeit nach dem Komma
                updateStatus(-1);
            }
        }catch(Exception e){e.printStackTrace();System.out.println("BOB FAILED");}
    }

    public boolean playerAnswered(Player player, JSONObject answer) {
        Score s = player.getScore();
        if (player.exerciseCreator.checkAnswer(answer)) { //TODO muss natürlich noch korigiert werden
            exercisesSolved++;
            s.updateScore(5);
            player.exerciseCreator.createNext();
            player.sendExercise(player.exerciseCreator.getExerciseString());
            updateStatus(1);
            game.broadcastMessage(player.getName() + " hat einen Punkt gewonnen");
            synchronized (answerLock) {
                answerLock.notify();
            }
            game.broadcastScoreboard();
            return true;
        } else {
            if (s.getScoreValue() > 0) {
                s.updateScore(-1);
                game.broadcastScoreboard();
            }
            return false;
        }
    }

    private void updateStatus(int plus){
        status += plus;
        broadcastStatus();
        checkObjective();
    }

    public void checkObjective(){
        synchronized (answerLock) {
            if (status >= health) { //wenn bob tot ist
                giveReward();
                game.individualExercises = false;
                gameIsRunning = false; // schleife in run() beenden
                game.broadcastPlayerWon("die Spieler", getGameModeString());
                answerLock.notify();
            }
            if (status <= -health) { //wenn spieler tot sind
                try { Thread.sleep(3000); }catch(Exception e){}
                game.individualExercises = false;
                gameIsRunning = false; // schleife in run() beenden
                game.broadcastMessage("Bob hat gewonnen");
                game.broadcastPlayerWon("Bob", getGameModeString());
                answerLock.notify();
            }
        }
    }


    public String getGameModeString() {
        return "beatBob";
    }

    private void giveReward(){ //TODO
        game.broadcastMessage("Bob got rekt!");
        for(int i = 0; i<game.activePlayers.size();i++){
            game.activePlayers.get(i).getShop().addMoney(50);
        }
        try {
            Thread.sleep(3000);
        }catch(Exception e){}
    }

    @Override
    public void removePlayer(Player p) {
        function = calculateSolveTimeFunction();
    }

    public void broadcastStatus(){
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            p.sendBeatBobStatus(calculatePercentage());
        }
        //game.broadcastMessage(status+" ; "+bobSolveTime);
    }

    private double calculatePercentage(){
        if(status > 0){
            return status/health; // die anzeige kommt mit 300% nicht so gut an Xd
        }
        if(status < 0){
           return status/health;
        }
        if(status == 0){
            return 0;
        }
        return 0;
    }

    public int getIndex(Player p){ //gibt den index eines spielers in der aktiven liste zurück

        for (int i = 0; i < game.activePlayers.size(); i++) {
            if(game.activePlayers.get(i).equals(p)){
                return i;
            }
        }
        return -1;
    }

    public void exerciseTimeout() {}

    //============================ KI

    private double balanceBob(){
        bobSolveTime = calculateSolveTime();
        return calculateSolveTime();
    }

    private double calculateSolveTime(){
        //System.out.println(solvetimeBaseFunction() +" "+ (-calculateRating() * solvetimeBaseFunction()));
        return solvetimeBaseFunction() + -calculateRating() * solvetimeBaseFunction();
    }

    private double solvetimeBaseFunction(){

        int value = 0;

        for(int i = 0; i<function.length;i++){
            value += function[i]*Math.pow(status, 5-i);
        }
        return value;
    }

    private double[] calculateSolveTimeFunction(){
        double baseTime = bobStartSolveTime;
        double[] f = {0.0, -health, health}; //x
        double[] b = {baseTime, 2 * baseTime, 0.3 * baseTime, 0, 0, 0};
        double[][] A = new double[6][6]; //gleichungen

        for (int i = 0; i < 3; i++) {
            for(double j = 5.0; j >= 0; j--){
                A[i][5-(int)j] = Math.pow(f[i], j);
            }
            System.out.println();
        }

        for (int i = 0; i < 3; i++) {
            for(int j = 5; j > 1; j--){
                A[3+i][5-(int)j] = (j) * Math.pow(f[i], j);
            }
            A[3+i][4] = 1.0;
            A[3+i][5] = 0;
        }
        return GaussElimination.lsolve(A, b);
    }

    private double calculateBaseRep(){
        return exercisesSolved / (upTime * game.activePlayers.size() / game.exerciseCreator.getExpectedSolveTime());
    }

    private double calculateRating(){
        double baseRep = calculateBaseRep();
        if(baseRep > 1){
            if(baseRep%0.5 == 0){ return 0.5;}
            if(baseRep-1.0 > 0.5){ return 0.5;}
            return ((baseRep-1.0)%0.5);}
        if(baseRep == 1){ return 0;}
        if(baseRep < 1){
            if(baseRep%0.5 == 0){ return -0.5;}
            if(1-baseRep > 0.5){ return -0.5;}
            return -((1.0 - baseRep)%0.5);}
        return 0.0;
    }

    private int calculateMilliSeconds(double SolveTime){
        return (int) (1000*SolveTime);
    }
}
