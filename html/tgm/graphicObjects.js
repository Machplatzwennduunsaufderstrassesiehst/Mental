

// sprite can also be a container
function GraphicObject(sprite_) {
    var positionQueue = [new Position(0,0)];
    var movements = {};
    var movementQueue = [];
    
    var sprite = sprite_;
    
    // pop the next position on the position queue and return it
    // called by render loop
    this.move = function() {
        if (sprite == undefined) {
            log("GO.move: sprite still undefined");
            return false;
        }
        var p = positionQueue.pop();
        if (positionQueue.length == 0) {
            if (movementQueue.length > 0) { // no positions on queue, but movements to be extracted to the positionQueue
                var m = movementQueue.pop();
                var steps = m.getSteps();
                for (var i = 0; i < steps.length; i++) {
                    positionQueue.unshift(steps[i]);
                }
            } else { // no movements to be performed, and positionQueue empty => stay at current position (p)
                positionQueue.push(p); 
            }
        }
        if (p == undefined) return false;
        sprite.position.x = p.x;
        sprite.position.y = p.y;
        sprite.rotation = p.rotation;
        return p;
    }
    
    this.setPos = function(position) {;
        positionQueue = [position]; // set positionQueue so it only contains the new position
        movementQueue = [];
    }
    
    this.setSprite = function(sprite_) {
        sprite = sprite_;
    }
    
    this.getSprite = function() {
        return sprite;
    }
    
    this.addMovement = function(key, movement) {
        movements[key] = movement;
    }
    
    this.queueMovement = function(movement) {
        movementQueue.unshift(movement);
    }
    
    this.queueMovementByKey = function(key) {
        movementQueue.unshift(movements[key]);
    }
}

