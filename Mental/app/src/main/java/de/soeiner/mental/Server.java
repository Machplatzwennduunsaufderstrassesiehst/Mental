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

    public Server( InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onOpen(WebSocket newConnection, ClientHandshake handshake) {
        String host = newConnection.getRemoteSocketAddress().getAddress().getHostAddress();
        Player player = Player.getByHost(host);
        if (player == null) {
            new Player(newConnection);
        } else {
            player.newSocket(newConnection);
        }
    }

    @Override
    public void onClose( WebSocket socket, int code, String reason, boolean remote ) {
        String host = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        Player player = Player.getByHost(host);
        Game g = player.getGame();
        g.leave(player);
    }

    @Override
    public void onMessage( WebSocket socket, String message ) {
        String host = socket.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(message + " from " + host);
        Player player = Player.getByHost(host);
        player.onMessage(message);
        System.out.println(player.getName());
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
