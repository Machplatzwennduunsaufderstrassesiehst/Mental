
function Train(trainId, destinationId, color, startTrack) {
    var container = new PIXI.Container();
    createPNGObject("train.png", container);
    // add color
    var graphicObject = new GraphicObject(container);
    trainGameGraphics.addGraphicObject(graphicObject);
    
    
}

