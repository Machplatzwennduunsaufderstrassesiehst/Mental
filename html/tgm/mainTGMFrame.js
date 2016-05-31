
/* global byID, serverConnection, Switch, Goal, Train, GameGraphics, TextureGenerator, PIXI, particles */

var mainTrainGameFrame = new Frame("mainTrainGameFrame");

var trainGame = null;

var clickHandler = undefined;

mainTrainGameFrame.setOnOpen(function() {
    byID("page_").style.display = "none";
    
    serverConnection.addObserver(trainMapObserver);
    serverConnection.addObserver(newtrainObserver);
    serverConnection.addObserver(switchChangedObserver);
    serverConnection.addObserver(trainDecisionObserver);
    serverConnection.addObserver(trainArrivedObserver);
    serverConnection.addObserver(trainWaveObserver);
    
    if (trainGameGraphics != undefined) {
        trainGameGraphics.stop();
        trainGameGraphics.clearEnvironment();
    }
    var trainGameGraphics = new GameGraphics("mainTrainGameFrame");
    trainGame = new TrainGame(trainGameGraphics);
    
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
    
    trainGame.stop();
    byID("mainTrainGameFrame").innerHTML = "";
    
    clickHandler.onclick = function(){};
    clickHandler.style.display = "none";
});

// FUNCTIONALITY =================================================================================================

function fitGraphics(xMapSize, yMapSize) {
    var frame = byID("mainTrainGameFrame");
    var frameRatio = frame.clientWidth / frame.clientHeight;
    var mapRatio = xMapSize / yMapSize;
    var viewGridSize;
    if (frameRatio > mapRatio) {
        viewGridSize = frame.clientHeight / yMapSize;
    } else {
        viewGridSize = frame.clientWidth / xMapSize;
    }
    var gridSize = viewGridSize / 2;
    if (gridSize > 90) gridSize = 90;
    var stageScale = viewGridSize / gridSize;
    trainGame.graphics.resizeRenderer(xMapSize*viewGridSize, yMapSize*viewGridSize);
    trainGame.graphics.setStageScale(stageScale);
    trainGame.setGridSize(gridSize);
    trainGame.setViewGridSize(viewGridSize);
} 

function Map(rawdata) {
    // initialize array to hold the track objects later
    var trackArray = [null];  
    var trainSpawn = null; // reference to the first track of the double linked list
    var firstTrackId = 0;
    
    for (var i = 0; i < rawdata.length; i++) {
        for (var j = 0; j < rawdata[i].length; j++) {
            var trackData = rawdata[i][j];
            var trackId = Number(trackData.id);
            if (trackId < 0 || trackId == undefined) continue;
            if (trackArray[trackId] != undefined) {
                log("Trackid " + trackId + " doppelt vergeben");
            }
            trackArray[trackId] = trackData;
            if (trackData.xpos == 1 && trackData.ypos == 1) {
                firstTrackId = trackId;
            }
        }
    }
    
    // recursive strategy to build the track objects needed for the map
    function build(trackId, predecessor) {
        //if (!trackArray[trackId]) return null;
        try {
            var trackData = trackArray[trackId];
        } catch (e) {
            log(e);
            return null;
        }
        try {
            switch(trackData.trackType) {
                case "blocked":return null;
                case "track":
                    var successorId = trackData.successorId;
                    var t = new Track(trackData.xpos, trackData.ypos);
                    var futureSuccessor = build(successorId, t);
                    t.setPredecessor(predecessor);
                    t.setSuccessor(futureSuccessor);
                    t.initialize();
                    return t;
                case "switch":
                    var successorIds = trackData.successorIds;
                    var successors = [];
                    var sw = new Switch(trackData.switchId, trackData.xpos, trackData.ypos);
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
                    var g = new Goal(goalId, trackData.xpos, trackData.ypos);
                    g.setPredecessor(predecessor);
                    g.initialize();
                    return g;
            }
        } catch (e) {
            console.log(e);
        }
    }
    
    Switch.es = [];
    Goal.s = [];
    Train.s = [];
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
        TextureGenerator.setGridSize(gridSize);
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
        for (var i = 0; i < Switch.es.length; i++) {
            var sw = Switch.es[i];
            if (sw == undefined) continue;
            var switchRect = sw.getRect();
            if (switchRect.contains(x, y)) {
                performSwitchChange(sw);
            }
        }
    };
    
    var performSwitchChange = this.performSwitchChange = function(sw) {
        TrainGame.latencyCalculator.onRequest("switchChange" + sw.id);
        var newSwitchedTo = sw.getNextLaneIndex();
        setTimeout(function(sw, swto){
            return function(){sw.change(swto);};
        }(sw, newSwitchedTo), TrainGame.latencyCalculator.getCurrentLatency() / 2);
        serverConnection.send({type:"answer", answer:{switch: sw.id, switchedTo:newSwitchedTo}});
    };
}
TrainGame.TGMPATH = "graphics/tgm/";
TrainGame.trainTexture = TextureGenerator.generate(TrainGame.TGMPATH + "train.png");
TrainGame.straightTexture = TextureGenerator.generate(TrainGame.TGMPATH + "straight.png");
TrainGame.turnTexture = TextureGenerator.generate(TrainGame.TGMPATH + "turn.png");
TrainGame.goalTexture = TextureGenerator.generate(TrainGame.TGMPATH + "goal.png");
TrainGame.starTexture = TextureGenerator.generate(TrainGame.TGMPATH + "star.png");

