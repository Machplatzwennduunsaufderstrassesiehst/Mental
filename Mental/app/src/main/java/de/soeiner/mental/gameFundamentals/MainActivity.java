package de.soeiner.mental.gameFundamentals;

import java.io.IOException;
import de.soeiner.mental.communication.Server;

public class MainActivity{

    final static int PORT = 1297;
    final static boolean DEBUG = true;
    private boolean serverIsActive;

    private Server server;

    public static void main(String[] args) {
        new MainActivity().serverStart();
    }

    private void serverStart(){
        try {
            server = new Server(PORT);
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Game();
        new Game();
    }
}
