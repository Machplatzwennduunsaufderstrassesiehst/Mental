
var mainTrainGameFrame = new Frame("mainTrainGameFrame");

(function() {
    
    var trainGame = undefined;
    var clickHandler = undefined;

    mainTrainGameFrame.setOnOpen(function() {
        byID("page_").style.display = "none";

        serverConnection.addObserver(trainMapObserver);
        serverConnection.addObserver(newtrainObserver);
        serverConnection.addObserver(switchChangedObserver);
        serverConnection.addObserver(trainDecisionObserver);
        serverConnection.addObserver(trainArrivedObserver);
        serverConnection.addObserver(trainWaveObserver);
        serverConnection.addObserver(textObserver);

        if (trainGameGraphics != undefined) {
            trainGameGraphics.stop();
            trainGameGraphics.clearEnvironment();
        }
        var trainGameGraphics = new GraphicsEngine.graphics.GameGraphics("mainTrainGameFrame");
        TrainGame.instance = trainGame = new TrainGame(trainGameGraphics);

        clickHandler = byID("clickHandler");
        clickHandler.style.display = "block";
        clickHandler.onclick = trainGame.mouseDown;
    });

    mainTrainGameFrame.setOnClose(function() {
        byID("page_").style.display = "block";

        serverConnection.removeObserver(trainMapObserver);
        serverConnection.removeObserver(newtrainObserver);
        serverConnection.removeObserver(switchChangedObserver);
        serverConnection.removeObserver(trainDecisionObserver);
        serverConnection.removeObserver(trainArrivedObserver);
        serverConnection.removeObserver(trainWaveObserver);
        serverConnection.removeObserver(textObserver);

        trainGame.stop();
        byID("mainTrainGameFrame").innerHTML = "";

        clickHandler.onclick = function(){};
        clickHandler.style.display = "none";
    });

    function fitGraphics(xMapSize, yMapSize) {
        var frame = byID("mainTrainGameFrame");
        var frameRatio = frame.clientWidth / frame.clientHeight;
        var mapRatio = xMapSize / yMapSize;
        var viewGridSize;
        if ((frameRatio > 1 && 1 > mapRatio) || (mapRatio > 1 && 1 > frameRatio)) {
            trainGame.flipMap = true;
            mapRatio = 1 / mapRatio;
            var temp = xMapSize;
            xMapSize = yMapSize;
            yMapSize = temp;
        }
        if (frameRatio > mapRatio) {
            viewGridSize = frame.clientHeight / yMapSize;
        } else {
            viewGridSize = frame.clientWidth / xMapSize;
        }
        var gridSize = viewGridSize; // currently testing, seems to be better not to scale
        if (isMobile() && gridSize > 120) {
            gridSize = 120;
        }
        var stageScale = viewGridSize / gridSize;

        trainGame.graphics.resizeRenderer(xMapSize*viewGridSize, yMapSize*viewGridSize);
        trainGame.graphics.setStageScale(stageScale);
        trainGame.setGridSize(gridSize);
        trainGame.setViewGridSize(viewGridSize);
    }

    function Map(rawdata, firstTrackId) {
        // initialize array to hold the track objects later
        var trackArray = [];
        var trainSpawn = null; // reference to the first track of the double linked list

        log(firstTrackId);

        for (var i = 0; i < rawdata.length; i++) {
            for (var j = 0; j < rawdata[i].length; j++) {
                var trackData = rawdata[i][j];
                if (trackData == null) {
                    continue;
                }
                if (trainGame.flipMap) {
                    var temp = trackData.xpos;
                    trackData.xpos = trackData.ypos;
                    trackData.ypos = temp;
                }
                var trackId = Number(trackData.id);
                if (trackId < 0 || trackId == undefined) continue;
                if (trackArray[trackId] != undefined) {
                    log("Trackid " + trackId + " doppelt vergeben");
                }
                trackArray[trackId] = trackData;
            }
        }

        log(trackArray);
        log(firstTrackId);

        // recursive strategy to build the track objects needed for the map
        function build(trackId, predecessor) {
            //if (!trackArray[trackId]) return null;
            try {
                var trackData = trackArray[trackId];
            } catch (e) {
                log(e);
                return null;
            }
            if (trackData == undefined) return null;
            try {
                switch(trackData.trackType) {
                    case "blocked":return null;
                    case "track":
                        var successorId = trackData.successorId;
                        var t = new TrainGame.environment.Track(trackData.xpos, trackData.ypos);
                        var futureSuccessor = build(successorId, t);
                        t.setPredecessor(predecessor);
                        t.setSuccessor(futureSuccessor);
                        t.initialize();
                        return t;
                    case "switch":
                        var successorIds = trackData.successorIds;
                        var successors = [];
                        var sw = new TrainGame.environment.Switch(trackData.switchId, trackData.xpos, trackData.ypos);
                        for (var s = 0; s < successorIds.length; s++) {
                            successors[s] = build(successorIds[s], sw);
                        }
                        sw.setPredecessor(predecessor);
                        sw.setSuccessors(successors);
                        sw.initialize();
                        sw.change(trackData.switchedTo);
                        return sw;
                    case "goal":
                        var goalId = trackData.goalId;
                        var g = new TrainGame.environment.Goal(goalId, trackData.xpos, trackData.ypos);
                        g.setPredecessor(predecessor);
                        g.initialize();
                        return g;
                }
            } catch (e) {
                console.log(e);
            }
        }

        TrainGame.environment.Switch.es = [];
        TrainGame.environment.Goal.s = [];
        TrainGame.gameObjects.Train.s = [];
        trainSpawn = build(firstTrackId, null);

        this.getTrainSpawn = function() {
            return trainSpawn;
        };

        this.getSize = function() {
            return {x:rawdata.length, y:rawdata[0].length};
        };
    }

    function TrainGame(graphics) {
        var trainMap = null;
        this.graphics = graphics;
        this.trainMap = null;
        this.flipMap = false;
        var gridSize = 0;
        var viewGridSize = 0;
        var running = false;

        this.setMap = function(map) {
            trainMap = this.trainMap = map;
        };

        this.start = function() {
            running = true;
            graphics.start();
            // move clickHandler so it acts as a mask to the graphics
            /*var stageRect = graphics.getRenderer().view.getClientRects()[0];
             clickHandler.style.top = stageRect.top + "px";
             clickHandler.style.left = stageRect.left + "px";
             clickHandler.style.width = stageRect.width + "px";
             clickHandler.style.height = stageRect.height + "px";*/
        };

        this.stop = function() {
            graphics.stop();
            running = false;
        };

        this.isRunning = function() {
            return running;
        };

        this.getTrainSpawn = function() {
            return trainMap.getTrainSpawn();
        };

        this.setGridSize = function(gridSize_) {
            gridSize = gridSize_;
            GraphicsEngine.graphics.TextureGenerator.setGridSize(gridSize);
        };

        this.getGridSize = function() {
            return gridSize;
        };

        this.setViewGridSize = function(vgs) {
            viewGridSize = vgs;
        };

        this.getViewGridSize = function() {
            return viewGridSize;
        };

        this.mouseDown = function(event) {
            var g = graphics.getRenderer().view;
            var x = event.clientX + g.clientLeft;
            var y = event.clientY + g.clientTop;
            var nearestSwitch = null;
            var shortestDistanceSquare = g.clientWidth * g.clientWidth * 2 + g.clientHeight * g.clientHeight * 2;
            for (var i = 0; i < TrainGame.environment.Switch.es.length; i++) {
                var sw = TrainGame.environment.Switch.es[i];
                if (sw == undefined) continue;
                var d = Math.pow(x - sw.getLane().getPosVector().getX(), 2) + Math.pow(y - sw.getLane().getPosVector().getY(), 2);
                if (d < shortestDistanceSquare) {
                    shortestDistanceSquare = d;
                    nearestSwitch = sw;
                }
            }
            if (nearestSwitch != null) {
                performSwitchChange(nearestSwitch);
            }
        };

        var performSwitchChange = this.performSwitchChange = function(sw) {
            TrainGame.latencyCalculator.onRequest("switchChange" + sw.id);
            var newSwitchedTo = sw.getNextLaneIndex();
            var oldSwitchedto = sw.getSwitchedTo();
            setTimeout(function(sw, swto){
             return function(){sw.change(swto);};
             }(sw, newSwitchedTo), TrainGame.latencyCalculator.getCurrentLatency() / 2);
            serverConnection.send({type:"answer", answer:{switch: sw.id, switchedTo:newSwitchedTo}}, function(msg) {
                if (msg.isCorrect === undefined || msg.isCorrect === True) {
                    sw.change(swto);
                } else {
                    console.log("non-correct switch change answer");
                    sw.change(oldSwitchedto);
                }
            });
        };
    }
    TrainGame.TGMPATH = "graphics/tgm/";
    TrainGame.trainTexture = GraphicsEngine.graphics.TextureGenerator.generate(TrainGame.TGMPATH + "train.png");
    TrainGame.straightTexture = GraphicsEngine.graphics.TextureGenerator.generate(TrainGame.TGMPATH + "straight.png");
    TrainGame.turnTexture = GraphicsEngine.graphics.TextureGenerator.generate(TrainGame.TGMPATH + "turn.png");
    TrainGame.goalTexture = GraphicsEngine.graphics.TextureGenerator.generate(TrainGame.TGMPATH + "goal.png");
    TrainGame.starTexture = GraphicsEngine.graphics.TextureGenerator.generate(TrainGame.TGMPATH + "star.png");

    TrainGame.idColors = ["8808ff", "ffffff", "00ff00", "ff0000", "ffff00", "ff00ff", "00dfdf", "ffffff", "ff8800", "333333"];

    TrainGame.latencyCalculator = new LatencyCalculator();

    // OBSERVERS =====================================================================================================

    var trainMapObserver = new Observer("exercise", function(msg) {
        if (msg.exercise.type != "trainMap") return;
        fitGraphics(msg.exercise.trainMap.length, msg.exercise.trainMap[0].length);
        var trainMap = new Map(msg.exercise.trainMap, msg.exercise.firstTrackId);
        trainGame.setMap(trainMap);
        trainGame.start();
        trainGame.graphics.cacheStaticEnvironment();
        // send confirmation
        setTimeout(function() {
            serverConnection.send({type:"confirm"});
        }, 1000);
    });

    var newtrainObserver = new Observer("newTrain", function(msg) {
        var estimatedSpawnTime = new Date().getTime() - TrainGame.latencyCalculator.getCurrentLatency() * 2;
        new TrainGame.gameObjects.Train(msg.trainId, msg.destinationId, msg.speed, trainGame.getTrainSpawn(), estimatedSpawnTime);
    });

    var switchChangedObserver = new Observer("switchChange", function(msg) {
        TrainGame.latencyCalculator.onAnswer("switchChange" + msg.switchChange.switchId);
        TrainGame.environment.Switch.es[msg.switchChange.switchId].change(msg.switchChange.switchedTo);
    });

    var trainDecisionObserver = new Observer("trainDecision", function(msg) {
        var sw = TrainGame.environment.Switch.es[msg.switchId];
        var successor = sw.getSuccessor(msg.switchedTo);
        var lane = sw.getLane(msg.switchedTo);
        TrainGame.gameObjects.Train.s[msg.trainId].correctMovement(sw, successor, lane);
    });

    var trainArrivedObserver = new Observer("trainArrived", function(msg) {
        var train = TrainGame.gameObjects.Train.s[msg.trainId];
        var onArriveInGoal = function(msg, train) {
            return function() {
                if (msg.success) {
                    new TrainGame.particles.Star(TrainGame.environment.Goal.s[msg.goalId].getLane().getExitCoords());
                    train.fadeOut();
                } else {
                    train.explode();
                }
            };
        }(msg, train);
        train.arrive(onArriveInGoal);
    });

    function explodeTrains() {
        var timeout = 0;
        var timeoutStep = 500;
        for (var i = 0; i < TrainGame.gameObjects.Train.s.length; i++) {
            var train = TrainGame.gameObjects.Train.s[i];
            if (train == undefined || train.getGraphicsArrived()) continue;
            setTimeout((function(train){return function(){train.explode();};})(train), timeout);
            TrainGame.gameObjects.Train.s[i] = undefined;
            timeout += timeoutStep;
        }
    }

    var trainWaveObserver = new Observer("trainWaveCompleted", function(msg) {
        var success = msg.success; // wave survived
        var waveNo = msg.waveNo;
        var reward = msg.reward;
        var text;
        if (success) {
            text = new TrainGame.particles.Text("Wave " + waveNo + " survived!", 0xffffff);
        } else {
            text = new TrainGame.particles.Text("You lost!!", 0xff9999);
        }
        explodeTrains();
        text.fadeIn();
        setTimeout(function(text){
            return function(){text.fadeOut();};
        }(text), 2000);
    });

    var messageText = null;

    var textObserver = new Observer("message", function(msg) {
        if (messageText != null) messageText.fadeOut();
        messageText = new TrainGame.particles.Text(msg.message, 0xffffff);
        messageText.fadeIn();
        setTimeout(function(text){
            return function(){text.fadeOut();};
        }(messageText), 2000);
    });
    
    window.TrainGame = TrainGame;
    window.TrainGame.instance = null;

})();
