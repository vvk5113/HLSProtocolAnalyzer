package com.psu.hpa.models;

import java.io.Serializable;
import javax.validation.constraints.Size;

import com.psu.hpa.validators.annotations.EmailFormat;
import com.psu.hpa.validators.annotations.URLFormat;

/**
 * Data model for HPA Stream URL Submission.
 */
public class UploadModel implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 43505303873271983L;
	
	/** The stream url. */
	@URLFormat
	private String streamURL;
	
	/** The user email. */
	@Size(max=60)
	@EmailFormat
	private String userEmail;

	/** The master playlist validation result. */
	private String masterPlaylistValidationResult;
	
	/** The media playlist validation result. */
	private String mediaPlaylistValidationResult;
	
	private String guid;

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StartModel [streamURL=" + streamURL
				+ ", userEmail=" + userEmail + "]";
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

	/**
	 * @return the masterPlaylistValidationResult
	 */
	public String getMasterPlaylistValidationResult() {
		return masterPlaylistValidationResult;
	}

	/**
	 * @param masterPlaylistValidationResult the masterPlaylistValidationResult to set
	 */
	public void setMasterPlaylistValidationResult(
			String masterPlaylistValidationResult) {
		this.masterPlaylistValidationResult = masterPlaylistValidationResult;
	}

	/**
	 * @return the mediaPlaylistValidationResult
	 */
	public String getMediaPlaylistValidationResult() {
		return mediaPlaylistValidationResult;
	}

	/**
	 * @param mediaPlaylistValidationResult the mediaPlaylistValidationResult to set
	 */
	public void setMediaPlaylistValidationResult(
			String mediaPlaylistValidationResult) {
		this.mediaPlaylistValidationResult = mediaPlaylistValidationResult;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	
	
}
