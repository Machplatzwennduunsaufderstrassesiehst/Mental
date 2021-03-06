

TrainGame.gameObjects = (function() {
    
    function Train(trainId, destinationId, tracksPerSecond, startTrack, startTime) {
        Train.s[trainId] = this;
        var container = new PIXI.Container();

        var gridSize = TrainGame.instance.getGridSize();
        var scale = 0.38;
        var sprite = GraphicsEngine.graphics.TextureGenerator.generateSprite(TrainGame.trainTexture, scale);
        var colorTexture = new PIXI.Graphics();
        colorTexture.beginFill(Number("0x" + TrainGame.idColors[destinationId]), 1);
        colorTexture.drawRect(0, -gridSize/8*scale, gridSize / 3 * scale, gridSize / 1.5 * scale);
        container.addChild(sprite);
        container.addChild(colorTexture);

        sprite.pivot = GraphicsEngine.graphics.TextureGenerator.getSpritePivot(sprite);
        colorTexture.pivot = GraphicsEngine.graphics.TextureGenerator.getDisplayObjectPivot(colorTexture);

        container.cacheAsBitmap = true;

        // add color TODO
        var graphicObject = new GraphicsEngine.graphics.GraphicObject(container);
        TrainGame.instance.graphics.addGraphicObject(graphicObject);

        var secondsPerTrack = 1 / tracksPerSecond;
        var currentTrack = startTrack;
        var successorTrack = null;
        var predecessorTrack = null;
        var currentLane = null;
        var lastTrackChange;
        var arriveInGoal = function(){};
        var graphicsArrived = false;

        function move(bWithLatencyHeadStart) {
            var headStart = 0;
            if (bWithLatencyHeadStart) {
                headStart = new Date().getTime() - startTime;
                while (headStart > secondsPerTrack * 1000) {
                    headStart -= secondsPerTrack * 1000;
                    currentTrack = currentTrack.getSuccessor();
                }
            }
            setTimeout(function(){
                if (!TrainGame.instance.isRunning()) return;
                if (successorTrack != null) {
                    currentTrack = successorTrack;
                    move(false);
                } else {
                    arriveInGoal();
                    graphicsArrived = true;
                }
            }, secondsPerTrack * 1000 - headStart);

            currentLane = currentTrack.getLane();
            var lateness = (1 - graphicObject.getMovementProgress()) * secondsPerTrack;
            var movement = buildMovement(secondsPerTrack - lateness);
            if (bWithLatencyHeadStart) movement.setProgress(headStart / 1000);
            //graphicObject.setPos(movement.getFirst());
            graphicObject.queueMovement(movement);
            lastTrackChange = Date.now();
            successorTrack = currentTrack.getSuccessor();
            predecessorTrack = currentTrack.getPredecessor();
        }

        move(true);

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
                    correctMovement(successor, successor.getSuccessor(), successor.getLane(), retry);
                }, correctionRetryTimeout);
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
                    if (graphicsArrived) {
                        var timeUntilNextMoveCall = (Date.now() - lastTrackChange) % (secondsPerTrack * 1000);
                        setTimeout(move, timeUntilNextMoveCall);
                    }
                }
            }
        };

        // TODO movement caching
        function buildMovement(movementTime) {
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
                        movement = new GraphicsEngine.physics.StraightMovement(startRotation, GraphicsEngine.physics.Vector.newFromTo(u, v), movementTime);
                        break;
                    case 1: // turn
                        movement = new GraphicsEngine.physics.TurnMovement(startRotation, currentLane.getTurnRadius(), degrees, movementTime);
                        break;
                    default:
                        log("fehler in gameObjects.js: degrees: " + degrees);
                        movement = new GraphicsEngine.physics.Movement([]);
                }
                break;
                case "goal":
                    movement = new GraphicsEngine.physics.StraightDeaccelerationMovement(startRotation, GraphicsEngine.physics.Vector.newFromTo(u, v), movementTime);
                    break;
            }
            movement.addVector(u);
            return movement;
        }

        this.explode = function() {
            new TrainGame.particles.Explosion(graphicObject.getPos());
            TrainGame.instance.graphics.removeGraphicObject(graphicObject);
        };

        this.fadeOut = function() {
            graphicObject.fadeOut(function() {
                TrainGame.instance.graphics.removeGraphicObject(graphicObject);
            }, 1);
        };

        this.arrive = function(onArriveInGoal) {
            arriveInGoal = onArriveInGoal;
            if (graphicsArrived) { // manual call if graphics are already in goal, only occurs if server msg too late
                arriveInGoal();
            }
        };

        this.getGraphicsArrived = function() {
            return graphicsArrived;
        };
    }
    Train.s = [];
    
    return {
        Train: Train
    };
})();
