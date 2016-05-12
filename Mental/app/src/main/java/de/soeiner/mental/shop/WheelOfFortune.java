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
    private int PRICE_PER_SPIN = 100;
    private int SLICES = 8;
    private int angle = 0;
    int[] slicePrizes = {-1, 50, 500, 0, 200, 100, 150, 0};

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

    public boolean buySpin(){
        if(shop.getMoney() >= PRICE_PER_SPIN){ //wenn genug Geld vorhanden
            shop.addMoney(-PRICE_PER_SPIN); //zieht das entsprechende Geld ab
            addSpin(); //fügt einen Spin hinzu
            return true;
        }
        return false;
    }

    public boolean spin(){
        substractSpin(); //zieht Spin ab
        int rounds = (int) ((double) (angle)/360.0);
        int degrees = angle%360;
        int prize = SLICES - 1 - (int) (double) (((double) (degrees)) / (360.0 / ((double) (SLICES)))); //berechnung des preises
        if(slicePrizes[prize] == -1){ //preis anrechnen
            addSpin();
        }else{
            player.getShop().addMoney(slicePrizes[prize]);
        }
        player.sendGameString();
        /*
        * TODO
        * rad konfigurieren
        * glück je nach bisherigem erfolg einstellen ?
        * ablauf/Verlauf des Spins an Client schicken
        * Ergebnisse mit Shop verrechen -> gewonnenes Geld, Titel uws.
         */
        return true;
    }
    public int calculateAngel(){
        angle = (int) ((Math.random()*750)+720);
        System.out.println("gesendeter angle XXX: "+angle);
        return angle;
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
