package de.soeiner.mental;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Malte on 30.03.2016.
 */
public class Shop{

    protected int money;
    protected int moneySpent; //müsste noch in den shopString eingebaut werden
    Score score;
    Player player;
    ShopItem[] shopItemList;
    int[] partition = new int[8]; //8 Plätze

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
        ShopItem item4 = new Title(this, 4, "recruit", 2000, false, false, 2);
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


    private void easterEggs() {
        if (player.getName().contains("exlo") || player.getName().contains("ppel")) {
            shopItemList = createSpecialShopItemList();
        }
        if (player.getName().contains("marc")) {
            shopItemList[5].setBought(true);
            shopItemList[5].setEquipped(true);
            buyItem(5);
            equipItem(5);
        }
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
        //player.sendGameString(); //das ist zu langsam, jedes mal das zu senden
    }

    private void loadPartition(String partitionString){
        for(int i = 0; i<partition.length ; i++){
           partition[i] = ((int) partitionString.charAt(i))-33; //+20 um nicht lesbare asciis zu vermeiden
        }
    }

    private String addPartitionString(String shopString){
        for(int i = 0; i<partition.length ; i++){
            shopString += (char) (partition[i]+33); //+20 um nicht lesbare asciis zu vermeiden
        }
        return shopString;
    }

    private void setPartitionPassage(int passage, int size) {
        partition[passage] = size;
    }

    private void loadItems(String itemsBought) {
        System.out.println("loadItems mit String: "+itemsBought);
        itemsBought = Integer.toString(Integer.parseInt(itemsBought), 3); //Umrechnug ins dreier System
        System.out.println("ins dreier System umrechnen: "+itemsBought);
        while (itemsBought.length() < shopItemList.length) { //nötige nullen werden angehängt (nicht hardgecoded)
            itemsBought = "0" + itemsBought;
        }
        System.out.println("nötige nullen vorne angehängt: "+itemsBought);
        for (int i = 0; i < itemsBought.length(); i++) {
            if (itemsBought.charAt(i) == '1') {
                getItemById(i).setBought(true);
            }
            if (itemsBought.charAt(i) == '2') {
                getItemById(i).setBought(true);
                getItemById(i).equip();
            }
        }
    }

    public void loadShopString(String shopString) {
        System.out.println("Kontrollbit überprüfen: "+shopString);
        if(checkShopString(shopString)) { //Kontrollbit überprüfen
            shopString = shopString.substring(0, shopString.length()-1); //Kontrollbit abschneiden
            System.out.println("Kontrollbit abschneiden: " + shopString);

            loadPartition(shopString.substring(shopString.length() - partition.length, shopString.length())); //partition laden
            System.out.println("partition laden: " + shopString + " --> " + "Shop partition: " + Arrays.toString(partition));
            shopString = shopString.substring(0, shopString.length() - partition.length); // Partition abschneiden
            System.out.println("Partition abschneiden: "+shopString);
            //shopString = Integer.toString((int) Long.parseLong(shopString, 16)); // Umwandlung von hex ins Zehnersystem
            System.out.println("Umwandlung ins Zehnersystem: "+shopString);;
            for (int passage = partition.length - 1; passage >= 0; passage--) { //von hinten angefangen um den String verkleinern zu können
                System.out.println("String tempString =  shopString.substring("+shopString.length()+" - "+partition[passage]+", "+shopString.length()+");");
                String tempString =  shopString.substring(shopString.length() - partition[passage], shopString.length());//aktuell zu behandelnden String wie nach partition vorgesehen isolieren
                System.out.println(passage+". passage: "+tempString);
                shopString = shopString.substring(0, shopString.length() - partition[passage]); //und abschneiden
                System.out.println("Shopstring nachdem tempString abgescnitten wurde: "+shopString);

                //wenn die Passage einen zweck hat die dafür vorgesehene Methode aufrufen
                if (passage == 7) {}
                if (passage == 6) {}
                if (passage == 5) {}
                if (passage == 4) {}
                if (passage == 3) {}
                if (passage == 2) {}
                if (passage == 1) {
                    System.out.println("setMoneySpent(Integer.parseInt("+tempString+")");
                    setMoneySpent(Integer.parseInt(tempString));
                }
                if (passage == 0) {
                    System.out.println("loadItems("+tempString+")");
                    loadItems(tempString);
                }
            }
            updateMoney(); //initialisierung des Geldes mit den gewonnenen Informationen
            easterEggs(); //Spielereien
        }
    }


