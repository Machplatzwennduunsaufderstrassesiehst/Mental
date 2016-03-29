package de.soeiner.mental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        httpServer = new PingHttpServer();
        httpServer.start();

        new ClassicGame(new MixedExerciseCreator2(1));
        new ClassicGame(new SimpleMultExerciseCreator(1));
        new ClassicGame(new MultExerciseCreator(1));

        new KnockoutGame(new MixedExerciseCreator2(30));
        new KnockoutGame(new SimpleMultExerciseCreator(30));
        new KnockoutGame(new MultExerciseCreator(30));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServer.stop();
    }
}
