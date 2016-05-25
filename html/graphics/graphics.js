

/* global PIXI, byID */

function GameGraphics(htmlContainer) {
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
    
    this.resizeRenderer = function(width, height) {
        renderer.resize(width, height);
    };
    
    this.setStageScale = function(scale) {
        stage.scale = new PIXI.Point(scale, scale);
    };
    
    this.getStage = function() {
        return stage;
    };
    this.getRenderer = function() { return renderer; };
    
    var start = this.start = function() {
        running = true;
        // The renderer will create a canvas element for you that you can then insert into the DOM.
        byID(htmlContainer).appendChild(renderer.view);
        animate();
        fpsMeasureThread = setInterval(measureFPS, 1000);
    };
    
    var stop = this.stop = function() {
        byID(htmlContainer).removeChild(renderer.view);
        staticEnvironment.cacheAsBitmap = false;
        running = false;
        clearInterval(fpsMeasureThread);
    };
    
    var addGraphicObject = this.addGraphicObject = function(graphicObject) {
        graphicObjects.push(graphicObject);
        stage.addChild(graphicObject.getSprite());
    };
    
    this.addSprite = function(sprite) {stage.addChild(sprite);};
    this.removeSprite = function(sprite) {stage.removeChild(sprite);};
    
    var removeGraphicObject = this.removeGraphicObject = function(graphicObject) {
        graphicObjects.remove(graphicObject);
        stage.removeChild(graphicObject.getSprite());
    };
    
    var addEnvironment = this.addEnvironment = function(sprite, cache) {
        if (cache === true) {
            staticEnvironment.addChild(sprite);
        } else {
            environment.addChild(sprite);
        }
        environmentSprites.push(sprite);
        //log("environment sprite added: " + sprite.position.x + " " + sprite.position.y);
    };
    
    var removeEnvironment = this.removeEnvironment = function(sprite) {
        environment.removeChild(sprite);
        staticEnvironment.removeChild(sprite);
        environmentSprites.remove(sprite);
    };
    
    this.cacheStaticEnvironment = function() {
        staticEnvironment.cacheAsBitmap = true;
    };
    
    this.clearEnvironment = function() {
        for (var i = 0; i < environmentSprites.length; i++) {
            removeEnvironment(environmentSprites[i]);
        }
    };
    
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
        
        requestAnimationFrame(animate);

        for (var i = 0; i < graphicObjects.length; i++) {
            try {
                graphicObjects[i].move();
            } catch (e) {
                //log(e);
            }
        }

        renderer.render(stage);
    }
}

var TextureGenerator = new (function () {
    var gridSize = undefined;
    
    this.generate = function(path) {
        var texture = PIXI.Texture.fromImage(path);
        return texture;
    };
    
    this.setGridSize = function(gs) {
        gridSize = gs;
    };
    
    // scale is an optional additional custom scaling factor
    this.generateSprite = function(texture, scale) {
        if (scale == undefined) scale = 1;
        var sprite = new PIXI.Sprite(texture);
        var xScale = 1, yScale = 1;
        if (gridSize != undefined) {
            xScale = gridSize/sprite.width * scale;
            yScale = gridSize/sprite.height * scale;
        }
        sprite.scale = new PIXI.Point(xScale, yScale);
        return sprite;
    };
    
    this.getSpritePivot = function(sprite) {
        return new PIXI.Point(sprite._texture.width/2, sprite._texture.height/2);
    };
    
    this.getDisplayObjectPivot = function(displayObject) {
        return new PIXI.Point(displayObject.width/2, displayObject.height/2);
    };
})();


