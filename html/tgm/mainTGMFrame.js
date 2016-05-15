
var mainTrainGameFrame = new Frame("mainTrainGameFrame");

var trainGame = null;

mainTrainGameFrame.setOnOpen(function() {
    byID("page_").style.display = "none";
    
    serverConnection.addObserver(trainMapObserver);
    serverConnection.addObserver(newtrainObserver);
    serverConnection.addObserver(switchChangedObserver);
    //serverConnection.addObserver(trainDecisionObserver);
    
    if (trainGameGraphics != undefined) {
        trainGameGraphics.stop();
        trainGameGraphics.clearEnvironment();
    }
    var trainGameGraphics = new GameGraphics();
    trainGame = new TrainGame(trainGameGraphics);
    trainGame.setGridSize(90);
    trainGame.start();
    
    byID("mainTrainGameFrame").onmousedown = trainGame.mouseDown;
});

mainTrainGameFrame.setOnClose(function() {
    byID("page_").style.display = "block";
    
    serverConnection.removeObserver(trainMapObserver);
    serverConnection.removeObserver(newtrainObserver);
    serverConnection.removeObserver(switchChangedObserver);
    serverConnection.removeObserver(trainDecisionObserver);
    
    trainGame.stop();
    byID("mainTrainGameFrame").innerHTML = "";
    trainGame = null;
});

// FUNCTIONALITY =================================================================================================

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
    }
    
    this.getSize = function() {
        return {x:mapArray.length, y:mapArray[0].length};
    }
}

function TrainGame(graphics) {
    var trainMap = null;
    this.graphics = graphics;
    this.trainMap = null;
    this.trains = [];
    var gridSize = 0;
    
    this.setMap = function(map) {
        trainMap = this.trainMap = map;
        var size = map.getSize();
        graphics.resize(size.x*gridSize, size.y*gridSize);
    }
    
    this.start = function() {
        graphics.start();
    }
    
    this.stop = function() {
        graphics.stop();
    }
    
    this.getTrainSpawn = function() {
        return trainMap.getTrainSpawn();
    }
    
    this.setGridSize = function(gridSize_) {
        gridSize = gridSize_;
    }
    
    this.getGridSize = function() {
        return gridSize;
    }
    
    this.mouseDown = function(event) {
        var clickPoint = new PIXI.Point(event.clientX, event.clientY);
        for (var i = 0; i < Switch.es.length; i++) {
            var sw = Switch.es[i];
            if (sw == undefined) continue;
            var switchRect = sw.getRect();
            if (switchRect.contains(clickPoint)) {
                performSwitchChange(sw);
            }
        }
    }
    
    var performSwitchChange = this.performSwitchChange = function(sw) {
        serverConnection.send({type:"changeSwitch", switchId:sw.id, value:sw.getNextLaneIndex()}); // TODO!!
    }
}
TrainGame.trainTexture = TextureGenerator.generate(GameGraphics.TGMPATH + "train.png");
TrainGame.straightTexture = TextureGenerator.generate(GameGraphics.TGMPATH + "straight.png");
TrainGame.turnTexture = TextureGenerator.generate(GameGraphics.TGMPATH + "turn.png");

// OBSERVERS =====================================================================================================

var trainMapObserver = new Observer("exercise", function(msg) {
    if (msg.exercise.type != "trainMap") return;
    trainMap = new Map(msg.exercise.trainMap);
    trainGame.setMap(trainMap);
    trainGame.graphics.cacheStaticEnvironment();
});

var newtrainObserver = new Observer("newTrain", function(msg) {
    var train = new Train(msg.trainId, msg.destinationId, msg.speed, msg.color, trainGame.getTrainSpawn());
    trainGame.trains.push(train);
});

var switchChangedObserver = new Observer("switchChanged", function(msg) {
    Switch.es[msg.switchId].change(msg.switchedTo);
});

var trainDecisionObserver = new Observer("trainDecision", function(msg) {
    var lane = Switch.es[msg.switchId].getLane(msg.switchedTo);
    var track = Switch.es[msg.switchId];
    Train.s[msg.trainId].setCurrentTrack(track);
    Train.s[msg.trainId].correctLane(lane);
});


