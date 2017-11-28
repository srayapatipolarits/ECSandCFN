package com.sp.web.form.feed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.CommentForm;
import com.sp.web.form.Operation;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.product.CompanyFactory;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The form to capture the dashboard messages.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardMessageForm {
  
  private CommentForm comment;
  private boolean allMembers;
  private List<String> taggedMemberIds;
  private List<String> groupIds;
  private List<String> competencyIds;
  private List<String> actionPlanIds;
  private String messageId;
  private List<String> deletedTaggedMemberIds;
  private boolean follow;
  private boolean announcement;
  private boolean doLike;
  private int cid;
  private int childCid;
  private boolean sendEmail;
  
  /**
   * Default Constructor.
   */
  public DashboardMessageForm() {
  }
  
  /**
   * Constructor from message id.
   * 
   * @param messageId
   *          - message id
   */
  public DashboardMessageForm(String messageId) {
    this.messageId = messageId;
  }
  
  /**
   * Constructor.
   * 
   * @param messageId
   *          - message id
   * @param follow
   *          - flag to indicate follow or unfollow
   */
  public DashboardMessageForm(String messageId, boolean follow) {
    this(messageId);
    this.setFollow(follow);
  }
  
  public CommentForm getComment() {
    return comment;
  }
  
  public void setComment(CommentForm comment) {
    this.comment = comment;
  }
  
  public boolean isAllMembers() {
    return allMembers;
  }
  
  public void setAllMembers(boolean allMembers) {
    this.allMembers = allMembers;
  }
  
  public List<String> getGroupIds() {
    return groupIds;
  }
  
  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }
  
  public List<String> getCompetencyIds() {
    return competencyIds;
  }
  
  public void setCompetencyIds(List<String> competencyIds) {
    this.competencyIds = competencyIds;
  }
  
  public List<String> getActionPlanIds() {
    return actionPlanIds;
  }
  
  public void setActionPlanIds(List<String> actionPlanIds) {
    this.actionPlanIds = actionPlanIds;
  }
  
  public List<String> getTaggedMemberIds() {
    return Optional.ofNullable(taggedMemberIds).orElse(new ArrayList<String>());
  }
  
  public void setTaggedMemberIds(List<String> taggedMemberIds) {
    this.taggedMemberIds = taggedMemberIds;
  }
  
  public String getMessageId() {
    return messageId;
  }
  
  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }
  
  public List<String> getDeletedTaggedMemberIds() {
    return deletedTaggedMemberIds;
  }
  
  public void setDeletedTaggedMemberIds(List<String> deletedTaggedMemberIds) {
    this.deletedTaggedMemberIds = deletedTaggedMemberIds;
  }
  
  public boolean isFollow() {
    return follow;
  }
  
  public void setFollow(boolean follow) {
    this.follow = follow;
  }
  
  public boolean isAnnouncement() {
    return announcement;
  }
  
  public void setAnnouncement(boolean announcement) {
    this.announcement = announcement;
  }
  
  /**
   * Update the given dashboard message from the data in the form.
   * 
   * @param message
   *          - message to update
   */
  public void updateMessage(DashboardMessage message) {
    comment.updateComment(message.getMessage());
    message.setUpdatedOn(LocalDateTime.now());
  }
  
  /**
   * Validate the update request.
   */
  public void validateUpdate() {
    Assert.hasText(messageId, "Message id is required.");
    // validate the comment
    Assert.notNull(comment, "Comment is required.");
    comment.validate();
  }
  
  /**
   * Validate the add comment request.
   */
  public void validateAddComment() {
    Assert.hasText(messageId, "Message id is required.");
    // validate the comment
    Assert.notNull(comment, "Comment is required.");
    comment.validate();
  }
  
  /**
   * Validate the request.
   */
  public void validate() {
    // validate the comment
    Assert.notNull(comment, "Comment is required.");
    comment.validate();
    
    if (announcement) {
      Assert.isTrue(allMembers, "Announcement cannot be made to specific users only.");
    }
    
    if (!allMembers) {
      Assert
          .isTrue(
              !(CollectionUtils.isEmpty(groupIds) && CollectionUtils.isEmpty(competencyIds) && CollectionUtils
                  .isEmpty(actionPlanIds)), "Member list not found to share message.");
    }
  }
  
  /**
   * Validate the parameters depending on the operation.
   * 
   * @param op
   *          - operation
   */
  public void validate(Operation op) {
    
    switch (op) {
    
    case DELETE:
      Assert.hasText(messageId, "Message id is required.");
      break;
    
    case LIKE_COMMENT:
      Assert.hasText(messageId, "Message id is required.");
      break;
    
    case ADD:
      validate();
      break;
    
    case UPDATE:
      validateUpdate();
      break;
    
    case ADD_COMMENT:
      validateAddComment();
      break;
    
    default:
      break;
    }
  }
  
  /**
   * Get the dashboard message from the form.
   * 
   * @param user
   *          - user
   * @param companyFactory
   *          - company factory
   * @return
   *    the new dashboard message
   */
  public DashboardMessage getMessage(User user, CompanyFactory companyFactory) {
    DashboardMessage newMessage = DashboardMessage.newMessage(comment.newComment(user),
        user.getCompanyId(), allMembers);
    if (announcement) {
      newMessage.setType(DashboardMessageType.Announcement);
      // updating the on behalf user
      // for the message
      newMessage.getMessage().setOnBehalfUser(
          new UserMarkerDTO(companyFactory.getCompany(user.getCompanyId())));
    } 
    
    if (comment.getMiniPoll() != null) {
      newMessage.setType(DashboardMessageType.MiniPolls);
    }
    return newMessage;
  }

  public boolean isDoLike() {
    return doLike;
  }

  public void setDoLike(boolean doLike) {
    this.doLike = doLike;
  }

  public int getCid() {
    return cid;
  }

  public void setCid(int cid) {
    this.cid = cid;
  }

  public int getChildCid() {
    return childCid;
  }

  public void setChildCid(int childCid) {
    this.childCid = childCid;
  }

  public boolean isSendEmail() {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

}
