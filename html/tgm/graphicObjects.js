

// sprite can also be a container
function GraphicObject(sprite_) {
    var latestPosition = new Position(0,0);
    var positionQueue = [latestPosition];
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
        var p = latestPosition = positionQueue.pop();
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
    };
    
    this.getPos = function() {
        return latestPosition;
    };
    
    this.setPos = function(position) {;
        positionQueue = [position]; // set positionQueue so it only contains the new position
        movementQueue = [];
    };
    
    this.setSprite = function(sprite_) {
        sprite = sprite_;
    };
    
    this.getSprite = function() {
        return sprite;
    };
    
    this.addMovement = function(key, movement) {
        movements[key] = movement;
    };
    
    this.queueMovement = function(movement) {
        movementQueue.unshift(movement);
    };
    
    this.queueMovementByKey = function(key) {
        movementQueue.unshift(movements[key]);
    };
    
    this.fadeOut = function(onFaded, seconds) {
        var startFading = function(displayObject, onFaded) {
            var frames = calculateFrameAmount(seconds);
            var c = 0;
            function fade() {
                if (c >= frames) {
                    onFaded();
                    return;
                }
                c++;
                setTimeout(fade, seconds * 1000 / frames);
                displayObject.alpha = displayObject.alpha - 1 / frames;
            }
            return function() {
                fade();
            };
        }(sprite, onFaded);
        startFading();
    };
}

