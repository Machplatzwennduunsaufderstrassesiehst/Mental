
var shop = {};

function buyItem(index) {
    serverConnection.communicate(makeSimpleCmd("buyItem", "index", index), function(msg){
        if (msg.success) {
            byID("boughtCheck"+index).style.display = "inline";
            backgroundColorAnimate("buyButton"+index, "#afa");
            byID("equipButton"+index).classList.remove("disabled");
        } else {
            backgroundColorAnimate("buyButton"+index, "#faa");
        }
    });
}

function equipItem(index) {
    serverConnection.communicate(makeSimpleCmd("equipItem", "index", index), function(msg){
        if (msg.success) {
            byID("equipCheck"+index).style.display = "inline";
            backgroundColorAnimate("equipButton"+index, "#afa");
        } else {
            backgroundColorAnimate("equipButton"+index, "#faa");
        }
    });
}

function listShopItems() {
    var html = "";
    for (var i = 0; i < shop.shopItemList.length; i++) {
        var item = shop.shopItemList[i];
        var unlocked = item.lvlUnlock <= player.playerLevel;
        var equipPossible = item.bought && !item.equipped;
        var buyPossible = !item.bought && (player.money >= item.price);
        var ocBuy = (true ? "buyItem("+i+");" : "");
        var ocEquip = (equipPossible ? "equipItem("+i+");" : "");
        html += '\
            <div style="display: block;height: auto;" class="shopItem"> \
                <div style="float:right;"> \
                    <div style="margin-bottom:5px;"><span id="buyButton'+i+'" onclick="'+ocBuy+'" class="btn'+(unlocked ? "" : "")+'">'+createIcon('cart')+"Buy"+'</span></div> \
                    <div><span id="equipButton'+i+'" onclick="'+ocEquip+'" class="btn'+(equipPossible ? "" : " disabled")+'">'+createIcon('wrench')+'Equip</span></div> \
                </div> \
                <span style="font-weight:bold;">'+(unlocked ? createIcon("lock-unlocked") : createIcon("lock-locked"))+' \
                ('+item.nr+') '+item.name+'</span> \
                <span id="boughtCheck'+i+'" style="display:'+(item.bought ? "inline" : "none")+';">'+createIcon("check")+'</span> \
                <span id="equipCheck'+i+'" style="display:'+(item.equipped ? "inline" : "none")+';">'+createIcon("paperclip")+'</span><br> \
                <span style="font-style:italic;">'+(unlocked ? "Buy for "+item.price+" "+createIcon("dollar",2,0) : "Unlocks at level " + item.lvlUnlock)+'</span> \
            </div>';
    }
    byID("shopItemList").innerHTML = "<div style='padding:10px;'>"+html+"</div>";
    setTimeout(function() {
        var shopItems = document.getElementsByClassName("shopItem");
        for (var i = 0; i < shopItems.length; i++) {
            var s = shopItems[i];
            s.style.height = s.children[0].clientHeight + "px";
        }
    },3);

}

//OBSERVERS ============================================================

var shopItemListObserver = new Observer("shopItemList", function(msg) {
    shop.shopItemList = msg.shopItemList;
    listShopItems();
});
