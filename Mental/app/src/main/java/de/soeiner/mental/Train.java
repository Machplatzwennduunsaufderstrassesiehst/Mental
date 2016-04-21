package de.soeiner.mental;

/**
 * Created by Malte on 21.04.2016.
 */
public class Train {
    private int id;
    private String color;

    public Train(int i, String c){
        id = i;
        color = c;
    }

    public String getColor(){
        return color;
    }
    public int getId(){
        return id;
    }

}
