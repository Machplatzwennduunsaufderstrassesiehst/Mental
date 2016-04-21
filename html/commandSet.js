
/*
 * (c) Sven Langner 2015
 * 
 */

// JSON command helper functions

function makeSetCmd(key, value) {
    var cmd = {};
    cmd.type = "set" + key.capitalize();
    cmd[key] = value;
    return cmd;
}

function makeSimpleCmd(type_, key, value) {
    var cmd = {};
    cmd.type = type_;
    cmd[key] = value;
    return cmd;
}

function makeGetCmd(type_) {
    var cmd = {};
    cmd.type = type_;
    return cmd;
}

