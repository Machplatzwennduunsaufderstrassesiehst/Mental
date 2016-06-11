package de.soeiner.mental.trainGameRelated;

import org.json.JSONObject;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.gameModes.TrainGameMode;
import de.soeiner.mental.trainGameRelated.trainTracks.Goal;
import de.soeiner.mental.trainGameRelated.trainTracks.Switch;

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
    private int positionId;

    public Train(int i, int d, double s, TrainGameMode tg){
        id = i;
        destinationId = d;
        speed = s;
        traingame = tg;
        positionId = traingame.trainMap[1][1].getId();
        JSONObject train = CmdRequest.makeCmd(CmdRequest.SEND_NEWTRAIN);;
        try{
            train.put("trainId", id);
            train.put("destinationId", destinationId);
            train.put("speed", s);
        }catch(Exception e){ e.printStackTrace();}
        traingame.broadcastNewTrain(train);
        x = y = 1;
        Thread t = new Thread(this);
        t.start();
    }

    public int getId(){
        return id;
    }

    public void run() {
        boolean moving = true;
        int z = 0;
        while(moving && traingame.getGameIsRunning()){
            try {
                z++;
                Thread.sleep(calculateTimeToDestination()); //calculateTimeToDestination() TODO
                //System.out.println("Train " + this.getId() + " is now at (" + x + "|" + y + ")");
            }catch(Exception e){
                e.printStackTrace();
                try{
                    System.out.println("Train " + id + " waiting 10 s due to error");
                    Thread.sleep(10000);
                }catch (Exception e2){e2.printStackTrace();}
            }
            if(traingame.trainMap[x][y].getType().equals("goal")){
                Goal tempGoal = (Goal) traingame.trainMap[x][y];
                if(destinationId == tempGoal.getGoalId()){
                    traingame.trainArrived(id, tempGoal.getGoalId(), true);
                }else{
                    traingame.trainArrived(id, tempGoal.getGoalId(), false);
                }
                moving = false; //beende thread
            }
        }
    }

/*    private int calculateTimeToDestination(){ //in millisek
        if(traingame.trainMap[x][y].getType().equals("goal")){return 0;}
        double distance = 0;
        boolean broadcast = false;
        Switch s = null;
        int direction = 0;
        x = traingame.getTrackById(positionId).getX();
        y = traingame.getTrackById(positionId).getY();

        do{
            if(distance == 0 && traingame.trainMap[x][y].getType().equals("switch")){
                s = (Switch) traingame.trainMap[x][y];
                direction = s.getSwitchedTo();
                //System.out.println("Train " + this.getId() + " now switching. switchId:" + s.getSwitchId() + " Pos(" + x + "|" + y + ")");
                traingame.broadcastTrainDecision(id, s.getSwitchId(), direction);
            }
            try {
                System.out.println("getTrackById("+positionId+")");
                positionId = traingame.getTrackById(positionId).getSuccessor().id;
                x = traingame.getTrackById(positionId).getX();
                y = traingame.getTrackById(positionId).getY();
                }catch(Exception e){
                System.out.println("Train ist gecrasht an stelle x: "+x+", y: "+y+" , "+traingame.trainMap[x][y].getType()+" mit id: "+ traingame.trainMap[x][y].id+" und value: "+traingame.trainMap[x][y].getValue()+" vorgänger: "+traingame.trainMap[x][y].getPredecessor().getType()+", "+traingame.trainMap[x][y].id+", x:"+traingame.trainMap[x][y].getX()+", y: "+traingame.trainMap[x][y].getY());
                //throw new RuntimeException();
            }
            distance++;
        }while(!(traingame.trainMap[x][y].getType().equals("switch")) && !(traingame.trainMap[x][y].getType().equals("goal")));
        return (int) (distance/speed * 1000);
    }*/

    private int calculateTimeToDestination(){ //in millisek
        if(traingame.trainMap[x][y].getType().equals("goal")){return 0;}
        double distance = 0;
        boolean broadcast = false;
        Switch s = null;
        int direction = 0; //0 oben, 1 rechts, 2 unten, 3 links
        int xtemp = 0;
        int ytemp = 0;
        do{
            if(distance == 0 && traingame.trainMap[x][y].getType().equals("switch")){
                s = (Switch) traingame.trainMap[x][y];
                direction = s.getSwitchedTo();
                //System.out.println("Train " + this.getId() + " now switching. switchId:" + s.getSwitchId() + " Pos(" + x + "|" + y + ")");
                traingame.broadcastTrainDecision(id, s.getSwitchId(), direction);
            }
            try {
                xtemp = traingame.trainMap[x][y].getSuccessor().getX();
                ytemp = traingame.trainMap[x][y].getSuccessor().getY();
                x = xtemp;
                y = ytemp;
            }catch(Exception e){
                System.out.println("Train ist gecrasht an stelle x: "+x+", y: "+y+" , "+traingame.trainMap[x][y].getType()+" mit id: "+ traingame.trainMap[x][y].id+" und value: "+traingame.trainMap[x][y].getValue()+" vorgänger: "+traingame.trainMap[x][y].getPredecessor().getType()+", "+traingame.trainMap[x][y].id+", x:"+traingame.trainMap[x][y].getX()+", y: "+traingame.trainMap[x][y].getY());
                throw new RuntimeException();
            }
            distance++;
        }while(!(traingame.trainMap[x][y].getType().equals("switch")) && !(traingame.trainMap[x][y].getType().equals("goal")));
        return (int) (distance/speed * 1000);
    }
}