package com.sp.web.model.todo;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.task.TaskType;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Dax Abraham
 *
 *         The entity to store the todo task request.
 */
public class TodoRequest {

  private String refId;
  private String parentRefId;
  private TodoType type;
  private LocalDateTime createdOn;
  private LocalDateTime dueBy;
  private String text;
  private String url;
  private boolean completed;
  private UserMarkerDTO user;

  /**
   * Default Constructor.
   */
  public TodoRequest() {
  }

  /**
   * Constructor.
   * 
   * @param text
   *          - text
   * @param dueBy
   *          - due by
   */
  public TodoRequest(String text, LocalDateTime dueBy) {
    this(text, dueBy, TodoType.Todo);
  }

  /**
   * Constructor.
   * 
   * @param text
   *          - text
   * @param dueBy
   *          - due by
   * @param type
   *          - todo type
   */
  public TodoRequest(String text, LocalDateTime dueBy, TodoType type) {
    createdOn = LocalDateTime.now();
    this.text = text;
    this.dueBy = dueBy;
    this.type = type;
    this.refId = GenericUtils.getId();
  }

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }

  public String getParentRefId() {
    return parentRefId;
  }

  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }

  public TodoType getType() {
    return type;
  }

  public void setType(TodoType type) {
    this.type = type;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LocalDateTime getDueBy() {
    return dueBy;
  }

  public void setDueBy(LocalDateTime dueBy) {
    this.dueBy = dueBy;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Check if the due by is same.
   * 
   * @param dueByToCheck
   *          - due by
   * @return true if same else false.
   */
  public boolean isSameDueBy(LocalDateTime dueByToCheck) {
    if (dueByToCheck == null && dueBy == null) {
      return true;
    }

    if (dueByToCheck != null) {
      if (dueBy != null) {
        return dueByToCheck.isEqual(dueBy);
      }
    }
    return false;
  }

  /**
   * Validates the request.
   */
  public void validate() {
    Assert.hasText(refId, "Reference id required.");
    Assert.notNull(type, "Todo type required.");
  }

  /**
   * Validate the parent request.
   */
  public void validateParentRequest() {
    Assert.hasText(parentRefId, "Parent id required.");
    validate();
  }

  /**
   * Creates a new todo request.
   * 
   * @param type
   *          - todo type
   * @param refId
   *          -reference id
   * @param dueBy
   *          - due by
   * @return the todo request
   */
  public static TodoRequest newInstanceFromRefId(TodoType type, String refId,
      LocalDateTime dueBy) {
    return newInstanceFromParentRefId(type, null, refId, dueBy);
  }

  /**
   * Create a new todo request.
   * 
   * @param type
   *          - todo type
   * @param parentRefId
   *          - parent reference id
   * @param refId
   *          - reference id
   * @param dueBy
   *          - due by
   * @return todo request
   */
  public static TodoRequest newInstanceFromParentRefId(TodoType type,
      String parentRefId, String refId, LocalDateTime dueBy) {
    TodoRequest request = new TodoRequest();
    request.setCreatedOn(LocalDateTime.now());
    request.setDueBy(dueBy);
    request.setRefId(refId);
    request.setParentRefId(parentRefId);
    request.setType(type);
    return request;
  }

  /**
   * New todo request from the given feedback request.
   * 
   * @param user
   *          - user requesting for feedback
   * @param feedbackRequest
   *          - feedback request
   * @param feedbackUser 
   *          - feedback user
   * @return - the new todo request
   */
  public static TodoRequest newPrismLensRequest(User user,
      FeedbackRequest feedbackRequest, FeedbackUser feedbackUser) {
    TodoRequest request = newInstanceFromRefId(TodoType.PrismLens,
        feedbackRequest.getFeedbackUserId(),
        LocalDateTime.of(feedbackRequest.getEndDate(), LocalTime.MIDNIGHT));
    request.setUrl(feedbackRequest.getTokenUrl());
    request.setText(MessagesHelper.getMessage("prismLens.todo.request",
        user.getFirstName(), user.getLastName()));
    request.setUser(new UserMarkerDTO(user));
    return request;
  }

  /**
   * New todo request for workspace pulse.
   * 
   * @param pulseQuestionSetName
   *          - pulse question set name
   * @param refId
   *          - reference id
   * @param dueBy
   *          - due by
   * @return the new todo request
   */
  public static TodoRequest newPulseRequest(String pulseQuestionSetName,
      String refId, LocalDate dueBy) {
    TodoRequest request = newInstanceFromParentRefId(TodoType.Pulse, null,
        refId, LocalDateTime.of(dueBy, LocalTime.MIDNIGHT));
    request.setText(pulseQuestionSetName);
    return request;
  }

  /**
   * Get the Todo request for blueprint approval.
   * 
   * @param user
   *          - user
   * @param feedbackUser
   *          - feedback user
   * @return the new todo request
   */
  public static TodoRequest newBlueprintApprovalRequest(User user,
      FeedbackUser feedbackUser) {
    TodoRequest request = newInstanceFromRefId(TodoType.BlueprintApproval,
        feedbackUser.getId(), null);
    request.setUrl(feedbackUser.getTokenUrl());
    final UserMarkerDTO userMarkerDTO = new UserMarkerDTO(user);
    request.setText(userMarkerDTO.getName());
    request.setUser(userMarkerDTO);
    return request;
  }

  /**
   * Get the Todo request for blueprint feedback.
   * 
   * @param user
   *          - user
   * @param feedbackUser
   *          - feedback user
   * @return the new todo request
   */
  public static TodoRequest newBlueprintFeedbackRequest(User user,
      FeedbackUser feedbackUser) {
    TodoRequest request = newInstanceFromRefId(TodoType.BlueprintFeedback,
        feedbackUser.getId(), null);
    request.setUrl(feedbackUser.getTokenUrl());
    final UserMarkerDTO userMarkerDTO = new UserMarkerDTO(user);
    request.setText(userMarkerDTO.getName());
    request.setUser(userMarkerDTO);
    return request;
  }

  /**
   * Get the todo request for competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @param taskType
   *          - task type
   * @return the new todo request
   */
  public static CompetencyTodoRequest newCompetencyEvaluationRequest(
      CompetencyEvaluation competencyEvaluation, TaskType taskType) {
    String id = competencyEvaluation.getId();
    CompetencyTodoRequest request = new CompetencyTodoRequest();
    request.setCreatedOn(LocalDateTime.now());
    request.setType(TodoType.CompetencyEvaluation);
    request.setParentRefId(id);
    request.setRefId(id);
    request.setDueBy(competencyEvaluation.getEndDate());
    request.setTaskType(taskType);
    return request;
  }

  /**
   * Create a new todo request for the competency evaluation requests.
   * 
   * @param reviewer
   *          - reviewer
   * @param competencyEvaluation
   *          - competency evaluation
   * @param user
   *          - user for
   * @return the new todo request
   */
  public static TodoRequest newCompetencyEvaluationRequest(
      FeedbackUser reviewer, CompetencyEvaluation competencyEvaluation,
      User user) {
    CompetencyTodoRequest request = new CompetencyTodoRequest();
    request.setCreatedOn(LocalDateTime.now());
    request.setType(TodoType.CompetencyEvaluation);
    request.setParentRefId(competencyEvaluation.getId());
    request.setRefId(user.getId());
    request.setDueBy(competencyEvaluation.getEndDate());
    request.setTaskType(TaskType.CompetencyEvaluation);
    final UserMarkerDTO userMarkerDTO = new UserMarkerDTO(user);
    request.setText(userMarkerDTO.getName());
    request.setUser(userMarkerDTO);
    request.setUrl(reviewer.getTokenUrl());
    return request;
  }

  /**
   * Get a new todo request of the given type from the feedback user.
   * 
   * @param user
   *          - user
   * @param type
   *          - type
   * @param feedbackUser
   *          - feedback user
   * @param parentRefId
   *          - parent reference id
   * @param dueBy
   *          - due by
   * @return the todo request
   */
  public static TodoRequest newFeedbackUserRequest(User user, TodoType type,
      FeedbackUser feedbackUser, String parentRefId, LocalDateTime dueBy) {
    TodoRequest request = newInstanceFromRefId(type, feedbackUser.getId(), null);
    request.setUrl(feedbackUser.getTokenUrl());
    request.setText(new UserMarkerDTO(user).getName());
    request.setParentRefId(parentRefId);
    request.setDueBy(dueBy);
    return request;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * Get a new todo request of the given type from the feedback user.
   * 
   * @param feedbackUser
   *          - feedbackUser is the feedback user.
   * @param type
   *          - type
   * @param id
   *          - ref id
   * 
   * @return the todo request
   */
  public static TodoRequest newDevFeedbackUserRequest(User feedbackUser,
      TodoType type, String id) {
    TodoRequest request = new TodoRequest();
    request.setUser(new UserMarkerDTO(feedbackUser));
    request.setCreatedOn(LocalDateTime.now());
    request.setRefId(id);
    request.setType(type);
    return request;
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
}
