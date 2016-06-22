package de.soeiner.mental.gameFundamentals;

import com.koushikdutta.async.http.WebSocket;

import java.util.Arrays;

import de.soeiner.mental.shop.Shop;
import de.soeiner.mental.shop.shopItems.ShopItem;
import de.soeiner.mental.communication.ClientConnection;
import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.communication.PushRequest;
import de.soeiner.mental.exerciseCreators.ExerciseCreator;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;
import de.soeiner.mental.util.JSONArray;
import de.soeiner.mental.util.JSONException;
import de.soeiner.mental.util.JSONObject;

/**
 * Created by sven on 12.02.16.
 */
public class Player extends ClientConnection {

    private String name;
    private Score score;
    private Game game;
    private Shop shop;
    public ExerciseCreator exerciseCreator;
    int[] partition = new int[4]; //4 Plätze
    public boolean finished;

    public Player (WebSocket socket) {
        super(socket);
        //name = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        name = "New Player";
        score = new Score(this);
        shop = new Shop(this);
        connections.add(this);
    }

    public void sendExercise(JSONObject ex) {
        System.out.println("Player.sendExercise()");
        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_EXERCISE);
        try {
            jsonObject.put("exercise", ex);
        } catch (Exception e) {}
        PushRequest request = new PushRequest(jsonObject);
        makePushRequest(request);
    }

    public void sendScoreBoard(Score[] playerScores) {
        if(shop == null) { return; }
        shop.updateMoney(); //TODO CARE
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

    public void sendSwitchChange(Switch changedSwitch){
            JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_SWITCHCHANGE);
            try {
                j.put("switchChange", changedSwitch);
                makePushRequest(new PushRequest(j));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void sendTrainDecision(int trainId, int switchId, int switchedTo){
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TRAINDECISION);
        try {
            j.put("trainId", trainId);
            j.put("switchId", switchId);
            j.put("switchedTo", switchedTo);
            makePushRequest(new PushRequest(j));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTrainArrived(int trainId, int goalId, boolean success){
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TRAIN_ARRIVED);
        try {
            j.put("trainId", trainId);
            j.put("goalId", goalId);
            j.put("success", success);
            makePushRequest(new PushRequest(j));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendNewTrain(JSONObject train){
        try {
            makePushRequest(new PushRequest(train));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void sendWaveCompleted(boolean success, int waveNo, int reward){
        JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_TRAIN_WAVE_COMPLETED);
        try {
            j.put("success", success);
            j.put("waveNo", waveNo);
            j.put("reward", reward);
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

    public void updatePlayerInfo(){ //TODO CARE
        Score[] s = new Score[1];
        s[0] = getScore();
        sendScoreBoard(s);
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
        System.out.println(getName() + ": " + json.toString());
        try {
            String type = json.getString("type");
            System.out.println(type);
            int index;
            JSONObject callback = null;
            switch (type) {
                case "getGames":
                    JSONArray jsonGameArray = Game.getGamesJSONArray();
                    callback = CmdRequest.makeCmd(CmdRequest.SEND_GAMES);
                    callback.put("games", jsonGameArray);
                    break;
                case "join":
                    int id = Integer.parseInt(json.getString("gameId"));
                    Game g = Game.getGames().get(id);
                    g.addPlayer(this);
                    game = g;
                    break;
                case "confirm":
                    this.game.confirm();
                    break;
                case "answer":
                    JSONObject answer = json.getJSONObject("answer");
                    boolean isCorrect = game.gameMode.playerAnswered(this, answer);
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("isCorrect", isCorrect);
                    callback.put("pointsGained", this.getScore().getPointsGained());
                    break;
                case "setName":
                    String name = json.getString("name");
                    this.name = name;
                    this.score.setPlayerName(name);
                    break;
                case "setGameString":
                    String gameString = json.getString("gameString");
                    try {
                        loadGameString(gameString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("set game string");
                    updatePlayerInfo(); //<--- anstelle von
                    /*--->
                    Score[] s = new Score[1];
                    s[0] = getScore();
                    sendScoreBoard(s);*/                    break;
                case "buyItem":
                    index = Integer.parseInt(json.getString("index"));
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("success", this.shop.buyItem(index));
                    callback.put("price", this.shop.shopItemList[index].getPrice());
                    callback.put("index", index);
                    sendGameString();
                    break;
                case "equipItem":
                    index = Integer.parseInt(json.getString("index"));
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("success", this.shop.equipItem(index));
                    callback.put("index", index);
                    callback.put("itemType", shop.shopItemList[index].getType());
                    sendGameString();
                    break;
                case "unequipItem":
                    index = Integer.parseInt(json.getString("index"));
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("success", this.shop.unequipItem(index));
                    callback.put("index", index);
                    sendGameString();
                    break;
                case "getShopItemList":
                    ShopItem[] shopItemList = shop.getShopItemList();
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("shopItemList", new JSONArray(shopItemList));
                    break;
                case "spin":
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("angle", this.shop.getWheel().calculateAngel());
                    callback.put("success", this.shop.getWheel().spin());
                    sendGameString();
                    break;
                case "buySpin":
                    callback = CmdRequest.makeResponseCmd(type);
                    callback.put("success", this.shop.getWheel().buySpin());
                    callback.put("price", this.shop.getWheel().PRICE_PER_SPIN);
                    sendGameString();
                    break;
                case "vote":
                    int suggestionID = Integer.parseInt(json.getString("suggestionID"));
                    System.out.println(game);
                    game.voting.receiveVote(suggestionID, this);
                    break;
                case "leave":
                    try {
                        game.removePlayer(this);
                    } catch (Exception e) {
                        System.out.println("[processData] [leave] Player already disconnected.");
                    }
                    break;
            }
            if (callback != null) send(new PushRequest(callback));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e);
        }
    }

    public void loadGameString(String gameString) {

        if(gameString.length() == 0){return;}
        System.out.println("loadPartition(gameString.substring(" + gameString.length() + " - " + partition.length + ", " + gameString.length() + "));"); //partition laden
        loadPartition(gameString.substring(gameString.length() - partition.length, gameString.length())); //partition laden
        gameString = gameString.substring(0, gameString.length() - partition.length); // Partition abschneiden
        System.out.println(Arrays.toString(partition));
        for (int passage = partition.length - 1; passage >= 0; passage--) { //von hinten angefangen um den String verkleinern zu können
            System.out.println("String tempString =  gameString.substring("+gameString.length()+" - "+partition[passage]+", "+gameString.length()+");");//aktuell zu behandelnden String wie nach partition vorgesehen isolieren
            String tempString =  gameString.substring(gameString.length() - partition[passage], gameString.length());//aktuell zu behandelnden String wie nach partition vorgesehen isolieren
            gameString = gameString.substring(0, gameString.length() - partition[passage]); //und abschneiden

            //wenn die Passage einen zweck hat die dafür vorgesehene Methode aufrufen
            if (passage == 3) {}
            if (passage == 2) {}
            if (passage == 1) {
                //if(!shop.checkShopString(tempString)){ return; } //Wenn der shopString manipuliert wurde, abbrechen
                shop.loadShopString(tempString);
           }
            if (passage == 0) {
                //if(!score.checkScoreString(tempString)){ return; } //Wenn der scoreString manipuliert wurde, abbrechen
                System.out.println("loadScoreString()");
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
        gameString = addPartitionString(gameString); //anhängen der Partition
        return gameString;
    }

    private void loadPartition(String partitionString){
        for(int i = 0; i<partition.length ; i++) {
            partition[i] = ((int) partitionString.charAt(i)) - 35; //+20 um nicht lesbare asciis zu vermeiden
        }
    }

    private String addPartitionString(String gameString){
        for(int i = 0; i<partition.length ; i++){
            gameString += (char) (partition[i]+35); //+20 um nicht lesbare asciis zu vermeiden
        }
        return gameString;
    }

    public void setPartitionPassage(int passage, int size) {
        partition[passage] = size;
    }
}