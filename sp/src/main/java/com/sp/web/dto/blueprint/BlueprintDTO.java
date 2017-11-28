package com.sp.web.dto.blueprint;

import com.sp.web.comments.CommentsFactory;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CommentsDTO;
import com.sp.web.dto.goal.UserDSActionCategory;
import com.sp.web.model.Comments;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintApprover;
import com.sp.web.model.blueprint.BlueprintFeedback;
import com.sp.web.model.blueprint.BlueprintMissionStatement;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The blueprint DTO class once the blueprint has been published.
 */
public class BlueprintDTO extends BaseBlueprintDTO {
  
  private BlueprintMissionStatementDTO missionStatement;
  private List<UserDSActionCategory> devStrategyActionCategoryList;
  private boolean valid;
  private int newFeedbackReceivedCount;
  private List<BlueprintFeedbackDTO> feedbackReceivedList;
  private BlueprintApproverDTO approver;
  private BlueprintApproverDTO prevApprover;
  private String publishedDateFormatted;
  
  /**
   * Constructor.
   * 
   * @param blueprint
   *          - blueprint
   */
  public BlueprintDTO(Blueprint blueprint, CommentsFactory commentsFactory) {
    super(blueprint);
    // get all the comments converted to DTO
    final Map<String, List<Comments>> commentsMap = blueprint.getCommentsMap();
    final Map<String, List<CommentsDTO>> commentsDTOMap = new HashMap<String, List<CommentsDTO>>();
    if (!CollectionUtils.isEmpty(commentsMap) && commentsFactory != null) {
      final Map<String, BaseUserDTO> userCache = new HashMap<String, BaseUserDTO>();
      commentsMap.forEach((uid, commentList) -> commentsDTOMap.put(uid,
          commentsFactory.getCommentsDTO(commentList, userCache)));
    }
    
    BlueprintMissionStatement missionStatementSource = blueprint.getMissionStatement();
    if (missionStatementSource != null) {
      missionStatement = new BlueprintMissionStatementDTO(missionStatementSource, commentsDTOMap);
    }
    final List<DSActionCategory> objectivesList = blueprint.getDevStrategyActionCategoryList();
    if (!CollectionUtils.isEmpty(objectivesList)) {
      devStrategyActionCategoryList = objectivesList
          .stream()
          .filter(dsa -> dsa.getStatus() == GoalStatus.ACTIVE)
          .collect(
              Collectors.mapping(dsa -> new UserDSActionCategory(dsa, blueprint::hasCompleted,
                  commentsDTOMap), Collectors.toList()));
    }
    
    final BlueprintApprover approverSource = blueprint.getApprover();
    if (approverSource != null) {
      approver = new BlueprintApproverDTO(approverSource);
    }
    
    final BlueprintApprover prevApproverSource = blueprint.getPrevApprover();
    if (prevApproverSource != null) {
      prevApprover = new BlueprintApproverDTO(prevApproverSource);
    }
    
    if (blueprint.getPublishedOn() != null) {
      this.setPublishedDateFormatted(MessagesHelper.formatDate(blueprint.getPublishedOn()));
    }
    
  }
  
  /**
   * Constructor.
   * 
   * @param blueprint
   *          - blueprint
   */
  public BlueprintDTO(Blueprint blueprint) {
    this(blueprint, null);
  }
  
  /**
   * Constructor that validates the blueprint settings as well.
   * 
   * @param blueprint
   *          - blueprint
   * @param commentsFactory
   *          - comments factory
   * @param userFactory
   *          - user factory
   */
  public BlueprintDTO(Blueprint blueprint, CommentsFactory commentsFactory, UserFactory userFactory) {
    this(blueprint, commentsFactory);
    // add the comments received entity
    final List<BlueprintFeedback> feedbackList = blueprint.getFeedbackReceivedList();
    if (!CollectionUtils.isEmpty(feedbackList)) {
      feedbackReceivedList = feedbackList.stream()
          .map(feedback -> new BlueprintFeedbackDTO(feedback, userFactory))
          .collect(Collectors.toList());
    }
  }
  
  public List<UserDSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(
      List<UserDSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  public BlueprintMissionStatementDTO getMissionStatement() {
    return missionStatement;
  }
  
  public void setMissionStatement(BlueprintMissionStatementDTO missionStatement) {
    this.missionStatement = missionStatement;
  }
  
  public boolean isValid() {
    return valid;
  }
  
  public List<BlueprintFeedbackDTO> getFeedbackReceivedList() {
    return feedbackReceivedList;
  }
  
  public void setFeedbackReceivedList(List<BlueprintFeedbackDTO> feedbackReceivedList) {
    this.feedbackReceivedList = feedbackReceivedList;
  }
  
  public void setValid(boolean valid) {
    this.valid = valid;
  }
  
  public BlueprintApproverDTO getApprover() {
    return approver;
  }
  
  public void setApprover(BlueprintApproverDTO approver) {
    this.approver = approver;
  }
  
  /**
   * Filter the comments for the given user email id.
   * 
   * @param user
   *          - user
   */
  public BlueprintDTO filterComments(User user) {
    // filtering the comments for the current user
    final String email = user.getEmail();
    missionStatement.filterComments(email);
    // filtering from objectives and initiatives
    if (devStrategyActionCategoryList != null) {
      devStrategyActionCategoryList.forEach(d -> d.filterComments(email));
    }
    return this;
  }
  
  public BlueprintApproverDTO getPrevApprover() {
    return prevApprover;
  }
  
  public void setPrevApprover(BlueprintApproverDTO prevApprover) {
    this.prevApprover = prevApprover;
  }
  
  public int getNewFeedbackReceivedCount() {
    return newFeedbackReceivedCount;
  }
  
  public void setNewFeedbackReceivedCount(int newFeedbackReceivedCount) {
    this.newFeedbackReceivedCount = newFeedbackReceivedCount;
  }
  
  public void setPublishedDateFormatted(String publishedDateFormatted) {
    this.publishedDateFormatted = publishedDateFormatted;
  }
  
  public String getPublishedDateFormatted() {
    return publishedDateFormatted;
  }
}
