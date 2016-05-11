
var mainTrainGameFrame = new Frame("mainTrainGameFrame");

var trainGame = null;

mainTrainGameFrame.setOnOpen(function() {
   byID("page_").style.display = "none";
   serverConnection.addObserver(trainMapObserver);
   var trainGameGraphics = new GameGraphics();
   trainGame = new TrainGame(trainGameGraphics);
});

mainTrainGameFrame.setOnClose(function() {
   byID("page_").style.display = "block";
   serverConnection.removeObserver(trainMapObserver);
   
   trainGame.stop();
   byID("mainTrainGameFrame").innerHTML = "";
   trainGame = null;
});

// FUNCTIONALITY =================================================================================================

function Map(rawdata) {
    // initialize array to hold the track objects later
    var mapArray = [];    
    for (var i = 0; i < rawdata.length; i++) {
        mapArray.push([]);
        for (var j = 0; j < rawdata[i].length; j++) {
            mapArray[i].push(null);
        }
    }
    log(mapArray);
    
    // recursive strategy to build the track objects needed for the map
    function build(i, j, predecessor) {
        log("i: " + i + "  j:" + j);
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
        
        /*
        switch (t.value) {
            case BLOCKED: return false;
            case BLOCKED2: return false;
            case SWITCH:
                var s = new Switch(i, j);
                s.setPredecessor(predecessor);
                
                mapArray[i][j] = s;
                
        }*/
        
        
    }
    
    build(1, 1, null);
}

function TrainGame(graphics) {
    var trainMap = null;
    this.graphics = graphics;
    this.trainMap = null;
    
    this.setMap = function(map) {
        trainMap = this.trainMap = map;
    }
    
    this.start = function() {
        graphics.start();
    }
    
    this.stop = function() {
        graphics.stop();
    }
}

// OBSERVERS =====================================================================================================

var trainMapObserver = new Observer("exercise", function(msg) {
   if (msg.exercise.type != "trainMap") return;
   trainMap = new Map(msg.exercise.trainMap);
   trainGame.setMap(trainMap);
   trainGame.start();
});

