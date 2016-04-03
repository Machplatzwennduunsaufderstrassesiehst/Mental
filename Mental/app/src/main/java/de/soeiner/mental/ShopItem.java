package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 02.04.2016.
 */
public class ShopItem extends JSONObject {

    private int nr; //index
    private String name; //Name
    private int price; //Kosten
    private boolean bought; //schon gekauft ?
    private int lvlUnlock; // benötigtes lvl zum Freischalten


    public ShopItem(int n, String na, int p, boolean b, int l){
        nr = n;
        name = na;
        price = p;
        bought = b;
        lvlUnlock = l;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getNr() {
        return nr;
    }
    public boolean getBought(){
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int getLvlUnlock() {
        return lvlUnlock;
    }


}
