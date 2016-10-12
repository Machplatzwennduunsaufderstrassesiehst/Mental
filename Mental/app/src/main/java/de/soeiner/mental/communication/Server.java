package de.soeiner.mental.communication;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import de.soeiner.mental.main.Game;
import de.soeiner.mental.main.Player;


/**
 * Created by sven on 12.02.16.
 */
public class Server {

    private AsyncHttpServer asyncServer;
    private PingHttpServer httpServer;
    private int port;
    private AssetManager assetManager;

    public Server(int port, AppCompatActivity context) throws UnknownHostException {
        this.port = port;
        this.assetManager = context.getAssets();
        initialize();
    }

    public void initialize() {
        asyncServer = new AsyncHttpServer();

        WebSocketRequestHandler webSocketRequestHandler = new WebSocketRequestHandler();
        asyncServer.websocket("/mental", webSocketRequestHandler);

        HttpServerRequestCallback httpServerRequestCallback = new HttpServerRequestCallback() {

            @Override
            public void onRequest(AsyncHttpServerRequest asyncHttpServerRequest, AsyncHttpServerResponse asyncHttpServerResponse) {
                String path = asyncHttpServerRequest.getPath();
                int fileEnding = path.lastIndexOf(".");
                if (fileEnding < 0) {
                    path += "index.html";
                }
                String suffix = path.substring(fileEnding + 1);
                path = path.substring(1);
                System.out.println("GET PATH: " + path + "  , suffix: " + suffix);
                String contentType = "";
                switch (suffix) {
                    case "js":
                        contentType = "text/javascript";
                        break;
                    case "css":
                        contentType = "text/css";
                        break;
                    case "html":
                        contentType = "text/html";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                }
                if (contentType.length() > 0) {
                    asyncHttpServerResponse.setContentType(contentType);
                }
                try {
                    InputStream iStream = assetManager.open(path);
                    asyncHttpServerResponse.sendStream(iStream, iStream.available());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                asyncHttpServerResponse.send("");
            }
        };
        asyncServer.get("/.*", httpServerRequestCallback);
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
