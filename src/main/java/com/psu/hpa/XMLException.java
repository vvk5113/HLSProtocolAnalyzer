package com.psu.hpa;

public class XMLException extends Exception {
	private static final long serialVersionUID = -4401826548365593378L;
	
	public XMLException() {
		super();
	}
	
	public XMLException(String message) {
		super(message);
	}
	
	public XMLException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public XMLException(Throwable cause) {
		super(cause);
	}
}
