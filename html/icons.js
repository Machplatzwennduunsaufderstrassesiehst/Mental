
function createIcon(key, size, offsetpx) {
    if (size === undefined) size = 2;
    var size_;
    if (size < 2) {
        size_ = "";
    } else {
        size_ = "-" + size + "x";
    }
    if (offsetpx !== undefined) size = offsetpx;
    var html = '<img style="margin-bottom:-'+size+'px;" src="graphics/icons/open-iconic-master/png/'+key+size_+'.png" ';
    html += 'alt="'+key+'">&nbsp;';
    return html;
}

function iconize() {
    var icons = document.getElementsByTagName("span");
    for (var i = 0; i < icons.length; i++) {
        var icon = icons[i];
        if (!icon.hasAttribute("data-icon")) continue;
        icon.innerHTML = createIcon(icon.getAttribute("data-icon"));
    }
}
