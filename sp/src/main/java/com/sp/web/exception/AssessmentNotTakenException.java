package com.sp.web.exception;

/**
 * Assessment Excpetio be shown when user has not taken the assessment.
 * 
 * @author pradeep
 *
 */
public class AssessmentNotTakenException extends SPException {

  /**
   * Mesasge for the assessment not taken.
   * 
   * @param message
   *          to be shown
   */
  public AssessmentNotTakenException(String message) {
    super(message);
  }

  /**
   * Default serial version id
   */
  private static final long serialVersionUID = 1L;

}
