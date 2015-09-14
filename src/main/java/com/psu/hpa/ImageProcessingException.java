package com.psu.hpa;

/** Exception while processing an image. */
public class ImageProcessingException extends Exception {
	private static final long serialVersionUID = 8164263706916373736L;

	public ImageProcessingException(String message) {
		super(message);
	}

	public ImageProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
