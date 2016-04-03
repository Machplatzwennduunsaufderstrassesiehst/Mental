package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 30.03.2016.
 */
public class Shop extends JSONObject {

    private int money;
    private int moneySpent; //müsste noch in den Scorestring eingebaut werden
    Score score;
    Player player;
    ShopItem[] shopItemList;

    public Shop(Player p){
        player = p;
        score = player.getScore();
        calculateMoney();
        shopItemList = createShopItemList();
        player.sendShopItemList(getShopItemList()); //zu Beginn einmal senden
    }

    private ShopItem[] createShopItemList(){
        ShopItem item1 = new ShopItem(1, "title1", 100, false, 0);
        ShopItem item2 = new ShopItem(2, "title1", 500, false, 0);
        ShopItem item3 = new ShopItem(3, "title1", 1000, false, 0);

        ShopItem[] s = {item1, item2, item3};
        return s;
    }

    public ShopItem[] getShopItemList(){
        return shopItemList;
    }



    public boolean buyTitle(int index) {
        if(shopItemList[index].getPrice() <= money && !shopItemList[index].getBought() && score.getPlayerLevel() >= shopItemList[index].getLvlUnlock()){ //wenn der titel noch nicht gekauft wurde und genug geld vorhanden ist
            money -= shopItemList[index].getPrice();
            moneySpent += shopItemList[index].getPrice();
            shopItemList[index].setBought(true);
            equipTitle(index);
            player.sendShopItemList(getShopItemList()); //wird bei jeder Änderung neu gesendet
            return true;
        }
        return false;
    }

    public boolean equipTitle(int index){
        if(shopItemList[index].getBought()){
            score.setTitle(shopItemList[index].getName());
            return true;
        }

        return false;
    }

    public void calculateMoney() {
        money = score.getOverallScoreValue() - moneySpent;
    }

    //TODO
    private void loadMoneySpent(){
        //String s = score.getScoreString();
    }

    public void addMoney(int plus){
        this.money += plus;
    }

    public int getMoney(){
        return money;
    }
}
