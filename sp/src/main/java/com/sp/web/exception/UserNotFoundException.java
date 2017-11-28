/**
 * 
 */
package com.sp.web.exception;

/**
 * UserNotFoundException is thrown when user is not found in the respository.
 * @author pradeep
 *
 */
public class UserNotFoundException extends AccountException {

	/**
	 * @param message
	 */
	public UserNotFoundException(String message) {
		super(message);
	}
	
	/**
	 * <code>Constructor</code> with throwable support
	 * @param message exception message
	 * @param cause throwable exception.
	 */
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 3686259362038144405L;

}
