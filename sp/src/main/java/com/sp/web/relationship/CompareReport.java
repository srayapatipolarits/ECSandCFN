package com.sp.web.relationship;

/**
 * @author Dax Abraham
 * 
 *         This is the bean to hold the contents of the comparison report.
 */
public class CompareReport {

  private String title;
  private String effort;
  private String avoid;
  private String personalityPrimary;
  private String personalityUnderPressure;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getEffort() {
    return effort;
  }

  public void setEffort(String effort) {
    this.effort = effort;
  }

  public String getAvoid() {
    return avoid;
  }

  public void setAvoid(String avoid) {
    this.avoid = avoid;
  }

  public String getPersonalityPrimary() {
    return personalityPrimary;
  }

  public void setPersonalityPrimary(String personalityPrimary) {
    this.personalityPrimary = personalityPrimary;
  }

  public String getPersonalityUnderPressure() {
    return personalityUnderPressure;
  }

  public void setPersonalityUnderPressure(String personalityUnderPressure) {
    this.personalityUnderPressure = personalityUnderPressure;
  }
}
