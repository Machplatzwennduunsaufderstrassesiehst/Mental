// static
var Tracks = {};
Tracks.RES = 200;// TODO, must be set related to screen width and height

function Track(i, j, predecessor, successor) {
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
        return successor;
    }
    
}

function Switch(type, i, j, predecessor, successor, id, switchLanes, switchedTo) {
    Track.call(this, i, j, predecessor, successor);
    this.type = "switch";
    
    
}
Switch.prototype = new Track;
Switch.prototype.constructor = Switch;

