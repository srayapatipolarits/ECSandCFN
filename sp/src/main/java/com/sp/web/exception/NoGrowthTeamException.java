package com.sp.web.exception;

/**
 * No Growth Team Exception.
 * 
 * @author pradeep
 *
 */
public class NoGrowthTeamException extends SPException {

  /**
   * Mesasge for the assessment not taken.
   * 
   * @param message
   *          to be shown
   */
  public NoGrowthTeamException(String message) {
    super(message);
  }

  /**
   * Default serial version id
   */
  private static final long serialVersionUID = 1L;

}
