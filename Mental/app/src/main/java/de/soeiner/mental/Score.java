package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 16.02.16.
 */
public class Score extends JSONObject{

    public Score(String playerName, int score) {
        try {
            put("playerName", playerName);
            put("scoreValue", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerName(String playerName) {
        this.remove("playerName");
        try {
            this.put("playerName", playerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getScoreValue() {
        try {
            return this.getInt("scoreValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setScoreValue(int scoreValue) {
        this.remove("scoreValue");
        try {
            this.put("scoreValue", scoreValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getScoreString(){
        int score = getScoreValue();
        if(score == 0){
            return "git gud nub";
        }
        String scoreString = Integer.toString(score);
        int k = 0;
        int a = 0;
        int kontrollbit = 0;
        for(int i = 0;i < scoreString.length();i++){
            a = Character.getNumericValue(scoreString.charAt(i));
            switch(k%4){
                case 0 : kontrollbit += 7*a; break;
                case 1 : kontrollbit += 3*a; break;
                case 2 : kontrollbit += 5*a; break;
                case 3 : kontrollbit += 13*a; break;
            }
        }
        kontrollbit %= 10;
        scoreString = score+""+kontrollbit;
//		int Value = Integer.parseInt(scoreString);
//		System.out.println(Value);

        return scoreString;
    }

    public boolean checkScoreString(String scoreString){

        int k = 0;
        int a = 0;
        int kontrollbit = 0;
        for(int i = 0;i < scoreString.length()-1;i++){
            a = Character.getNumericValue(scoreString.charAt(i));
            switch(k%4){
                case 0 : kontrollbit += 7*a; break;
                case 1 : kontrollbit += 3*a; break;
                case 2 : kontrollbit += 5*a; break;
                case 3 : kontrollbit += 13*a; break;
            }
        }
        kontrollbit %= 10;
        if(kontrollbit == Character.getNumericValue(scoreString.charAt(scoreString.length()-1))){
            return true;
        }else{
            return false;
        }
    }
}
