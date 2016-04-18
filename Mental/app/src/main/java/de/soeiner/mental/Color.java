package de.soeiner.mental;

/**
 * Created by Malte on 17.04.2016.
 */
public class Color extends ShopItem {

    //ich plane, dass man sich später beliebig viel von rot grün und blau kaufen kann und sich somit seine eigene Farbe mischen kann

    String rgb;
    String black = "#000000";
    public Color(Shop s, int n, String na, int p, boolean b, boolean e, int l, String r) {
        super(s, n, na, p, b, e, l);
        rgb = r;
    }

    @Override
    public String getType() {
        return "Color";
    }

    @Override
    public boolean equip() {
        if(bought){
            equipSingleItem();
            shop.score.setColor(rgb);
            shop.updateMoney();
            return true;
        }
        return false;
    }

    @Override
    public boolean unEquip() {
        setEquipped(false);
        shop.score.setColor(black);
        return true;
    }
}
