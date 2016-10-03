package de.soeiner.mental.communication;

import com.koushikdutta.async.http.WebSocket;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 25.02.16.
 */
public abstract class ClientConnection implements RequestAnswerObserver {

    protected static ArrayList<ClientConnection> connections = new ArrayList<ClientConnection>();

    public static ClientConnection[] getConnections() {
        return (ClientConnection[]) connections.toArray();
    }

    public static ClientConnection getBySocket(WebSocket socket) {
        for (int i = 0; i < connections.size(); i++) {
            ClientConnection c = connections.get(i);
            if (c.compareSocket(socket)) return c;
        }
        return null;
    }

    public static ClientConnection getByHost(String host) {
        for (int i = 0; i < connections.size(); i++) {
            ClientConnection c = connections.get(i);
            if (c.compareHost(host)) return c;
        }
        return null;
    }

    protected WebSocket socket;
    private String host;
    private GetRequest pendingRequest = null;

    public ClientConnection (WebSocket socket) {
        this.socket = socket;

        //host = socket.getRemoteSocketAddress().getAddress().getHostAddress();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {}
        connections.remove(this);
    }

    public void newSocket(WebSocket socket) {
        this.socket = socket;
    }

    public boolean compareSocket(WebSocket socket) {
        return (socket == this.socket);
    }
    public boolean compareHost(String host) {return (host.equals(this.host));}

    public void makeGetRequest(GetRequest r) {
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

    public void makePushRequest(PushRequest r) {
        send(r);
    }

    protected void send(CmdRequest r) {
        try {
            socket.send(r.toString());
        } catch (Exception e) {
            e.printStackTrace();
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
