package de.soeiner.mental.gameModes;

import de.soeiner.mental.trainTracks.*;

/**
 * Created by Malte on 21.04.2016.
 */
public class Train implements Runnable{
    private int id;
    private String color;
    private int destinationId;
    private double speed; //tracks pro sekunde
    private TrainGameMode traingame;
    private int x,y;

    public Train(int i, String c, int d, double s, TrainGameMode tg){
        id = i;
        color = c;
        destinationId = d;
        speed = s;
        traingame = tg;
        x = y = 1;
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
        boolean moving = true;
        while(moving){
            try {
                this.wait(calculateTimeToDestination());
            }catch(Exception e){}
            if(traingame.trainMap[x][y].getType().equals("goal")){
                Goal tempGoal = (Goal) traingame.trainMap[x][y]; //TODO possible breaking point
                if(id == tempGoal.getGoalId()){
                    traingame.trainArrived();
                }
                /* sicherere möglichkeit: if(id == traingame.trainMap[x][y].getValue()){ traingame.trainArrived(); } */
                moving = false; //beende thread
            }
        }

        //while unterwegs
            //zeit bis zum nächsten switch ausrechen
            //so lange warten
        //end while
        //wenn richtiges ziel erreicht, benrachrichtigen
    }

    private int calculateTimeToDestination(){ //in millisek
        double distance = 0;
        Switch s = null;
        int direction = 0; //0 oben, 1 rechts, 2 unten, 3 links
        do{
            if(distance == 0){
                s = (Switch) traingame.trainMap[x][y];
            }
            x = traingame.trainMap[x][y].getSuccessor().getX();
            y = traingame.trainMap[x][y].getSuccessor().getY();
            if(distance == 0){
                switch (s.getX() - x){
                    case -1: switch (s.getY() - y){
                        case -1: break;
                        case  0: break;
                        case  1: break;
                    }break;
                    case  0: switch (s.getY() - y){
                        case -1: break;
                        case  0: break;
                        case  1: break;
                    }break;
                    case  1: switch (s.getY() - y){
                        case -1: break;
                        case  0: break;
                        case  1: break;
                    }break;
                }
                //traingame.broadcastTrainDecision(id, s.getSwitchId(), traingame.trainMap[x][y], direction);
            }
            distance++;
        }while(!(traingame.trainMap[x][y].getType().equals("switch") || traingame.trainMap[x][y].getType().equals("goal")));
        return (int) (distance/speed * 1000);
    }
}
