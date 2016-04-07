package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 30.03.2016.
 */
public class Shop extends JSONObject {

    private int money;
    private int moneySpent; //müsste noch in den shopString eingebaut werden
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
        ShopItem item1 = new ShopItem(1, "uninstall pls", 100, false, 0);
        ShopItem item2 = new ShopItem(2, "scrub", 500, false, 0);
        ShopItem item3 = new ShopItem(3, "big noob", 1000, false, 0);
        ShopItem item4 = new ShopItem(4, "Asiate", 2000, false, 2);
        ShopItem item5 = new ShopItem(5, "Global Elite", 2500, false, 4);
        ShopItem item6 = new ShopItem(6, "Marco die Schlange", 5000, false, 8);
        ShopItem item7 = new ShopItem(7, "Marten", 100000, false, 16);

        ShopItem[] s = {item1, item2, item3, item4, item5, item6, item7};
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
            //player.sendConfirmation(true); wird schon in equip aufgerufen
            return true;
        }
        player.sendConfirmation(false);
        return false;
    }

    public boolean equipTitle(int index){
        if(shopItemList[index].getBought()){
            score.setTitle(shopItemList[index].getName());
            player.sendConfirmation(true);
            return true;
        }
        player.sendConfirmation(false);
        return false;
    }

    public void calculateMoney() {
        money = score.getOverallScoreValue() - moneySpent;
    }

    public void addMoney(int plus){
        this.money += plus;
    }

    public int getMoney(){
        return money;
    }

    private int getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(int moneySpent) {
        this.moneySpent = moneySpent;
    }

    public void loadShopString(String shopString) {
        String itemsBought = "";
        if (checkShopString(shopString)) {
            shopString = shopString.substring(0, shopString.length() - 1);
            itemsBought = shopString.substring(0, 3);
            itemsBought = Integer.toBinaryString(Integer.parseInt(itemsBought));
            while(itemsBought.length() < 7){
                itemsBought = "0"+itemsBought;
            }
            for(int i = 0;i<itemsBought.length();i++){
                if(itemsBought.charAt(i) == '1'){
                    shopItemList[i].setBought(true);
                }
            }
            setMoneySpent(Integer.parseInt(shopString.substring(3, shopString.length())));
        }
    }


    public String getshopString(){ //die ersten drei zeichen geben die gekauften Gegenstände an, die darauf folgenden, das ausgegebene Geld
        //000 -> 0000000 = nichts gekauft
        //001 -> 0000001 = gegenstand 7 gekauft
        //002 -> 0000010 = gegenstand 6 gekauft
        //003 -> 0000011 = gegenstand 7 und 6 gekauft
        //...
        //127 -> 1111111 = alle gegenstände gekauft

        int moneyspent = getMoneySpent();
        String itemsBought = "";

        for(int i = 0; i<shopItemList.length;i++){
            if(shopItemList[i].getBought()){
                itemsBought += '1';
            }else{
                itemsBought += '0';
            }
        }
        String dez = Integer.parseInt(itemsBought, 2)+"";
        while(dez.length() < 3){
            dez = "0"+dez;
        }
        itemsBought = dez;

        String shopString = itemsBought+moneyspent;
        int k = 0;
        int a = 0;
        int checksum = 0;
        for(int i = 0;i < shopString.length();i++){
            a = Character.getNumericValue(shopString.charAt(i));
            switch(k%4){
                case 0 : checksum += 7*a; break;
                case 1 : checksum += 3*a; break;
                case 2 : checksum += 5*a; break;
                case 3 : checksum += 13*a; break;
            }
        }
        checksum %= 10;
        shopString = itemsBought + moneyspent + checksum;

        return shopString;
    }

    public static boolean checkShopString(String shopString){

        if(shopString == ""){
            return false;
        }

        int k = 0;
        int a = 0;
        int checksum = 0;
        for(int i = 0;i < shopString.length()-1;i++){
            a = Character.getNumericValue(shopString.charAt(i));
            switch(k%4){
                case 0 : checksum += 7*a; break;
                case 1 : checksum += 3*a; break;
                case 2 : checksum += 5*a; break;
                case 3 : checksum += 13*a; break;
            }
        }
        checksum %= 10;
        if(checksum == Character.getNumericValue(shopString.charAt(shopString.length()-1))){
            return true;
        }else{
            return false;
        }
    }
}
