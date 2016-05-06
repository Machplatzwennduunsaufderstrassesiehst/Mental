package de.soeiner.mental.shop;

import de.soeiner.mental.gameFundamentals.Player;
import de.soeiner.mental.gameFundamentals.Score;

/**
 * Created by Malte on 06.05.2016.
 *
 */
public class WheelOfFortune {
    Player player;
    Shop shop;
    Score score;
    int spinsLeft;
    int spinsSpent = 0;
    int PRICE_PER_SPIN = 100;

    public WheelOfFortune(Player p){
        player = p;
        shop = p.getShop();
        score = p.getScore();
        calculateSpinsLeft();
    }

    protected void calculateSpinsLeft(){
        spinsLeft = score.getPlayerLevel()-spinsSpent;
    }

    protected int getSpinsLeft(){
        return spinsLeft;
    }

    public int getSpinsSpent() {
        return spinsSpent;
    }

    public void addSpinsSpent(int plus) {
        spinsSpent += plus;
        calculateSpinsLeft();
    }

    public void addSpin() {
        addSpinsSpent(-1);
    }

    public void substractSpin() {
        addSpinsSpent(1);
    }

    public void setSpinsSpent(int spinsSpent) {
        this.spinsSpent = spinsSpent;
        calculateSpinsLeft();
    }

    public void buySpin(){
        shop.addMoney(-PRICE_PER_SPIN); //zieht das entsprechende Geld ab
        addSpin(); //fügt einen Spin hinzu
    }

    public void spin(){
        substractSpin(); //zieht Spin ab
        /*
        * TODO
        * rad konfigurieren
        * glück je nach bisherigem erfolg einstellen ?
        * ablauf/Verlauf des Spins an Client schicken
        * Ergebnisse mit Shop verrechen -> gewonnenes Geld, Titel uws.
         */
    }
}
/*
*          _________
*      ___|         |___
*    _|                 |_
*   |                     |
*  |                       |
*  |                       |
* |                         |
*  |                       |
*  |_                     _|
*    |___             ___|
*        |___________|
 */
