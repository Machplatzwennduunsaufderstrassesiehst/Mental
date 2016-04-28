package de.soeiner.mental.communication;

import org.json.JSONObject;

import de.soeiner.mental.communication.CmdRequest;

/**
 * Created by sven on 12.02.16.
 */
public class PushRequest extends CmdRequest {

    public PushRequest(JSONObject cmd) {
        super(cmd);
    }

}
