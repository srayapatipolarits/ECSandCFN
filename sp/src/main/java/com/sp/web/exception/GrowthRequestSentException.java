package com.sp.web.exception;

/**
 * GrowthRequestSentException.
 * 
 * @author pradeep
 *
 */
public class GrowthRequestSentException extends SPException {

  /**
   * Constructor for NoGrowthTeam Exception.
   * 
   * @param message
   *          of the growth team.
   */
  public GrowthRequestSentException(String message) {
    super(message);
  }

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 1894795112109176417L;
}
