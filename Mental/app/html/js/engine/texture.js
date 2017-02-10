engine.texture = (function () {

    var textures = [];
    var texturePacks = [];
    var gridSize = 100;

    function TexturePack(path) {

        if (path[path.length - 1] != "/") {
            path += "/";
        }

        var objects = {};
        var animations = {};
        var packScale = 1;
        var staticLayers = [];

        var assetLoader = new PIXI.loaders.Loader();

        var spriteToTextureField = this.spriteToTextureField = function (sprite, scale) {
            sprite.setOption = function (key, value) {
                switch (key) {
                    case "imageFile":
                        if (!(value in textures)) {
                            textures[value] = PIXI.Texture.fromImage(path + value);
                        }
                        this.texture = textures[value];
                        break;
                    case "relPosition":
                        this.pivot.set(-value.x, -value.y);
                        break;
                    case "relRotation":
                        this.rotation = value;
                        break;
                    default:
                        try {
                            this[key] = value;
                        } catch (e) {
                            console.err(e);
                            console.err("sprite.setOption: not defined for " + key + " = " + value);
                        }
                }
            };
            sprite.changeRotation = function (deltaRotation) {
                this.rotation += deltaRotation;
            };
            sprite.scale.set(scale, scale);
            sprite.anchor.set(0.5, 0.5);
            return sprite;
        };

        var loadAnimationTextures = function (then) {
            try {
                for (var animationName in animations) {
                    animations[animationName]["frameTextures"] = [];
                    var filename = animations[animationName]["filename"] + ".json";
                    assetLoader.add(path + filename);
                }
                assetLoader.once("complete", function () {
                    for (var animationName in animations) {
                        for (var i = 1; i <= animations[animationName]["frames"]; i++) {
                            animations[animationName]["frameTextures"].push(new PIXI.Texture.fromFrame(animationName + i + ".png"));
                        }
                    }
                    if (then) {
                        then();
                    }
                });
                assetLoader.load();
            } catch (e) {
                console.log(e);
                console.log("Could not load animation frame textures.");
                if (then) {
                    then();
                }
            }
        };

        this.load = function (then) {
            ajax.getFile(path + "_pack.json", function (jsonString) {
                try {
                    var pack = JSON.parse(jsonString);
                    packScale = gridSize / pack["gridSize"];
                    staticLayers = pack["staticLayers"];
                    animations = pack["animations"];
                    var graphicObjectsData = pack["objects"];
                    for (var object in graphicObjectsData) {
                        objects[object] = graphicObjectsData[object];
                    }
                    loadAnimationTextures(function() {
                        if (then) {
                            then();
                        }
                    });
                } catch (e) {
                    console.log("Error loading TexturePack.");
                    console.err(e);
                }
            });
        };

        this.createTextureFields = function (objectKey) {
            var textureFields = {};
            var textureFieldsData = objects[objectKey]["textureFields"];
            for (var field in textureFieldsData) {
                var fieldData = textureFieldsData[field];
                var textureField = spriteToTextureField(new PIXI.Sprite(), packScale);
                for (var optionKey in fieldData) {
                    textureField.setOption(optionKey, fieldData[optionKey]);
                }
                if (!("layer" in fieldData)) {
                    textureField.setOption("layer", 0);
                }
                textureFields[field] = textureField;
            }
            return textureFields;
        };

        this.createAnimation = function (animationKey) {
            var animationData = animations[animationKey];
            var frames = animationData["frameTextures"];
            var animation = new PIXI.extras.MovieClip(frames);
            animation.anchor.set(0.5, 0.5);
            for (var key in animationData["properties"]) {
                try {
                    var value = animationData["properties"][key];
                    switch (key) {
                        case "scale":
                            animation.scale.set(value, value);
                            break;
                        default:
                            animation[key] = value;
                    }
                } catch(e) {
                    console.log(e);
                    console.log("Could not apply animation property: " + key);
                }
            }
            return animation;
        };

        this.getAppearances = function (objectKey) {
            return objects[objectKey]["appearances"];
        };

        this.getStaticLayers = function () {
            return staticLayers;
        };

        this.createGraphicObject = function(objectKey) {
            return new engine.graphics.GraphicObject(this.createTextureFields(objectKey), this.getAppearances(objectKey));
        };

        this.createMovingObject = function(objectKey) {
            return new engine.graphics.MovingObject(this.createTextureFields(objectKey), this.getAppearances(objectKey));
        };

    }

    function addTexturePack(path) {
        var pack = new TexturePack(path);
        texturePacks.push(pack);
        return pack;
    }

    function loadTexturePacks(then) {
        function loadNext(i) {
            texturePacks[i].load(function () {
                i++;
                if (i < texturePacks.length) {
                    loadNext(i);
                } else {
                    if (then) {
                        then();
                    }
                }
            });
        }

        loadNext(0);
    }


    return {
        addTexturePack: addTexturePack,

        loadTexturePacks: loadTexturePacks,

        setGridSize: function (size_) {
            gridSize = size_;
        },

        clearTexturePacks: function () {
            texturePacks = [];
        },

        getTexturePacks: function () {
            return texturePacks;
        }
    }
})();