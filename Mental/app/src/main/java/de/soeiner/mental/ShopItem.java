package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 02.04.2016.
 */
public class ShopItem extends JSONObject {

    private int nr; //index
    private String name; //Name
    private int price; //Kosten
    public boolean bought; //schon gekauft ?
    public boolean equipped; //ausgerüstet ?
    private int lvlUnlock; // benötigtes lvl zum Freischalten


    public ShopItem(int n, String na, int p, boolean b, boolean e, int l){
        nr = n;
        name = na;
        price = p;
        bought = b;
        equipped = e;
        lvlUnlock = l;
        try {
            this.put("nr", nr);
            this.put("name", name);
            this.put("price", price);
            this.put("bought", bought);
            this.put("equipped", equipped);
            this.put("lvlUnlock", lvlUnlock);
        } catch (JSONException s) {
            s.printStackTrace();
        }
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
        try {
            this.put("bought", bought);
        } catch (JSONException s) {
            s.printStackTrace();
        }
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
        try {
            this.put("equipped", equipped);
        } catch (JSONException s) {
            s.printStackTrace();
        }
    }

    public int getLvlUnlock() {
        return lvlUnlock;
    }


}
