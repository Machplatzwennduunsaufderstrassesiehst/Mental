

window.onload = function() {


// You can use either `new PIXI.WebGLRenderer`, `new PIXI.CanvasRenderer`, or `PIXI.autoDetectRenderer`
// which will try to choose the best renderer for the environment you are in.
var renderer = new PIXI.autoDetectRenderer(
    window.innerWidth, window.innerHeight,
    {antialias:true,resolution: window.innerWidth / window.innerHeight,}
);

// The renderer will create a canvas element for you that you can then insert into the DOM.
document.body.appendChild(renderer.view);

// You need to create a root container that will hold the scene you want to draw.
var stage = new PIXI.Container();

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
    // start the timer for the next animation loop
    requestAnimationFrame(animate);

    // each frame we spin the bunny around a bit
    bunny.rotation += 0.001;

    // this is the main render call that makes pixi draw your container and its children.
    renderer.render(stage);
}




}
