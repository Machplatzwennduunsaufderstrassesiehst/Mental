
/* global trainGame, PIXI, TrainGame, Vector, TextureGenerator */

// keep everything here in a seperate scope
var particles = (function() {
    
    function Star(posVector) {
        var gridSize = trainGame.getGridSize();
        var scale = gridSize / 100;
        var sprite = new PIXI.Sprite(TrainGame.starTexture);
        sprite.pivot = TextureGenerator.getSpritePivot(sprite);
        sprite.scale = new PIXI.Point(scale, scale);
        
        var vector = Vector.newWithRandomDirection(gridSize / 1.7);
        
        var graphicObject = new GraphicObject(sprite);
        var movement = new StraightDeaccelerationMovement(0, vector, 0.5);
        movement.addVector(posVector);
        trainGame.graphics.addGraphicObject(graphicObject);
        
        graphicObject.setPos(movement.getFirst());
        graphicObject.queueMovement(movement);
        
        function fadeOut() {
            graphicObject.fadeOut(function() {
                trainGame.graphics.removeGraphicObject(graphicObject);
            }, 1);
        }
        setTimeout(fadeOut, 500);
    }
    
    return {
        Star: Star
    };
})();
