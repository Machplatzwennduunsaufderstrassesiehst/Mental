package de.soeiner.mental;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sven on 12.02.16.
 */
public class GetRequest extends CmdRequest {

    private ArrayList<RequestAnswerObserver> observers = new ArrayList<RequestAnswerObserver>();

    public String host;
    public JSONObject answer;

    public GetRequest (JSONObject cmd, String host) {
        super(cmd);
        this.host = host;
    }

    public void notifyObservers() {
        for (RequestAnswerObserver observer : observers) {
            observer.onRequestAnswer(this);
        }
        this.notifyAll();
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
