
GraphicsEngine.physics = (function() {
    
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
    function Position(x_, y_, r_) {
        var x = this.x = x_;
        var y = this.y = y_;
        this.getX = function () {
            return x;
        };
        this.getY = function () {
            return y;
        };
        var r = this.rotation = r_;

        // only move this position relative to the parameter position
        this.move = function (vector) {
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
    }

    function Movement(steps) {
        if (steps == undefined) steps = [];

        this.getSteps = function () {
            return steps;
        };

        var addVector = this.addVector = function (vector) {
            steps = copyTo(vector).getSteps();
        };

        var copyTo = this.copyTo = function (vector) {
            var movedSteps = [];
            for (var i = 0; i < steps.length; i++) {
                //alert(steps[i]);
                movedSteps[i] = steps[i].copyBy(vector);
                //alert(movedSteps[i]);
            }
            return new Movement(movedSteps);
        };

        this.getFirst = function () {
            return steps[0];
        };

        /**
         * remove the first p percent positions from position array
         * @param {number} p percentage between 0 and 1
         * @returns {undefined}
         */
        this.setProgress = function (p) {
            var newFirstIndex = Math.floor(steps.length * p);
            steps = steps.splice(newFirstIndex);
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
        return GraphicsEngine.graphics.calculateFrameAmount(time);
    }

    function StraightMovement(rotation, vector, time) {
        var steps = [];

        var dx = vector.getX();
        var dy = vector.getY();

        var frames = calculateFrameAmount(time);

        var x = 0,
            y = 0,
            p;
        for (var f = 0; f < frames; f++) {
            x += dx / frames;
            y += dy / frames;
            p = new Position(x, y, rotation);
            steps.push(p);
        }

        Movement.call(this, steps);
    }

    StraightMovement.prototype = new Movement;
    StraightMovement.prototype.constructor = StraightMovement;

    /**
     * @param degrees in radian - for left, + for right turn
     */
    function TurnMovement(startRotation, radius, degrees, time) {
        var steps = [];

        var frames = calculateFrameAmount(time);
        var stepWide = -degrees / frames;
        var x, y, p;
        var r = startRotation;
        for (var f = 0; f < frames; f++) {
            r += stepWide;
            x = Math.cos(r) - Math.cos(startRotation);
            x *= -Math.sign(degrees) * radius;
            y = Math.sin(r) - Math.sin(startRotation);
            y *= -Math.sign(degrees) * radius;
            p = new Position(x, y, r);
            steps.push(p);
        }

        Movement.call(this, steps);
    }

    TurnMovement.prototype = new Movement;
    TurnMovement.prototype.constructor = TurnMovement;

    /**
     * Straight deacceleration movement
     * @param{number} rotation
     * @param {GraphicsEngine.physics.Vector} vector
     * @param {number} initialTimePerTrack
     * @returns {StraightDeaccelerationMovement}
     */
    function StraightDeaccelerationMovement(rotation, vector, initialTimePerTrack) {
        var steps = [];

        var frames = calculateFrameAmount(initialTimePerTrack);

        var xSpeed = vector.getX() / frames;
        var ySpeed = vector.getY() / frames;

        var xAcc = -xSpeed / frames;
        var yAcc = -ySpeed / frames;

        var x = 0,
            y = 0,
            p;
        for (var f = 0; f < frames; f++) {
            x += xSpeed;
            y += ySpeed;
            xSpeed += xAcc;
            ySpeed += yAcc;
            p = new Position(x, y, rotation);
            steps.push(p);
        }

        Movement.call(this, steps);
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