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
    
    // 0 is the top side for the entranceSide, exitSide properties
    function buildSprite(predecessorCoords, successorCoords) {
        var dx1 = i - predecessorCoords.x;
        var dx2 = successorCoords.x - i;
        var dy1 = j - predecessorCoords.y;
        var dy2 = successorCoords.y - j;
        log("dx1: " + dx1 + "  dy1: " + dy1);
        log("dx2: " + dx2 + "  dy2: " + dy2);
        var entranceSide = 2 * Math.abs(dx1) + dx1 + Math.abs(dy1) - dy1;
        var exitSide = 2 * Math.abs(dx2) - dx2 + Math.abs(dy2) - dy2;
        var d = exitSide - entranceSide;
        if (Math.abs(d) % 2 == 0) {
            if (Math.abs(d) > 2) d -= Math.sign(d) * 4;
            log("entranceSide: " + entranceSide + "  exitSide: " + exitSide + "  d: " + d)
        }
        var rotation, png;
        rotation = entranceSide;
        switch (d) {
            case 0: // straight
                png = "straight";
                break;
            case 1: // left turn
                png = "turn";
                break;
            case -1: // right turn
                png = "turn";
                rotation -= 1;
                rotation = (rotation + 4) % 4;
                break;
        }
        rotation *= Math.PI / 4;
        log("png: " + png + "   rotation: " + rotation);
        
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
