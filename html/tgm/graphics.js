

function GameGraphics() {
    var graphicObjects = [];
    var environmentSprites = [];
    var running = false;
    
    var renderer = new PIXI.autoDetectRenderer(
        1000, 1000,
        {antialias:true}
    );
    var stage = new PIXI.Container();
    var environment = new PIXI.Container();
    var staticEnvironment = new PIXI.Container();
    stage.addChild(environment);
    stage.addChild(staticEnvironment);
    //stage.width = 1000;
    //stage.height = 1000;
    
    this.resize = function(width, height) {
        renderer.resize(width, height);
    }
    
    this.getStage = function() {
        return stage;
    }
    
    var start = this.start = function() {
        running = true;
        // The renderer will create a canvas element for you that you can then insert into the DOM.
        byID("mainTrainGameFrame").appendChild(renderer.view);
        animate();
        fpsMeasureThread = setInterval(measureFPS, 1000);
    }
    
    var stop = this.stop = function() {
        byID("mainTrainGameFrame").removeChild(renderer.view);
        staticEnvironment.cacheAsBitmap = false;
        running = false;
        clearInterval(fpsMeasureThread);
    }
    
    var addGraphicObject = this.addGraphicObject = function(graphicObject) {
        graphicObjects.push(graphicObject);
        stage.addChild(graphicObject.getSprite());
    }
    
    var removeGraphicObject = this.removeGraphicObject = function(graphicObject) {
        graphicObjects.remove(graphicObject);
        stage.removeChild(graphicObject.getSprite());
    }
    
    var addEnvironment = this.addEnvironment = function(sprite, cache) {
        if (cache) {
            staticEnvironment.addChild(sprite);
        } else {
            environment.addChild(sprite);
        }
        environmentSprites.push(sprite);
        //log("environment sprite added: " + sprite.position.x + " " + sprite.position.y);
    }
    
    var removeEnvironment = this.removeEnvironment = function(sprite) {
        environment.removeChild(sprite);
        staticEnvironment.removeChild(sprite);
        environmentSprites.remove(sprite);
    }
    
    this.cacheStaticEnvironment = function() {
        staticEnvironment.cacheAsBitmap = true;
    }
    
    this.clearEnvironment = function() {
        for (var i = 0; i < environmentSprites.length; i++) {
            removeEnvironment(environmentSprites[i]);
        }
    }
    
    var fpsMeasureThread = null;
    var measurements = [60];
    var fpsMeasurementsSize = 3;
    function measureFPS() {
        measurements.unshift(frameCounter);
        if (measurements.length > fpsMeasurementsSize) measurements.pop();
        frameCounter = 0;
        // calculate framerate from measurements
        var totalFPS = 0;
        for (var i = 0; i < measurements.length; i++) totalFPS += measurements[i];
        currentFPS = totalFPS / measurements.length;
    }
    var currentFPS = 60;
    this.getCurrentFPS = function(){return currentFPS;};
    var frameCounter = 0;

    function animate() {
        if (!running) return;
        frameCounter++;
        // start the timer for the next animation loop
        requestAnimationFrame(animate);

        for (var i = 0; i < graphicObjects.length; i++) {
            try {
                graphicObjects[i].move();
            } catch (e) {
                //log(e);
            }
        }

        // this is the main render call that makes pixi draw your container and its children.
        renderer.render(stage);
    }
}

GameGraphics.TGMPATH = "graphics/tgm/";

var TextureGenerator = new function() {
    var textures = [];
    
    this.generate = function(path) {
        var texture = PIXI.Texture.fromImage(path);
        textures.push(texture);
        return texture;
    }
    
    // scale is an optional additional custom scaling factor
    this.generateSprite = function(texture, scale) {
        if (scale == undefined) scale = 1;
        var sprite = new PIXI.Sprite(texture);
        var gridSize = trainGame.getGridSize();
        sprite.scale = new PIXI.Point(gridSize/sprite.width*scale, gridSize/sprite.height*scale);
        return sprite;
    }
    
    this.getTextures = function() {
        return textures;
    }
    
    this.getSpritePivot = function(sprite) {
        return new PIXI.Point(sprite._texture.width/2, sprite._texture.height/2);
    }
}


