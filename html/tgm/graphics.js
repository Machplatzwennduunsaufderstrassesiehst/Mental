
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
    
    function start() {
        running = true;
        animate();
    }
    
    function stop() {
        running = false;
    }
    
    function addGraphicObject(graphicObject) {
        graphicObjects.push(graphicObject);
        stage.addChild(graphicObject.sprite);
    }
    
    function removeGraphicObject(graphicObject) {
        graphicObjects.remove(graphicObject);
        stage.removeChild(graphicObject.sprite);
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



