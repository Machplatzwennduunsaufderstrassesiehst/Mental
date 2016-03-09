
/*
 * (c) Sven Langner 2015
 * 
 */

// all JSON Commands should be listed and hard coded here as builder functions
// return value must be a string!

function makeSetCmd(key, value) {
    var s = '{"type": "set_'+key+'", "'+key+'": "'+value+'"}';
    var cmd = JSON.parse(s);
    return cmd;
}

function makeSimpleCmd(type_, key, value) {
    var s = '{"type": "'+type_+'", "'+key+'": "'+value+'"}';
    var cmd = JSON.parse(s);
    return cmd;
}


