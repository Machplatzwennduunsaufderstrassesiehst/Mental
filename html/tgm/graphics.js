
var trainGameGraphics = new GameGraphics();

function GameGraphics() {
    var graphicObjects = [];
    var running = false;
    
    var renderer = new PIXI.autoDetectRenderer(
        window.innerWidth, window.innerHeight,
        {antialias:true,resolution: window.innerWidth / window.innerHeight,}
    );
    var stage = new PIXI.Container();
    
    this.getStage = function() {
        return stage;
    }
    
    var start = this.start = function() {
        running = true;
        animate();
    }
    
    var stop = this.stop = function() {
        running = false;
    }
    
    var addGraphicObject = this.addGraphicObject = function(graphicObject) {
        graphicObjects.push(graphicObject);
        stage.addChild(graphicObject.sprite);
    }
    
    var removeGraphicObject = this.removeGraphicObject = function(graphicObject) {
        graphicObjects.remove(graphicObject);
        stage.removeChild(graphicObject.sprite);
    }
    
    var addEnvironment = this.addEnvironment = function(sprite) {
        stage.addChild(sprite);
    }
    
    var removeEnvironment = this.removeEnvironment = function(sprite) {
        stage.removeChild(sprite);
    }

    // The renderer will create a canvas element for you that you can then insert into the DOM.
    document.body.appendChild(renderer.view);

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



