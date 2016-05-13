
function Lane(i, j, predecessorCoords, successorCoords) {
    this.i = i;
    this.j = j;
    this.type = null;
    
    var gridSize = trainGame.getGridSize();
    
    var entranceSide = 0;
    var exitSide = 0;
    var direction = 0;
    
    var movement = null;
    
    // Vector that points to the middle of the track elements
    var relMidVector = new Vector(gridSize/2, gridSize/2);
    
    // Vector that points to the sprite's position
    var posVector = new Vector(i*gridSize, j*gridSize);
    posVector.add(relMidVector);
    
    var sprite = null;
    
    // get the position Vector that points to the specified side of the element
    var getSideCoords = this.getSideCoords = function(side) {
        var deg = ((side) % 4) * Math.PI/2;
        var v = new Vector(Math.sin(deg), -Math.cos(deg)); // vector that points to the entranceSide
        v.normalize();
        v.multiply(gridSize/2);
        v.add(posVector);
        return v;
    }
    
    var getStartRotation = this.getStartRotation = function() {
        return entranceSide * Math.PI / 2;
    }
    
    var getEntranceSide = this.getEntranceSide = function() {
        return entranceSide;
    }
    
    var getEntranceCoords = this.getEntranceCoords = function() {
        return getSideCoords(entranceSide);
    }
     
    var getExitCoords = this.getExitCoords = function() {
        return getSideCoords(exitSide);
    }
    
    this.setSwitched = function(bSwitched) {
        sprite.alpha = (bSwitched ? 1 : 0.4);
    }
    
    // only used for Turns!
    this.getTurnRadius = function() {
        return gridSize/2;
    }
    this.getTurnDegrees = function() {
        return direction * Math.PI / 2;
    }
    
    function initializeOrientation() {
        var dx1 = i - predecessorCoords.x;
        var dx2 = successorCoords.x - i;
        var dy1 = j - predecessorCoords.y;
        var dy2 = successorCoords.y - j;
        //log("dx1: " + dx1 + "  dy1: " + dy1);
        //log("dx2: " + dx2 + "  dy2: " + dy2);
        entranceSide = 2 * Math.abs(dx1) + dx1 + Math.abs(dy1) - dy1;
        exitSide =     2 * Math.abs(dx2) - dx2 + Math.abs(dy2) + dy2;
        direction = exitSide - entranceSide;
        if (Math.abs(direction) % 2 == 0) {
            //log("this is a straight");
            direction = 0;
        } else {
            //log("this is a turn");
            if (Math.abs(direction) > 2) direction = -Math.sign(direction);
        }
        //log("entranceSide: " + entranceSide + "  exitSide: " + exitSide + "  d: " + d);
    }
    
    initializeOrientation();
    
    var buildSprite = this.buildSprite = function(onload) {
        var rotation, texture;
        rotation = entranceSide;
        
        switch (direction) {
            case 0: // straight
                this.type = "straight";
                texture = TrainGame.straightTexture;
                break;
            case 1: // left turn
                texture = TrainGame.turnTexture;
                this.type = "turnLeft";
                break;
            case -1: // right turn
                this.type = "turnRight";
                texture = TrainGame.turnTexture;
                rotation -= 1;
                rotation = (rotation + 4) % 4;
                break;
        }
        rotation *= Math.PI / 2;
        //log("png: " + png + "   rotation: " + rotation);
        
        sprite = TextureGenerator.generateSprite(texture);
        
        sprite.position = new PIXI.Point(posVector.getX(), posVector.getY());
        sprite.pivot = TextureGenerator.getSpritePivot(sprite);
        sprite.rotation = rotation;
        
        onload(sprite);
    }
}

function Track(i, j) {    
    var predecessor = null;
    var successor = null;
    
    var lane = null;
    
    this.type = "track";
    
    this.getX = function() {
        return i;
    }
    this.getY = function() {
        return j;
    }
    
    this.getLane = function() {
        return lane;
    }
    
    var hasPredecessor = this.hasPredecessor = function() {
        return predecessor != null;
    }
    
    var hasSuccessor = this.hasSuccessor = function() {
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
    
    // to be called after successor and predecessor are set
    this.initialize = function() {
        if (hasPredecessor()) {
            var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
        } else {
            //log("no predecessor");
            var predecessorCoords = {x:i, y:j-1};
        }
        if (hasSuccessor()) {
            var successorCoords = {x:successor.getX(), y:successor.getY()};
        } else {
            //log("no successor");
            var successorCoords = {x:i+1, y:j};
        }
        lane = new Lane(i, j, predecessorCoords, successorCoords);
        lane.buildSprite(function(sprite) {
            trainGame.graphics.addEnvironment(sprite, true);
        });
    }
    
}
// static
gridSize = 90;// TODO, must be set relative to screen width and height

function Switch(id, i, j) {
    Track.call(this, i, j);
    this.type = "switch";
    this.id = id;
    
    var switchedTo = 0;
    
    Switch.es[id] = this;
    
    // the possible lanes this switch has
    var lanes = [];
    
    var successors = null;
    
    this.getLane = function() {
        return lanes[switchedTo];
    }
    
    // overwritten
    this.hasSuccessor = function() {
        return true;
    }
    
    // overwritten
    this.getSuccessor = function() {
        return successors[switchedTo];
    }
    
    this.setSuccessors = function(s) {
        successors = s;
    }
    
    // overwritten
    this.setSuccessor = function(s) {
        change(successors.indexOf(s));
    }
    
    var change = this.change = function(newSwitchedTo) {
        switchedTo = newSwitchedTo;
        for (var l = 0; l < lanes.length; l++) {
            if (l == switchedTo) {
                lanes[switchedTo].setSwitched(true);
            } else {
                lanes[l].setSwitched(false);
            }
        }
    }
    
    this.initialize = function() {
        var predecessor = this.getPredecessor();
        if (this.hasPredecessor()) {
            var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
        } else {
            var predecessorCoords = {x:i, y:j-1};
        }
        for (var s = 0; s < successors.length; s++) {
            var successorCoords = {x:successors[s].getX(), y:successors[s].getY()};
            var lane = new Lane(i, j, predecessorCoords, successorCoords);
            lanes[s] = lane;
            var onl = function(index) {
                return function(sprite) {
                    trainGame.graphics.addEnvironment(sprite);
                }
            }(s);
            lanes[s].buildSprite(onl);
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

