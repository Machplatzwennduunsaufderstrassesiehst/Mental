

// sprite can also be a container
function GraphicObject(sprite_) {
    var positions = [];
    
    var sprite = sprite_;
    
    /**
     * @param degrees in radian - for left, + for right turn
     */
    var pushTurn = this.pushTurn = function(startPos, radius, degrees, time) {
        var frames = calculateFrameAmount(time);
        var currentRotation = startPos.rotation;
        var desiredRotation = currentRotation + degrees;
        var stepWide = (desiredRotation - currentRotation) / frames;
        var x, y, p;
        for (var r = currentRotation; r < desiredRotation; r += stepWide) {
            x = startPos.x + (Movement.cos(r) * radius - Movement.cos(currentRotation) * radius);
            y = startPos.y + (Movement.sin(r) * radius - Movement.sin(currentRotation) * radius);
            p = new Position(x, y, r);
            positions.unshift(p);
        }
    }
    
    var pushStraight = this.pushStraight = function(startPos, endx, endy, time) {
        var frames = calculateFrameAmount(time);
        var dx = endx - startPos.x;
        var dy = endy - startPos.y;
        var x = startPos.x, 
            y = startPos.y, 
            p;
        for (var f = 0; f < frames; f++) {
            x += dx / frames;
            y += dy / frames;
            p = new Position(x, y, startPos.rotation);
            positions.unshift(p);
        }
    }
    
    // calculates the amount of frames to be rendered in the "time" (in seconds)
    function calculateFrameAmount(time) {
        return time * 60;
    }
    
    // pop the next position on the position queue and return it
    // called by render loop
    this.move = function() {
        var p = positions.pop();
        if (sprite == undefined) {
            console.log("GO.move: sprite still undefined");
            return;
        }
        if (positions.length == 0) positions.push(p);
        sprite.position.x = p.x;
        sprite.position.y = p.y;
        sprite.rotation = p.rotation;
        return p;
    }
    
    this.setSprite(sprite_) {
        sprite = sprite_;
    }
}

function createPNGObject(png, container) {
    if (container == undefined) container = trainGameGraphics.getStage();
    var g = new GraphicObject();
    gameGraphics.addGraphicObject(g);
    PIXI.loader
        .add(png)
        .load(function(loader, resources){
            var sprite = new PIXI.Sprite(resources[png].texture);
            stage.addChild(sprite);
            g.setSprite(sprite);
        });
    return g;
}


