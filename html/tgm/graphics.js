

function GameGraphics() {
    var graphicObjects = [];
    var environmentSprites = [];
    var running = false;
    
    var renderer = new PIXI.autoDetectRenderer(
        window.innerWidth, window.innerHeight,
        {antialias:true}
    );
    var stage = new PIXI.Container();
    
    this.getStage = function() {
        return stage;
    }
    
    var start = this.start = function() {
        running = true;
        // The renderer will create a canvas element for you that you can then insert into the DOM.
        byID("mainTrainGameFrame").appendChild(renderer.view);
        animate();
    }
    
    var stop = this.stop = function() {
        byID("mainTrainGameFrame").removeChild(renderer.view);
        running = false;
    }
    
    var addGraphicObject = this.addGraphicObject = function(graphicObject) {
        graphicObjects.push(graphicObject);
        stage.addChild(graphicObject.getSprite());
    }
    
    var removeGraphicObject = this.removeGraphicObject = function(graphicObject) {
        graphicObjects.remove(graphicObject);
        stage.removeChild(graphicObject.getSprite());
    }
    
    var addEnvironment = this.addEnvironment = function(sprite) {
        stage.addChild(sprite);
        environmentSprites.push(sprite);
        //log("environment sprite added: " + sprite.position.x + " " + sprite.position.y);
    }
    
    var removeEnvironment = this.removeEnvironment = function(sprite) {
        stage.removeChild(sprite);
        environmentSprites.remove(sprite);
    }
    
    this.clearEnvironment = function() {
        for (var i = 0; i < environmentSprites.length; i++) {
            removeEnvironment(environmentSprites[i]);
        }
    }

    function animate() {
        if (!running) return;
        // start the timer for the next animation loop
        requestAnimationFrame(animate);

        for (var i = 0; i < graphicObjects.length; i++) {
            graphicObjects[i].move();
        }

        // this is the main render call that makes pixi draw your container and its children.
        renderer.render(stage);
    }
}

GameGraphics.TGMPATH = "graphics/tgm/";


