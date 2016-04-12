package de.soeiner.mental;

/**
 * Created by Malte on 09.04.2016.
 */
public class BeatBobGameMode extends GameMode {

    /*ich stelle mir das so vor, dass die aktuelle situation über einen int wert (status) dargestellt wird
       0: ist unentschieden
      -10: Spieler haben verloren
       10: Bob hat verloren
       Anstatt 10 wird ein Wert (health) durch die Anzahl der Spieler ermittelt

       Das Spiel startet bei 0.
       Bob löst alle x (bobSolveTime) sekunden eine Aufgabe, die Spieler müssen gegenhalten
     */

    /* mögliche Erweiterungen
     *   für jeden Statuspunkt den die spieler holen, bekommen sie einen punkt (als Währung)
      *  von den gewonnen punkten können sie sich dann kleine boni kaufen z.B Bob vorrübergehend ausschalten
      *  diese boni kann man im shop upgraden (10% bonus (z.B auf cooldown oder Bob vorrübergehend ausschalten) für jeden spieler der das Upgrade gekauft hat und im Spiel ist)
      *  ein Spieler kann zu beginn des gameModes zum Commander gewählt werden. Dieser muss dann keine Aufgaben lösen sondern managt die upgrades und boni vom erspielten Geld.
      *  (doppelter Punkte boost für Spieler, Zeit verlangsamen, Bob ausschalten, Bob angreifen (+5 status), bot kaufen der auch in regelmäßigem Abstand aufgaben löst, bot kaufen der Punkte produziert
      * */

    int bobSolveTime;
    int playerHeadstart;
    int health;
    int status = 0;
    Thread t;
    Object bobLock = new Object();

    public BeatBobGameMode(Game g){
        super(g);
    }

    public void prepareGame() {
        super.prepareGame();
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
        }
        if(game.activePlayers.size() != 0) {
            bobSolveTime = 10 / game.activePlayers.size(); //angenommen ein Spieler benötigt 10 sekunden um eine Aufgabe zu lösen
            health = 5 * game.activePlayers.size();
            playerHeadstart = 5;
        }else{
            gameIsRunning = false;
        }
    }

    /*

    public void loop() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    //synchronized (bobLock) {
                    wait(playerHeadstart * 1000);
                    while(gameIsRunning){
                        wait(bobSolveTime * 1000);
                        updateStatus(-1);
                    }
                    //}
                }catch(Exception e){}
            }
        };
        t = new Thread(r);
        t.start();

        while(gameIsRunning){

        }
    }

    */

    public void loop() {
        while(gameIsRunning){
            try {
                wait(playerHeadstart * 1000);
                while(gameIsRunning){
                    wait(bobSolveTime * 1000);
                    updateStatus(-1);
                }
            }catch(Exception e){}
        }
    }

    public boolean playerAnswered(Player player, int answer) {

        Score s = player.getScore();
        synchronized (answerLock) {
            if (player.exerciseCreator.checkAnswer(answer)) {
                s.updateScore(5);
                player.exerciseCreator.createNext();
                player.sendExercise(player.exerciseCreator.getExerciseString());
                updateStatus(1);
                game.broadcastMessage(player.getName() + " hat einen Punkt gewonnen");
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
}
