package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 30.03.2016.
 */
public class Shop{

    protected int money;
    protected int moneySpent; //müsste noch in den shopString eingebaut werden
    Score score;
    Player player;
    ShopItem[] shopItemList;

    public Shop(Player p){
        player = p;
        score = player.getScore();
        calculateMoney();
        shopItemList = createShopItemList();
    }

    private ShopItem[] createShopItemList(){
        ShopItem item1 = new Title(this, 1, "uninstall pls", 100, false, false, 0);
        ShopItem item2 = new Title(this, 2, "scrub", 500, false, false, 1);
        ShopItem item3 = new Title(this, 3, "big noob", 1000, false, false, 1);
        ShopItem item4 = new Title(this, 4, "Asiate", 2000, false, false, 2);
        ShopItem item5 = new Title(this, 5, "Global Elite", 2500, false, false, 4);
        ShopItem item6 = new Title(this, 6, "Marco die Schlange", 5000, false, false, 8);
        ShopItem item7 = new Title(this, 7, "Marten", 100000, false, false, 16);
        ShopItem item8 = new Color(this, 8, "Rot", 500, false, false, 4, "#ff0033");
        ShopItem item9 = new Color(this, 9, "Grün", 500, false, false, 4, "#40ff00");
        ShopItem item10 = new Color(this, 10, "Blau", 500, false, false, 4, "#0900ff");
        ShopItem item11 = new Color(this, 11, "Türkise", 3000, false, false, 8, "#00eeff");
        ShopItem item12 = new Color(this, 12, "Gold", 10000, false, false, 16, "#dbd000");

        ShopItem[] s = {item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12};
        return s;
    }

    private ShopItem[] createSpecialShopItemList(){
        ShopItem item1 = new Title(this, 1, "eine Seele", 100, false, false, 0);
        ShopItem item2 = new Title(this, 2, "zwei Seelen", 500, false, false, 0);
        ShopItem item3 = new Title(this, 3, "eine handvoll Seelen", 1000, false, false, 0);
        ShopItem item4 = new Title(this, 4, "eine asiatische Seele", 2000, false, false, 2);
        ShopItem item5 = new Title(this, 5, "ein Paket Seelen", 2500, false, false, 4);
        ShopItem item6 = new Title(this, 6, "Marco die Schlange ihm seine Seele", 5000, false, false, 8);
        ShopItem item7 = new Title(this, 7, "Martens Seele", 1000000, false, false, 20);
        ShopItem item8 = new Color(this, 8, "seelenrot", 100, false, false, 4, "#ff0033");
        ShopItem item9 = new Color(this, 9, "seelengrün", 100, false, false, 4, "#40ff00");
        ShopItem item10 = new Color(this, 10, "seelenblau", 100, false, false, 4, "#0900ff");
        ShopItem item11 = new Color(this, 11, "seelentürkise", 10000, false, false, 8, "#00eeff");
        ShopItem item12 = new Color(this, 12, "seelengold", 100000, false, false, 16, "#dbd000");

        ShopItem[] s = {item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12};
        return s;
    }

    public ShopItem[] getShopItemList(){
        return shopItemList;
    }

    public boolean buyItem(int index) {
        return shopItemList[index].buy();
    }

    public boolean equipItem(int index){
        return shopItemList[index].equip();
    }

    public void calculateMoney() {
        money = score.getOverallScoreValue() - moneySpent;
    }

    public void addMoney(int plus){
        this.moneySpent -= plus;
        calculateMoney();
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

    public void updateMoney(){
        calculateMoney();
        player.getScore().setMoney(getMoney());

        player.sendGameString();
    }

    public void loadShopString(String hexShopString) {
        if(hexShopString.length()< 4){return;}
        int parsedResult = (int) Long.parseLong(hexShopString, 16);
        String shopString = Integer.toString(parsedResult);
        String itemsBought = "";
        System.out.println("loadShopString");
        if (checkShopString(shopString)) {
            shopString = shopString.substring(0, shopString.length() - 1);
            itemsBought = shopString.substring(0, 8); // index out of bounds TODO
            itemsBought = Integer.toBinaryString(Integer.parseInt(itemsBought));
            while(itemsBought.length() < 8){
                itemsBought = "0"+itemsBought;
            }
            for(int i = 0;i<itemsBought.length();i++){
                if(itemsBought.charAt(i) == '1'){
                    getItemById(i).buy();
                }
                if(itemsBought.charAt(i) == '2'){
                    getItemById(i).buy();
                    getItemById(i).equip();
                }
            }
            setMoneySpent(Integer.parseInt(shopString.substring(8, shopString.length())));
            updateMoney(); //evtl ohne sendGamestring besser
            score.setTitle(shopItemList[Character.getNumericValue(shopString.charAt(shopString.length()-1))].getName());

        }
        if(player.getName().contains("exlo") || player.getName().contains("ppel")){
            shopItemList = createSpecialShopItemList();
        }
        if(player.getName().contains("marc")){
            shopItemList[5].setBought(true);
            shopItemList[5].setEquipped(true);
            buyItem(5);
            equipItem(5);
        }
    }


    public String getShopString(){ //die ersten acht zeichen geben die gekauften Gegenstände an, die darauf folgenden, das ausgegebene Geld
        //Danach Umwandlung in hexadezimal

        int moneyspent = getMoneySpent();
        int itemEquipped = 0;
        String itemsBought = "";
	ShopItem[] itemList = sortCopyById();

        for(int i = 0; i<itemList.length;i++){
            if(itemList[i].getBought() && itemList[i].equipped){
                itemsBought += '2';
            }else if(itemList[i].getBought()){
                itemsBought += '1';
            }else{
                itemsBought += '0';
            }
        }
        int dezInt = Integer.parseInt(itemsBought, 3);
        String dezString = ""+dezInt;
        while(dezString.length() < 8){
            dezString = "0"+dezString;
        }
        if(dezString.length() > 8){ throw new RuntimeException("das wird niemals in der konsole auftauchen xD");}
        itemsBought = dezString;

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
        shopString = (shopString.length() == 0 ? "0" : shopString);
        while (shopString.length() > 1 && shopString.charAt(0) == '0') shopString = shopString.substring(1); // vorangehende Nullen entfernen
        long convert = Long.parseLong(shopString);
        return Long.toHexString(convert);
    }
    ShopItem temp;

    private ShopItem[] sortCopyById(){  //gibt eine sortierte Kopie der ShopitemList zurück
	ShopItem[] itemList = shopItemList.clone();
	for(int i = 0; i<itemList.length;i++){
	    for(int j = 1; j<itemList.length;j++){
		if(itemList[j-1].getId() < itemList[j].getId()){
		    temp = itemList[j];
		    itemList[j] = itemList[j-1];
		    itemList[j-1] = temp;
		}
	    }
	}
	return itemList;
    }

    private ShopItem getItemById(int id){
	id--; //weil die id zum anzeigen bei 1 anfaengt 
	for(int i = 0; i < shopItemList.length;i++){
	    if(shopItemList[i].getId() == id){
		return shopItemList[i];
	    }
	}
	throw new RuntimeException("cant get item for given id");
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
