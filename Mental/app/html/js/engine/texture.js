engine.texture = (function () {

    var textures = [];
    var texturePacks = [];
    var gridSize = 100;



    function TexturePack(path) {

        if (path[path.length - 1] != "/") {
            path += "/";
        }

        var objects = {};
        var packScale = 1;

        var spriteToTextureField = this.spriteToTextureField = function(sprite, scale) {
            sprite.setOption = function (key, value) {
                switch (key) {
                    case "imageFile":
                        if (!(value in textures)) {
                            textures[value] = PIXI.Texture.fromImage(path + value);
                        }
                        this.texture = textures[value];
                        break;
                    case "relPosition":
                        this.pivot = new PIXI.Point(-value.x, -value.y);
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
            sprite.setPosition = function (x, y) {
                this.position = new PIXI.Point(x, y);
            };
            sprite.changeRotation = function (deltaRotation) {
                this.rotation += deltaRotation
            };
            sprite.scale = new PIXI.Point(scale, scale);
            sprite.anchor = new PIXI.Point(0.5, 0.5);
            return sprite;
        };

        this.load = function (then) {
            ajax.getFile(path + "_pack.json", function (jsonString) {
                var pack = JSON.parse(jsonString);
                packScale =  gridSize / pack["gridSize"];
                var graphicObjectsData = pack["objects"];
                for (var object in graphicObjectsData) {
                    objects[object] = graphicObjectsData[object];
                }
                if (then) {
                    then();
                }
            });
        };

        this.getTextureFields = function (objectKey) {
            var textureFields = {};
            var textureFieldsData = objects[objectKey]["textureFields"];
            for (var field in textureFieldsData) {
                var fieldData = textureFieldsData[field];
                var textureField = spriteToTextureField(new PIXI.Sprite(), packScale);
                for (var optionKey in fieldData) {
                    textureField.setOption(optionKey, fieldData[optionKey]);
                }
                textureFields[field] = textureField;
            }
            return textureFields;
        };

        this.getAppearances = function (objectKey) {
            return objects[objectKey]["appearances"];
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
        updateTexturePackList: function () { // TODO
            texturePacks = [];
            var i = 0;
            var loading = true;

            function tryLoad(path) {
                if (loading) {
                    ajax.fileExists(path, function (exists) {
                        if (exists) {
                            TexturePack.load(path.replace("pack", ""));
                        } else {
                            loading = false;
                        }
                    });
                }
            }
        },

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