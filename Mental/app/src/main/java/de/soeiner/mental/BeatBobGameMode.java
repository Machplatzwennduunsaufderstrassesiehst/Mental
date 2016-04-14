package de.soeiner.mental;

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

    public BeatBobGameMode(Game g){
        super(g);
    }

    public void prepareGame() {
        super.prepareGame();
        status = 0;
        game.individualExercises = true;
        for(int i = 0; i<game.joinedPlayers.size();i++){
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
            bobSolveTime = 4 / game.activePlayers.size()+2; //angenommen ein Spieler benötigt 10 sekunden um eine Aufgabe zu lösen
            bobStartSolveTime = bobSolveTime;
            health = 5 * game.activePlayers.size();
            playerHeadstart = game.exerciseCreator.getExpectedSolveTime();
        }else{
            gameIsRunning = false;
        }
    }

    public void loop() {
        for (int i = 0; i < game.activePlayers.size(); i++) {
            Player player = game.activePlayers.get(i);
            player.sendExercise(player.exerciseCreator.createNext());
        }
        while(getGameIsRunning()){
            try {
                Thread.sleep(calculateMilliSeconds(playerHeadstart));
                upTime += playerHeadstart;
                while(gameIsRunning){
                    Thread.sleep(calculateMilliSeconds(bobSolveTime));
                    upTime += bobSolveTime;
                    updateStatus(-1);
                    balanceBob();
                }
            }catch(Exception e){e.printStackTrace();}
        }
    }

    public boolean playerAnswered(Player player, int answer) {
            Score s = player.getScore();
        if (player.exerciseCreator.checkAnswer(answer) || true) { //TODO muss natürlich noch korigiert werden
            exercisesSolved++;
            s.updateScore(5);
            player.exerciseCreator.createNext();
            player.sendExercise(player.exerciseCreator.getExerciseString());
            updateStatus(1);
            //game.broadcastMessage(player.getName() + " hat einen Punkt gewonnen");
            answerLock.notify();
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
        if (status >= health) { //wenn bob tot ist
            game.individualExercises = false;
            gameIsRunning = false; // schleife in run() beenden
            game.broadcastPlayerWon("die Spieler", getGameModeString());
            answerLock.notify();
        }
        if(status <= -health){ //wenn spieler tot sind
            game.individualExercises = false;
            gameIsRunning = false; // schleife in run() beenden
            game.broadcastMessage("Bob hat gewonnen");
            game.broadcastPlayerWon("Bob", getGameModeString());
            answerLock.notify();
        }
    }


    public String getGameModeString() {
        return "beatBob";
    }

    public void broadcastStatus(){
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            p.sendStatus(status);
        }
        game.broadcastMessage(status+" ; "+bobSolveTime);
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

    private void balanceBob(){
        bobSolveTime = calculateSolveTime();
    }

    private double calculateSolveTime(){
        return solvetimeBaseFunction() + (calculateRating() % 0.5) * solvetimeBaseFunction();
    }

    private double solvetimeBaseFunction(){

        double baseTime = 4;
        double[] f = {0.0, -2, 2}; //x
        double[] b = {baseTime, 2.5 * baseTime, 0.25 * baseTime, 0, 0, 0};
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
        double[] x = GaussElimination.lsolve(A, b);
        int value = 0;

        for(int i = 0; i<x.length;i++){
            value += x[i]*Math.pow(status, 5-i);
        }
        return value;
    }

    private double calculateBaseRep(){
        return exercisesSolved / (upTime / game.exerciseCreator.getExpectedSolveTime());
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
