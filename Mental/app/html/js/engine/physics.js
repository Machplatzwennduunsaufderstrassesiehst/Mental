engine.physics = (function () {

    function Vector(x, y) {
        this.multiply = function (skalar) {
            x *= skalar;
            y *= skalar;
        };

        this.add = function (u) {
            x += u.getX();
            y += u.getY();
        };

        this.normalize = function () {
            this.multiply(1 / length());
        };

        var length = this.length = function () {
            return Math.sqrt(x * x + y * y);
        };

        this.getX = function () {
            return x;
        };
        this.getY = function () {
            return y;
        };

        this.copy = function () {
            return new Vector(x, y);
        };
    }

    // static
    Vector.newFromTo = function (u, v) {
        u = u.copy();
        v = v.copy();
        u.multiply(-1);
        v.add(u);
        return v;
    };
    Vector.newWithRandomDirection = function (distance) {
        var direction = Math.random() * Math.PI * 2;
        var x = Movement.cos(direction) * distance;
        var y = Movement.sin(direction) * distance;
        return new Vector(x, y);
    };

    // used to describe one element of a movement
    function Position(x, y, r) {
        this.x = x;
        this.y = y;
        if (r == undefined) {
            r = 0;
        }
        this.rotation = r;
        this.getX = function () {
            return x;
        };
        this.getY = function () {
            return y;
        };

        // only move this position relative to the parameter position
        this.getNextPosition = function (vector) {
            this.x = x = x + vector.getX();
            this.y = y = y + vector.getY();
        };

        this.toString = function () {
            return "Pos(" + x + "," + y + ", " + r + ")\n";
        };

        this.copyBy = function (vector) {
            if (vector == undefined) vector = new Vector(0, 0);
            return new Position(Math.floor(x + vector.getX()), Math.floor(y + vector.getY()), r);
        };

        this.set = function () {
            if (arguments.length == 1) {
                x = this.x = arguments[0].x;
                y = this.y = arguments[0].y;
                r = this.rotation = arguments[0].rotation;
            } else {
                x = this.x = arguments[0];
                y = this.y = arguments[1];
                r = this.rotation = arguments[2];
            }
        };
    }

    function Movement(startPosition) {

        this.startPosition = startPosition;

        this.getPosition = function (percentage) {
        };

    }

    // static Movement class part
    Movement.sinValues = [];
    Movement.cosValues = [];
    Movement.rotationResolution = 250;
    Movement.isSetUp = false;

    Movement.sin = function (x) {
        if (!Movement.isSetUp) Movement.setup();
        var i = Math.floor(x * Movement.rotationResolution / Math.PI / 2);
        return Movement.sinValues[i];
    };

    Movement.cos = function (x) {
        if (!Movement.isSetUp) Movement.setup();
        var i = Math.floor(x * Movement.rotationResolution / Math.PI / 2);
        return Movement.cosValues[i];
    };

    Movement.setup = function () {
        for (var i = 0; i <= Math.PI * 2; i += Math.PI * 2 / Movement.rotationResolution) {
            Movement.sinValues.push(Math.sin(i));
            Movement.cosValues.push(Math.cos(i));
        }
        Movement.isSetUp = true;
    };

    // calculates the amount of frames to be rendered in the "time" (in seconds)
    function calculateFrameAmount(time) {
        return engine.graphics.calculateFrameAmount(time);
    }

    function StraightMovement(rotation, vector) {
        Movement.call(this, new Position(0, 0, rotation));

        var dx = vector.getX();
        var dy = vector.getY();

        this.getPosition = function (percentage) {
            return {
                x: this.startPosition.x + percentage * dx,
                y: this.startPosition.y + percentage * dy,
                rotation: this.startPosition.rotation
            }
        };

    }

    StraightMovement.prototype = new Movement;
    StraightMovement.prototype.constructor = StraightMovement;

    /**
     * @param degrees in radian + for left, - for right turn
     */
    function TurnMovement(startRotation, radius, degrees) {
        Movement.call(this, new Position(0, 0, startRotation));

        this.getPosition = function (percentage) {
            r = startRotation - percentage * degrees;
            var x, y;
            x = Math.cos(r) - Math.cos(startRotation);
            x *= -Math.sign(degrees) * radius;
            y = Math.sin(r) - Math.sin(startRotation);
            y *= -Math.sign(degrees) * radius;
            return {
                x: this.startPosition.x + x,
                y: this.startPosition.y + y,
                rotation: r
            }
        };

    }

    TurnMovement.prototype = new Movement;
    TurnMovement.prototype.constructor = TurnMovement;

    /**
     * Straight deacceleration movement
     * @param{number} rotation
     * @param {engine.physics.Vector} vector direction and distance
     * @returns {StraightDeaccelerationMovement}
     */
    function StraightDeaccelerationMovement(rotation, vector) {
        Movement.call(this, new Position(0, 0, rotation));

        this.getPosition = function (percentage) {
            percentage = percentage - percentage * percentage * 0.5;
            var x = vector.getX() * percentage * 2;
            var y = vector.getY() * percentage * 2;
            return {
                x: this.startPosition.x + x,
                y: this.startPosition.y + y,
                rotation: this.startPosition.rotation
            }
        };
    }

    StraightDeaccelerationMovement.prototype = new Movement;
    StraightDeaccelerationMovement.prototype.constructor = StraightDeaccelerationMovement;

    return {
        Vector: Vector,
        Position: Position,
        Movement: Movement,
        calculateFrameAmount: calculateFrameAmount,
        StraightDeaccelerationMovement: StraightDeaccelerationMovement,
        StraightMovement: StraightMovement,
        TurnMovement: TurnMovement
    };

})();