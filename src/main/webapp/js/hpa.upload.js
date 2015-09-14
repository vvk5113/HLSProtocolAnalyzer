(function($, undefined) {
  // Populate a dropzone's thumbnail with a custom image.
  function populateThumbnail(file, imgSrc) {
    var $elem = $(file.previewElement);
    var $img = $elem.find('img:first').attr('src', imgSrc).show();
    $img.load(function() {
      // Hide text label
      $elem.find('.dz-filename').hide();
      // Fix aspect ratio
      var width = $img.prop('naturalWidth');
      var height = $img.prop('naturalHeight');
      if(width && height) {
        $img.css('width', width);
        if(width < 100) {
          $img.css('left', (100 - width) / 2);
        }
        $img.css('height', height);
        if(height < 100) {
          $img.css('top', (100 - height) / 2);
        }
      }
    });
  }

  $(document).on("pagecreate", function() {
    // Initialize the dropzone.
    Dropzone.options.upload = {
        url: "drop",
        maxFiles: 1,
        maxFilesize: 10,
        addRemoveLinks: true,
        dictDefaultMessage: "Drop image here (or click here.)",
        init: function() {
          // If the user already uploaded a file, populate it now.
          if($.hpa.originalFile) {
            this.emit("addedfile", $.hpa.originalFile);
            this.emit("success", $.hpa.originalFile);
            this.files.push($.hpa.originalFile);
            $.hpa.originalFile.accepted = true;
            $.hpa.originalFile.upload = {bytesSent: 0};
            populateThumbnail($.hpa.originalFile, $.hpa.originalThumbnail);
          }
          // Remove any successfully uploaded file from the server.
          this.on("removedfile", function(file) {
            if(file.status == "success" || file === $.hpa.originalFile) {
              $.ajax({
                url: "delete",
                type: "POST"
              });
            }  
          });
          // On a successful upload, fetch the thumbnail from the server.
          this.on("success", function(file, response) {
            if(response && 'thumbnail' in response) {
              populateThumbnail(file, response.thumbnail);
            }
          });
        }
    };

    // Display a modal dialog on submit.
    $('#submitBtn').click(function() {
      $.hpa.showBusyPopup("Submitting HPA request.  Please wait...");
      $('form').submit();
    });

  });
})(jQuery);
