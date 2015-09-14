(function($, undefined) {
  $.hpa = $.hpa || {};

  // Shortcut for filtering by not having a class, then adding a class.
  $.fn.extend({
    runOnceClass: function(cls) {
      return this.not('.' + cls).addClass(cls);
    }
  });

  //
  // Input Masking
  // ------------------------------------------------------------------------------
  //
  // Use jquery.maskedinput to bind masks based on input class.
  //
  var MASKS = { // Map from selector to mask format
    'input.date': '?99/99/9999', 
  };
  // Selector for all masked input fields
  var MASKED_FIELDS_SELECTOR = $.map(MASKS, function(val, key) { return key; }).join(', ');
  var MASK_OPTS = {placeholder: "  "};
  function maskAll(elems) {
    $.each(MASKS, function(selector, mask) {
      elems.filter(selector).mask(mask, MASK_OPTS);
    });
  }
  $(document).bind("pagecreate create", function(e) {
    maskAll($(MASKED_FIELDS_SELECTOR));
  });
  
  //
  // Numeric Formatting
  // ------------------------------------------------------------------------------
  //
  // Automatically format numeric fields when they are modified.
  //
  function unformatNumber(number) {
    return number.replace(/[^0-9.-]/g, '');
  }
  $(document).bind("pagecreate create", function() {
    $('input.currency').each(function() {
      // Determine the maximum value based on the maximum character length.
      var vMax = 9999999999999;
      var $input = $(this);
      var maxlength = $input.attr("maxlength");
      if(typeof maxlength != 'undefined') {
        vMax = Array(parseInt(maxlength) + 1).join("9"); // maxlength 9's
      }
      $input.autoNumeric({
        'aSign': '$',
        'wEmpty': 'sign',
        'vMin': 0,
        'vMax': vMax,
        'lZero': 'deny'
      });
      // Workaround: autonumeric 1.8.6 seems to add a leading zero if the
      // field starts with $, so unformat after initializing.
      $input.autoNumeric('set', unformatNumber($input.val()));
    });
  });
  
  //
  // Submit error popup
  // ------------------------------------------------------------------------------
  // Display the submit error popup dialog when the page is fist shown.
  //
  $(document).on('pagecreate', function() {
    var $warningPopup = $('#submitError');
    if($warningPopup.length) {
      $(document).on('pageshow.errors', function() {
        // Do not display if pageshow fires again, e.g. for other dialogs.
        $(document).off('pageshow.errors');
        $warningPopup.popup('open');
      });
    }
  });

})(jQuery);
