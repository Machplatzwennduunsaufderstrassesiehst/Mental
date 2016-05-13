
function Train(trainId, destinationId, tracksPerSecond, color, startTrack) {
    var container = new PIXI.Container();
    
    var sprite = TextureGenerator.generateSprite(TrainGame.trainTexture)
    container.addChild(sprite);
	
    container.pivot = TextureGenerator.getSpritePivot(sprite);
	
    // add color TODO
    var graphicObject = new GraphicObject(container);
	trainGame.graphics.addGraphicObject(graphicObject);
	
	var timePerTrack = 1 / tracksPerSecond;
	var currentTrack = startTrack;
	var currentLane = null;
	
    function move() {
        setTimeout(function(){
            if (currentTrack.hasSuccessor()) {
                currentTrack = currentTrack.getSuccessor();
                move();
            } else {
                trainGame.graphics.removeGraphicObject(graphicObject); // TODO
            }
        }, timePerTrack * 1000);
        currentLane = currentTrack.getLane();
        var movement = buildMovement();
        graphicObject.setPos(currentLane.getEntranceCoords().getX(), currentLane.getEntranceCoords().getY());
        graphicObject.queueMovement(movement);
    }
    
    move(currentTrack);
    
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
        switch (Math.abs(Math.sign(degrees))) {
            case 0:
                movement = new StraightMovement(startRotation, Vector.newFromTo(u, v), timePerTrack);
                movement.addVector(u);
                return movement;
            case 1: // turn
                movement = new TurnMovement(startRotation, currentLane.getTurnRadius(), degrees, timePerTrack);
                movement.addVector(u);
                return movement;
            default:
                log("fehler in train.js: degrees: " + degrees);
        }
    }
}

