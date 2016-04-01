package de.soeiner.mental;

import org.json.JSONObject;

/**
 * Created by Malte on 30.03.2016.
 */
public class Shop extends JSONObject {

    private int money;
    private int moneySpent; //müsste noch in den Scorestring eingebaut werden
    String[] titles = {"title1", "title2", "title3"};
    int[] prices =    {100, 500, 1000};
    boolean[] bought = {false, false, false};
    Score score;

    public Shop(Score s){
        score = s;
        calculateMoney();
    }


    public boolean buyTitle(int index) {
        if(prices[index] <= money && !bought[index]){ //wenn der titel noch nicht gekauft wurde und genug geld vorhanden ist
            money -= prices[index];
            moneySpent += prices[index];
            bought[index] = true;
            equipTitle(index);
            return true;
        }
        return false;
    }

    public boolean equipTitle(int index){
        if(bought[index]){
            score.setTitle(titles[index]);
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
