package com.psu.hpa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.psu.hpa.application.GUID;
import com.psu.hpa.models.UploadModel;

/** Controller for starting a new upload.
 *
 * This is split from the MainController because in order for @ModelAttribute
 * to populate the current upload, the "id" @PathVariable must be present in
 * all paths.  Creating a new upload means there's no "id" in the path yet.
 */
@Controller
@SessionAttributes(SessionUploadFormController.FORM_NAME)
public class NewUploadController extends SessionUploadFormController {
	/**
	 * Handle a GET request for a new upload.  Add it to the session and
	 * redirect to its upload page.
	 *
	 * @param form the upload form
	 * @return the redirect view to the new upload
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public View getNewUpload(@ModelAttribute(FORM_NAME) Form form) {
		// Redirect to a new random GUID.
		GUID guid = new GUID();
		form.getUploads().put(guid, new UploadModel());
		String next = String.format("%s/%s", guid.urlRepresentation, MainController.RELATIVE_URL);
		return new RedirectView(next);
	}
}
