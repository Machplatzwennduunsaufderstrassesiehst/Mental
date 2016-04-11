package de.soeiner.mental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.java_websocket.WebSocketImpl;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    PingHttpServer httpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            WebSocketImpl.DEBUG = true;
            int port = 6382;
            Server s = new Server( port );
            s.start();
            System.out.println("Server started on port: " + s.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mentalist.lima-city.de
        httpServer = new PingHttpServer();
        httpServer.start();

        new Game();
    }

    public void buttonOnClick(View v){
        System.out.println("Button wird gedrückt.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServer.stop();
    }
}
