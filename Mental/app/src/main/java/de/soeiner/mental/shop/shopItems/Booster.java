package de.soeiner.mental.shop.shopItems;

import de.soeiner.mental.shop.Shop;

/**
 * Created by Malte on 17.04.2016.
 */
public class Booster extends ShopItem {

    public Booster(Shop s, int n, String na, int p, boolean b, boolean e, int l) {
        super(s, n, na, p, b, e, l);
    }

    @Override
    public String getType() {
        return "Booster";
    }

    @Override
    public boolean equip() {
        return true;
    }

    @Override
    public boolean unEquip() {
        return true;
    }
}
