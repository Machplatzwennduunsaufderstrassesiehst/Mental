package de.soeiner.mental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.java_websocket.WebSocketImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    PingHttpServer httpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] args = {};
        try {
            WebSocketImpl.DEBUG = true;
            int port = 6382;
            Server s = new Server( port );
            s.start();
            System.out.println("Server started on port: " + s.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpServer = new PingHttpServer();
        httpServer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServer.stop();
    }
}
