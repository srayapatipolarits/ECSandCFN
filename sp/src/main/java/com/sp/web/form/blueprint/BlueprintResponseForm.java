package com.sp.web.form.blueprint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.model.Comments;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.blueprint.Blueprint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The form for blueprint response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintResponseForm {
  private String id;
  private String feedbackUserId;
  private String comment;
  private Map<String, String> commentsMap;
  private boolean approved;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public Map<String, String> getCommentsMap() {
    return commentsMap;
  }
  
  public void setCommentsMap(Map<String, String> commentsMap) {
    this.commentsMap = commentsMap;
  }

  /**
   * Method to validate the response.
   */
  public void validate() {
    Assert.hasText(id, "Blueprint id is required.");
    Assert.hasText(feedbackUserId, "Feedback User id is required.");
  }

  /**
   * Method that updates the response to the blueprint share response.
   * 
   * @param blueprint
   *            - blueprint to update
   * @param feedbackUser 
   *            - feedback user
   */
  public void updateResponse(Blueprint blueprint, User feedbackUser) {
    Map<String, List<Comments>> blueprintCommentsMap = blueprint.getCommentsMap();
    if (!CollectionUtils.isEmpty(commentsMap)) {
      if (CollectionUtils.isEmpty(blueprintCommentsMap)) {
        blueprintCommentsMap = new HashMap<String, List<Comments>>();
        blueprint.setCommentsMap(blueprintCommentsMap);
      }
      
      for (Map.Entry<String, String> entry : commentsMap.entrySet()) {
        addComment(entry.getKey(), entry.getValue(), blueprintCommentsMap, feedbackUser);
      }
    }
  }

  /**
   * Add/update the comment.
   * 
   * @param uid
   *          - the UID
   * @param commentStr
   *          - comment
   * @param blueprintCommentsMap
   *          - blueprint comments
   * @param feedbackUser
   *          - feedback user
   */
  private void addComment(String uid, String commentStr,
      Map<String, List<Comments>> blueprintCommentsMap, User feedbackUser) {
    List<Comments> commentsList = blueprintCommentsMap.get(uid);
    final boolean emptyComment = StringUtils.isBlank(commentStr);
    if (!emptyComment && CollectionUtils.isEmpty(commentsList)) {
      commentsList = new ArrayList<Comments>();
      blueprintCommentsMap.put(uid, commentsList);
    }
    
    
    Comments comment = null;
    if (commentsList != null) {
      Optional<Comments> findFirst = commentsList.stream()
          .filter(c -> c.getByEmail().equals(feedbackUser.getEmail())).findFirst();
      if (findFirst.isPresent()) {
        comment = findFirst.get();
      }
    }
    
    if (emptyComment) {
      if (comment != null) {
        commentsList.remove(comment);
        if (commentsList.isEmpty()) {
          blueprintCommentsMap.remove(uid);
        }
      }
    } else {
      if (comment != null) {
        if (!StringUtils.equals(comment.getComment(), commentStr)) {
          commentsList.remove(comment);
          comment.setComment(commentStr);
          comment.setCreatedOn(LocalDate.now());
          commentsList.add(comment);
        }
      } else {
        comment = new Comments(feedbackUser, commentStr);
        comment.setUserType(UserType.Feedback);
        commentsList.add(comment);
      }
    }
  }

  public boolean isApproved() {
    return approved;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }

  public String getFeedbackUserId() {
    return feedbackUserId;
  }

  public void setFeedbackUserId(String feedbackUserId) {
    this.feedbackUserId = feedbackUserId;
  }
}
