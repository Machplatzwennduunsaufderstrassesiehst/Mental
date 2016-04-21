
var shoppingFrame = new Frame("shoppingFrame");

shoppingFrame.setOnOpen(function() {
    updateShopItems();
    byID("toLobby").style.display = "inline";
    var oldonclick = byID("toLobby").onclick;
    byID("toLobby").onclick = function(){byID("toLobby").onclick = oldonclick;navigation.openFrames(lobbyFrame);};
});

shoppingFrame.setOnClose(function() {
    byID("shopItemList").style.opacity = 0;
});

// FUNCTIONALITY =======================================================

var shop = {};
shop.equipButtonText = createIcon('wrench')+'Equip';
shop.unequipButtonText = createIcon('ban')+'Unequip';

function buyItem(index) {
    serverConnection.communicate(makeSimpleCmd("buyItem", "index", index), function(msg){
        if (msg.success) {
            byID("boughtCheck"+index).style.display = "inline";
            backgroundColorAnimate("buyButton"+index, "#afa");
            byID("equipButton"+index).classList.remove("disabled");
            player.update_("money", -msg.price);
        } else {
            backgroundColorAnimate("buyButton"+index, "#faa");
        }
    });
}

function reconfigureEquipButton(id, onclick, text) {
    var b = byID(id);
    b.onclick = onclick;
    b.innerHTML = text;
}

function equipItem(index) {
    serverConnection.communicate(makeSimpleCmd("equipItem", "index", index), function(msg){
        if (msg.success) {
            for (var i = 0; i < shop.shopItemList.length; i++) {// TODO für Booster etc
                if (byID("shopItem"+i).getAttribute("data-item-type") != msg.itemType) continue; // wenn die Typen nicht übereinstimmen, das equip symbol nicht löschen
                byID("equipCheck"+i).style.display = "none"; 
            }
            reconfigureEquipButton("equipButton"+index, function(){unequipItem(index);}, shop.unequipButtonText);
            byID("equipCheck"+index).style.display = "inline";
            backgroundColorAnimate("equipButton"+index, "#afa");
        } else {
            backgroundColorAnimate("equipButton"+index, "#faa");
        }
    });
}

function unequipItem(index) {
    serverConnection.communicate(makeSimpleCmd("unequipItem", "index", index), function(msg){
        if (msg.success) {
            for (var i = 0; i < shop.shopItemList.length; i++) {// TODO für Booster etc
                if (byID("shopItem"+i).getAttribute("data-item-type") != msg.itemType) continue; // wenn die Typen nicht übereinstimmen, das equip symbol nicht löschen
                byID("equipButton"+i).style.display = "none"; 
                reconfigureEquipButton("equipButton"+index, function(){equipItem(index);}, shop.equipButtonText);
            }
            byID("equipCheck"+index).style.display = "none";
            reconfigureEquipButton("equipButton"+index, function(){equipItem(index);}, shop.equipButtonText);
            backgroundColorAnimate("equipButton"+index, "#afa");
        } else {
            backgroundColorAnimate("equipButton"+index, "#faa");
        }
    });
}
            

function updateShopItems() {
    byID("shopItemList").innerHTML = "Loading Shop...";
    serverConnection.communicate(makeGetCmd("getShopItemList"), function(msg) {
        shop.shopItemList = msg.shopItemList;
        listShopItems();
    });
}

function listShopItems() {
    var html = "";
    for (var i = 0; i < shop.shopItemList.length; i++) {
        var item = shop.shopItemList[i];
        var unlocked = item.lvlUnlock <= player.playerLevel;
        var buyPossible = !item.bought && (player.money >= item.price);
        var ocBuy = (true ? "buyItem("+(item.nr-1)+");" : "");
        var ocEquip = (!item.equipped ? "equipItem("+(item.nr-1)+");" : "unequipItem("+(item.nr-1)+");");
        var textEquip = (!item.equipped ? shop.equipButtonText : shop.unequipButtonText);
        html += '\
            <div id="shopItem'+(item.nr-1)+'" style="display: block;height: auto;" class="shopItem" data-item-type="'+item.type+'"> \
                <div style="float:right;"> \
                    <div style="margin-bottom:5px;"><span id="buyButton'+i+'" onclick="'+ocBuy+'" class="btn'+(unlocked ? "" : " disabled")+'">'+createIcon('cart')+"Buy"+'</span></div> \
                    <div><span id="equipButton'+(item.nr-1)+'" onclick="'+ocEquip+'" class="btn'+(item.bought ? "" : " disabled")+'">'+textEquip+'</span></div> \
                </div> \
                <span style="font-weight:bold;">'+(unlocked ? createIcon("lock-unlocked") : createIcon("lock-locked"))+' \
                ('+item.nr+') '+item.name+'</span> \
                <span id="boughtCheck'+(item.nr-1)+'" style="display:'+(item.bought ? "inline" : "none")+';">'+createIcon("check")+'</span> \
                <span id="equipCheck'+(item.nr-1)+'" style="display:'+(item.equipped ? "inline" : "none")+';">'+createIcon("paperclip")+'</span><br> \
                <span style="font-style:italic;">'+(unlocked ? "Buy for $"+item.price+"." : "Unlocks at level " + item.lvlUnlock)+'</span> \
            </div>';
    }
    byID("shopItemList").innerHTML = ""+html+"";
    setTimeout(function() {
        var shopItems = document.getElementsByClassName("shopItem");
        for (var i = 0; i < shopItems.length; i++) {
            var s = shopItems[i];
            s.style.height = s.children[0].clientHeight + "px";
        }
        byID("shopItemList").style.opacity = 1;
    },50);

}

//OBSERVERS ============================================================

