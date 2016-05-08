
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
            var t = rawdata[i][j];
        } catch (e) {
            log(e);
            return false;
        }
        log(t);
        if (t.value == undefined || t.value == null) return false;
        switch (t.value) {
            case BLOCKED: return false;
            case BLOCKED2: return false;
            case SWITCH:
                var s = new Switch(i, j);
                s.setPredecessor(predecessor);
                
                mapArray[i][j] = s;
                
        }
        
    }
    
    build(1,1,null);
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