    public String getShopString(){

        String shopString = "";

        for (int passage = 0; passage < partition.length; passage++) { //von vorne angefangen um zum String hinzufügen zu können
            int length = 0; //länge der einzelnen Passagen

            //wenn die Passage einen zweck hat die dafür vorgesehene Methode aufrufen und um den entsprechenden String erweitern
            if (passage == 0) {
                length = shopString.length(); //vorherige länge speichern
                shopString = addItemString(shopString); //passage hinzufügen
                length = shopString.length() - length; //differenz ermitteln
            }
            if (passage == 1) {
                length = shopString.length(); //vorherige länge speichern
                shopString = addMoneySpent(shopString); //passage hinzufügen
                length = shopString.length() - length; //differenz ermitteln
            }
            if (passage == 2) {}
            if (passage == 3) {}
            if (passage == 4) {}
            if (passage == 5) {}
            if (passage == 6) {}
            if (passage == 7) {}

            setPartitionPassage(passage, length); //länge in Partition reservieren
        }
        System.out.println("getShopString ohne partition in dezimal: "+shopString);
        //shopString = Long.toHexString(Long.parseLong(shopString)); //Umwandlung in Hexadezimal -- GEÄNDERT ZU LONG
        System.out.println("getShopString ohne partition in hexadezimal: "+shopString);
        shopString = addPartitionString(shopString); //anhängen der Partition
        System.out.println("getShopString mit partition in hexadezimal: "+shopString);
        shopString += calculateCheckBit(shopString); //Kontrollbit anhängen
        System.out.println("ergebnis von getShopString(): "+shopString);
        return shopString;
    }

    private String addItemString(String shopString){
        System.out.println("addItemsString()");
        String itemsBought = "";
        ShopItem[] itemList = sortCopyById(); //nach id sortierte liste der Items
        System.out.println(Arrays.toString(itemList));

        for(int i = 0; i<itemList.length;i++){
            System.out.println("i = "+i+", itemsbought: "+itemsBought);
            if(itemList[i].getBought() && itemList[i].equipped){
                itemsBought += '2';
            }else if(itemList[i].getBought()){
                itemsBought += '1';
            }else{
                itemsBought += '0';
            }
        }
        System.out.println("fertiger itemsBoughtString: "+itemsBought);
        itemsBought = Integer.toString(Integer.parseInt(itemsBought, 3));
        System.out.println("als dezimalzahl: "+itemsBought);
        /*while(itemsBought.length() < shopItemList.length){
            itemsBought = "0"+itemsBought; //nullen anhängen
        }*/
        return shopString+itemsBought; //itemString anhängen und zurückgeben
    }

    private String addMoneySpent(String shopString){
        return shopString+Integer.toString(moneySpent);
    }


    private ShopItem[] sortCopyById(){  //gibt eine sortierte Kopie der ShopitemList zurück
        ShopItem temp;
	    ShopItem[] itemList = shopItemList.clone();
	    for(int i = 0; i<itemList.length;i++){
	        for(int j = 1; j<itemList.length;j++){
		    if(itemList[j-1].getId() > itemList[j].getId()){
		        temp = itemList[j];
		        itemList[j] = itemList[j-1];
		        itemList[j-1] = temp;
		    }
	        }
	    }
	    return itemList;
    }

    private ShopItem getItemById(int id){
	 id++;//weil die id zum anzeigen bei 1 anfaengt
	for(int i = 0; i < shopItemList.length;i++){
	    if(shopItemList[i].getId() == id){
            System.out.println("das "+i+"te item hat die passende id = "+id);
            return shopItemList[i];
	    }
	}
	throw new RuntimeException("cant get item for given id: "+id);
    }

    public boolean checkShopString(String shopString){
        if(shopString == ""){ return false; }
        if(Character.getNumericValue(shopString.charAt(shopString.length()-1)) == calculateCheckBit(shopString.substring(0, shopString.length()-1))){
            return true;
        }
        return true;
    }

    private int calculateCheckBit(String shopString){
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
        return checksum;
    }
}
