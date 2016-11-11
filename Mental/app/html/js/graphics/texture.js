
engine.texture = (function() {

    var texturePacks = [];

    function createTextureField() { // TODO
        return new PIXI.Sprite();
    }

    function Appearance(jsonData) {
        var textures = {};
        for (var key in jsonData) {
            textures[key] = new PIXI.Texture.fromImage(jsonData[key]["path"]);
        }
    }

    function TexturePack(path) {

        ajax.getFile(path, function(jsonString) {
            var graphicObjectsData = JSON.parse(jsonString);
            for (var object in graphicObjectsData) {
                var appearancesData = json[object];
                var appearances = [];
                for (var data in appearancesData) {
                    appearances = new Appearance(appearancesData[data]);
                }
            }
        });
        
    }

    function loadTexturePack(path) {
        console.log("Loading Texture Pack from " + path);
        var pack = TexturePack(path);
        texturePacks.add(pack);
        return pack;
    }


    return {
        updateTexturePackList: function() { // TODO
            texturePacks = [];
            var i = 0;
            var loading = true;
            function tryLoad(path) {
                if (loading) {
                    ajax.fileExists(path, function(exists) {
                        if (exists) {
                            TexturePack.load(path.replace("pack", ""));
                        } else {
                            loading = false;
                        }
                    });
                }
            }
        },

        loadTexturePack: loadTexturePack,

        getTexturePacks: function() {
            return texturePacks;
        }
    }
})();