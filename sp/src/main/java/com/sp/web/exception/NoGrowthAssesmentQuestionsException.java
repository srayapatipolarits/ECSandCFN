/**
 * 
 */
package com.sp.web.exception;

/**
 * @author pradeep
 *
 */
public class NoGrowthAssesmentQuestionsException extends SPException {

  /**
   * Exception to be thrown in case no growth question are present in the system.
   * 
   * @param message
   */
  public NoGrowthAssesmentQuestionsException(String message) {
    super(message);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
