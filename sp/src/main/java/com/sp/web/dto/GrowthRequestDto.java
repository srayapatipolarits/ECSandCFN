/**
 * 
 */
package com.sp.web.dto;

import com.sp.web.model.GrowthFeedback;
import com.sp.web.model.GrowthFeedbackResponse;
import com.sp.web.model.GrowthRequest;
import com.sp.web.model.User;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pradeep
 */
public class GrowthRequestDto extends GrowthRequestSummaryDTO {

  private boolean selectedMember;

  private LocalDate startDate;

  private LocalDate endDate;

  private List<GoalDto> goal;

  private List<GrowthFeedbackDto> feedbackIntervals;

  private String[] requestProgressStatus;

  private String invitationNotAcceptedMessage;

  private int feedbacksCompletedCount;

  /**
   * Constructor
   */
  public GrowthRequestDto(GrowthRequest growthRequest, User user) {
    super(growthRequest, user);
    //BeanUtils.copyProperties(growthRequest, this);
    setId(growthRequest.getId());
    BaseUserDTO baseUserDTO = new BaseUserDTO();
    baseUserDTO.load(user);
    setUser(baseUserDTO);
    this.setRequestStatus(MessagesHelper.getMessage("growthStatus." + growthRequest.getRequestStatus().toString()));
    this.setRequestType(growthRequest.getRequestType());
    this.startDate = growthRequest.getStartDate();
    this.endDate = growthRequest.getEndDate();
    this.formattedStartDate = this.getStartDate().format(DateTimeFormatter.ISO_DATE);
    this.formattedEndDate = this.getEndDate().format(DateTimeFormatter.ISO_DATE);
    populateGrowthFeedbackDto(growthRequest.getFeedbackIntervals(), growthRequest.getFeedbacks());
    this.feedbacksCompletedCount = growthRequest.getFeedbacks() != null ? growthRequest.getFeedbacks().size() : 0;

  }

  /**
   * Constructor for getting the goals.
   */
  public GrowthRequestDto(GrowthRequest growthRequest, User user, SPGoalFactoryHelper spGoalFactory) {
    this(growthRequest, user);
    this.goal = spGoalFactory.getGoal(growthRequest.getGoals()).stream().map(GoalDto::new).collect(Collectors.toList());

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

  /**
   * @return the requestProgressStatus
   */
  public String[] getRequestProgressStatus() {
    return requestProgressStatus;
  }

  /**
   * @param requestProgressStatus
   *          the requestProgressStatus to set
   */
  public void setRequestProgressStatus(String[] requestProgressStatus) {
    this.requestProgressStatus = requestProgressStatus;
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
   * @param invitationNotAcceptedMessage
   *          the invitationNotAcceptedMessage to set
   */
  public void setInvitationNotAcceptedMessage(String invitationNotAcceptedMessage) {
    this.invitationNotAcceptedMessage = invitationNotAcceptedMessage;
  }

  /**
   * @return the invitationNotAcceptedMessage
   */
  public String getInvitationNotAcceptedMessage() {
    return invitationNotAcceptedMessage;
  }

  /**
   * @return the feedbackIntervals
   */
  public List<GrowthFeedbackDto> getFeedbackIntervals() {
    return feedbackIntervals;
  }

  /**
   * @param feedbackIntervals
   *          the feedbackIntervals to set
   */
  public void setFeedbackIntervals(List<GrowthFeedbackDto> feedbackIntervals) {
    this.feedbackIntervals = feedbackIntervals;
  }

  private void populateGrowthFeedbackDto(List<LocalDate> feedbackIntervals, List<GrowthFeedback> feedbacks) {
    if (feedbackIntervals != null) {
      List<GrowthFeedbackDto> collect = feedbackIntervals.stream().map(ld -> {
        GrowthFeedbackDto feedbackDto = new GrowthFeedbackDto();
        feedbackDto.setCreatedOn(ld.format(DateTimeFormatter.ISO_LOCAL_DATE));
        feedbackDto.setCreatedOnLocalDate(ld);
        /* check if feedback is given for this date or not */

        if (feedbacks != null) {
          feedbacks.stream().forEach(gf -> {
            if (gf.getCreatedOn().isEqual(ld)) {
              List<GrowthFeedbackResponse> gfResponseList = gf.getResponseFeedbacks();
              feedbackDto.setResponseFeedbacks(gfResponseList);
            }
          });
        }
        return feedbackDto;
      }).collect(Collectors.toList());

      this.feedbackIntervals = collect;
    }

  }

  public void setFeedbacksCompletedCount(int feedbacksCompletedCount) {
    this.feedbacksCompletedCount = feedbacksCompletedCount;
  }

  public int getFeedbacksCompletedCount() {
    return feedbacksCompletedCount;
  }
  
  public void setGoal(List<GoalDto> goal) {
    this.goal = goal;
  }
  
  public List<GoalDto> getGoal() {
    return goal;
  }
}
