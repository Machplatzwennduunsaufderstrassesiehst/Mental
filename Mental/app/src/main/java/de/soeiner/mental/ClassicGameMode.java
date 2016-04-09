package de.soeiner.mental;

/**
 * Created by Malte on 28.03.2016.
 */

class ClassicGameMode extends GameMode{

    public ClassicGameMode(Game g){
        super(g);
        minPlayers = 1;
    }

    public String getGameModeString() {
        return "Classic";
    }

    public void prepareGame() {
        super.prepareGame();
        for(int i = 0; i<game.joinedPlayers.size();i++){
            Player p = game.joinedPlayers.get(i);
            game.activePlayers.add(p);
        }
    }

    public void loop() {}

    public boolean playerAnswered(Player player, int answer) {
            boolean allFinished = true;
            Score s = player.getScore();
            synchronized (game) {
                if(!player.finished) { // sonst kann man 2x mal punkte absahnen ;; spieler kriegt jetzt keine punkte mehr abgezogen für doppeltes antworten
                    if (game.exerciseCreator.checkAnswer(answer)) {
                        s.updateScore(game.getPoints());
                        game.broadcastMessage(player.getName()+" hat die Aufgabe als "+(game.getRank()+1)+". gelöst!");
                        if (s.getScoreValue() > 100) {
                            gameIsRunning = false; // schleife in run() beenden
                            game.broadcastPlayerWon(player.getName(), getGameModeString());
                            game.notify(); // hat einer gewonnen, muss das wait im game loop ebenfalls beendet werden.
                        }
                        player.finished = true;
                        for (int i = 0; i < game.activePlayers.size(); i++) {
                            Player p = game.activePlayers.get(i);
                            if (!p.finished) {
                                allFinished = false;
                            }
                        }
                        if (allFinished) {
                                game.notify();
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
                return true;
            }
        }
    }

