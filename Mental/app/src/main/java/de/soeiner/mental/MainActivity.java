package de.soeiner.mental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import org.java_websocket.WebSocketImpl;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    PingHttpServer httpServer;
    boolean serverIsActive;
    boolean DEBUG = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverIsActive = false;
        if(DEBUG){
            debugServerStart();
        }
    }

    public void buttonJoinServer(View v){
        Uri url = Uri.parse("http://www.mentalist.lima-city.de");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    public void buttonStartServer(View v){
        Button btnJoin = (Button) findViewById(R.id.buttonJoinGame);
        Button btnHost = (Button) findViewById(R.id.buttonHostGame);
        btnJoin.setEnabled(true);
        try {
            WebSocketImpl.DEBUG = true;
            int port = 6382;
            Server s = new Server( port );
            s.start();
            System.out.println("Server started on port: " + s.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(serverIsActive){
            btnHost.setText("Spiel hosten");
            btnJoin.setEnabled(false);
            serverIsActive = false;
        } else {
            btnHost.setText("Spiel beenden");
            serverIsActive = true;
        }
        httpServer = new PingHttpServer();
        httpServer.start();
        new Game();
    }

    private void debugServerStart(){
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
        new Game();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServer.stop();
    }
}
