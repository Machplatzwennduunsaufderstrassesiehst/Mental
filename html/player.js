
var player = new Player();

function Player() {
    this.money = 0;
    this.level = 0;
    this.title = "";
    this.name = "";
    var self = this;
    
    this.set_ = function(id, value) {
        self[id] = value;
        updateDataFields(id, value);
    }
    
    this.update_ = function(id, value) {
        self[id] = value;
        updateDataFields(id, value);
    }
}
    
    
