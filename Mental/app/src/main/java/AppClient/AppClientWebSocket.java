package AppClient;
import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;


/**
 * Created by Appel on 19.04.2016.
 */
public class AppClientWebSocket extends WebSocketClient {

    public AppClientWebSocket (URI serverUri, Draft draft){
        super(serverUri, draft);
    }

    public AppClientWebSocket(URI serverURI) {
        super(serverURI);
    }

    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient client = new AppClientWebSocket(new URI("ws://localhost:8887"), new Draft_10());
        //WebSocketClient client = new AppClientWebSocket(new URI("ttp://www.mentalist.lima-city.de"));
        client.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}
