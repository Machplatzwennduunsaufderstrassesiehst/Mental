package de.soeiner.mental.gameModes;

/**
 * Created by Malte on 21.04.2016.
 */
public class Train implements Runnable{
    private int id;
    private String color;
    private double speed; //tracks pro sekunde

    public Train(int i, String c, double s){
        id = i;
        color = c;
        speed = s;
        Thread t = new Thread(this);
        t.start();
    }

    public String getColor(){
        return color;
    }
    public int getId(){
        return id;
    }

    public void run() { // TODO
        //while unterwegs
            //zeit bis zum nächsten switch ausrechen
            //so lange warten
        //end while
        //wenn richtiges ziel erreicht, benrachrichtigen
    }
}
