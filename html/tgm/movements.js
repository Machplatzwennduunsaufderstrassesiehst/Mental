
function Position(x, y, r) {
    this.x = x;
    this.y = y;
    this.rotation = r;
}


// static Movement class, maybe used later for performance improvement
var Movement = {};
Movement.sinValues = [];
Movement.cosValues = [];
Movement.rotationResolution = 200;
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

