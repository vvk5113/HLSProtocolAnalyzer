(function( $, window ) {
  // jQuery Mobile's get_orientation() takes ~350ms initial render.
  // Stub it out on IE8.
  $.event.special.orientationchange = {
    orientation: function() {
      return "portrait";
    }
  };

  // acceptData eats a lot of time on hover.  This does additional checking, so it's safe to
  // remove in production, so long as testing is performed on a faster browser.
  $.acceptData = function(elem) {
    return true;
  };

  var fixCheckboxWidths = function() {
    // IE requires a fixed width for horizontal checkboxes to display without breaks.
    $(":jqmData(ie-width)").each(function() {
      var elem = $(this);
      var width = elem.jqmData("ie-width") + 'px';
      elem.css('min-width', width);
      elem.css('width', width);
    });
  };

  $(document).ready(fixCheckboxWidths)
	     .bind('pageinit', fixCheckboxWidths);
})( jQuery, window );
