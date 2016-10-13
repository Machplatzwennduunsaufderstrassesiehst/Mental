package de.soeiner.mental.trainGame;

import org.json.JSONObject;

import de.soeiner.mental.communication.CmdRequest;
import de.soeiner.mental.trainGame.events.TrainArrivedEvent;
import de.soeiner.mental.trainGame.gameModes.TrainGameMode;
import de.soeiner.mental.trainGame.trainTracks.Goal;
import de.soeiner.mental.trainGame.trainTracks.Switch;
import de.soeiner.mental.trainGame.trainTracks.TrainTrack;
import de.soeiner.mental.util.event.EventDispatcher;

/**
 * Created by Malte on 21.04.2016.
 */
public class Train implements Runnable {
    private int id;
    private int matchingId;
    private double speed; //tracks pro sekunde
    private TrainGameMode trainGame;
    private int x, y;
    private int positionId;

    public EventDispatcher<TrainArrivedEvent> trainArrived = new EventDispatcher<>();

    public Train(int trainId, int matchingId, double speed, TrainGameMode trainGameMode, boolean bombtrain, TrainTrack startTrack) {
        id = trainId;
        this.matchingId = matchingId;
        this.speed = speed;
        trainGame = trainGameMode;
        positionId = startTrack.getId();
        try {
            JSONObject train = CmdRequest.makeCmd(CmdRequest.NEWTRAIN);
            train.put("trainId", id);
            train.put("destinationId", this.matchingId);
            train.put("speed", speed);
            train.put("bombtrain", bombtrain);
            train.put("startTrackId", positionId);
            trainGame.broadcastNewTrain(train);
        } catch (Exception e) {
            e.printStackTrace();
        }
        x = trainGame.getTrackById(positionId).getX();
        y = trainGame.getTrackById(positionId).getY();
        Thread t = new Thread(this);
        t.start();
    }

    public int getId() {
        return id;
    }

    public void run() {
        boolean moving = true;
        int z = 0;
        try {
            while (moving && trainGame.runState.isRunning()) {
                Thread.sleep(calculateTimeToDestination()); //calculateTimeToDestination() TODO
                //System.out.println("Train " + this.getId() + " is now at (" + x + "|" + y + ")");
                if (trainGame.trainMap[x][y].getType().equals("goal")) {
                    Goal tempGoal = (Goal) trainGame.trainMap[x][y];
                    TrainArrivedEvent event;
                    if (matchingId == tempGoal.getGoalId()) {
                        event = new TrainArrivedEvent(this, tempGoal, true);
                    } else {
                        event = new TrainArrivedEvent(this, tempGoal, false);
                    }
                    trainArrived.dispatchEvent(event);
                    moving = false; //beende thread
                }
            }
        } catch (Exception e) {
            System.out.println("Train " + id + " shut down due to error:");
            e.printStackTrace();
        }
    }

    // Wird diese noch benötigt?
/*    private int calculateTimeToDestination(){ //in millisek
        if(trainGame.trainMap[x][y].getType().equals("goal")){return 0;}
        double distance = 0;
        boolean broadcast = false;
        Switch s = null;
        int direction = 0;
        x = trainGame.getTrackById(positionId).getX();
        y = trainGame.getTrackById(positionId).getY();

        do{
            if(distance == 0 && trainGame.trainMap[x][y].getType().equals("switch")){
                s = (Switch) trainGame.trainMap[x][y];
                direction = s.getSwitchedTo();
                //System.out.println("Train " + this.getId() + " now switching. switchId:" + s.getSwitchId() + " Pos(" + x + "|" + y + ")");
                trainGame.broadcastTrainDecision(id, s.getSwitchId(), direction);
            }
            try {
                System.out.println("getTrackById("+positionId+")");
                positionId = trainGame.getTrackById(positionId).getSuccessor().id;
                x = trainGame.getTrackById(positionId).getX();
                y = trainGame.getTrackById(positionId).getY();
                }catch(Exception e){
                System.out.println("Train ist gecrasht an stelle x: "+x+", y: "+y+" , "+trainGame.trainMap[x][y].getType()+" mit id: "+ trainGame.trainMap[x][y].id+" und value: "+trainGame.trainMap[x][y].getValue()+" vorgänger: "+trainGame.trainMap[x][y].getPredecessor().getType()+", "+trainGame.trainMap[x][y].id+", x:"+trainGame.trainMap[x][y].getX()+", y: "+trainGame.trainMap[x][y].getY());
                //throw new RuntimeException();
            }
            distance++;
        }while(!(trainGame.trainMap[x][y].getType().equals("switch")) && !(trainGame.trainMap[x][y].getType().equals("goal")));
        return (int) (distance/speed * 1000);
    }*/

    // TODO fire event before train enters a track - useful for Bomb consumable later on
    private int calculateTimeToDestination() { //in millisek
        if (trainGame.trainMap[x][y].getType().equals("goal")) {
            return 0;
        }
        double distance = 0;
        boolean broadcast = false;
        Switch s = null;
        int direction = 0; //0 oben, 1 rechts, 2 unten, 3 links
        int xtemp = 0;
        int ytemp = 0;
        do {
            //System.out.println("Train " + this.getId() + " at Pos(" + x + "|" + y + ")");
            if (distance == 0 && trainGame.trainMap[x][y].getType().equals("switch")) {
                s = (Switch) trainGame.trainMap[x][y];
                direction = s.getSwitchedTo();
                //System.out.println("Train " + this.getId() + " now switching. switchId:" + s.getSwitchId() + " Pos(" + x + "|" + y + ")");
                trainGame.broadcastTrainDecision(id, s.getSwitchId(), direction);
            }
            try {
                xtemp = trainGame.trainMap[x][y].getSuccessor().getX();
                ytemp = trainGame.trainMap[x][y].getSuccessor().getY();
                x = xtemp;
                y = ytemp;
            } catch (Exception e) {
                System.out.println("Train ist gecrasht an stelle x: " + x + ", y: " + y + " , " + trainGame.trainMap[x][y].getType() + " mit id: " + trainGame.trainMap[x][y].getId() + " und value: " + trainGame.trainMap[x][y].getValue() + " vorgänger: " + trainGame.trainMap[x][y].getPredecessor().getType() + ", " + trainGame.trainMap[x][y].getId() + ", x:" + trainGame.trainMap[x][y].getX() + ", y: " + trainGame.trainMap[x][y].getY());
                throw new RuntimeException();
            }
            distance++;
        }
        while (!(trainGame.trainMap[x][y].getType().equals("switch")) && !(trainGame.trainMap[x][y].getType().equals("goal")));
        return (int) (distance / speed * 1000);
    }
}