package com.psu.hpa.application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.psu.hpa.controllers.MainController;

/**
 * Intercept upload requests where the upload has already been completed,
 * and return an error instead.
 */
public class CompletedUploadInterceptor extends HandlerInterceptorAdapter {
	/** The pattern to match an application URL. */
	private String match;

	/** The compiled match pattern. */
	private Pattern matchPattern;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	if(!request.getMethod().equalsIgnoreCase("POST")) {
    		return true;
    	} else {
        	String fullPath = request.getRequestURI();
        	String rootPath = request.getContextPath();
        	String relativePath = fullPath.substring(rootPath.length());

        	Matcher matcher = matchPattern.matcher(relativePath);
        	if(matcher.matches()) {
        		// Potential filtering URL!  Find the user's completed uploads.
        		HttpSession session = request.getSession(false);
        		if(session != null) {
        			MainController.Form form = MainController.getForm(session);
        			if(form != null && form.getCompleted() != null) {
        				// Check if this upload is already completed.
        				String id = matcher.group(1);
        				if(form.getCompleted().contains(new GUID(id))) {
        					// User is trying to POST on a completed upload.  Reject the request.
        					response.sendError(HttpServletResponse.SC_CONFLICT, "Upload has already been submitted");
        					return false;
        				}
        			}
        		}
        	}
        	return true;
    	}
    }

	/**
	 * Gets the pattern to match an application URL.
	 *
	 * @return the match
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * Sets the pattern to match an application URL.  The first match group should
	 * contain the application ID.
	 *
	 * @param match the new match
	 */
	public void setMatch(String match) {
		this.match = match;
		this.matchPattern = Pattern.compile(match);
	}

}