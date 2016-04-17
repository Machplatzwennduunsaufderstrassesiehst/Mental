package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Malte on 02.04.2016.
 */
public abstract class ShopItem extends JSONObject {

    protected int nr; //index
    protected String name; //Name
    protected int price; //Kosten
    public boolean bought; //schon gekauft ?
    public boolean equipped; //ausgerüstet ?
    protected int lvlUnlock; // benötigtes lvl zum Freischalten
    protected Shop shop;


    public ShopItem(Shop sh, int n, String na, int p, boolean b, boolean e, int l){
        shop = sh;
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
            this.put("type", getType());
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
    
    public abstract String getType();
    public boolean buy() {
        if(price <= shop.money && !bought && shop.score.getPlayerLevel() >= lvlUnlock){ //wenn das Item noch nicht gekauft wurde und genug geld vorhanden ist
            shop.money -= price;
            shop.moneySpent += price;
            bought = true;
            shop.updateMoney();
            return true;
        }
        return false;
    }
    public abstract boolean equip();
    public abstract boolean unEquip();
    


}
