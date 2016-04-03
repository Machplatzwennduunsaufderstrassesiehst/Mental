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
    public boolean finished;

    public Player (WebSocket socket) {
        super(socket);
        name = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        score = new Score(name);
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
            JSONArray scoreJSONArray = new JSONArray(shopItemList);
            jsonObject.put("shopItemList", scoreJSONArray);
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
            j.put("score_string", gameString);
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
            if (type.equals("get_games")) {
                JSONArray jsonGameArray = Game.getGamesJSONArray();
                JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_GAMES);
                j.put("games", jsonGameArray);
                send(new PushRequest(j));
            }
            if (type.equals("join")) {
                int id = Integer.parseInt(json.getString("game_id"));
                Game g = Game.getGames().get(id);
                g.join(this);
                game = g;
            }
            if (type.equals("create")) {
                // TODO spaeter sollte diese option vllt komplett geloescht werden und create direkt in der Android app erfolgen
                if (true || socket.getRemoteSocketAddress().getAddress().isLinkLocalAddress()) { // TODO check for local ip

                }
            }
            if (type.equals("answer")) {
                int answer = Integer.parseInt(json.getString("answer"));
                boolean isCorrect = game.playerAnswered(this, answer);
                JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_ANSWER_FEEDBACK);
                j.put("isCorrect", isCorrect);
                send(new PushRequest(j));
            }
            if (type.equals("set_name")) {
                String name = json.getString("name");
                this.name = name;
                this.score.setPlayerName(name);
            }
            if (type.equals("set_game_string")) { //musst du noch ändern
                String g = json.getString("game_string");
                loadGameString(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e);
        }
    }

    public String getGameString(){ //funktioniert nur für scorestrings der länge <= 9
        return this.getScore().getScoreString()+this.getShop().getshopString()+this.getScore().getScoreString().length(); //gameString besteht aus
        // scoreString + shopString + länge von scorestring // TODO + anzahl der ziffern der länge von scorestring (für längere Scorestrings)
    }
    public void loadGameString(String gameString){ //klappt nur wenn der scoreString <= 9 Zeichen lang ist
        this.getScore().loadScoreString(gameString.substring(0, Character.getNumericValue(gameString.charAt(gameString.length()-1))));
        this.getShop().loadShopString(gameString.substring(Character.getNumericValue(gameString.charAt(gameString.length()-1)), gameString.length()-1));
    }



}