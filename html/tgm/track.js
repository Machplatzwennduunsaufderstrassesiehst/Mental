
function Track(i, j) {    
    var predecessor = null;
    var successor = null;
    
    var entranceSide = 0;
    var exitSide = 0;
    // Vector that points to the sprite's position
    var posVector = new Vector(i*Track.trackSize, j*Track.trackSize);
    
    var relMidVector = new Vector(Track.trackSize/2, Track.trackSize/2);
    // Vector that points to the middle of the track elements
    var midVector = posVector.copy();
    midVector.add(relMidVector); 
    
    var trackSprite = null;
    
    this.type = "track";
    
    this.getX = function() {
        return i;
    }
    this.getY = function() {
        return j;
    }
    
    // get the position Vector that points to the specified side of the element
    var getSideCoords = this.getSideCoords = function(side) {
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
        console.log("Track.getSuccessor");
        return successor;
    }
    
    // 0 is the top side for the entranceSide, exitSide properties
    var buildSprite = this.buildSprite = function(predecessorCoords, successorCoords, onload) {
        var dx1 = i - predecessorCoords.x;
        var dx2 = successorCoords.x - i;
        var dy1 = j - predecessorCoords.y;
        var dy2 = successorCoords.y - j;
        log("dx1: " + dx1 + "  dy1: " + dy1);
        log("dx2: " + dx2 + "  dy2: " + dy2);
        entranceSide = 2 * Math.abs(dx1) + dx1 + Math.abs(dy1) - dy1;
        exitSide =     2 * Math.abs(dx2) - dx2 + Math.abs(dy2) + dy2;
        var d = exitSide - entranceSide;
        if (Math.abs(d) % 2 == 0) {
            log("this is a straight");
            d = 0;
        } else {
            log("this is a turn");
            if (Math.abs(d) > 2) d = -Math.sign(d);
        }
        log("entranceSide: " + entranceSide + "  exitSide: " + exitSide + "  d: " + d);
        var rotation, png;
        rotation = entranceSide;
        switch (d) {
            case 0: // straight
                png = "straight";
                break;
            case 1: // left turn
                png = "turn";
                log("turn left");
                break;
            case -1: // right turn
                log("turn right");
                png = "turn";
                rotation -= 1;
                rotation = (rotation + 4) % 4;
                break;
        }
        rotation *= Math.PI / 2;
        log("png: " + png + "   rotation: " + rotation);
        
        png = "graphics/tgm/" + png + ".png";
        log(png);
        var sprite = new PIXI.Sprite.fromImage(png);
        
        sprite.position = new PIXI.Point(posVector.getX(), posVector.getY());
        sprite.pivot = new PIXI.Point(Track.trackSize, Track.trackSize);
        sprite.rotation = rotation;
        sprite.scale = new PIXI.Point(Track.trackSize/200, Track.trackSize/200);
        
        onload(sprite);
        /*
        var createSprite = function(loader, resources) {
            var sprite = new PIXI.Sprite(PIXI.loader.resources[png].texture);
            
            sprite.position = new PIXI.Point(posVector.getX(), posVector.getY());
            sprite.pivot = new PIXI.Point(relMidVector.getX(), relMidVector.getY());
            sprite.rotation = rotation;
            
            onload(sprite);
        }
        if (PIXI.loader.resources[png] == undefined) {
            PIXI.loader
                .add(png)
                .load(createSprite);
        } else {
            createSprite(null, null);
        }*/
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
            trainGame.graphics.addEnvironment(sprite);
        });
    }
    
}
// static
Track.trackSize = 100;// TODO, must be set relative to screen width and height

function Switch(id, i, j) {
    Track.call(this, i, j);
    this.type = "switch";
    this.id = id;
    Switch.es[id] = this;
    
    // the possible lanes (Sprite objects) this switch has
    var lanes = [];
    
    var successors = null;
    
    // overwritten
    this.getSuccessor = function() {
        log("Switch.getSuccessor");
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
        for (var i = 0; i < lanes.length; i++) {
            lanes[i].alpha = 0.2;
        }
        lanes[switchedTo].alpha = 1;
    }
    
    this.initialize = function() {
        var predecessor = this.getPredecessor();
        if (this.hasPredecessor()) {
            var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
        } else {
            var predecessorCoords = {x:i, y:j-1};
        }
        for (var i = 0; i < successors.length; i++) {
            var successorCoords = {x:successors[i].getX(), y:successors[i].getY()};
            var onl = function(index) {
                return function(sprite) {
                    lanes[index] = sprite;
                    trainGame.graphics.addEnvironment(sprite);
                }
            }(i);
            this.buildSprite(predecessorCoords, successorCoords, onl);
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

