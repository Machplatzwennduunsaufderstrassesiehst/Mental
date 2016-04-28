package AppClient;
import android.util.Log;
import android.os.Build;
import android.widget.TextView;
import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import de.soeiner.mental.R;
import de.soeiner.mental.Server;


/**
 * Created by Appel on 19.04.2016.
 */
public class AppClientWebSocket {

    WebSocketClient clientSocket;
    String location;

    public AppClientWebSocket(String location){
        this.location = location;
        connectWebSocket();
    }

    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(location);
            System.out.println(location);
            clientSocket = new WebSocketClient(uri, new Draft_75()) {
                public void onMessage(String message ) {
                    System.out.println(message);
                    try {
                        JSONObject j = new JSONObject(message);
                        String type = j.getString("type");
                        switch (type) {
                            case "message":
                                String textMessage = j.getString("message");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Client Socket opened");
                }

                public void onClose(int code, String reason, boolean remote) {

                }

                public void onError(Exception e) {

                }
            };
            clientSocket.connect();
            clientSocket.send("{type:'setName', 'name':'Appel'}");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
