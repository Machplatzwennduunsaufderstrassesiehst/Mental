
var mainTrainGameFrame = new Frame("mainTrainGameFrame");

var trainGame = null;

mainTrainGameFrame.setOnOpen(function() {
   byID("page_").style.display = "none";
   serverConnection.addObserver(trainMapObserver);
   trainGame = new TrainGame();
});

mainTrainGameFrame.setOnClose(function() {
   byID("page_").style.display = "block";
   serverConnection.removeObserver(trainMapObserver);
   
   
   trainGame = null;
});

// FUNCTIONALITY =================================================================================================

function Map(rawdata) {
    var BLOCKED = 8;
    var BLOCKED2 = 0;
    var SWITCH = 9;

    // initialize array to hold the track objects later
    var mapArray = [];    
    for (var i = 0; i < rawdata.length; i++) {
        mapArray.push([]);
        for (var j = 0; j < rawdata[i].length; j++) {
            mapArray[i].push(null);
        }
    }
    log(mapArray);
    
    // recursive strategy to build the track object needed for the map
    function build(i, j, predecessor) {
        log("i: " + i + "  j:" + j);
        if (mapArray[i][j] != null) return;
        try {
            var trackData = rawdata[i][j];
        } catch (e) {
            log(e);
            return false;
        }
        log(trackData);
        switch(trackData.trackType) {
            case "blocked":return false;
            case "track":
                var i2 = trackData.successorPosition.xpos;
                var j2 = trackData.successorPosition.ypos;
                var futureSuccessor = build(i2, j2);
                var t = new Track(trackData.xpos, trackData.ypos);
                t.setPredecessor(predecessor);
                t.setSuccessor(futureSuccessor);
                t.initialize();
                return t;
            case "switch":
                var successorPositions = trackData.successorList;
                var successors = [];
                for (var s = 0; s < successorPositions.length; s++) {
                    var i2 = successorPositions[s].xpos;
                    var j2 = successorPositions[s].ypos;
                    successors[s] = build(i2, j2);
                }
                var s = new Switch(trackData.switchId, trackData.xpos, trackData.ypos, successors, trackData.switchedTo);
                s.setPredecessor(predecessor);
                s.initialize();
                return s;
            case "goal":
                var goalId = trackData.goalId;
                var g = new Goal(trackData.xpos, trackData.ypos);
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

function TrainGame() {
    var trainMap = null;
    this.trainMap = null;
    
    this.setMap = function(map) {
        trainMap = this.trainMap = map;
    }
}

// OBSERVERS =====================================================================================================

var trainMapObserver = new Observer("exercise", function(msg) {
   if (msg.exercise.type != "trainMap") return;
   trainMap = new Map(msg.exercise.trainMap);
   trainGame.setMap(trainMap);
});
