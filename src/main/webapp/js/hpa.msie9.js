(function( $, window ) {
  var fixCheckboxWidths = function() {
    // IE requires a fixed width for horizontal checkboxes to display without breaks.
    $(":jqmData(ie-width)").each(function() {
      var elem = $(this);
      // IE9 requires 1 (or sometimes 3) more pixel(s) than 8 (rounding differently?)
      var width = elem.jqmData("ie-width") + 1 + 'px';
      elem.css('min-width', width);
      elem.css('width', width);
    });
  };
	
  $(document).ready(fixCheckboxWidths)
             .bind("pageinit", fixCheckboxWidths);
})( jQuery, window );
