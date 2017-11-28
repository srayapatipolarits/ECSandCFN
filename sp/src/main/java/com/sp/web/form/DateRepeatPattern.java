/**
 * 
 */
package com.sp.web.form;

/**
 * @author pradeep
 *
 */
public enum DateRepeatPattern {

  Monthly, Weekly,

  /* Default repeat pattern, for giving growth feedback on last day. */
  Default;

  /**
   * Method return the date repeat pattern.
   * 
   * @param dateRepeatPattern
   * @return
   */
  public static DateRepeatPattern getValue(String dateRepeatPattern) {
    if (dateRepeatPattern.equalsIgnoreCase(Monthly.toString())) {
      return DateRepeatPattern.Monthly;
    } else if (dateRepeatPattern.equalsIgnoreCase(Weekly.toString())) {
      return DateRepeatPattern.Weekly;
    } else {
      return DateRepeatPattern.Default;
    }

  }

}
