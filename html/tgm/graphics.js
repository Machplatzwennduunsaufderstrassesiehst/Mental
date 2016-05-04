
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
    
    // You can use either `new PIXI.WebGLRenderer`, `new PIXI.CanvasRenderer`, or `PIXI.autoDetectRenderer`
    // which will try to choose the best renderer for the environment you are in.

    // The renderer will create a canvas element for you that you can then insert into the DOM.
    document.body.appendChild(renderer.view);

    // You need to create a root container that will hold the scene you want to draw.

    // Declare a global variable for our sprite so that the animate function can access it.
    var bunny = null;

    // load the texture we need
    PIXI.loader.add('bunny', 'bunny.png').load(function (loader, resources) {
        // This creates a texture from a 'bunny.png' image.
        bunny = new PIXI.Sprite(resources.bunny.texture);

        // Setup the position and scale of the bunny
        bunny.position.x = 600;
        bunny.position.y = 500;

        bunny.scale.x = 0.5;
        bunny.scale.y = 0.5;
        
        bunny.rotation = Math.PI / 2;

        // Add the bunny to the scene we are building.
        stage.addChild(bunny);

        // kick off the animation loop (defined below)
        animate();
    });

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



