
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
    var gridSize;
    if (frameRatio > mapRatio) {
        gridSize = frame.clientHeight / yMapSize;
    } else {
        gridSize = frame.clientWidth / xMapSize;
    }
    trainGame.graphics.resize(xMapSize*gridSize, yMapSize*gridSize);
    trainGame.setGridSize(gridSize);
} 

function Map(rawdata) {
    // initialize array to hold the track objects later
    var mapArray = [];  
    var trainSpawn = null;
      
    for (var i = 0; i < rawdata.length; i++) {
        mapArray.push([]);
        for (var j = 0; j < rawdata[i].length; j++) {
            mapArray[i].push(null);
        }
    }
    log(mapArray);
    
    // recursive strategy to build the track objects needed for the map
    function build(i, j, predecessor) {
        if (mapArray[i][j] != null) return null;
        try {
            var trackData = rawdata[i][j];
        } catch (e) {
            log(e);
            return null;
        }
        switch(trackData.trackType) {
            case "blocked":return null;
            case "track":
                var i2 = trackData.successorPosition.xpos;
                var j2 = trackData.successorPosition.ypos;
                var t = new Track(trackData.xpos, trackData.ypos);
                var futureSuccessor = build(i2, j2, t);
                t.setPredecessor(predecessor);
                t.setSuccessor(futureSuccessor);
                t.initialize();
                return t;
            case "switch":
                var successorPositions = trackData.successorList;
                var successors = [];
                var sw = new Switch(trackData.switchId, trackData.xpos, trackData.ypos);
                for (var s = 0; s < successorPositions.length; s++) {
                    var i2 = successorPositions[s].xpos;
                    var j2 = successorPositions[s].ypos;
                    successors[s] = build(i2, j2, sw);
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
    }
    
    log("START MAP BUILD");
    Switch.es = [];
    Goal.s = [];
    Train.s = [];
    trainSpawn = build(1, 1, null);
    
    this.getTrainSpawn = function() {
        return trainSpawn;
    };
    
    this.getSize = function() {
        return {x:mapArray.length, y:mapArray[0].length};
    };
}

function TrainGame(graphics) {
    var trainMap = null;
    this.graphics = graphics;
    this.trainMap = null;
    var gridSize = 0;
    var running = false;
    
    this.setMap = function(map) {
        trainMap = this.trainMap = map;
    };
    
    this.start = function() {
        running = true;
        graphics.start();
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
    };
    
    this.getGridSize = function() {
        return gridSize;
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
        serverConnection.send({type:"answer", answer:{switch: sw.id}});
    };
}
TrainGame.TGMPATH = "graphics/tgm/";
TrainGame.trainTexture = TextureGenerator.generate(TrainGame.TGMPATH + "train.png");
TrainGame.straightTexture = TextureGenerator.generate(TrainGame.TGMPATH + "straight.png");
TrainGame.turnTexture = TextureGenerator.generate(TrainGame.TGMPATH + "turn.png");
TrainGame.goalTexture = TextureGenerator.generate(TrainGame.TGMPATH + "goal.png");
TrainGame.starTexture = TextureGenerator.generate(TrainGame.TGMPATH + "star.png");

TrainGame.idColors = ["ff0000", "00ff00", "0000ff", "ffff00", "ff00ff", "00ffff", "ffffff", "000000"];

// OBSERVERS =====================================================================================================

var trainMapObserver = new Observer("exercise", function(msg) {
    if (msg.exercise.type != "trainMap") return;
    fitGraphics(msg.exercise.trainMap.length, msg.exercise.trainMap[0].length);
    trainMap = new Map(msg.exercise.trainMap);
    trainGame.setMap(trainMap);
    trainGame.start();
    trainGame.graphics.cacheStaticEnvironment();
    // send confirmation
    setTimeout(function() {
        serverConnection.send({type:"confirm"});
    }, 1000);
});

var newtrainObserver = new Observer("newTrain", function(msg) {
    new Train(msg.trainId, msg.destinationId, msg.speed, msg.color, trainGame.getTrainSpawn());
});

var switchChangedObserver = new Observer("switchChange", function(msg) {
    Switch.es[msg.switchChange.switchId].change(msg.switchChange.switchedTo);
});

var trainDecisionObserver = new Observer("trainDecision", function(msg) {
    var sw = Switch.es[msg.switchId];
    var successor = sw.getSuccessor(msg.switchedTo);
    var lane = sw.getLane(msg.switchedTo);
    Train.s[msg.trainId].correctMovement(sw, successor, lane);
});

var trainArrivedObserver = new Observer("trainArrived", function(msg) {
    var onArriveInGoal = function(msg) {
        return function() {
            if (msg.success) {
                new particles.Star(Goal.s[msg.goalId].getLane().getExitCoords());
            }
        }
    }(msg);
    Train.s[msg.trainId].arrive(onArriveInGoal);
});
