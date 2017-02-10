/* global PIXI, byID */

var engine = {};

engine.graphics = (function () {

    var currentFPS = 60;

    function GameGraphics(htmlContainerId) {
        GameGraphics.newestInstance = this;

        var stage = new PIXI.Container();
        var graphicObjects = [];
        var movingObjects = [];
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

        this.addGraphicObject = function (graphicObject) {
            graphicObjects.push(graphicObject);
            if (graphicObject instanceof MovingObject) {
                movingObjects.push(graphicObject);
            }
            var textureFields = graphicObject.getTextureFields();
            for (var i in textureFields) {
                this.addTextureField(textureFields[i]);
            }
        };

        this.removeGraphicObject = function (graphicObject) {
            graphicObjects.remove(graphicObject);
            if (graphicObject instanceof MovingObject) {
                movingObjects.remove(graphicObject);
            }
            var textureFields = graphicObject.getTextureFields();
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

            for (var i = 0; i < movingObjects.length; i++) {
                try {
                    graphicObjects[i].getNextPosition();
                } catch (e) {
                    log(e);
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

        this.getTextureFields = function () {
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
            if (r === undefined) {
                r = 0;
            }
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
                var r = arguments[2];
                setPositionByValues(x, y, r);
            }
        };

        this.getPosition = function() {
            return position;
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

        var position = this.getPosition();
        var currentMovement = null;
        var moveStartTime = 0;
        var moveEndTime = 0;

        // called by render loop
        this.getNextPosition = function () {
            var percentage = this.getMovementProgress();
            if (percentage <= 1) {
                var newPosition = currentMovement.getPosition(percentage);
                this.setPosition(newPosition.x, newPosition.y, newPosition.rotation);
            }
            return position;
        };

        this.setMovement = function (movement, time) {
            moveStartTime = new Date().getTime();
            currentMovement = movement;
            moveEndTime = moveStartTime + time * 1000;
        };

        this.getMovementProgress = function () {
            var total = moveEndTime - moveStartTime;
            if (total == 0) {
                return 0;
            } else {
                var now = new Date().getTime() - moveStartTime;
                return now / total;
            }
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
            }(this.getTextureFields(), onFaded);
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
