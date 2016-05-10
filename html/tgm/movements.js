
function Vector(x, y) {
    this.multiply = function(skalar) {
        x *= skalar;
        y *= skalar;
    }
    
    this.add = function(u) {
        x += u.getX();
        y += u.getY();
    }
    
    this.normalize = function() {
        multiply(1 / length());
    }
    
    var length = this.length = function() {
        return Math.sqrt(x*x + y*y);
    }
    
    this.getX = function(){return x;};
    this.getY = function(){return y;};
}

// used to describe one element of a movement
function Position(x_, y_, r_) {
    var x = this.x = x_;
    var y = this.y = y_;
    var r = this.rotation = r_;
    
    // only move this position relative to the parameter position
    this.move = function(dx, dy) {
        this.x = x = x + dx;
        this.y = y = y + dy;
    }
}

// calculates the amount of frames to be rendered in the "time" (in seconds)
function calculateFrameAmount(time) {
    return time * 60;
}

function StraightMovement(direction, dx, dy, time) {
    var frames = calculateFrameAmount(time);
    var steps = [];
    
    var x = 0, 
        y = 0, 
        p;
    for (var f = 0; f < frames; f++) {
        x += dx / frames;
        y += dy / frames;
        p = new Position(x, y, startPos.rotation);
        steps.push(p);
    }
    
    this.getSteps = function() {
        return steps;
    }
}

/**
 * @param degrees in radian - for left, + for right turn
 */
function TurnMovement(startRotation, radius, degrees, time) {
    var steps = [];
    
    var frames = calculateFrameAmount(time);
    var desiredRotation = startRotation + degrees;
    var stepWide = (desiredRotation - startRotation) / frames;
    var x, y, p;
    for (var r = startRotation; r < desiredRotation; r += stepWide) {
        x = (Movement.cos(r) * radius - Movement.cos(startRotation) * radius);
        y = (Movement.sin(r) * radius - Movement.sin(startRotation) * radius);
        p = new Position(x, y, r);
        steps.push(p);
    }
    
    this.getSteps = function() {
        return steps;
    }
}

// static Movement class, maybe used later for performance improvement
var Movement = {};
Movement.sinValues = [];
Movement.cosValues = [];
Movement.rotationResolution = 250;
Movement.isSetUp = false;

Movement.sin = function(x) {
    if (!Movement.isSetUp) Movement.setup();
    var i = Math.floor(x * Movement.rotationResolution / Math.PI);
    return Movement.sinValues[i];
}

Movement.cos = function(x) {
    if (!Movement.isSetUp) Movement.setup();
    var i = Math.floor(x * Movement.rotationResolution / Math.PI);
    return Movement.cosValues[i];
}

Movement.setup = function() {
   for (var i = 0; i <= Math.PI*2; i+=Math.PI/Movement.rotationResolution) {
       Movement.sinValues.push(Math.sin(i));
       Movement.cosValues.push(Math.cos(i));
   } 
   Movement.isSetUp = true;
}

