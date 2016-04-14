
var shop = {};

function listShopItems() {
    var html = "";
    for (var i = 0; i < shop.shopItemList.length; i++) {
        var item = shop.shopItemList[i];
        html += '\
            <div style="display: block;height: auto;" class="shopItem"> \
                <div style="float:right;"> \
                    <div style="margin-bottom:5px;"><span class="btn" style="">Buy</span></div> \
                    <div><span class="btn">Equip</span></div> \
                </div> \
                <span style="font-weight:bold;">('+item.nr+') '+item.name+'</span><br> \
                <span style="font-style:italic;">'+item+'</span> \
            </div>';
    }
    byID("shoppingFrame").innerHTML = html;
    setTimeout(function(){
        var shopItems = document.getElementsByClassName("shopItem");
        for (var i = 0; i < shopItems.length; i++) {
            var s = shopItems[i];
            s.style.height = s.children[0].clientHeight + "px";
        }
    },100);

}

//OBSERVERS ============================================================

var shopItemListObserver = new Observer("shopItemList", function(msg) {
    shop.shopItemList = msg.shopItemList;
    console.log("test");
});
