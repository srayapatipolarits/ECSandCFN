/**
 * 
 */
package com.sp.web.exception;

/**
 * @author daxabraham
 * 
 * The exception class for invalid parameters sent to SP
 */
public class InvalidParameterException extends SPException {

	/**
	 * The generated serial version
	 */
	private static final long serialVersionUID = 8413651131292284371L;

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidParameterException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
	}
}
