
function listShopItems(items) {
    var html = "";
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        html += "";
    }
}

//OBSERVERS ============================================================

var shopItemListObserver = new Observer("shopItemList", function(msg) {
    listShopItems(msg.shopItemList);
});
