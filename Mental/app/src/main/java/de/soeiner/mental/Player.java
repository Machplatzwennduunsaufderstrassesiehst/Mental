package de.soeiner.mental;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 12.02.16.
 */
public class Player extends ClientConnection {

    private String name;
    private Score score;
    private Game game;
    public boolean finished;

    public Player (WebSocket socket) {
        super(socket);
        name = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        score = new Score(name, 0);
        connections.add(this);
    }

    public void sendExercise(String ex, int length) {
        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.SEND_EXERCISE);
        try {
            jsonObject.put("exercise", ex);
            jsonObject.put("length", length);
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

    public Score updateScore() {
        JSONObject jsonObject = CmdRequest.makeCmd(CmdRequest.GET_POINTS);
        GetRequest request = new GetRequest(jsonObject, socket);
        makeGetRequest(request);
        try {
            synchronized (request) {
                request.wait(2000);
            }
        } catch (Exception e) {}
        int points = 0;
        try {
            points = request.getAnswer().getInt("points");
        } catch (Exception e) {}
        score.setScoreValue(points);
        return score;
    }

    public Score getScore() {
        return score;
    }

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
            System.out.println(type);
            if (type.equals("get_games")) {
                String s = "[";
                ArrayList<Game> games = Game.getGames();
                for (int i = 0; i < games.size(); i++) {
                    Game g = games.get(i);
                    if (g == null) continue;
                    String name = g.getName();
                    String desc = g.getDescription();
                    s += "{'name':'" + name + "', desc:'" + desc + "', game_id:' " + i + "},";
                }
                s += "false]";
                JSONObject j = CmdRequest.makeCmd(CmdRequest.SEND_GAMES);
                j.put("games", new JSONArray(s));
                send(new PushRequest(j));
            }
            if (type.equals("join")) {
                int id = Integer.parseInt(json.getString("game_id"));
                Game g = Game.getGames().get(id);
                g.join(this);
                game = g;
            }
            if (type.equals("create")) {
                if (true || socket.getRemoteSocketAddress().getAddress().isLinkLocalAddress()) { // TODO check for local ip
                    String name = json.getString("name");
                    Game.getGames().add(new Game(name));
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
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e);
        }
    }



}