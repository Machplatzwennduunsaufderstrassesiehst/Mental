package de.soeiner.mental;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 16.02.16.
 */
public class Score extends JSONObject{

    public Score(String playerName) {
        setPlayerName(playerName);
        setScoreValue(0);
    }

    private static int calculateLevel(int score){
        return (int) ((score/250)+Math.log10((double) score)+1);
    }

    private static int calculateLevelProgress(int score){ //in Prozent
        double level = ((score/250)+Math.log10((double) score)+1);
        level %= 1;
        level *= 100;
        return (int) level;
    }

    public void updateScore(int plus) {
        int scoreValue = this.getScoreValue() + plus;
        setScoreValue(scoreValue);
        int overallScoreValue = this.getOverallScoreValue()+plus;
        setOverallScoreValue(overallScoreValue);
        setPlayerLevel(overallScoreValue);
        setPlayerLevelProgress(overallScoreValue);
    }

    // ich habe den scoreString mal aus dem konstruktor entfernt, weil wir eher nicht in die situation kommen,
    // einen scoreString zu haben, wenn das Score Objekt noch nicht existiert
    // stattdessen, kann man jetzt zur Laufzeit einen ScoreString "laden"
    public void loadScoreString(String scoreString) {
        int overallScoreValue = 0;

        if (checkScoreString(scoreString)) {
            scoreString = scoreString.substring(0,scoreString.length()-1);
            overallScoreValue = Integer.parseInt(scoreString);
        }

        setOverallScoreValue(overallScoreValue);
        setPlayerLevel(overallScoreValue);
        setPlayerLevelProgress(overallScoreValue);
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

    public void setPlayerName(String playerName) {
        if (has("playerName")) this.remove("playerName");
        try {
            this.put("playerName", playerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setInt(String key, int value) {
        if (has(key)) this.remove(key);
        try {
            this.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setScoreValue(int scoreValue) {
        setInt("scoreValue", scoreValue);
    }

    public void resetScoreValue(){
        setScoreValue(0);
    }

    private void setOverallScoreValue(int overallScoreValue) {
        setInt("overallScoreValue", overallScoreValue);
    }

    private void setPlayerLevel(int overallScoreValue) {
        setInt("playerLevel", calculateLevel(overallScoreValue));
    }

    private void setPlayerLevelProgress(int overallScoreValue) {
        setInt("playerLevelProgress", calculateLevelProgress(overallScoreValue));
    }


    public String getScoreString(){
        int score = getOverallScoreValue();
        if(score == 0){
            return "git gud nub";
        }
        String scoreString = Integer.toString(score);
        int k = 0;
        int a = 0;
        int checksum = 0;
        for(int i = 0;i < scoreString.length();i++){
            a = Character.getNumericValue(scoreString.charAt(i));
            switch(k%4){
                case 0 : checksum += 7*a; break;
                case 1 : checksum += 3*a; break;
                case 2 : checksum += 5*a; break;
                case 3 : checksum += 13*a; break;
            }
        }
        checksum %= 10;
        scoreString = score+""+checksum;
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
        int checksum = 0;
        for(int i = 0;i < scoreString.length()-1;i++){
            a = Character.getNumericValue(scoreString.charAt(i));
            switch(k%4){
                case 0 : checksum += 7*a; break;
                case 1 : checksum += 3*a; break;
                case 2 : checksum += 5*a; break;
                case 3 : checksum += 13*a; break;
            }
        }
        checksum %= 10;
        if(checksum == Character.getNumericValue(scoreString.charAt(scoreString.length()-1))){
            return true;
        }else{
            return false;
        }
    }
}
