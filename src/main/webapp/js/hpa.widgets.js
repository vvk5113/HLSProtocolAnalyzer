(function($, undefined) {
  var log = function log(message) {
    if(typeof console == 'object') {
        console.log(message);
    }
  };

  //
  // checkedRadio widget
  // ------------------------------------------------------------------------------
  //
  // This is a specialized version of the "checkboxradio" widget that only displays
  // checkboxes for radio buttons
  //
  $.widget("hpa.checkedRadio", $.mobile.checkboxradio, {
    options: {
      initSelector: ".checkedRadio"
    },
    _create: function() {
      var self = this,
        input = this.element,
        o = this.options,
        inheritAttr = function( input, dataAttr ) {
          return input.jqmData( dataAttr ) || input.closest( "form, fieldset" ).jqmData( dataAttr );
        },
      // NOTE: Windows Phone could not find the label through a selector
      // filter works though.
        parentLabel = $( input ).closest( "label" ),
        label = parentLabel.length ? parentLabel : $( input ).closest( "form, fieldset, :jqmData(role='page'), :jqmData(role='dialog')" ).find( "label" ).filter( "[for='" + input[0].id + "']" ).first(),
        inputtype = input[0].type,
        mini = inheritAttr( input, "mini" ) || o.mini,
        checkedState = "checkbox-on",
        uncheckedState = "checkbox-off",
        iconpos = inheritAttr( input, "iconpos" ),
        checkedClass = "ui-" + checkedState,
        uncheckedClass = "ui-" + uncheckedState;

      // Expose for other methods
      $.extend( this, {
        label: label,
        inputtype: inputtype,
        checkedClass: checkedClass,
        uncheckedClass: uncheckedClass,
        checkedicon: checkedState,
        uncheckedicon: uncheckedState
      });

      // If there's no selected theme check the data attr
      if ( !o.theme ) {
        o.theme = $.mobile.getInheritedTheme( this.element, "c" );
      }

      label.buttonMarkup({
        theme: o.theme,
        icon: uncheckedState,
        shadow: false,
        mini: mini,
        iconpos: iconpos
      });

      // Wrap the input + label in a div
      var wrapper = document.createElement('div');
      wrapper.className = 'ui-checkbox';

      input.add( label ).wrapAll( wrapper );

      label.bind({
        vmouseover: function( event ) {
          if ( $( this ).parent().is( ".ui-disabled" ) || input.prop("readonly")) {
            event.stopPropagation();
          }
        },

        vclick: function( event ) {
          if (input.is( ":disabled") || input.prop("readonly")) {
            event.preventDefault();
            return;
          }

          self._cacheVals();

          input.prop( "checked", /* inputtype === "radio" && true || */ !input.prop( "checked" ) );

          // trigger click handler's bound directly to the input as a substitute for
          // how label clicks behave normally in the browsers
          // TODO: it would be nice to let the browser's handle the clicks and pass them
          //       through to the associate input. we can swallow that click at the parent
          //       wrapper element level
          input.triggerHandler( 'click' );

          // Input set for common radio buttons will contain all the radio
          // buttons, but will not for checkboxes. clearing the checked status
          // of other radios ensures the active button state is applied properly
          self._getInputSet().not( input ).prop( "checked", false );

          // HPA: clicking on a checkbox/radio focuses it
          input.focus();

          self._updateAll();
          return false;
        }
      });
      input
        .bind({
          vmousedown: function() {
            self._cacheVals();
          },

          vclick: function() {
            var $this = $( this );
            if(!$this.prop('readonly')) {

              // Adds checked attribute to checked input when keyboard is used
              if($this.is(":checked")) {

                $this.prop("checked", true);
                self._getInputSet().not($this).prop("checked", false);
              } else {

                $this.prop("checked", false);
              }

              self._updateAll();
            }
          },

          focus: function() {
            label.addClass( $.mobile.focusClass );
          },

          blur: function() {
            label.removeClass( $.mobile.focusClass );
          },

          keydown: function(event) {
            // Tab to the next/previous element in the input set.
            if(event.which == 9) { // tab
              var direction = event.shiftKey ? -1 : 1,
                inputSet = self._getInputSet(),
                currentIndex = inputSet.index(event.target),
                nextIndex = currentIndex + direction;
              if(nextIndex >= 0 && nextIndex < inputSet.length) {
                event.preventDefault();
                event.stopPropagation();
                $(event.target).blur();
                $(inputSet.get(nextIndex)).focus();
              }
            }
          }
        });
      this._handleFormReset();
      this.refresh();
    },

    _updateAll: function() {
      var self = this;

      this._getInputSet().each(function() {
        var $this = $( this );

        if ( this.checked || self.inputtype === "checkbox" ) {
          $this.trigger( "change" );
        }
      })
        .checkedRadio( "refresh" );
    },

    refresh: function() {
      var input = this.element[ 0 ],
        checkedClass = this.checkedClass,
        label = this.label;

      if ( input.checked ) {
        label.removeClass( this.uncheckedClass ).addClass( checkedClass ).buttonMarkup( { icon: this.checkedicon } );
      } else {
        label.removeClass( checkedClass ).addClass( this.uncheckedClass ).buttonMarkup( { icon: this.uncheckedicon } );
      }

      if ( input.disabled || input.readonly ) {
        this.disable();
      } else {
        this.enable();
      }
    }
  }, $.mobile.behaviors.formReset );

  //
  // Dropdown keyboard navigation
  // ------------------------------------------------------------------------------
  //
  // Handle keydown events while a selectmenu popup is displayed.  In the handler,
  // jump to the first list item of the input key, advancing to the next matching
  // item if the same key is pressed again.
  //
  $(document).on("pagecreate create", function(ev) {
    // We need to search the entire DOM every time, since dialogs are added outside of the target.
    $('.ui-selectmenu.ui-popup')
      .runOnceClass('selectkeyhandler')
      .on("popupafteropen", function() {
        $(document).on("keydown.selectkeyhandler", selectMenuKeyHandler);
      })
      .on("popupafterclose", function() {
        $(document).off("keydown.selectkeyhandler");
      });

    // Allow the up and down keys to be used on a select button not displaying a menu.
    $(ev.target).find('select').each(function() {
      var $select = $(this),
        selectmenuWidget = $select.data($.mobile.selectmenu.prototype.widgetFullName);
      if(selectmenuWidget) {
        var button = document.getElementById(selectmenuWidget.buttonId);
        $(button).keydown(selectButtonKeyHandler);
      }
    });
  });

  function selectMenuKeyHandler(ev) {
    var $list = $(ev.target).parents('ul').find('a'),
      selectedItem = document.activeElement,
      nextItem = optionForKeycode($list, selectedItem, ev.which);
    if(nextItem) {
      ev.preventDefault();
      $(nextItem).focus();
    }
  }

  function selectButtonKeyHandler(ev) {
    var direction = (ev.which == 38) ? -1 : 1,
      $select = $(ev.target).siblings('select'),
      realSelectId = $select.data('select-id'),
      isDummySelect = !!realSelectId,
      $realSelect = isDummySelect ? $(document.getElementById(realSelectId)).find('select') : $select,
      $options = $realSelect.children('option:enabled'),
      currentValue = $select.val();

    if(ev.which == 38 || ev.which == 40) { // up or down arrow
      ev.preventDefault();
      for(var i = 0; i < $options.length; i++) {
        if($options.get(i).value === currentValue) {
          var nextIndex = i + direction;
          if(nextIndex >= 0 && nextIndex < $options.length) {
            if(isDummySelect) {
              setDummyDropdownOption($select, $options.eq(nextIndex));
            } else {
              $select.val($options.get(nextIndex).value);
              refreshSelectmenu($select);
              $select.change();
            }
          }
          break;
        }
      }
    } else {
      // Not up/down.  Try a letter key search.
      var selectedOption;
      if(isDummySelect) {
        selectedOption = $options.filter(function() { return this.value === currentValue; })
      } else {
        selectedOption = $select.children('option:selected');
      }
      var option = optionForKeycode($options, selectedOption, ev.which);
      if(option != null) {
        if(isDummySelect) {
          setDummyDropdownOption($select, $(option));
        } else {
          $select.val(option.value);
          refreshSelectmenu($select);
          $select.change();
        }
      }
    }
  }

  /**
   * Find the option in a list of options for a keycode.  Each time the user presses the same key, the selected item
   * will cycle through the list of options whose text begins with the character of that key.
   *
   * @param $options jQuery array of elements whose text will be examined
   * @param selectedOption the currently selected item, an element of $options
   * @param keycode the keydown event keycode
   * @returns {*} an item in the $options array, or null if none match
   */
  function optionForKeycode($options, selectedOption, keycode) {
    var firstLetter = $.trim(String.fromCharCode(keycode));
    if(firstLetter.length) {
      var upperLetter = firstLetter.toUpperCase(),
        $matchingItems = $options.filter(function() {
          return (upperLetter == $(this).text().charAt(0).toUpperCase());
        });
      // Quick abort if no matching items.
      if($matchingItems.length == 0) {
        return null;
      }
      // If the selected item is already in this list, advance to the next item.
      var matchingIndex = $matchingItems.index(selectedOption),
        nextIndex;
      if(matchingIndex != -1) {
        nextIndex = matchingIndex + 1;
        if(nextIndex == $matchingItems.length) {
          nextIndex = 0;
        }
      } else {
        // Selected item is not in the matching list.  Jump to the first item.
        nextIndex = 0;
      }
      return $matchingItems.get(nextIndex);
    }
  }

  //
  // Force dropdown popup overlay
  // ------------------------------------------------------------------------------
  //
  // Patch the $.height method to return a large number while a dropdown popup is
  // opening to force the display method to overlay.
  //
  $( document ).bind( "selectmenubeforecreate", function( event ) {
    var selectmenuWidget = $(event.target).data($.mobile.selectmenu.prototype.widgetFullName),
      realDecideFormat = selectmenuWidget._decideFormat;
    // Alex requested overlay menus for select menus larger than the screen.
    // jQuery Mobile has no option for this, so we have to patch the _decideFormat method.
    selectmenuWidget._decideFormat = function() {
      // Patch height to prevent the dialog mode from being selected.
      var realHeight = $.fn.height;
      $.fn.height = function() {
        var elem = this.jquery ? this[0] : this;
        return $.isWindow(elem) ? 999999 : realHeight.call(this);
      };
      try {
        realDecideFormat.apply(selectmenuWidget);
      } finally {
        $.fn.height = realHeight;
      }
    };
  });

  // Strip out unnecessary title from select menus.  It just duplicates the placeholder item.
  $(document).on('pagecreate create', function() {
    $('.ui-selectmenu .ui-title').remove();
  });

  //auto self-init widgets
  $( document ).bind( "pagecreate create", function( e ){
    $.hpa.checkedRadio.prototype.enhanceWithin(e.target);
  });
  
  //
  // Refresh a checkbox.
  // ------------------------------------------------------------------------------
  // Handle refreshing a hpa.checkedRadio widget.  If the widget has not been
  // initialized yet, do nothing.
  //
  function refreshCheckbox($cb) {
    if($cb.data($.hpa.checkedRadio.prototype.widgetFullName)) {
      $cb.checkedRadio('refresh');
    }
  }

  //
  // Refresh a select menu.
  // ------------------------------------------------------------------------------
  // Handle refreshing a mobile.selectmenu widget.  If the widget has not been
  // initialized yet, do nothing.
  //
  function refreshSelectmenu($selects, forceRefresh) {
    $selects.each(function() {
      var $select = $(this);
      if($select.data($.mobile.selectmenu.prototype.widgetFullName)) {
        $select.selectmenu('refresh', forceRefresh);
      }
    });
  }

  //
  // Display a "busy" popup
  // ------------------------------------------------------------------------------
  // Show a modal popup used for when a long operation is running.
  //
  function showBusyPopup(message) {
    // Open the busy popup.
    $('#busyPopup')
      .popup('open')
      .find('.message').text(message);
    // Show the loading spinner.
    $.mobile.loading('show');
    // Prevent the user from scrolling.
    $("body").css("overflow", "hidden")
  }

  function hideBusyPopup() {
    $('#busyPopup').popup('close');
    $.mobile.loading('hide');
    $("body").css("overflow", "hidden");
  }


  // jQuery Mobile 1.3 fixes
  $(document).on("pagecreate create", function(ev) {
    // Resize the text input containers to fit their contents.
    $(ev.target).find('div.ui-input-text').each(function() {
      var $div = $(this),
        $input = $div.find('input:not(.noresize)');
      if($input.length) {
        var width = $div.find('input').width();
        $div.width(width);
      }
    });
    // Copy the error class from a text input to its enclosing div.
    $(ev.target).find('input[type=text].error').each(function() {
      $(this).parent().addClass('error');
    })
    // Fix spacebar handler for select menus.
    $(ev.target).find('.ui-select .ui-btn').runOnceClass('spacefix').keydown(function(ev) {
      if(ev.keyCode === $.mobile.keyCode.SPACE) {
        $(ev.target).parent().find('select').selectmenu('open');
        ev.preventDefault();
      }
    });
  });

  // Export functions
  $.hpa.refreshCheckbox = refreshCheckbox;
  $.hpa.refreshSelectmenu = refreshSelectmenu;
  $.hpa.showBusyPopup = showBusyPopup;
  $.hpa.hideBusyPopup = hideBusyPopup;
})(jQuery);
