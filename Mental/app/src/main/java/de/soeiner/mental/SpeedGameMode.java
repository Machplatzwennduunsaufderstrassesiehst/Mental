package de.soeiner.mental;

/**
 * Created by Malte on 09.04.2016.
 */
public class SpeedGameMode extends GameMode { //Es empfiehlt sich vll. diesen Modus mit dem kleinem 1x1 zu paaren

    private int EXERCISE_TIMEOUT_OLD;
    private int EXERCISE_TIMEOUT_NEW;

    public SpeedGameMode(Game g){
        super(g);
        minPlayers = 1;
        EXERCISE_TIMEOUT_NEW = 5;
    }

    public String getGameModeString() {
        return "Speed";
    }

    public void prepareGame() {
        super.prepareGame();
        EXERCISE_TIMEOUT_OLD = 30;//game.EXERCISE_TIMEOUT;
        game.EXERCISE_TIMEOUT = EXERCISE_TIMEOUT_NEW; //zeit uum bearbeiten der Aufgabe wird geändert
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
    }

    public void loop() {}

    public boolean playerAnswered(Player player, int answer) {
        Score s = player.getScore();
        synchronized (game) {
            if (game.exerciseCreator.checkAnswer(answer)) {
                s.updateScore(game.getPoints());
                game.broadcastMessage(player.getName() + " hat die Aufgabe als 1. gelöst");
                if (s.getScoreValue() >= 100) {
                    gameIsRunning = false; // schleife in run() beenden
                    game.broadcastPlayerWon(player.getName(), getGameModeString());
                    game.EXERCISE_TIMEOUT = EXERCISE_TIMEOUT_OLD; //zeit uum bearbeiten der Aufgabe wird zurückgesetzt
                    game.notify();
                }
                game.notify();
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
}
