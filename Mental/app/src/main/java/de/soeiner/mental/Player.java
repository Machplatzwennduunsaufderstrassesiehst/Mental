package de.soeiner.mental;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class Player extends ClientConnection {

    private String name;
    private Score score;
    private Game game;
    private Shop shop;
    ExerciseCreator exerciseCreator;
    public boolean finished;

    public Player (WebSocket socket) {
        super(socket);
        name = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        score = new Score(this);
        shop = new Shop(this);
        connections.add(this);
    }

    public void sendExercise(String ex) {
        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_EXERCISE);
        try {
            jsonObject.put("exercise", ex);
        } catch (Exception e) {}
        PushRequest request = new PushRequest(jsonObject);
        makePushRequest(request);
    }

    public void sendScoreBoard(Score[] playerScores) {

        for(int i = 0; i < playerScores.length;i++){ // richtiger Spieler wird gehilightet
            if(playerScores[i].attributeOf(this)){
                playerScores[i].setHiglight(true);
            }else{
                playerScores[i].setHiglight(false);
            }
        }

        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_SCOREBOARD);
        try {
            JSONArray scoreJSONArray = new JSONArray(playerScores);
            jsonObject.put("scoreboard", scoreJSONArray);
        } catch(Exception e) {
            e.printStackTrace();
        }
        PushRequest request = new PushRequest(jsonObject);
        makePushRequest(request);
    }

    public void sendShopItemList(ShopItem[] shopItemList) {
        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_SHOP_ITEM_LIST);
        try {
            JSONArray shopJSONArray = new JSONArray(shopItemList);
            jsonObject.put("shopItemList", shopJSONArray);
        } catch(Exception e) {
            e.printStackTrace();
        }
        PushRequest request = new PushRequest(jsonObject);
        makePushRequest(request);
    }

    public void sendSuggestions(Suggestion[] suggestions) {

        for(int i = 0; i < suggestions.length;i++){ // richtiger Spieler wird gehilightet
            if(suggestions[i].votersContain(this)){
                suggestions[i].setHiglight(true);
            }else{
                suggestions[i].setHiglight(false);
            }
        }

        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_SUGGESTIONS);
        try {
            JSONArray suggestionJSONArray = new JSONArray(suggestions);
            jsonObject.put("suggestions", suggestionJSONArray);
        } catch(Exception e) {
            e.printStackTrace();
        }
        PushRequest request = new PushRequest(jsonObject);
        makePushRequest(request);
    }

    public void sendGameString() {
        String gameString = this.getGameString();
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_GAME_STRING);
        try {
            j.put("gameString", gameString);
            makePushRequest(new PushRequest(j));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendBeatBobStatus(double status){ //bekommt double e [-1, 1]
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_BEATBOB);
        try {
            j.put("status", status);
            makePushRequest(new PushRequest(j));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Score getScore() { return score; }

    public Shop getShop() { return shop; }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void processData(JSONObject json) {
        try {
            String type = json.getString("type");
            // TODO switch anstatt if
            if (type.equals("getGames")) {
                JSONArray jsonGameArray = Game.getGamesJSONArray();
                JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_GAMES);
                j.put("games", jsonGameArray);
                send(new PushRequest(j));
            }
            if (type.equals("join")) {
                int id = Integer.parseInt(json.getString("gameId"));
                Game g = Game.getGames().get(id);
                g.addPlayer(this);
                game = g;

            }
            if (type.equals("answer")) {
                JSONObject answer = json.getJSONObject("answer");
                boolean isCorrect = game.playerAnswered(this, answer);
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("isCorrect", isCorrect);
                j.put("pointsGained", this.getScore().getPointsGained());
                send(new PushRequest(j));
            }
            if (type.equals("setName")) {
                String name = json.getString("name");
                this.name = name;
                this.score.setPlayerName(name);
            }
            if (type.equals("setGameString")) {
                String g = json.getString("gameString");
                loadGameString(g);
                System.out.println("set game string");
                Score[] s = new Score[1];
                s[0] = getScore();
                sendScoreBoard(s);
            }
            if (type.equals("buyItem")) {
                int index = Integer.parseInt(json.getString("index"));
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("success", this.shop.buyItem(index));
                j.put("index", index);
                send(new PushRequest(j));
            }
            if (type.equals("equipItem")) {
                int index = Integer.parseInt(json.getString("index"));
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("success", this.shop.equipItem(index));
                j.put("index", index);
                send(new PushRequest(j));
            }
            if (type.equals("getShopItemList")) {
                ShopItem[] shopItemList = shop.getShopItemList();
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("shopItemList", new JSONArray(shopItemList));
                send(new PushRequest(j));
            }
            if (type.equals("vote")) {
                int suggestionID = Integer.parseInt(json.getString("suggestionID"));
                System.out.println(game);
                game.voting.receiveVote(suggestionID, this);
            }
            if (type.equals("leave")) {
                game.removePlayer(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e);
        }
    }

    public String getGameString(){ //funktioniert nur für scorestrings der länge <= 9
        return this.getScore().getScoreString()+this.getShop().getShopString()+this.getScore().getScoreString().length(); //gameString besteht aus
        // scoreString + shopString + länge von scorestring // TODO + anzahl der ziffern der länge von scorestring (für längere Scorestrings)
    }
    public void loadGameString(String gameString){ //klappt nur wenn der scoreString <= 9 Zeichen lang ist
        if(gameString.length()< 8){return;}
        this.getScore().loadScoreString(gameString.substring(0, Character.getNumericValue(gameString.charAt(gameString.length()-1))));
        this.getShop().loadShopString(gameString.substring(Character.getNumericValue(gameString.charAt(gameString.length()-1)), gameString.length()-1));
    }
}