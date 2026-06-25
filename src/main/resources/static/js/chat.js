function showMessage(element, flag) {
    console.log(flag);
    debugger;
    var message = jQuery(element).parents(".message");
    var shortElement = message.find(".description-short");
    var longElement = message.find(".description-long");
    if (flag) {
        shortElement.hide();
        longElement.show();
    }
    else {
        shortElement.show();
        longElement.hide();
    }
}