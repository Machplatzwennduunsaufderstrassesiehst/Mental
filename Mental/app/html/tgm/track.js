
TrainGame.environment = (function() {
    
    function Lane(i, j, predecessorCoords, successorCoords) {
        this.i = i;
        this.j = j;
        this.type = null;

        var gridSize = TrainGame.instance.getGridSize();

        var entranceSide = 0;
        var entranceCoords = null;
        var exitSide = 0;
        var exitCoords = null;
        var direction = 0;

        var spriteRotation, texture;

        // Vector that points to the middle of the track elements
        var relMidVector = new GraphicsEngine.physics.Vector(gridSize/2, gridSize/2);
        // Vector that points to the sprite's position
        var posVector = new GraphicsEngine.physics.Vector(i*gridSize, j*gridSize);
        posVector.add(relMidVector);

        var sprite = null;

        // get the position Vector that points to the specified side of the element
        var calculateSideCoords = function(side) {
            var deg = ((side) % 4) * Math.PI/2;
            var v = new GraphicsEngine.physics.Vector(Math.sin(deg), -Math.cos(deg)); // vector that points to the side
            v.normalize();
            v.multiply(gridSize/2);
            v.add(posVector);
            return v;
        };

        var getStartRotation = this.getStartRotation = function() {
            return entranceSide * Math.PI / 2;
        };

        var getEntranceSide = this.getEntranceSide = function() {
            return entranceSide;
        };

        var getExitSide = this.getExitSide = function() {
            return exitSide;
        };

        var getEntranceCoords = this.getEntranceCoords = function() {
            return entranceCoords;
        };

        var getExitCoords = this.getExitCoords = function() {
            return exitCoords;
        };

        var getPosVector = this.getPosVector = function() {
            return posVector;
        };

        this.setExitCoords = function(vector) {
            exitCoords = vector;
        };

        this.setSwitched = function(bSwitched) {
            sprite.alpha = (bSwitched ? 1 : 0.5);
        };

        // only used for Turns!
        this.getTurnRadius = function() {
            return gridSize/2;
        };
        this.getTurnDegrees = function() {
            return direction * Math.PI / 2;
        };

        function initializeDimensions() {
            var dx1 = i - predecessorCoords.x;
            var dx2 = successorCoords.x - i;
            var dy1 = j - predecessorCoords.y;
            var dy2 = successorCoords.y - j;
            //log("dx1: " + dx1 + "  dy1: " + dy1);
            //log("dx2: " + dx2 + "  dy2: " + dy2);
            entranceSide = 2 * Math.abs(dx1) + dx1 + Math.abs(dy1) - dy1;
            exitSide =     2 * Math.abs(dx2) - dx2 + Math.abs(dy2) + dy2;
            direction = exitSide - entranceSide;
            if (Math.abs(direction) % 2 == 0) {
                //log("this is a straight");
                direction = 0;
            } else {
                //log("this is a turn");
                if (Math.abs(direction) > 2) direction = -Math.sign(direction);
            }
            //log("entranceSide: " + entranceSide + "  exitSide: " + exitSide + "  d: " + d);
            entranceCoords = calculateSideCoords(entranceSide);
            exitCoords = calculateSideCoords(exitSide);
            spriteRotation = entranceSide;
        }

        var initTextureType = this.initTextureType = function() {
            switch (direction) {
                case 0: // straight
                    this.type = "straight";
                    texture = TrainGame.straightTexture;
                    break;
                case 1: // left turn
                    texture = TrainGame.turnTexture;
                    this.type = "turnLeft";
                    break;
                case -1: // right turn
                    this.type = "turnRight";
                    texture = TrainGame.turnTexture;
                    spriteRotation -= 1;
                    spriteRotation = (spriteRotation + 4) % 4;
                    break;
            }
        };

        var setTexture = this.setTexture = function(tex) {
            texture = tex;
        };

        var buildSprite = this.buildSprite = function(onload) {
            if (texture == undefined) initTextureType();
            spriteRotation *= Math.PI / 2;

            sprite = GraphicsEngine.graphics.TextureGenerator.generateSprite(texture);
            sprite.position = new PIXI.Point(posVector.getX(), posVector.getY());
            sprite.pivot = GraphicsEngine.graphics.TextureGenerator.getSpritePivot(sprite);
            sprite.rotation = spriteRotation;
            onload(sprite);
        };


        initializeDimensions();
    }

    function Track(i, j) {
        var predecessor = null;
        var successor = null;

        var lane = null;

        this.type = "track";

        this.getX = function() {
            return i;
        };
        this.getY = function() {
            return j;
        };

        this.getLane = function() {
            return lane;
        };

        var hasPredecessor = this.hasPredecessor = function() {
            return predecessor != null;
        };

        var hasSuccessor = this.hasSuccessor = function() {
            return successor != null;
        };

        this.setPredecessor = function(p) {
            predecessor = p;
        };

        this.setSuccessor = function(s) {
            successor = s;
        };

        this.getPredecessor = function() {
            return predecessor;
        };

        this.getSuccessor = function() {
            return successor;
        };

        // to be called after successor and predecessor are set
        this.initialize = function() {
            if (hasPredecessor()) {
                var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
            } else {
                //log("no predecessor");
                var predecessorCoords = {x:i, y:j-1};
            }
            if (hasSuccessor()) {
                var successorCoords = {x:successor.getX(), y:successor.getY()};
            } else {
                //log("no successor");
                var successorCoords = {x:i+1, y:j};
            }
            lane = new Lane(i, j, predecessorCoords, successorCoords);
            lane.buildSprite(function(sprite) {
                TrainGame.instance.graphics.addEnvironment(sprite, true);
            });
        };

        this.getRect = function() {
            var gridSize = TrainGame.instance.getViewGridSize();
            return new PIXI.Rectangle(i*gridSize, j*gridSize, gridSize, gridSize);
        };

    }

    function Switch(id, i, j) {
        Track.call(this, i, j);
        this.type = "switch";
        this.id = id;

        var switchedTo = 0;

        Switch.es[id] = this;

        // the possible lanes this switch has
        var lanes = [];

        var successors = null;

        // overwritten
        this.getLane = function(index) {
            if (index == undefined) index = switchedTo;
            return lanes[index];
        };

        // overwritten
        this.hasSuccessor = function() {
            return true;
        };

        // overwritten
        this.getSuccessor = function(index) {
            if (index == undefined) index = switchedTo;
            return successors[index];
        };

        this.setSuccessors = function(s) {
            successors = s;
        };

        // overwritten
        this.setSuccessor = function(s) {
            change(successors.indexOf(s));
        };

        var change = this.change = function(newSwitchedTo) {
            switchedTo = newSwitchedTo;
            switchingOrderIndex = switchingOrder.indexOf(switchedTo);
            for (var l = 0; l < lanes.length; l++) {
                if (l == switchedTo) {
                    lanes[switchedTo].setSwitched(true);
                } else {
                    lanes[l].setSwitched(false);
                }
            }
        };

        this.initialize = function() {
            var predecessor = this.getPredecessor();
            if (this.hasPredecessor()) {
                var predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
            } else {
                var predecessorCoords = {x:i, y:j-1};
            }
            for (var s = 0; s < successors.length; s++) {
                switchingOrder[s] = s;
                var successorCoords = {x:successors[s].getX(), y:successors[s].getY()};
                var lane = new Lane(i, j, predecessorCoords, successorCoords);
                lanes[s] = lane;
                var onl = function(index) {
                    return function(sprite) {
                        TrainGame.instance.graphics.addEnvironment(sprite);
                    };
                }(s);
                lanes[s].buildSprite(onl);
            }
            if (lanes.length == 3) {
                for (var l = 0; l < lanes.length; l++) {
                    if ((lanes[l].getEntranceSide() - lanes[l].getExitSide()) % 2 == 0) {// the straight one in the middle
                        log("3-Switch enhancement: found the Straight");
                        var ids = [0,1,2];
                        ids.remove(l);
                        if (lanes[ids[0]].getTurnDegrees() < 0) ids.swap(0, 1); // wenn 0 eine rechts- und 1 eine linkskurve ist, tauschen, sodass links vorne ist
                        switchingOrder[0] = ids[0];
                        switchingOrder[1] = l;
                        switchingOrder[2] = ids[1];
                        log(switchingOrder);
                    }
                }
            }
        };

        var switchingOrder = [];
        var switchingOrderIndex = 0;

        this.getNextLaneIndex = function() {
            switchingOrderIndex += 1;
            return switchingOrder[switchingOrderIndex % switchingOrder.length];
        };

        this.getSwitchedTo = function() {
            return switchedTo;
        };

        // pendeln, ist aber vllt doch nicht so gut
        /*
         var switchingDirection = 0;
         this.getNextLaneIndex = function() {
         var index = switchingOrderIndex;
         if (index <= 0) switchingDirection = 1;
         if (index >= successors.length - 1) switchingDirection = -1;
         switchingOrderIndex = index += switchingDirection;
         return switchingOrder[index % switchingOrder.length];
         };
         */
    }
    Switch.es = [];
    Switch.prototype = new Track;
    Switch.prototype.constructor = Switch;


    function Goal(id, i, j) {
        Track.call(this, i, j);
        Goal.s[id] = this;
        var lane;

        var gridSize = TrainGame.instance.getGridSize();

        // overwritten
        this.getLane = function(){return lane;};

        this.type = "goal";

        // overwritten
        this.getSuccessor = function() {
            return null;
        };

        this.initialize = function() {
            var predecessor = this.getPredecessor();
            var predecessorCoords, successorCoords;
            if (this.hasPredecessor()) {
                predecessorCoords = {x:predecessor.getX(), y:predecessor.getY()};
                successorCoords   = {x:i+i-predecessor.getX(), y:j+j-predecessor.getY()};
            } else {
                predecessorCoords = {x:i, y:j-1};
                successorCoords   = {x:i, y:j+1};
            }

            lane = new Lane(i, j, predecessorCoords, successorCoords);
            lane.setExitCoords(lane.getPosVector());
            lane.setTexture(TrainGame.goalTexture);
            lane.buildSprite(function(sprite) {
                var colorTexture = new PIXI.Graphics();
                colorTexture.beginFill(Number("0x" + TrainGame.idColors[id]), 1);
                colorTexture.drawCircle(lane.getPosVector().getX(), lane.getPosVector().getY(), gridSize/5.7);
                TrainGame.instance.graphics.addEnvironment(sprite, true);
                TrainGame.instance.graphics.addEnvironment(colorTexture, true);
            });
        };
    }
    Goal.s = [];
    Goal.prototype = new Track;
    Goal.prototype.constructor = Goal;

    return {
        Lane: Lane,
        Track: Track,
        Switch: Switch,
        Goal: Goal
    };
})();
