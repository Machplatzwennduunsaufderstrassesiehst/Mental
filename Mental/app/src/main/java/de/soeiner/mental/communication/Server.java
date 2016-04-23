package de.soeiner.mental.communication;

import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.net.UnknownHostException;

import de.soeiner.mental.Game;
import de.soeiner.mental.Player;


class WebSocketRequestHandler implements AsyncHttpServer.WebSocketRequestCallback {

    @Override
    public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
        new Player(webSocket);

        //Use this to clean up any references to your websocket
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


/**
 * Created by sven on 12.02.16.
 */
public class Server {

    private AsyncHttpServer asyncServer;
    private WebSocketRequestHandler webSocketRequestHandler;

    public Server( int port ) throws UnknownHostException {
        asyncServer = new AsyncHttpServer();
        webSocketRequestHandler = new WebSocketRequestHandler();
        asyncServer.websocket("/", webSocketRequestHandler);
        System.out.println("test");
        asyncServer.listen(port);
    }

    // TODO add stopping funtionality


/*
    @Override
    public void onOpen(WebSocket newConnection, ClientHandshake handshake) {
        String host = newConnection.getRemoteSocketAddress().getAddress().getHostAddress();
        Player player = (Player) Player.getByHost(host);
        if (player == null) {
        } else {
            player.newSocket(newConnection);
            System.out.println("new socket");
        }
    }

    @Override
    public void onClose( WebSocket socket, int code, String reason, boolean remote ) {
    }

    @Override
    public void onMessage( WebSocket socket, String message ) {
    }

    @Override
    public void onFragment( WebSocket conn, Framedata fragment ) {
        System.out.println("received fragment: " + fragment);
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }*/


}
