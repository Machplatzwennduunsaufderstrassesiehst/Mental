

// sprite can also be a container
function GraphicObject(sprite) {
    var positions = [];
    
    var sprite = {}; 
    
    /**
     * @param degrees in radian - for left, + for right turn
     */
    var pushTurn = this.pushTurn = function(startPos, radius, degrees, time) {
        var frames = calculateFrameAmount(time);
        var currentRotation = startPos.rotation;
        var desiredRotation = currentRotation + degrees;
        var stepWide = Math.abs(desiredRotation - currentRotation) / frames;
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
            x += 1 / frames * dx;
            y += 1 / frames * dy;
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
        return positions.pop();
    }
}


function makeSprite() {
    
}


