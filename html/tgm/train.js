
function Train(trainId, destinationId, tracksPerSecond, color, startTrack) {
    var container = new PIXI.Container();
    createPNGObject("graphics/tgm/train.png", container);
    // add color TODO
    var graphicObject = new GraphicObject(container);
	trainGame.graphics.addGraphicObject(graphicObject);
	
	var timePerTrack = 1 / tracksPerSecond;
	var currentTrack = startTrack;
	
    function move() {
        setTimeout(function(){
            currentTrack = currentTrack.getSuccessor();
            move();
        }, timePerTrack);
        
    }
    
    move(currentTrack);
}

