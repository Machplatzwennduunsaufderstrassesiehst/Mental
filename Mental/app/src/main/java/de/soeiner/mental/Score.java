package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 16.02.16.
 */
public class Score extends JSONObject{


    public Score(String playerName, String score) {

        int overallScoreValue = 0;

        if(checkScoreString(score)){
            score = score.substring(0,score.length()-2);
            overallScoreValue = Integer.parseInt(score);
        }

        try {
            put("playerName", playerName);
            put("scoreValue", 0);
            put("overallScoreValue", overallScoreValue);
            put("playerLevel", calculateLevel(overallScoreValue));
            put("playerLevelProgress", calculateLevelProgress(overallScoreValue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static int calculateLevel(int score){
        return (int) ((score/500)+Math.log10((double) score)+1);
    }
    private static int calculateLevelProgress(int score){ //in Prozent
        double level = ((score/500)+Math.log10((double) score)+1);
        level %= 1;
        level *= 100;
        return (int) level;
    }

    public void updateScore(int plus){
        int scoreValue = this.getScoreValue() + plus;
        setScoreValue(scoreValue);
        int overallScoreValue = this.getOverallScoreValue()+plus;
        setOverallScoreValue(overallScoreValue);
        setPlayerLevel(overallScoreValue);
        setPlayerLevelProgress(overallScoreValue);
    }

    public void resetScore(){
        setScoreValue(0);
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

    private int getOverallScoreValue() {
        try {
            return this.getInt("overallScoreValue");
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

    private void setOverallScoreValue(int scoreValue) {
        this.remove("overallScoreValue");
        try {
            this.put("overallScoreValue", scoreValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerLevel(int scoreValue){
        this.remove("playerLevel");
        try {
            this.put("playerLevel", calculateLevel(scoreValue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerLevelProgress(int scoreValue){
        this.remove("playerLevelProgress");
        try {
            this.put("playerLevelProgress", calculateLevelProgress(scoreValue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getScoreString(){
        int score = getOverallScoreValue();
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

    public static boolean checkScoreString(String scoreString){

        if(scoreString == ""){
            return false;
        }

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
