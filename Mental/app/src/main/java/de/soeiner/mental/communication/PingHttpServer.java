package de.soeiner.mental.communication;

/**
 * Created by sven on 17.03.16.
 */


import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PingHttpServer {

    ServerSocket httpServerSocket;

    public void start() {
        HttpServerThread httpServerThread = new HttpServerThread();
        httpServerThread.start();
    }

    public void stop() {
        if (httpServerSocket != null) {
            try {
                httpServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class HttpServerThread extends Thread {

        static final int PORT = 6383;

        @Override
        public void run() {
            Socket socket;

            try {
                httpServerSocket = new ServerSocket(PORT);

                while(true) {
                    socket = httpServerSocket.accept();

                    HttpResponseThread httpResponseThread = new HttpResponseThread(socket);
                    httpResponseThread.start();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

    private class HttpResponseThread extends Thread {

        Socket socket;

        HttpResponseThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader is;
            PrintWriter os;
            String request;


            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = is.readLine();

                os = new PrintWriter(socket.getOutputStream(), true);
                // ein 1px x 1px gif Bild in base64, damit man es hier direkt im code einbinden kann.
                byte[] response = Base64.decode("R0lGODlhAQABAIAAAP///////yH+EUNyZWF0ZWQgd2l0aCBHSU1QACwAAAAAAQABAAACAkQBADs=\n",0); // TODO flags ???

                os.print("HTTP/1.0 200" + "\r\n");
                os.print("Content type: image/gif" + "\r\n");
                os.print("Content length: " + response.length + "\r\n\r\n");
                os.flush();
                socket.getOutputStream().write(response);

                System.out.println("Request of " + request
                        + " from " + socket.getInetAddress().toString() + "\n");

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}