package de.soeiner.mental;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by sven on 12.02.16.
 */
public class Player extends ClientConnection {

    private String name;
    private Score score;
    private Game game;
    private Shop shop;
    ExerciseCreator exerciseCreator;
    int[] partition = new int[4]; //4 Plätze
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
                boolean isCorrect = game.gameMode.playerAnswered(this, answer);
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("isCorrect", isCorrect);
                j.put("pointsGained", this.getScore().getPointsGained());
                send(new PushRequest(j));
            }
            if (type.equals("setName")) {
                String name = json.getString("name");
                this.name = name;
                this.score.setPlayerName(name);
                this.score.updateScore(10000);
            }
            if (type.equals("setGameString")) {
                String g = json.getString("gameString");
                System.out.println(g);
                try {
                    loadGameString(g);
                } catch (Exception e){
                    e.printStackTrace();
                }
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
                sendGameString();
            }
            if (type.equals("equipItem")) {
                int index = Integer.parseInt(json.getString("index"));
                JSONObject j = CmdRequest.makeResponseCmd(type);
                j.put("success", this.shop.equipItem(index));
                j.put("index", index);
                send(new PushRequest(j));
                sendGameString();
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
                try {
                    game.removePlayer(this);
                } catch (Exception e) {
                    System.out.println("[processData] [leave] Player already disconnected.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e);
        }
    }

    /*
    public String getGameString(){ //funktioniert nur für scorestrings der länge <= 9
        return this.getScore().getScoreString()+this.getShop().getShopString()+this.getScore().getScoreString().length(); //gameString besteht aus
        // scoreString + gameString + länge von scorestring // TODO + anzahl der ziffern der länge von scorestring (für längere Scorestrings)
    }
    public void loadGameString(String gameString){ //klappt nur wenn der scoreString <= 9 Zeichen lang ist
        if(gameString.length() < 4){return;}
        this.getScore().loadScoreString(gameString.substring(0, Character.getNumericValue(gameString.charAt(gameString.length()-1))));
        this.getShop().loadShopString(gameString.substring(Character.getNumericValue(gameString.charAt(gameString.length()-1)), gameString.length()-1));
    }
    */

    public void loadGameString(String gameString) {

        if(gameString.length() == 0){return;}
        System.out.println("loadPartition(gameString.substring(" + gameString.length() + " - " + partition.length + ", " + gameString.length() + "));"); //partition laden
        loadPartition(gameString.substring(gameString.length() - partition.length, gameString.length())); //partition laden
        gameString = gameString.substring(0, gameString.length() - partition.length); // Partition abschneiden
        //zurückgestellt //gameString = Integer.toString((int) Long.parseLong(gameString, 16)); // Umwandlung ins Zehnersystem
        System.out.println(Arrays.toString(partition));
        for (int passage = partition.length - 1; passage >= 0; passage--) { //von hinten angefangen um den String verkleinern zu können
            System.out.println("String tempString =  gameString.substring("+gameString.length()+" - "+partition[passage]+", "+gameString.length()+");");//aktuell zu behandelnden String wie nach partition vorgesehen isolieren
            String tempString =  gameString.substring(gameString.length() - partition[passage], gameString.length());//aktuell zu behandelnden String wie nach partition vorgesehen isolieren
            gameString = gameString.substring(0, gameString.length() - partition[passage]); //und abschneiden

            //wenn die Passage einen zweck hat die dafür vorgesehene Methode aufrufen
            if (passage == 3) {}
            if (passage == 2) {}
            if (passage == 1) {
                shop.loadShopString(tempString);
            }
            if (passage == 0) {
                score.loadScoreString(tempString);
            }
        }
    }

    public String getGameString(){

        String gameString = "";

        for (int passage = 0; passage < partition.length; passage++) { //von vorne angefangen um zum String hinzufügen zu können
            int length = 0; //länge der einzelnen Passagen

            //wenn die Passage einen zweck hat die dafür vorgesehene Methode aufrufen und um den entsprechenden String erweitern
            if (passage == 0) {
                length = gameString.length(); //vorherige länge speichern
                gameString += score.getScoreString(); //passage hinzufügen
                length = gameString.length() - length; //differenz ermitteln
            }
            if (passage == 1) {
                length = gameString.length(); //vorherige länge speichern
                gameString += shop.getShopString(); //passage hinzufügen
                length = gameString.length() - length; //differenz ermitteln
            }
            if (passage == 2) {}
            if (passage == 3) {}

            setPartitionPassage(passage, length); //länge in Partition reservieren
        }
        //hexadezimal stelle ich hier mal zurück, da sonst die Partitionen evtl. probleme machen
        // gameString = Integer.toHexString(Integer.parseInt(gameString)); //Umwandlung in Hexadezimal
        gameString = addPartitionString(gameString); //anhängen der Partition
        return gameString;
    }

    private void loadPartition(String partitionString){
        for(int i = 0; i<partition.length ; i++) {
            partition[i] = ((int) partitionString.charAt(i)) - 33; //+20 um nicht lesbare asciis zu vermeiden
        }
    }

    private String addPartitionString(String gameString){
        for(int i = 0; i<partition.length ; i++){
            gameString += (char) (partition[i]+33); //+20 um nicht lesbare asciis zu vermeiden
        }
        return gameString;
    }

    public void setPartitionPassage(int passage, int size) {
        partition[passage] = size;
    }
}