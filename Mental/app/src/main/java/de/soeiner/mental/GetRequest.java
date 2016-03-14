package de.soeiner.mental;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 12.02.16.
 */
public class GetRequest extends CmdRequest {

    private ArrayList<RequestAnswerObserver> observers = new ArrayList<RequestAnswerObserver>();

    public WebSocket socket;
    public JSONObject answer;

    public GetRequest (JSONObject cmd, WebSocket socket) {
        super(cmd);
        this.socket = socket;
    }

    public void notifyObservers() {
        for (RequestAnswerObserver observer : observers) {
            observer.onRequestAnswer(this);
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void addObserver(RequestAnswerObserver observer) {
        observers.add(observer);
    }

    public void onAnswer(JSONObject json) {
        answer = json;
        notifyObservers();
    }

    public JSONObject getAnswer() {
        return answer;
    }
}
