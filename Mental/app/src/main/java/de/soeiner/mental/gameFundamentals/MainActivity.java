package de.soeiner.mental.gameFundamentals;

import android.content.ActivityNotFoundException;
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
import de.soeiner.mental.arithmetics.gameModes.ArenaGameMode;
import de.soeiner.mental.arithmetics.gameModes.BeatBobGameMode;
import de.soeiner.mental.arithmetics.gameModes.ClassicGameMode;
import de.soeiner.mental.arithmetics.gameModes.KnockoutGameMode;
import de.soeiner.mental.arithmetics.gameModes.SpeedGameMode;
import de.soeiner.mental.trainGame.gameModes.ClassicTrainGameMode;
import de.soeiner.mental.trainGame.gameModes.SuddenDeathTrainGameMode;

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

        webViewStart();
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
        setContentView(R.layout.gui_webview);

        org.xwalk.core.XWalkView xWalkWebView = (org.xwalk.core.XWalkView) findViewById(R.id.xwalkWebView);
        xWalkWebView.load("file:///android_asset/index.html", null);

        org.xwalk.core.XWalkPreferences.setValue(org.xwalk.core.XWalkPreferences.REMOTE_DEBUGGING, true);
    }

    public void buttonJoinServer(View v) {
        Uri url = Uri.parse("http://localhost:1297/");
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setPackage(null);
            startActivity(intent);
        }
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
            server = new Server(PORT, this);
            Game trainGame = new Game();
            trainGame.setVoting(new Voting(trainGame, new GameMode[]{new ClassicTrainGameMode(trainGame), new SuddenDeathTrainGameMode(trainGame)}));
            Game arithmeticsGame = new Game();
            arithmeticsGame.setVoting(new Voting(arithmeticsGame, new GameMode[]{new ArenaGameMode(arithmeticsGame), new BeatBobGameMode(arithmeticsGame),
                    new ClassicGameMode(arithmeticsGame), new KnockoutGameMode(arithmeticsGame), new SpeedGameMode(arithmeticsGame)}));
            System.out.println("Server started on port " + PORT);
            trainGame.setName("Train");
            trainGame.start();
            arithmeticsGame.setName("Mental Arithmetics");
            arithmeticsGame.start();
            System.out.println("Games started.");
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
