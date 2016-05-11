
function Train(trainId, destinationId, color, startTrack) {
    var container = new PIXI.Container();
    createPNGObject("graphics/tgm/train.png", container);
    // add color TODO
    var graphicObject = new GraphicObject(container);
	trainGameGraphics.addGraphicObject(graphicObject);
	
    
}

