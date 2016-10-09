package de.soeiner.mental.gameFundamentals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.view.Window;
import android.widget.Button;

import java.io.IOException;

import de.soeiner.mental.R;
import de.soeiner.mental.communication.Server;

public class MainActivity extends AppCompatActivity {

    final static int PORT = 1297;
    final static boolean DEBUG = true;
    private boolean serverIsActive;
    private Button btnJoin;
    private Button btnHost;

    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        serverIsActive = false;
        if (DEBUG) {
            // so startet man nicht aus Versehen den Server 2x im DEBUG modus
            serverStart();
        }

        androidApplicationStart();
    }

    public void androidApplicationStart() {
        setContentView(R.layout.activity_main);
        if (DEBUG) {
            btnJoin = (Button) findViewById(R.id.buttonJoinGame);
            btnHost = (Button) findViewById(R.id.buttonHostGame);
            btnHost.setText("SERVER LÄUFT");
            btnHost.setEnabled(false);
            btnHost.setClickable(false);
            btnJoin.setEnabled(true);
        }
    }

    public void webViewStart() {
        /*
        setContentView(R.layout.gui_webview);

        XWalkView xWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);
        xWalkWebView.load("file:///android_asset/index.html", null);

        // turn on debugging
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        */
    }

    public void buttonJoinServer(View v) {
        Uri url = Uri.parse("http://www.mentalist.lima-city.de");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    public void buttonStartServer(View v) {
        btnJoin.setEnabled(true);
        if (serverIsActive) {
            btnHost.setText("Spiel hosten");
            btnJoin.setEnabled(false);
            serverIsActive = false;
        } else {
            btnHost.setText("Spiel beenden");
            serverIsActive = true;
        }

        serverStart();
    }

    private void serverStart() {
        try {
            if (server != null) {
                server.stop();
            }
            server = new Server(PORT);
            new Game("Train");
            new Game("MA");
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.stop();
    }
}
