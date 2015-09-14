package com.psu.hpa.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.psu.hpa.application.GUID;
import com.psu.hpa.models.UploadModel;

/**
 * Base class for controllers that use the Upload Form.
 */
public class SessionUploadFormController {

	/** All attributes to store in the session for this controller. */
	public static class Form implements Serializable {
		private static final long serialVersionUID = 5480724966428525507L;

		/** Map from GUIDs to uploads. */
		Map<GUID, UploadModel> uploads;

		/** Set of GUIDs that have been successfully transmitted to NBSS. */
		Set<GUID> completed;

		public Map<GUID, UploadModel> getUploads() {
			return uploads;
		}

		public void setUploads(Map<GUID, UploadModel> uploads) {
			this.uploads = uploads;
		}

		public Set<GUID> getCompleted() {
			return completed;
		}

		public void setCompleted(Set<GUID> completed) {
			this.completed = completed;
		}
	}

	/** Session attribute name. */
	public static final String FORM_NAME = "UploadForm";

	/**
	 * Gets the upload form for this session.
	 *
	 * @param session the session
	 * @return the form
	 */
	public static Form getForm(HttpSession session) {
		return (Form)session.getAttribute(FORM_NAME);
	}

	/**
	 * Called on a new session to populate the model with defaults.
	 *
	 * @return the upload form
	 */
	@ModelAttribute(FORM_NAME)
	public Form populateForm() {
		Form form = new Form();
		form.setUploads(new HashMap<GUID, UploadModel>());
		form.setCompleted(new HashSet<GUID>());
		return form;
	}

}