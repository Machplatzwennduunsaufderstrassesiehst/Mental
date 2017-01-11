package de.soeiner.mental.communication;

import java.net.UnknownHostException;

import de.soeiner.mental.gameFundamentals.Game;
import de.soeiner.mental.gameFundamentals.Player;


/**
 * Created by sven on 12.02.16.
 */
public class Server {

    private AsyncHttpServer asyncServer;
    private PingHttpServer httpServer;
    private WebSocketRequestHandler webSocketRequestHandler;
    private int port;

    public Server( int port ) throws UnknownHostException {
        this.port = port;
        initialize();
    }

    public void initialize() {
        asyncServer = new AsyncHttpServer();
        webSocketRequestHandler = new WebSocketRequestHandler();
        asyncServer.websocket("/mental", webSocketRequestHandler);
        asyncServer.listen(port);

        httpServer = new PingHttpServer();
        httpServer.start();
    }

    public void stop() {
        asyncServer.stop();
        httpServer.stop();
    }


    private class WebSocketRequestHandler implements AsyncHttpServer.WebSocketRequestCallback {

        @Override
        public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
            new Player(webSocket);

            //Use this to clean up any references to your WebSocket
            webSocket.setClosedCallback(new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    try {
                        Player player = (Player) Player.getBySocket(webSocket);
                        Game g = player.getGame();
                        if (g != null) g.removePlayer(player);
                        System.out.println(player.getName() + " disconnected.");
                        player.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (ex != null)
                            Log.e("WebSocket", "Error");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            webSocket.setStringCallback(new WebSocket.StringCallback() {
                @Override
                public void onStringAvailable(String message) {
                    Player player = (Player) Player.getBySocket(webSocket);
                    if (player != null) {
                        player.onMessage(message);
                    }
                }
            });
        }
    }


}
