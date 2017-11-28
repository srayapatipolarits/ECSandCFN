package com.sp.web.form.discussion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.form.CommentForm;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.discussion.GroupDiscussion;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The form class for group discussion form.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupDiscussionForm {
  
  private String name;
  private String gdId;
  private List<String> memberIds;
  private CommentForm comment;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getGdId() {
    return gdId;
  }
  
  public void setGdId(String gdId) {
    this.gdId = gdId;
  }
  
  public List<String> getMemberIds() {
    return memberIds;
  }
  
  public void setMemberIds(List<String> memberIds) {
    this.memberIds = memberIds;
  }
  
  public CommentForm getComment() {
    return comment;
  }
  
  public void setComment(CommentForm comment) {
    this.comment = comment;
  }

  /**
   * Validate the form data.
   */
  public void validateCreate() {
    Assert.notEmpty(memberIds, "At least one member required.");
    Assert.notNull(comment, "Comment not found.");
    comment.validate();
  }

  /**
   * Validate update request data.
   */
  public void validateUpdate() {
    Assert.notNull(gdId, "Group discussion id required.");
    Assert.notNull(comment, "Comment not found.");
    comment.validate();
  }
  
  /**
   * Create a new group discussion object from the given data.
   * 
   * @param user
   *          - user
   * @return
   *    the new group discussion
   */
  public GroupDiscussion newGroupDiscussion(User user) {
    GroupDiscussion groupDiscussion = new GroupDiscussion();
    if (!StringUtils.isEmpty(name)) {
      groupDiscussion.setName(name);
      groupDiscussion.setNameOverriden(true);
    }
    addComment(user, groupDiscussion);
    groupDiscussion.addMember(user.getId());
    return groupDiscussion;
  }

  /**
   * Add the comment from the comment from to the group discussion.
   * 
   * @param user
   *          - user
   * @param groupDiscussion
   *          - group discussion
   * @return 
   *      the newly created comment
   */
  public Comment addComment(User user, GroupDiscussion groupDiscussion) {
    return groupDiscussion.addComment(comment.newComment(), user);
  }
  
}
