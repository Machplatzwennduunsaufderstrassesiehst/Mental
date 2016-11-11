

/* global PIXI, byID */

window.engine = {};

engine.graphics = (function() {

    var currentFPS = 30;

    function GameGraphics(htmlContainerId) {
        GameGraphics.newestInstance = this;
        var graphicObjects = [];
        var environmentSprites = [];
        var running = false;

        var renderer = new PIXI.autoDetectRenderer(
            1000, 1000,
            {antialias:true, transparent:true}
        );
        var stage = new PIXI.Container();
        var environment = new PIXI.Container();
        var staticEnvironment = new PIXI.Container();
        stage.addChild(environment);
        stage.addChild(staticEnvironment);

        this.resizeRenderer = function(width, height) {
            renderer.resize(width, height);
        };

        this.setStageScale = function(scale) {
            stage.scale = new PIXI.Point(scale, scale);
        };

        this.getStage = function() {
            return stage;
        };
        this.getRenderer = function() { return renderer; };

        var start = this.start = function() {
            running = true;
            // The renderer will create a canvas element for you that you can then insert into the DOM.
            byID(htmlContainerId).appendChild(renderer.view);
            animate();
            fpsMeasureThread = setInterval(measureFPS, 1000);
        };

        var stop = this.stop = function() {
            byID(htmlContainerId).removeChild(renderer.view);
            staticEnvironment.cacheAsBitmap = false;
            running = false;
            clearInterval(fpsMeasureThread);
        };

        var addGraphicObject = this.addGraphicObject = function(graphicObject) {
            graphicObjects.push(graphicObject);
            stage.addChild(graphicObject.getSprite());
        };

        this.addSprite = function(sprite) {stage.addChild(sprite);};
        this.removeSprite = function(sprite) {stage.removeChild(sprite);};

        this.centerSprite = function(sprite) {
            sprite.position.x = renderer.width / 2 / stage.scale.x;
            sprite.position.y = renderer.height / 2 / stage.scale.y;
        };

        var removeGraphicObject = this.removeGraphicObject = function(graphicObject) {
            graphicObjects.remove(graphicObject);
            stage.removeChild(graphicObject.getSprite());
        };

        var addEnvironment = this.addEnvironment = function(sprite, cache) {
            if (cache === true) {
                staticEnvironment.addChild(sprite);
            } else {
                environment.addChild(sprite);
            }
            environmentSprites.push(sprite);
            //log("environment sprite added: " + sprite.position.x + " " + sprite.position.y);
        };

        var removeEnvironment = this.removeEnvironment = function(sprite) {
            environment.removeChild(sprite);
            staticEnvironment.removeChild(sprite);
            environmentSprites.remove(sprite);
        };

        this.cacheStaticEnvironment = function() {
            staticEnvironment.cacheAsBitmap = true;
        };

        this.clearEnvironment = function() {
            for (var i = 0; i < environmentSprites.length; i++) {
                removeEnvironment(environmentSprites[i]);
            }
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
        this.getCurrentFPS = function(){return currentFPS;};
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

        this.degreesToRadian = function(deg) {
            return deg / 360 * Math.PI * 2;
        };
    }

    // new
    function GraphicObject(textureFields, appearances) {
        var availableAppearanceKeys = Object.keys(appearances);
        var currentAppearanceKey = availableAppearanceKeys[0];
        var position = new engine.physics.Position(0, 0, 0);

        function updateAppearance() {
            for (var textureFieldKey in textureFields) {
                var textureField = textureFields[textureFieldKey];
                if (textureFieldKey in Object.keys(appearances[currentAppearanceKey])) {
                    var textureFieldAppearance = appearances[currentAppearanceKey][textureFieldKey];
                    for (var optionKey in textureFieldAppearance) {
                        var optionValue = textureFieldAppearance[optionKey];
                        textureField.setOption(optionKey, optionValue);
                    }
                } else {
                    console.log("No textureField information in appearance.");
                }
            }
        }

        this.getSprites = function() {
            return textureFields;
        };

        this.getAppearance = function() {
            return appearances[currentAppearanceKey];
        };

        this.setAppearance = function(appearanceKey) {
            currentAppearanceKey = appearanceKey;
            updateAppearance();
        };

        var setPosition = this.setPosition = function(position_) {
            for (var i in textureFields) {
                textureFields[i].setPosition(position_);
            }
            position = position_;
        }
    }

    function GraphicObject(sprite_) {
        var latestPosition = new engine.physics.Position(-1000,-1000);
        var positionQueue = [latestPosition];
        var movements = {};
        var currentMovement = null;
        var movementQueue = [];

        var sprite = sprite_;

        // pop the next position on the position queue and return it
        // called by render loop
        this.getNextPosition = function() {
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
            sprite.position.x = p.x;
            sprite.position.y = p.y;
            sprite.rotation = p.rotation;
            return p;
        };

        this.getPos = function() {
            return (latestPosition != undefined ? latestPosition : new engine.physics.Position(-1000,-1000));
        };

        this.setPos = function(position) {
            positionQueue = [position]; // set positionQueue so it only contains the new position
            movementQueue = [];
        };

        this.getSprite = function() {
            return sprite;
        };

        this.addMovement = function(key, movement) {
            movements[key] = movement;
        };

        this.getMovementProgress = function() {
            if (currentMovement == null) {
                return 1;
            } else {
                return 1.0 - 1.0 * positionQueue.length / currentMovement.getSteps().length;
            }
        };

        this.queueMovement = function(movement) {
            movementQueue.unshift(movement);
        };

        this.queueMovementByKey = function(key) {
            movementQueue.unshift(movements[key]);
        };

        this.fadeOut = function(onFaded, seconds) {
            var startFading = function(displayObject, onFaded) {
                var frames = currentFPS * seconds;
                var c = 0;
                function fade() {
                    if (c >= frames) {
                        onFaded();
                        return;
                    }
                    c++;
                    setTimeout(fade, seconds * 1000 / frames);
                    displayObject.alpha = displayObject.alpha - 1 / frames;
                }
                return function() {
                    fade();
                };
            }(sprite, onFaded);
            startFading();
        };
    }

    return {
        TextureGenerator: new (function () {
            var gridSize = undefined;

            this.generate = function(path) {
                return PIXI.Texture.fromImage(path);
            };

            this.setGridSize = function(gs) {
                gridSize = gs;
            };

            // scale is an optional additional custom scaling factor
            this.generateSprite = function(texture, scale) {
                if (scale == undefined) scale = 1;
                var sprite = new PIXI.Sprite(texture);
                var xScale = 1, yScale = 1;
                if (gridSize != undefined) {
                    xScale = gridSize/sprite.width * scale;
                    yScale = gridSize/sprite.height * scale;
                }
                sprite.scale = new PIXI.Point(xScale, yScale);
                return sprite;
            };

            this.getSpritePivot = function(sprite) {
                return new PIXI.Point(sprite._texture.width/2, sprite._texture.height/2);
            };

            this.getDisplayObjectPivot = function(displayObject) {
                return new PIXI.Point(displayObject.width/2, displayObject.height/2);
            };
        })(),

        GameGraphics: GameGraphics,

        GraphicObject: GraphicObject,

        calculateFrameAmount: function (time) {
            return currentFPS * time;
        },
    };

})();
