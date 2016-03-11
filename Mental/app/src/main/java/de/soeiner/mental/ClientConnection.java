package de.soeiner.mental;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 25.02.16.
 */
public abstract class ClientConnection implements RequestAnswerObserver {

    protected static ArrayList<Player> players = new ArrayList<Player>();

    public static Player[] getPlayers() {
        return (Player[]) players.toArray();
    }

    public static Player getByHost(String host) {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.getHost().equals(host)) {
                return p;
            }
        }
        return null;
    }

    protected WebSocket socket;
    private String host;
    private GetRequest pendingRequest = null;

    public ClientConnection (WebSocket socket) {
        this.socket = socket;
        host = socket.getRemoteSocketAddress().getAddress().getHostAddress();

    }

    public void newSocket(WebSocket socket) {
        this.socket = socket;
    }

    public String getHost() {
        return host;
    }

    protected void makeGetRequest(GetRequest r) {
        if (pendingRequest != null) {
            try {
                synchronized (pendingRequest) {
                    pendingRequest.wait(2000);
                }
            } catch (Exception e) {}
        }
        pendingRequest = r;
        r.addObserver(this);
        send(r);
    }

    protected void makePushRequest(PushRequest r) {
        send(r);
    }

    protected void send(CmdRequest r) {
        try {
            socket.send(r.toString());
        } catch (WebsocketNotConnectedException e) {
            Logger.log("Can't send to Connection " + this.host + "! Socket not connected.");
        }
    }

    public void onRequestAnswer(GetRequest request) {
        pendingRequest = null;
    }

    public void onMessage(String message) {
        JSONObject json;
        try {
            json = new JSONObject(message);
            String type = json.getString("type");
            if (pendingRequest != null && type.equals("_" + pendingRequest.getJSONObject().getString("type") + "_")) {
                pendingRequest.onAnswer(json);
            }
            processData(json);
        } catch (Exception e) {e.printStackTrace();}
    }

    public abstract void processData(JSONObject json);
}
