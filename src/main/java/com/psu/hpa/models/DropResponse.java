package com.psu.hpa.models;

public class DropResponse {

	/** Error message. */
	private String error;

	/** Relative URL to thumbnail image. */
	private String thumbnail;

	public static DropResponse withError(String error) {
		DropResponse result = new DropResponse();
		result.error = error;
		return result;
	}

	public static DropResponse withThumbnail(String thumbnail) {
		DropResponse result = new DropResponse();
		result.thumbnail = thumbnail;
		return result;
	}


	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}


}
