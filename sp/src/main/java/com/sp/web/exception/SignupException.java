/**
 * 
 */
package com.sp.web.exception;

/**
 * @author daxabraham
 *
 */
public class SignupException extends SPException {

	private static final long serialVersionUID = 1773099666192594234L;

	/**
	 * @param message
	 */
	public SignupException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SignupException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SignupException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SignupException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
