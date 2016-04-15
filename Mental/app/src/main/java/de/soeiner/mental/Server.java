package de.soeiner.mental;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;

/**
 * Created by sven on 12.02.16.
 */
public class Server extends WebSocketServer {

    public Server( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    @Override
    public void onOpen(WebSocket newConnection, ClientHandshake handshake) {
        String host = newConnection.getRemoteSocketAddress().getAddress().getHostAddress();
        Player player = (Player) Player.getByHost(host);
        if (player == null) {
            player = new Player(newConnection);
        } else {
            player.newSocket(newConnection);
            System.out.println("new socket");
        }
    }

    @Override
    public void onClose( WebSocket socket, int code, String reason, boolean remote ) {
        Player player = (Player) Player.getBySocket(socket);
        Game g = player.getGame();
        if (g != null) {
            g.removePlayer(player);
        }
        System.out.println(player.getName() + " disconnected.");
        player.disconnect();
    }

    @Override
    public void onMessage( WebSocket socket, String message ) {
        Player player = (Player) Player.getBySocket(socket);
        if (player != null) {
            player.onMessage(message);
        }
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

    /*
     *
     */
    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }


}
