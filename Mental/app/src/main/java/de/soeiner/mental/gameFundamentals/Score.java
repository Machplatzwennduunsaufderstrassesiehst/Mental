package de.soeiner.mental.gameFundamentals;

import org.json.JSONException;
import org.json.JSONObject;

import de.soeiner.mental.gameFundamentals.Player;

/**
 * Created by sven on 16.02.16.
 */
public class Score extends JSONObject {

    String compare = "";
    int pointsGained;
    Player player;
    String color;

    public Score(Player p) {
        player = p;
        setPlayerName(player.getName());
        initialize();
    }

    private void initialize() {
        setMoney(0);
        setOverallScoreValue(0);
        setScoreValue(0);
        setColor("#00000");
        setTitle("amateur");
        setHiglight(false);
        setPlayerLevel(0);
        setPlayerLevelProgress(0);
        setPlayerSpins(0);
    }

    // quadratische abhängigkeit als vorschlag?
    private static int calculateLevel(int score) {
        return (int) Math.sqrt(score / 50.0);
    }

    private static int calculateLevelProgress(int score) {
        int lastLevelThreshold = (int) (Math.pow(calculateLevel(score), 2) * 50.0); // die letzte schwelle ist das aktuelle level
        int nextLevelThreshold = (int) (Math.pow(calculateLevel(score) + 1, 2) * 50.0);
        int diff = nextLevelThreshold - lastLevelThreshold;
        return (int) (100.0 * (score - lastLevelThreshold) / diff);
    }

    public void updateScore(int plus) {
        int scoreValue = this.getScoreValue() + plus;
        setScoreValue(scoreValue);
        int overallScoreValue = this.getOverallScoreValue() + plus;
        setOverallScoreValue(overallScoreValue);
        setPlayerLevel(overallScoreValue);
        setPlayerLevelProgress(overallScoreValue);
        player.getShop().updateMoney();
        setMoney(player.getShop().getMoney());
        pointsGained = plus;
    }

    public boolean attributeOf(Player p) { //vergleicht Spieler Objekt mit anderem Spielerobjekt
        if (player.equals(p)) {
            return true;
        } else {
            return false;
        }
    }

    public void setHiglight(boolean yeORnaw) {
        if (has("highlight")) this.remove("highlight");
        try {
            this.put("highlight", yeORnaw);
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

    public int getOverallScoreValue() {
        try {
            return this.getInt("overallScoreValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setPlayerId() { //useless
        if (has("playerId")) this.remove("playerId");
        try {
            this.put("playerId", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getPointsGained() {
        return pointsGained;
    }

    public void setPointsGained(int p) {
        pointsGained = p;
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

    public void setTitle(String title) {
        if (has("playerTitle")) this.remove("playerTitle");
        try {
            put("playerTitle", title);
        } catch (Exception e) {
        }

    }

    public void setColor(String color) {
        if (has("color")) this.remove("color");
        try {
            put("color", color);
        } catch (Exception e) {
        }
    }

    public void resetScoreValue() {
        setScoreValue(0);
    }

    private void setOverallScoreValue(int overallScoreValue) {
        setInt("overallScoreValue", overallScoreValue);
    }

    private void setPlayerLevel(int overallScoreValue) {
        setInt("playerLevel", calculateLevel(overallScoreValue));
    }

    public void setPlayerSpins(int playerSpins) {
        setInt("playerSpins", playerSpins);
    }


    public int getPlayerLevel() {
        return calculateLevel(getOverallScoreValue());
    }

    private void setPlayerLevelProgress(int overallScoreValue) {
        setInt("playerLevelProgress", calculateLevelProgress(overallScoreValue));
    }

    public void setMoney(int money) {
        setInt("playerMoney", money);
    }

    public void loadScoreString(String scoreString) {
        if (scoreString.length() < 2) {
            return;
        }
        int overallScoreValue = 0;

        if (checkScoreString(scoreString)) {
            scoreString = scoreString.substring(0, scoreString.length() - 1);
            System.out.println("Setze overallScoreValue mit String: " + scoreString);
            overallScoreValue = Integer.parseInt(scoreString);
            System.out.println("overallscoreValue ist nun: " + overallScoreValue);
        }

        setOverallScoreValue(overallScoreValue);
        setPlayerLevel(overallScoreValue);
        setPlayerLevelProgress(overallScoreValue);
    }

    public String getScoreString() {
        int score = getOverallScoreValue();
        if (score == 0) {
            return "";
        }
        String scoreString = Integer.toString(score);
        int k = 0;
        int a = 0;
        int checksum = 0;
        for (int i = 0; i < scoreString.length(); i++) {
            a = Character.getNumericValue(scoreString.charAt(i));
            switch (k % 4) {
                case 0:
                    checksum += 7 * a;
                    break;
                case 1:
                    checksum += 3 * a;
                    break;
                case 2:
                    checksum += 5 * a;
                    break;
                case 3:
                    checksum += 13 * a;
                    break;
            }
        }
        checksum %= 10;
        scoreString = score + "" + checksum;
        return scoreString;
    }

    public boolean checkScoreString(String scoreString) {
        int temp = getOverallScoreValue();
        this.setOverallScoreValue(Integer.parseInt(scoreString.substring(0, scoreString.length() - 1)));
        System.out.println(Integer.parseInt(scoreString.substring(0, scoreString.length() - 1)));
        System.out.println(scoreString);
        if (this.getScoreString().equals(scoreString)) {
            return true;
        }
        this.setOverallScoreValue(temp);
        return false;
    }
}
