
var welcomeFrame = new Frame("welcome");

welcomeFrame.setOnOpen(function() {
    setDoOnEnter(function(){netManager.scanManually(byID('ip').value);});
});

// FUNCTIONALITY =======================================================

function onServerJoinClick() {
    netManager.scanManually(byID('ip').value);
}
