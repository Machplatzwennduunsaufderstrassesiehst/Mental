
TrainGame.particles = (function() {
    
    function Star(posVector) {
        var gridSize = TrainGame.instance.getGridSize();
        var scale = gridSize / 100;
        var sprite = new PIXI.Sprite(TrainGame.starTexture);
        sprite.pivot = engine.graphics.TextureGenerator.getSpritePivot(sprite);
        sprite.scale = new PIXI.Point(scale, scale);
        
        var vector = engine.physics.Vector.newWithRandomDirection(gridSize / 1.7);
        
        var graphicObject = new engine.graphics.GraphicObject(sprite);
        var movement = new engine.physics.StraightDeaccelerationMovement(0, vector, 0.5);
        movement.addVector(posVector);
        TrainGame.instance.graphics.addGraphicObject(graphicObject);
        
        graphicObject.setPos(movement.getFirst());
        graphicObject.queueMovement(movement);
        
        function fadeOut() {
            graphicObject.fadeOut(function() {
                TrainGame.instance.graphics.removeGraphicObject(graphicObject);
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
        var gridSize = TrainGame.instance.getGridSize();
        var scale = gridSize / 100 * 1.15;
        
        var explosion = new PIXI.extras.MovieClip(explosionTextures);
        explosion.loop = false;
        explosion.animationSpeed = 0.33;
        
        explosion.position.x = posVector.getX();
        explosion.position.y = posVector.getY();
        explosion.anchor = new PIXI.Point(0.5, 0.5);
        explosion.rotation = Math.random() * Math.PI * 2;
        explosion.scale = new PIXI.Point(scale, scale);
        
        function checkAnimationEnded() {
            if (explosion.playing) {
                setTimeout(checkAnimationEnded, 100);
            } else {
                TrainGame.instance.graphics.removeSprite(explosion);
            }
        }
        
        TrainGame.instance.graphics.addSprite(explosion);
        explosion.play();
        checkAnimationEnded();
    }
    
    function Text(text, color) {
        var gridSize = TrainGame.instance.getGridSize();
        var scale = gridSize / 100;
        
        var sprite = new PIXI.Text(text, {
            font: '50px Arial',
            fill: color,
            align: 'center',
            strokeThickness: 10
        });
        sprite.anchor = new PIXI.Point(0.5, 0.5);
        sprite.scale = new PIXI.Point(scale, scale);
        sprite.alpha = 0.1;
        
        function fade(endValue, stepWide, onFaded) {
            if (Math.abs(endValue - sprite.alpha) <= Math.abs(stepWide)) {
                sprite.alpha = endValue;
                onFaded();
                return;
            }
            setTimeout(
                (function(e, s ,o){return function(){fade(e, s, o);};})
                (endValue, stepWide, onFaded)
            , 40);
            sprite.alpha += stepWide;
        }
        
        this.fadeIn = function() {
            TrainGame.instance.graphics.addSprite(sprite);
            TrainGame.instance.graphics.centerSprite(sprite);
            fade(1, 0.15, function() {});
        };
        
        this.fadeOut = function() {
            fade(0, -0.07, function() {
                TrainGame.instance.graphics.removeSprite(sprite);
            });
        };
    };
    
    return {
        Star: Star,
        Explosion: Explosion,
        Text: Text
    };
})();
