package com.psu.hpa.format;

/**
 * Thrown when a DateFormatter cannot parse a date.  It will always contain a message code,
 * as well as the default message it corresponds to.
 */
public class DateFormatterException extends RuntimeException {
	private static final long serialVersionUID = -1054654700600080235L;
	
	/** The message code. */
	private String code;
	
	/**
	 * Instantiates a new date formatter exception.
	 *
	 * @param code the code
	 * @param defaultMessage the default message
	 */
	public DateFormatterException(String code, String defaultMessage) {
		super(defaultMessage);
		this.code = code;
	}
	
	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
