package com.sp.web.form.feed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.CommentForm;
import com.sp.web.form.UserMarkerForm;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.Comment;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.feed.SPMessagePost;

import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The form class for SPMessage Post.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPMessagePostForm implements GenericForm<SPMessagePost> {
  
  private String id;
  private CommentForm message;
  private UserMarkerForm onBehalfUser;
  private List<String> companyIds;
  private List<RoleType> userRoles;
  private boolean allCompany;
  
  @Override
  public SPMessagePost create(User user) {
    SPMessagePost messagePost = SPMessagePost.newInstance();
    update(user, messagePost);
    return messagePost;
  }
  
  @Override
  public void update(User user, SPMessagePost messagePost) {
    Comment newComment = message.newComment(user);
    newComment.setOnBehalfUser(onBehalfUser.getUserMarkerDTO());
    messagePost.setMessage(newComment);
    messagePost.setUpdatedOn(LocalDateTime.now());
    messagePost.setUpdatedBy(new UserMarkerDTO(user));
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public CommentForm getMessage() {
    return message;
  }
  
  public void setMessage(CommentForm message) {
    this.message = message;
  }
  
  public List<String> getCompanyIds() {
    return companyIds;
  }
  
  public void setCompanyIds(List<String> companyIds) {
    this.companyIds = companyIds;
  }
  
  public List<RoleType> getUserRoles() {
    return userRoles;
  }
  
  public void setUserRoles(List<RoleType> userRoles) {
    this.userRoles = userRoles;
  }
  
  public boolean isAllCompany() {
    return allCompany;
  }
  
  public void setAllCompany(boolean allCompany) {
    this.allCompany = allCompany;
  }

  public UserMarkerForm getOnBehalfUser() {
    return onBehalfUser;
  }

  public void setOnBehalfUser(UserMarkerForm onBehalfUser) {
    this.onBehalfUser = onBehalfUser;
  }
  
  @Override
  public void validate() {
    Assert.notNull(onBehalfUser, "User is required.");
    onBehalfUser.validate();
    Assert.notNull(message, "Message is required.");
    message.validate();
  }
  
  @Override
  public void validateUpdate() {
    Assert.hasText(id, "Id is required.");
    validate();
  }
  
  /**
   * Validating the publish request.
   */
  public void validatePublish() {
    Assert.hasText(id, "Id is required.");
    Assert.notEmpty(companyIds, "Company ids required.");
    if (!allCompany) {
      Assert.notEmpty(userRoles, "User roles required.");
    }
  }

  @Override
  public void validateGet() {
    Assert.hasText(id, "Id is required.");
  }
  
  /**
   * Updating the message post with the data for publish.
   * @param user 
   * 
   * @param messagePost
   *            - message post
   */
  public void updatePublish(User user, SPMessagePost messagePost) {
    if (allCompany) {
      messagePost.setAllMember(true);
    } else {
      messagePost.setUserRoles(userRoles);
    }
    messagePost.setCompanyIds(companyIds);
    messagePost.setPublishedOn(LocalDateTime.now());
    messagePost.setPublishedBy(new UserMarkerDTO(user));
  }
}
