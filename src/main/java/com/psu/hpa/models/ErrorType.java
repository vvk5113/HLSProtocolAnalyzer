package com.psu.hpa.models;

public enum ErrorType {
	
	TYPE("Error Type"),

	MISSING_TAG("Missing Tag"),

	MISSING_STREAM_FILE("Missing Stream File"),
	
	MISSING_SEGMENT_FILE("Missing Media Playlist File"),
	
	INCORRECT_FORMAT("Incorrect Format"),
	
	DUPLICATE_TAG("Duplicate Tag"),
	
	FUNCTIONAL_TAG("Functional");
	
	private String value;
	
	ErrorType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

};
