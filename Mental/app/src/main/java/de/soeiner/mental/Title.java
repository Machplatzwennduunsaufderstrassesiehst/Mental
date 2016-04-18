package de.soeiner.mental;

/**
 * Created by Malte on 17.04.2016.
 */
public class Title extends ShopItem {
    public Title(Shop s, int n, String na, int p, boolean b, boolean e, int l) {
        super(s, n, na, p, b, e, l);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean equip() {
        if(bought){
            equipSingleItem();
            shop.score.setTitle(name);
            shop.updateMoney();
            return true;
        }
        return false;
    }

    @Override
    public boolean unEquip() {
        setEquipped(false);
        shop.score.setTitle("no Title");
        return true;
    }
}
