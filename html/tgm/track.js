// static
var Tracks = {};
Tracks.RES = 200;// TODO, must be set related to screen width and height

function Track(i, j) {    
    var predecessor = null;
    var successor = null;
    var dimension = this.dimension = {};
    
    var entranceSide = 0;
    var exitSide = 0;
    var container = new PIXI.Container();
    container.position.x = i * Tracks.RES;
    container.position.y = j * Tracks.RES;
    var trackSprite = null;
    
    this.type = "track";
    
    this.getX = function() {
        return i;
    }
    this.getY = function() {
        return j;
    }
    this.getEntranceCoords() {
        var deg = ((entranceSide) % 4) * Math.PI/2;
        log("entranceSide: " + entranceSide + "--> deg: " + deg);
        var v = {x:Math.sin(deg), y:-Math.cos(deg)};
        log("(" + v.x + ", " + v.y + ")");
    }
    
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
    function buildSprite(predecessorCoords, successorCoords, onload) {
        var dx1 = i - predecessorCoords.x;
        var dx2 = successorCoords.x - i;
        var dy1 = j - predecessorCoords.y;
        var dy2 = successorCoords.y - j;
        log("dx1: " + dx1 + "  dy1: " + dy1);
        log("dx2: " + dx2 + "  dy2: " + dy2);
        entranceSide = 2 * Math.abs(dx1) + dx1 + Math.abs(dy1) - dy1;
        exitSide = 2 * Math.abs(dx2) - dx2 + Math.abs(dy2) - dy2;
        var d = exitSide - entranceSide;
        if (Math.abs(d) % 2 == 0) {
            if (Math.abs(d) > 2) d -= Math.sign(d) * 4;
            log("entranceSide: " + entranceSide + "  exitSide: " + exitSide + "  d: " + d);
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
        PIXI.loader
        .add("/graphics/tgm/" + png + ".png")
        .load(function(loader, resources) {
            var sprite = new PIXI.Sprite(resources[png].texture);
            sprite.rotation = rotation;
            container.addChild(sprite);
            onload(sprite);
        });
    }
    
    // to be called after successor and predecessor are set
    this.initialize = function() {
        if (hasPredecessor()) {
            var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
        } else {
            log("no predecessor");
            var predecessorCoords = {x:i, y:j-1};
        }
        if (hasSuccessor()) {
            var successorCoords = {x:successor.getX(), y:successor.getY()};
        } else {
            log("no successor");
            var successorCoords = {x:i+1, y:j};
        }
        buildSprite(predecessorCoords, successorCoords, function(sprite) {
            trackSprite = sprite;
        });
    }
    
}

function Switch(id, i, j, successors, switchedTo) {
    Track.call(this, i, j);
    this.type = "switch";
    this.id = id;
    
    var lanes = [];
    
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
    
    this.initialize = function() {
        if (hasPredecessor()) {
            var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
        } else {
            var predecessorCoords = {x:i, y:j-1};
        }
        for (var i = 0; i < successors.length; i++) {
            var successorCoords = {x:successors[i].getX(), y:successors.getY()};
            var onl = function(index) {
                log("closure initialized");
                return function(sprite) {
                    log("returned function of the closure has been called");
                    lanes[index] = sprite;
                }
            }(i);
            buildSprite(predecessorCoords, successorCoords, onl);
        }
    }
}
Switch.prototype = new Track;
Switch.prototype.constructor = Switch;


function Goal(i, j) {
    Track.call(this, i, j);
    
    this.type = "goal";
    
    // overwritten
    this.getSuccessor = function() {
        return false;
    }
}
Goal.prototype = new Track;
Goal.prototype.constructor = Goal;
