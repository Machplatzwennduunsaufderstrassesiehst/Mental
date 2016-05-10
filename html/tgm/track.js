
function Track(i, j) {    
    var predecessor = null;
    var successor = null;
    
    var entranceSide = 0;
    var exitSide = 0;
    // Vector that points to the middle of the track element
    var midVector = new Vector(i*Track.trackSize + Track.trackSize/2, j*Track.trackSize + Track.trackSize/2); 
    
    var container = new PIXI.Container();
    container.position.x = i * Track.trackSize;
    container.position.y = j * Track.trackSize;
    var trackSprite = null;
    
    this.type = "track";
    
    this.getX = function() {
        return i;
    }
    this.getY = function() {
        return j;
    }
    
    // get the position Vector that points to the specified side of the element
    function getSideCoords(side) {
        var deg = ((side) % 4) * Math.PI/2;
        log("entranceSide: " + side + "--> deg: " + deg);
        var v = new Vector(Math.sin(deg), -Math.cos(deg)); // vector that points to the entraceSide
        log("(" + v.x + ", " + v.y + ")");
        v.normalize();
        v.multiply(Track.trackSize/2);
        log("(" + v.x + ", " + v.y + ")");
        v.add(midVector);
        return v;
    }
    
    this.getEntranceCoords = function() {
        return getSideCoords(entranceSide);
    }
     
    this.getExitCoords = function() {
        return getSideCoords(exitSide);
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
// static
Track.trackSize = 200;// TODO, must be set relative to screen width and height

function Switch(id, i, j, successors, switchedTo) {
    Track.call(this, i, j);
    this.type = "switch";
    this.id = id;
    Switch.es[id] = this;
    
    // the possible lanes (Sprite objects) this switch has
    var lanes = [];
    
    // overwritten
    this.getSuccessor = function() {
        log("Switch.getSuccessor");
        return successors[switchedTo];
    }
    
    // overwritten
    this.setSuccessor = function(s) {
        change(successors.indexOf(s));
    }
    
    var change = this.change = function(newSwitchedTo) {
        switchedTo = newSwitchedTo;
        for (var i = 0; i < lanes.length; i++) {
            lanes[i].alpha = 0.2;
        }
        lanes[switchedTo].alpha = 1;
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
Switch.es = [];
Switch.prototype = new Track;
Switch.prototype.constructor = Switch;


function Goal(id, i, j) {
    Track.call(this, i, j);
    Goal.s[id] = this;
    
    this.type = "goal";
    
    // overwritten
    this.getSuccessor = function() {
        return false;
    }
}
Goal.s = [];
Goal.prototype = new Track;
Goal.prototype.constructor = Goal;

