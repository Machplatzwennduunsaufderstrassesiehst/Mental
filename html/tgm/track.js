// static
var Tracks = {};
Tracks.RES = 200;// TODO, must be set related to screen width and height

function Track(i, j) {
    var predecessor = null;
    var successor = null;
    var dimension = this.dimension = {};
    
    this.type = "track";
      
    function calculateDimensions() {
        var res = Tracks.RES;
        dimension.middle = new Position(i*res, j*res, 0);
        dimension.middle.move(res/2, res/2);
        
        dimension.topleft = new Position(i*res, j*res, 0);
        
        dimension.topright = new Position(i*res, j*res, 0);
        dimension.topright.move(res, 0);
        
        dimension.bottomleft = new Position(i*res, j*res, 0);
        dimension.bottomleft.move(0, res);
        
        dimension.bottomright = new Position(i*res, j*res, 0);
        dimension.bottomright.move(res, res);
        
        dimension.entrance = new Position(i*res, j*res, 0);
    }
    
    calculateDimensions();
    
    this.hasPredecessor = function() {
        return predecessor != null;
    }
    
    this.hasSuccessor = function() {
        return successor != null;
    }
    
    this.setPredecessor = function(p) {
        predecessor = p;
    }
    
    this.setSuccessor = function(s) {
        successor = s;
    }
    
    this.getPredecessor = function() {
        return predecessor;
    }
    
    this.getSuccessor = function() {
        console.log("Track.getSuccessor");
        return successor;
    }
    
    function buildSprite() {
        
    }
    
    function buildGraphicObject() {
        
    }
    
}

function Switch(id, i, j, successors, switchedTo) {
    Track.call(this, i, j);
    this.type = "switch";
    this.id = id;
    
    // overwritten
    this.getSuccessor = function() {
        log("Switch.getSuccessor");
        return successors[switchedTo];
    }
    
    // overwritten
    this.setSuccessor = function(s) {
        switchedTo = successors.indexOf(s);
    }
    
    this.change = function(newSwitchedTo) {
        switchedTo = newSwitchedTo;
    }
}
Switch.prototype = new Track;
Switch.prototype.constructor = Switch;


function Goal(i, j) {
    Track.call(this, i, j);
    
    // overwritten
    this.getSuccessor = function() {
        return false;
    }
}
Goal.prototype = new Track;
Goal.prototype.constructor = Goal;
