/**
 * 
 */
package com.sp.web.dto;

import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.RequestType;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author pradeep
 *
 */
public class FeedbackMemberDto extends BaseUserDTO {

  private LocalDate startDate;

  private LocalDate endDate;

  private String requestStatus;

  private RequestType requestType;

  private List<GoalDto> goal;

  private boolean selectedMember;

  /**
   * Default constructor.
   */
  public FeedbackMemberDto() {
    super();
  }

  public FeedbackMemberDto(User user) {
    super(user);
  }

  /**
   * Constructor.
   */
  public FeedbackMemberDto(FeedbackRequest feedbackRequest) {
    BeanUtils.copyProperties(feedbackRequest, this);
    setId(feedbackRequest.getFeedbackUserId());
    this.setRequestStatus(MessagesHelper.getMessage("feedbackStatus." + feedbackRequest.getRequestStatus()));
    this.startDate = feedbackRequest.getStartDate();
    this.endDate = feedbackRequest.getEndDate();
    this.formattedStartDate = this.getStartDate().format(DateTimeFormatter.ISO_DATE);
    this.formattedEndDate = this.getEndDate().format(DateTimeFormatter.ISO_DATE);
  }

  /**
   * Constructor.
   */
  public FeedbackMemberDto(FeedbackArchiveRequest feedbackRequestArchived) {
    BeanUtils.copyProperties(feedbackRequestArchived, this);
    this.setRequestStatus(MessagesHelper.getMessage("feedbackStatus." + feedbackRequestArchived.getRequestStatus()));
    this.startDate = feedbackRequestArchived.getStartDate();
    this.endDate = feedbackRequestArchived.getEndDate();
    this.formattedStartDate = this.getStartDate().format(DateTimeFormatter.ISO_DATE);
    this.formattedEndDate = this.getEndDate().format(DateTimeFormatter.ISO_DATE);
  }

  /**
   * @return the startDate
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * @return the requestStatus
   */
  public String getRequestStatus() {
    return requestStatus;
  }

  /**
   * @param requestStatus
   *          the requestStatus to set
   */
  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  /**
   * @param requestType
   *          the requestType to set
   */
  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  /**
   * @return the requestType
   */
  public RequestType getRequestType() {
    return requestType;
  }

  /**
   * @param selectedMember
   *          the selectedMember to set
   */
  public void setSelectedMember(boolean selectedMember) {
    this.selectedMember = selectedMember;
  }

  /**
   * @return the selectedMember
   */
  public boolean isSelectedMember() {
    return selectedMember;
  }

  private String formattedStartDate;

  private String formattedEndDate;

  /**
   * @param formattedEndDate
   *          the formattedEndDate to set
   */
  public void setFormattedEndDate(String formattedEndDate) {
    this.formattedEndDate = formattedEndDate;
    ;
  }

  /**
   * @return the formattedEndDate
   */
  public String getFormattedEndDate() {
    return formattedEndDate;

  }

  /**
   * @return the formattedStartDate
   */
  public String getFormattedStartDate() {
    return formattedStartDate;
  }

  /**
   * @param formattedStartDate
   *          the formattedStartDate to set
   */
  public void setFormattedStartDate(String formattedStartDate) {
    this.formattedStartDate = this.getStartDate().format(DateTimeFormatter.ISO_DATE);
  }

  /**
   * @param goal
   *          the goal to set
   */
  public void setGoal(List<GoalDto> goal) {
    this.goal = goal;
  }

  /**
   * @return the goal
   */
  public List<GoalDto> getGoal() {
    return goal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FeedbackMemberDto [id=" + getId() + ", firstName=" + getFirstName() + ", selectedMember=" + selectedMember
        + ", lastName=" + getLastName() + ", email=" + getEmail() + ", title=" + getTitle() + ", startDate="
        + startDate + ", endDate=" + endDate + ", requestStatus=" + requestStatus + ", requestType=" + requestType
        + ", goal=" + goal + ", smallProfileImage=" + getSmallProfileImage() + ", formattedStartDate="
        + formattedStartDate + ", formattedEndDate=" + formattedEndDate + ", getStartDate()=" + getStartDate()
        + ", getEndDate()=" + getEndDate() + ", getRequestStatus()=" + getRequestStatus() + ", getFirstName()="
        + getFirstName() + ", getLastName()=" + getLastName() + ", getEmail()=" + getEmail() + ", getTitle()="
        + getTitle() + ", getRequestType()=" + getRequestType() + ", isSelectedMember()=" + isSelectedMember()
        + ", getId()=" + getId() + ", getFormattedEndDate()=" + getFormattedEndDate() + ", getFormattedStartDate()="
        + getFormattedStartDate() + ", getGoal()=" + getGoal() + ", getLargeProfileImage()=" + ", getClass()="
        + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
  }

}
