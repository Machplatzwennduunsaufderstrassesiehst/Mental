package de.soeiner.mental.gameModes.arithmetics;

import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;

/**
 * Created by Malte on 02.04.2016.
 */
public class ArenaGameMode extends ArithmeticGameMode {

    private int bet;
    private int zaehler = 0;

    public ArenaGameMode(Game g) {
        super(g);
        minPlayers = 2;
    }

    public String getGameModeString() {
        return "Arena";
    }

    public void prepareGame() {
        super.prepareGame();
        if (game.joinedPlayers.size() < 2) {
            gameIsRunning = false;
            game.broadcastMessage("Zu wenig Spieler um Arena zu starten");
        } else {
            for (int i = 0; i < 2; i++) {
                Player p = game.joinedPlayers.get(i);
                game.activePlayers.add(p);
            }
            for (int i = 2; i < game.joinedPlayers.size(); i++) {
                Player p = game.joinedPlayers.get(i);
                game.spectators.add(p);
            }
            agreeOnBet();
            game.broadcastMessage("Das Spiel startet mit einem Einsatz von " + bet + "$");
        }
    }


    public void loop() {
        gameIsRunning = game.activePlayers.size() == 2;
        zaehler++;
        if (zaehler > 5) { //gleichstand nicht möglich
            if (game.activePlayers.get(0).getScore().getScoreValue() > game.activePlayers.get(1).getScore().getScoreValue()) {
                game.broadcastPlayerWon(game.activePlayers.get(0).getName(), "Arena");
                game.activePlayers.get(0).getShop().addMoney(bet);
                game.activePlayers.get(1).getShop().addMoney(-bet);
            } else {
                game.broadcastPlayerWon(game.activePlayers.get(1).getName(), "Arena");
                game.activePlayers.get(1).getShop().addMoney(bet);
                game.activePlayers.get(0).getShop().addMoney(-bet);
            }
            gameIsRunning = false;
            zaehler = 0;
        }
    }

    private void agreeOnBet() { //TODO: einsatz über kommunikation zwischen den beiden spielern bestimmen
        for (int einsatz = 100; einsatz >= 10; einsatz -= 10) {
            if (game.activePlayers.get(0).getShop().getMoney() >= einsatz && game.activePlayers.get(1).getShop().getMoney() >= einsatz) {
                bet = einsatz;
            }
        }
    }

    public boolean playerAnswered(Player player, JSONObject answer) {
        if (game.activePlayers.contains(player)) {
            synchronized (answerLock) {
                if (game.exerciseCreator.checkAnswer(answer)) {
                    Score s = player.getScore();
                    s.updateScore(10);
                    game.broadcastMessage(player.getName() + " hat die " + zaehler + ". Runde für sich entschieden!");
                    answerLock.notify();
                    return true; // damit die GUI dem Benutzer auch mitteilt, dass er richtig liegt
                }
            }
        }
        return false;
    }
}
