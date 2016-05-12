

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
            return;
        }
        var p = positionQueue.pop();
        sprite.position.x = p.x;
        sprite.position.y = p.y;
        sprite.rotation = p.rotation;
        if (positionQueue.length == 0) {
            if (movementQueue.length > 0) { // no positions on queue, but movements to be extracted to the positionQueue
                var m = movementQueue.pop();
                var steps = m.getSteps();
                for (var i = 0; i < steps.length; i++) {
                    steps[i].move(p.x, p.y);
                    positionQueue.unshift(steps[i]);
                }
            } else { // no movements to be performed, and positionQueue empty => stay at current position (p)
                positionQueue.push(p); 
            }
        }
        return p;
    }
    
    this.setPos = function(x, y) {
        var r = 0;
        if (sprite != undefined) r = sprite.rotation;
        positionQueue = [new Position(x, y, r)]; // set positionQueue to just the new position
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

function createPNGObject(png, container) {
    if (container == undefined) container = trainGame.graphics.getStage();
    var g = new GraphicObject();
    trainGame.graphics.addGraphicObject(g);
    PIXI.loader
        .add(png)
        .load(function(loader, resources){
            var sprite = new PIXI.Sprite(resources[png].texture);
            container.addChild(sprite);
            g.setSprite(sprite);
        });
    return g;
}


