package com.sp.web.form;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * GrowthInviteForm class is the form containing the growth member details.
 * 
 * @author pradeep
 */
public class GrowthInviteForm extends BaseUserForm {

  /**
   * GrowthInviteForm Id.
   */
  private String id;

  /**
   * DateRepeaetPatten, wether montly or weekly.
   */
  private String dateRepeatPattern;

  /**
   * Date frequency specifying, whether first, second, thrid day of week in a
   * month.
   */
  private String onMonthDay;

  private String dayOfWeek;

  @NotEmpty
  private String memberEmail;

  @NotEmpty
  private String requestedByEmail;

  private String requestType;

  @NotEmpty
  private List<String> goal;

  private String startDate;

  private String endDate;

  private String comment;

  private String howDoYouKnow;

  private String accepted;

  private String linkedinUrl;

  /**
   * @param memeberEmail
   *          the memeberEmail to set
   */
  public void setMemberEmail(String memberEmail) {
    this.memberEmail = (memberEmail != null) ? memberEmail.toLowerCase() : null;
  }

  /**
   * @param requestedByEmail
   *          the requestedByEmail to set
   */
  public void setRequestedByEmail(String requestedByEmail) {
    this.requestedByEmail = (requestedByEmail != null) ? requestedByEmail.toLowerCase() : null;
  }

  /**
   * @return the requestedByEmail
   */
  public String getRequestedByEmail() {
    return requestedByEmail;
  }

  /**
   * @param requestType
   *          the requestType to set
   */
  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  /**
   * @return the requestType
   */
  public String getRequestType() {
    return requestType;
  }

  /**
   * @return the memeberEmail
   */
  public String getMemberEmail() {
    return memberEmail;
  }

  /**
   * @param goal
   *          the goal to set
   */
  public void setGoal(List<String> goal) {
    this.goal = goal;
  }

  /**
   * @return the goal
   */
  public List<String> getGoal() {
    return goal;
  }

  /**
   * @return the startDate
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  /**
   * @return the comment
   */
  public String getComment() {
    if (comment != null) {
      comment = StringUtils.replacePattern(comment, "\\r\\n", "<br/>");
      comment = StringUtils.replacePattern(comment, "\\n", "<br/>");
    }
    return comment;
  }

  /**
   * @param comment
   *          the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * @param howDoYouKnow
   *          the howDoYouKnow to set
   */
  public void setHowDoYouKnow(String howDoYouKnow) {
    this.howDoYouKnow = howDoYouKnow;
  }

  /**
   * @return the howDoYouKnow
   */
  public String getHowDoYouKnow() {
    return howDoYouKnow;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param accepted
   *          the accepted to set
   */
  public void setAccepted(String accepted) {
    this.accepted = accepted;
  }

  /**
   * @return the accepted
   */
  public String getAccepted() {
    return accepted;
  }

  /**
   * @return the dateRepeatPattern
   */
  public String getDateRepeatPattern() {
    return dateRepeatPattern;
  }

  /**
   * @param dateRepeatPattern
   *          the dateRepeatPattern to set
   */
  public void setDateRepeatPattern(String dateRepeatPattern) {
    this.dateRepeatPattern = dateRepeatPattern;
  }

  /**
   * @return the onMonthDay
   */
  public String getOnMonthDay() {
    return onMonthDay;
  }

  /**
   * @param onMonthDay
   *          the onMonthDay to set
   */
  public void setOnMonthDay(String onMonthDay) {
    this.onMonthDay = onMonthDay;
  }

  /**
   * @return the dayOfWeek
   */
  public String getDayOfWeek() {
    return dayOfWeek;
  }

  /**
   * @param dayOfWeek
   *          the dayOfWeek to set
   */
  public void setDayOfWeek(String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  /**
   * @param linkedinUrl
   *          the linkedinUrl to set
   */
  public void setLinkedinUrl(String linkedinUrl) {
    this.linkedinUrl = linkedinUrl;
  }

  /**
   * @return the linkedinUrl
   */
  public String getLinkedinUrl() {
    return linkedinUrl;
  }

}
