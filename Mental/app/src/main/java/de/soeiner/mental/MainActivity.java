package de.soeiner.mental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import java.io.IOException;

import de.soeiner.mental.communication.PingHttpServer;
import de.soeiner.mental.communication.Server;

public class MainActivity extends AppCompatActivity {

    final static int PORT = 1297;
    final static boolean DEBUG = true;
    private boolean serverIsActive;
    private Button btnJoin;
    private Button btnHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverIsActive = false;
        btnJoin = (Button) findViewById(R.id.buttonJoinGame);
        btnHost = (Button) findViewById(R.id.buttonHostGame);
        if(DEBUG) {
            // so startet man nicht aus Versehen den Server 2x im DEBUG modus
            btnHost.setText("SERVER LÄUFT");
            btnHost.setEnabled(false);
            btnHost.setClickable(false);
            btnJoin.setEnabled(true);
            serverStart();
        }
    }

    public void buttonJoinServer(View v) {
        Uri url = Uri.parse("http://www.mentalist.lima-city.de");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    public void buttonStartServer(View v) {
        btnJoin.setEnabled(true);
        if(serverIsActive){
            btnHost.setText("Spiel hosten");
            btnJoin.setEnabled(false);
            serverIsActive = false;
        } else {
            btnHost.setText("Spiel beenden");
            serverIsActive = true;
        }

        serverStart();
    }

    private void serverStart(){
        try {
            int port = PORT;
            Server s = new Server( port );
            System.out.println("Server started: " + s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Game();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