TrainGame.idColors = ["8808ff", "00ff00", "ff0000", "ffff00", "ff00ff", "00dfdf", "ffffff", "ff8800"];

TrainGame.latencyCalculator = new LatencyCalculator();

// OBSERVERS =====================================================================================================

var trainMapObserver = new Observer("exercise", function(msg) {
    if (msg.exercise.type != "trainMap") return;
    fitGraphics(msg.exercise.trainMap.length, msg.exercise.trainMap[0].length);
    trainMap = new Map(msg.exercise.trainMap);
    trainGame.setMap(trainMap);
    trainGame.start();
    trainGame.graphics.cacheStaticEnvironment();
    var text = new particles.Text("Game started!", 0xffffff);
    text.fadeIn();
    setTimeout(
        function(text) {
            return function() {text.fadeOut();}
        }(text)
    , 2000);
    // send confirmation
    setTimeout(function() {
        serverConnection.send({type:"confirm"});
    }, 1000);
});

var newtrainObserver = new Observer("newTrain", function(msg) {
    new Train(msg.trainId, msg.destinationId, msg.speed, trainGame.getTrainSpawn());
});

var switchChangedObserver = new Observer("switchChange", function(msg) {
    TrainGame.latencyCalculator.onAnswer("switchChange" + msg.switchChange.switchId);
    Switch.es[msg.switchChange.switchId].change(msg.switchChange.switchedTo);
});

var trainDecisionObserver = new Observer("trainDecision", function(msg) {
    var sw = Switch.es[msg.switchId];
    var successor = sw.getSuccessor(msg.switchedTo);
    var lane = sw.getLane(msg.switchedTo);
    Train.s[msg.trainId].correctMovement(sw, successor, lane);
});

var trainArrivedObserver = new Observer("trainArrived", function(msg) {
    var train = Train.s[msg.trainId];
    var onArriveInGoal = function(msg, train) {
        return function() {
            if (msg.success) {
                new particles.Star(Goal.s[msg.goalId].getLane().getExitCoords());
                train.fadeOut();
            } else {
                train.explode();
            }
        };
    }(msg, train);
    train.arrive(onArriveInGoal);
});

// TODO
var trainWaveObserver = new Observer("trainWaveCompleted", function(msg) {
    var success = msg.success; // wave survived
    var waveNo = msg.waveNo;
    var reward = msg.reward;
    var text;
    if (success) {
        var timeout = 0;
        var timeoutStep = 100;
        for (var i = 0; i < Train.s.length; i++) {
            var train = Train.s[i];
            if (train == undefined) continue;
            setTimeout((function(train){return function(){train.explode();};})(train), timeout);
            Train.s[i] = undefined;
            timeout += timeoutStep;
        }
        text = new particles.Text("Wave " + waveNo + " survived!", 0xffffff);
    } else {
        text = new particles.Text("You lost!!", 0xffaaaa);
    }
    text.fadeIn();
    setTimeout(function(text){
        return function(){text.fadeOut();};
    }(text), 2000);
});
