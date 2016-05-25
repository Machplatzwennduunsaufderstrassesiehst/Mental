
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
    
    var explosionTextures = [];
    var assetLoader = new PIXI.loaders.Loader();
    assetLoader.add("graphics/tgm/explosion.json");
    assetLoader.once("complete", function() {
        for (var i = 1; i <= 16; i++) {
            explosionTextures.push(new PIXI.Texture.fromFrame("exp" + i + ".png"));
        }
    });
    assetLoader.load();
    
    function Explosion(posVector) {
        var gridSize = trainGame.getGridSize();
        var scale = gridSize / 100 * 1.15;
        
        var explosion = new PIXI.extras.MovieClip(explosionTextures);
        explosion.loop = false;
        explosion.animationSpeed = 0.7;
        
        explosion.position.x = posVector.getX();
        explosion.position.y = posVector.getY();
        explosion.anchor = new PIXI.Point(0.5, 0.5);
        explosion.rotation = Math.random() * Math.PI * 2;
        explosion.scale = new PIXI.Point(scale, scale);
        
        function checkAnimationEnded() {
            if (explosion.playing) {
                setTimeout(checkAnimationEnded, 100);
            } else {
                trainGame.graphics.removeSprite(explosion);
            }
        }
        
        trainGame.graphics.addSprite(explosion);
        explosion.play();
        checkAnimationEnded();
    }
    
    return {
        Star: Star,
        Explosion: Explosion
    };
})();
