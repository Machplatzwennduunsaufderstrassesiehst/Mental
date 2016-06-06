
var player = new Player();

function Player() {
    var data = this.data = {};
    
    this.set_ = function(id, value) {
        data[id] = value;
        updateDataFields(id, value);
    };
    
    this.update_ = function(id, value) {
        if (data[id] === undefined) data[id] = 0;
        data[id] += value;
        updateDataFields(id, data[id]);
    };
    
    this.get = function(id) {
        return data[id];
    };
}
    
    
