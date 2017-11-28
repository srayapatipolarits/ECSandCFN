package com.sp.web.dto;

import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.SPNote;

/**
 * @author vikram
 * 
 *         The DTO for the practice feedback Request.
 */
public class PracticeFeedbackRequestDTO extends SPNoteDTO {
  
  private static final long serialVersionUID = -6396973178830007496L;
  
  private RequestStatus feedbackStatus;
  
  private String comment;
  
  private String feedbackUserId;
  
  private String feedbackUserEmail;
  
  private String feedbackResponse;
  
  private BaseUserDTO feedbackUser;
  
  private int totalCountOfFeedback;
  
  private BaseUserDTO user;
  
  private String feedbackRequestIntroduction;
  
  private SPNoteFeedbackPracticeAreaDTO practiceAreaDTO;
  
  private DevelopmentStrategy devStrategy;
  

  /**
   * Default constructor.
   * 
   * @param spNote
   *          - the sp note
   */
  public PracticeFeedbackRequestDTO(SPNote spNote) {
    super(spNote);
  }
  
  /**
   * Constructor.
   * Used in getRequestFeedbackReceived.
   * 
   * @param spNote
   *          - sp note
   * @param user
   *          - feedback user
   */
  public PracticeFeedbackRequestDTO(SPNote spNote, User user) {
    super(spNote);
    if (user != null) {
      setFeedbackUser(new BaseUserDTO(user));
    }
        
  }
  
  /**
   * Constructor for the dashboard listing for notes and feedback.
   * 
   * @param spNoteFeedback
   *          - the note or feedback
   * @param feedbackUser
   *          - the feedback user
   * @param goal
   *          - the goal
   */
  public PracticeFeedbackRequestDTO(SPNote spNoteFeedback, User feedbackUser, SPGoal goal) {
    this(spNoteFeedback, feedbackUser);
    
    // setting the goal
    if (goal != null) {
      practiceAreaDTO = new SPNoteFeedbackPracticeAreaDTO(goal);
    }
  }

  /**
   * Constructor for practice feedback requests.
   * 
   * @param spNote
   *          - the note
   * @param user
   *          - the user
   * @param spGoal
   *          - the practice area
   * @param devStrategy
   *          - the development strategy
   */
  public PracticeFeedbackRequestDTO(SPNote spNote, User user, SPGoal spGoal,
      DevelopmentStrategy devStrategy) {
    this(spNote, user, spGoal);
    this.devStrategy = devStrategy;
  }
    
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getFeedbackUserEmail() {
    return feedbackUserEmail;
  }
  
  public void setFeedbackUserEmail(String feedbackUserEmail) {
    this.feedbackUserEmail = feedbackUserEmail;
  }
  
  public String getFeedbackResponse() {
    return feedbackResponse;
  }
  
  public void setFeedbackResponse(String feedbackResponse) {
    this.feedbackResponse = feedbackResponse;
  }
  
  public int getTotalCountOfFeedback() {
    return totalCountOfFeedback;
  }
  
  public void setTotalCountOfFeedback(int totalCountOfFeedback) {
    this.totalCountOfFeedback = totalCountOfFeedback;
  }
  
  public BaseUserDTO getFeedbackUser() {
    return feedbackUser;
  }
  
  public void setFeedbackUser(BaseUserDTO baseUserDTO) {
    this.feedbackUser = baseUserDTO;
  }

  public String getFeedbackUserId() {
    return feedbackUserId;
  }

  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }

  public RequestStatus getFeedbackStatus() {
    return feedbackStatus;
  }

  public void setFeedbackStatus(RequestStatus feedbackStatus) {
    this.feedbackStatus = feedbackStatus;
  }

  public BaseUserDTO getUser() {
    return user;
  }

  public void setUser(BaseUserDTO user) {
    this.user = user;
  }

  public String getFeedbackRequestIntroduction() {
    return feedbackRequestIntroduction;
  }

  public void setFeedbackRequestIntroduction(String feedbackRequestIntroduction) {
    this.feedbackRequestIntroduction = feedbackRequestIntroduction;
  }

  public DevelopmentStrategy getDevStrategy() {
    return devStrategy;
  }

  public void setDevStrategy(DevelopmentStrategy devStrategy) {
    this.devStrategy = devStrategy;
  }

  public SPNoteFeedbackPracticeAreaDTO getPracticeAreaDTO() {
    return practiceAreaDTO;
  }

  public void setPracticeAreaDTO(SPNoteFeedbackPracticeAreaDTO practiceAreaDTO) {
    this.practiceAreaDTO = practiceAreaDTO;
  }  
}
