package com.psu.hpa.models;

import java.io.Serializable;

/**
 * Data model for SubmitResults.
 */
public class SubmitResults implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4397783225102448125L;

	/** True if the results are unavailable due to the case not being submitted yet. */
	private boolean unavailableNotSubmitted;

	/** True if the results are unavailable due to the case being submitted in a previous session. */
	private boolean unavailableAlreadySubmitted;

	/** The login user name. */
	private String loginUserName;

	private boolean submissionSuccessful = false;

	/**
	 * Gets the login user name.
	 *
	 * @return the login user name
	 */
	public String getLoginUserName() {
		return loginUserName;
	}

	/**
	 * Sets the login user name.
	 *
	 * @param loginUserName the new login user name
	 */
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public boolean isUnavailableNotSubmitted() {
		return unavailableNotSubmitted;
	}

	public void setUnavailableNotSubmitted(boolean unavailableNotSubmitted) {
		this.unavailableNotSubmitted = unavailableNotSubmitted;
	}

	public boolean isUnavailableAlreadySubmitted() {
		return unavailableAlreadySubmitted;
	}

	public void setUnavailableAlreadySubmitted(boolean unavailableAlreadySubmitted) {
		this.unavailableAlreadySubmitted = unavailableAlreadySubmitted;
	}

	/**
	 * @return the submissionSuccessful
	 */
	public boolean isSubmissionSuccessful() {
		return submissionSuccessful;
	}

	/**
	 * @param submissionSuccessful the submissionSuccessful to set
	 */
	public void setSubmissionSuccessful(boolean submissionSuccessful) {
		this.submissionSuccessful = submissionSuccessful;
	}


}