package com.psu.hpa;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** An upload could not be found. */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UploadNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8966662820071314083L;

	/** Instantiates a new application type not found exception. */
	public UploadNotFoundException() {
		super("Application not found");
	}
}
