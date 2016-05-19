
/* global PIXI, TextureGenerator, TrainGame, trainGame, Vector, particles */

function Train(trainId, destinationId, tracksPerSecond, color, startTrack) {
    Train.s[trainId] = this;
    var container = new PIXI.Container();
    
    var gridSize = trainGame.getGridSize();
    var scale = 0.38;
    var sprite = TextureGenerator.generateSprite(TrainGame.trainTexture, scale);
    var colorTexture = new PIXI.Graphics();
    colorTexture.beginFill(Number("0x" + TrainGame.idColors[destinationId]), 1);
    colorTexture.drawRect(0, -gridSize/8*scale, gridSize / 3 * scale, gridSize / 1.5 * scale);
    container.addChild(sprite);
    container.addChild(colorTexture);
	
    sprite.pivot = TextureGenerator.getSpritePivot(sprite);
    colorTexture.pivot = TextureGenerator.getDisplayObjectPivot(colorTexture);
    
    container.cacheAsBitmap = true;
	
    // add color TODO
    var graphicObject = new GraphicObject(container);
	trainGame.graphics.addGraphicObject(graphicObject);
	
	var timePerTrack = 1 / tracksPerSecond;
	var currentTrack = startTrack;
	var successorTrack = null;
	var predecessorTrack = null;
	var currentLane = null;
	var lastTrackChange;
    var arriveInGoal = function(){};
    var graphicsArrived = false;
	
    function move() {
        setTimeout(function(){
            if (!trainGame.isRunning()) return;
            if (successorTrack != null) {
                currentTrack = successorTrack;
                move();
            } else {
                arriveInGoal();
                graphicArrived = true;
            }
        }, timePerTrack * 1000);
        
        currentLane = currentTrack.getLane();
        var movement = buildMovement();
        graphicObject.setPos(movement.getFirst());
        graphicObject.queueMovement(movement);
        lastTrackChange = Date.now();
        successorTrack = currentTrack.getSuccessor();
        predecessorTrack = currentTrack.getPredecessor();
    }
    
    var correctionRetryTimeout = 50, // ms
        correctionMaxRetryCount = 10;
    
    var correctMovement = this.correctMovement = function(correctedTrack, successor, lane, retry) {
        if (retry == undefined) retry = 0;
        retry = retry + 1;
        if (retry > correctionMaxRetryCount) return;
        if (successorTrack == correctedTrack) { // train has not arrived at the corrected track yet
            setTimeout(function(){
                correctMovement(correctedTrack, successor, lane, retry);
            }, correctionRetryTimeout);
        } else if (predecessorTrack == correctedTrack) { // train has already passed the corrected track
            // this is a very bad case, should only occur if: latency > (1000 / TRAIN_MAX_SPEED)
            setTimeout(function(){
                correctMovement(successor, successor.getSuccessor(), successor.getLane(), correctionMaxRetryCount + 1);
            }, correctionRetryTimeout);
            return;
        } else { // train is on the correct track => only correct its lane and successorTrack if necessary
            // (another case corrected here: train is on a switch's wrong successor track due to INSANE lagg)
            currentTrack = correctedTrack;
            successorTrack = successor;
            if (currentLane != lane) {
                currentLane = lane;
                var movement = buildMovement();
                var movementProgress = 0;
                if (currentTrack == correctedTrack) { // correction on the current track uses movementProgress, only for graphics
                    movementProgress = (Date.now() - lastTrackChange) / 1000;
                }
                movement.setProgress(movementProgress);
                graphicObject.setPos(movement.getFirst());
                graphicObject.queueMovement(movement);
            }
        }
    };
    
    move();
    
    // TODO movement caching
    function buildMovement() {
        //log("BUILD MOVEMENT=========================================");
        var degrees = currentLane.getTurnDegrees();
        //log("degrees: " + degrees);
        var startRotation = currentLane.getStartRotation();
        //log("startRotation: " + startRotation);
        var u = currentLane.getEntranceCoords();
        var v = currentLane.getExitCoords();
        var movement = null;
        switch (currentTrack.type) {
            case "track":case "switch":
                switch (Math.abs(Math.sign(degrees))) {
                    case 0:
                        movement = new StraightMovement(startRotation, Vector.newFromTo(u, v), timePerTrack);
                        break;
                    case 1: // turn
                        movement = new TurnMovement(startRotation, currentLane.getTurnRadius(), degrees, timePerTrack);
                        break;
                    default:
                        log("fehler in train.js: degrees: " + degrees);
                        movement = new Movement([]);
                }
                break;
            case "goal":
                movement = new StraightDeaccelerationMovement(startRotation, Vector.newFromTo(u, v), timePerTrack/2);
                break;
        }
        movement.addVector(u);
        return movement;
    }
    
    this.explode = function() {
        new particles.Explosion(graphicObject.getPos());
        trainGame.graphics.removeGraphicObject(graphicObject);
    };
    
    this.fadeOut = function() {
        graphicObject.fadeOut(function() {
            trainGame.graphics.removeGraphicObject(graphicObject);
        }, 1);
    };
    
    this.arrive = function(onArriveInGoal) {
        arriveInGoal = onArriveInGoal;
        if (graphicsArrived) { // manual call if graphics are already in goal, only occurs if server msg too late
            arriveInGoal();
        }
    };
}
Train.s = [];

