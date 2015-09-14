// Put the original checkboxradio plugin into the "originalCheckboxRadio" class
// NOTE: This must be loaded BEFORE jQueryMobile, but AFTER jQuery

$(document).bind("mobileinit", function() {
  $.mobile.checkboxradio.prototype.options.initSelector = ".originalCheckboxRadio";

  $.mobile.ajaxEnabled = false;
  $.mobile.selectmenu.prototype.options.hidePlaceholderMenuItems = false;
  $.mobile.textinput.prototype.options.initSelector = "input[type='file'], " + $.mobile.textinput.prototype.options.initSelector;
});
