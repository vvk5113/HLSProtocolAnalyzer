package com.psu.hpa.models;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.psu.hpa.validators.annotations.EmailFormat;
import com.psu.hpa.util.DateUtils;
import com.psu.hpa.validators.annotations.NotInFuture;

/**
 * Data model for HPA Stream URL Submission.
 */
public class UploadModel implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 43505303873271983L;
	
	/** The stream url. */
	@NotBlank
	@Size(max=100)
	private String streamURL;
	
	/** The Agent email. */
	@Size(max=60)
	@EmailFormat
	private String userEmail;

	/** The effective date. */
	@NotNull
	@NotInFuture
	private Date effectiveDate;

	public UploadModel() {
		effectiveDate = new Date();
	}

	/**
	 * Gets the effective date.
	 *
	 * @return the effective date
	 */
	public Date getEffectiveDate() { return effectiveDate; }

	/**
	 * Sets the effective date.
	 *
	 * @param effectiveDate the new effective date
	 */
	public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StartModel [streamURL=" + streamURL
				+ ", userEmail=" + userEmail
				+ ", effectiveDate=" + effectiveDate + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((streamURL == null) ? 0 : streamURL.hashCode());
		result = prime * result + ((userEmail == null) ? 0 : userEmail.hashCode());
		result = prime * result
				+ ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UploadModel other = (UploadModel) obj;
		if (streamURL == null) {
			if (other.streamURL != null) {
				return false;
			}
		} else if (!streamURL.equals(other.streamURL)) {
			return false;
		}
		if (userEmail == null) {
			if (other.userEmail != null) {
				return false;
			}
		} else if (!userEmail.equals(other.userEmail)) {
			return false;
		}
		if (effectiveDate == null) {
			if (other.effectiveDate != null) {
				return false;
			}
		} else if (!DateUtils.datesEqual(effectiveDate, other.effectiveDate)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the streamURL
	 */
	public String getStreamURL() {
		return streamURL;
	}

	/**
	 * @param streamURL the streamURL to set
	 */
	public void setStreamURL(String streamURL) {
		this.streamURL = streamURL;
	}
	
	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	
}
