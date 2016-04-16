
/*
 * (c) Sven Langner 2015
 * 
 */

// JSON command helper functions

function makeSetCmd(key, value) {
    var s = '{"type": "set'+key.capitalize()+'", "'+key+'": "'+value+'"}';
    var cmd = JSON.parse(s);
    return cmd;
}

function makeSimpleCmd(type_, key, value) {
    var s = '{"type": "'+type_+'", "'+key+'": "'+value+'"}';
    var cmd = JSON.parse(s);
    return cmd;
}

function makeGetCmd(type_) {
    var s = '{"type": "'+type_+'"}';
    var cmd = JSON.parse(s);
    return cmd;
}

