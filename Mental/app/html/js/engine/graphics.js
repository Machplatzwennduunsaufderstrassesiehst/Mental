/* global PIXI, byID */

var engine = {};

engine.graphics = (function () {

    var currentFPS = 60;

    function GameGraphics(htmlContainerId) {
        GameGraphics.newestInstance = this;

        var stage = new PIXI.Container();
        var graphicObjects = [];
        var running = false;

        var layerContainer = [];
        var highestLayerIndex = -1;

        var renderer = new PIXI.autoDetectRenderer(
            200, 200,
            {antialias: true, transparent: true}
        );

        this.resizeRenderer = function (width, height) {
            renderer.resize(width, height);
        };

        this.setStageScale = function (scale) {
            stage.scale = new PIXI.Point(scale, scale);
        };

        this.getStage = function () {
            return stage;
        };

        this.getRenderer = function () {
            return renderer;
        };

        this.setStaticLayers = function (staticLayers) {
            for (var layerIndex in staticLayers) {
                var layer = staticLayers[layerIndex];
                if (layerContainer[layer] == undefined) {
                    layerContainer[layer] = new PIXI.Container();
                }
                layerContainer[layer].cacheAsBitmap = true;
            }
        };

        var start = this.start = function () {
            running = true;
            for (var i in layerContainer) {
                var c = layerContainer[i];
                if (c instanceof PIXI.Container && c.children.length > 0) {
                    stage.addChild(c);
                }
            }
            byID(htmlContainerId).appendChild(renderer.view);
            animate();
            fpsMeasureThread = setInterval(measureFPS, 1000);
        };

        var stop = this.stop = function () {
            byID(htmlContainerId).removeChild(renderer.view);
            running = false;
            clearInterval(fpsMeasureThread);
        };

        var addGraphicObject = this.addGraphicObject = function (graphicObject) {
            graphicObjects.push(graphicObject);
            var textureFields = graphicObject.createTextureFields();
            for (var i in textureFields) {
                this.addTextureField(textureFields[i]);
            }
        };

        var removeGraphicObject = this.removeGraphicObject = function (graphicObject) {
            graphicObjects.remove(graphicObject);
            var textureFields = graphicObject.createTextureFields();
            for (var i in textureFields) {
                this.removeTextureField(textureFields[i])
            }
        };

        this.addTextureField = function (textureField) {
            var layer = textureField.layer;
            if (layerContainer[layer] == undefined) {
                layerContainer[layer] = new PIXI.Container();
                if (layer > highestLayerIndex) {
                    highestLayerIndex = layer;
                }
            }
            layerContainer[layer].addChild(textureField);
        };
        this.removeTextureField = function (textureField) {
            textureField.parent.removeChild(textureField);
        };

        this.playAnimation = function (animation, position, layer) {
            var parent;
            if (layerContainer[layer]) {
                parent = layerContainer[layer];
            } else if (layerContainer[highestLayerIndex]) {
                parent = layerContainer[highestLayerIndex];
            } else {
                parent = stage;
            }
            animation.position.set(position.getX(), position.getY());
            animation.rotation = position.rotation;
            parent.addChild(animation);
            animation.onComplete = (function (parent, animation) {
                return function () {
                    parent.removeChild(animation);
                }
            })(parent, animation);
            animation.play();
        };

        this.centerSprite = function (sprite) {
            sprite.position.x = renderer.width / 2 / stage.scale.x;
            sprite.position.y = renderer.height / 2 / stage.scale.y;
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

        this.getCurrentFPS = function () {
            return currentFPS;
        };
        var frameCounter = 0;

        function animate() {
            if (!running) return;
            frameCounter++;

            requestAnimationFrame(animate);

            for (var i = 0; i < graphicObjects.length; i++) {
                try {
                    graphicObjects[i].getNextPosition();
                } catch (e) {
                    //log(e);
                }
            }

            renderer.render(stage);
        }

        this.degreesToRadian = function (deg) {
            return deg / 360 * Math.PI * 2;
        };
    }

    function GraphicObject(textureFields, appearances) {

        function updateAppearance() {
            for (var textureFieldKey in textureFields) {
                if (textureFieldKey in appearances[currentAppearanceKey]) {
                    var textureField = textureFields[textureFieldKey];
                    var textureFieldAppearance = appearances[currentAppearanceKey][textureFieldKey];
                    for (var optionKey in textureFieldAppearance) {
                        var optionValue = textureFieldAppearance[optionKey];
                        textureField.setOption(optionKey, optionValue);
                    }
                } else {
                    console.log("INFO: No textureField information in appearance for key: " + textureFieldKey + ". appearance:");
                    console.log(appearances[currentAppearanceKey]);
                }
            }
        }

        this.createTextureFields = function () {
            return textureFields;
        };

        this.getTextureFieldById = function (textureFieldKey) {
            return textureFields[textureFieldKey];
        };

        this.getAppearance = function () {
            return appearances[currentAppearanceKey];
        };

        this.setAppearance = function (appearanceKey) {
            currentAppearanceKey = appearanceKey;
            updateAppearance();
        };

        var setPositionByValues = function (x, y, r) {
            var deltaRotation = r - position.rotation;
            for (var i in textureFields) {
                textureFields[i].position.set(x, y);
                textureFields[i].changeRotation(deltaRotation);
            }
            position.set(x, y, r);
        };

        var setPositionByObject = function (position_) {
            setPositionByValues(position_.getX(), position_.getY(), position_.rotation);
        };

        var setPosition = this.setPosition = function () {
            if (arguments.length == 1) {
                var position_ = arguments[0];
                setPositionByObject(position_);
            } else if (arguments.length >= 2) {
                var x = arguments[0];
                var y = arguments[1];
                var r;
                if (arguments[2]) {
                    r = arguments[2];
                } else {
                    r = 0;
                }
                setPositionByValues(x, y, r);
            }
        };

        try {
            var availableAppearanceKeys = Object.keys(appearances);
            var currentAppearanceKey = availableAppearanceKeys[0];
            var position = new engine.physics.Position(0, 0, 0);
            updateAppearance();
        } catch (e) {

        }

    }

    function MovingObject(textureFields, appearances) {
        GraphicObject.call(this, textureFields, appearances);

        var latestPosition = new engine.physics.Position(-1000, -1000);
        var positionQueue = [latestPosition];
        var movements = {};
        var currentMovement = null;
        var movementQueue = [];

        // pop the next position on the position queue and return it
        // called by render loop
        this.getNextPosition = function () {
            if (sprite == undefined) {
                log("GO.getNextPosition: sprite still undefined");
                return false;
            }
            var p = latestPosition = positionQueue.pop();
            if (positionQueue.length == 0) {
                if (movementQueue.length > 0) { // no positions on queue, but movements to be extracted to the positionQueue
                    currentMovement = movementQueue.pop();
                    var steps = currentMovement.getSteps();
                    for (var i = 0; i < steps.length; i++) {
                        positionQueue.unshift(steps[i]);
                    }
                } else { // no movements to be performed, and positionQueue empty => stay at current position (p)
                    positionQueue.push(p);
                }
            }
            if (p == undefined) return false;
            this.setPosition(p);
            return p;
        };

        this.getPos = function () {
            return (latestPosition != undefined ? latestPosition : new engine.physics.Position(-1000, -1000));
        };

        this.setPos = function (position) {
            positionQueue = [position]; // set positionQueue so it only contains the new position
            movementQueue = [];
        };

        this.addMovement = function (key, movement) {
            movements[key] = movement;
        };

        this.getMovementProgress = function () {
            if (currentMovement == null) {
                return 1;
            } else {
                return 1.0 - 1.0 * positionQueue.length / currentMovement.getSteps().length;
            }
        };

        this.queueMovement = function (movement) {
            movementQueue.unshift(movement);
        };

        this.queueMovementByKey = function (key) {
            movementQueue.unshift(movements[key]);
        };

        this.fadeOut = function (onFaded, seconds) {
            var startFading = function (displayObjects, onFaded) {
                var frames = currentFPS * seconds;
                var c = 0;

                function fade() {
                    if (c >= frames) {
                        onFaded();
                        return;
                    }
                    c++;
                    setTimeout(fade, seconds * 1000 / frames);
                    for (i in displayObjects) {
                        displayObjects[i].alpha = displayObjects[i].alpha - 1 / frames;
                    }
                }

                return function () {
                    fade();
                };
            }(this.getSprites(), onFaded);
            startFading();
        };
    }

    MovingObject.prototype = new GraphicObject;
    MovingObject.prototype.constructor = MovingObject;

    return {
        GameGraphics: GameGraphics,

        GraphicObject: GraphicObject,

        MovingObject: MovingObject,

        calculateFrameAmount: function (time) {
            return currentFPS * time;
        }
    };

})
();
