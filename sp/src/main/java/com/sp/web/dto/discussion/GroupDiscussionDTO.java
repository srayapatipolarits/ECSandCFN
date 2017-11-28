package com.sp.web.dto.discussion;

import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.discussion.GroupDiscussion;
import com.sp.web.model.poll.SPMiniPollResult;

import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for group discussion.
 */
public class GroupDiscussionDTO {
  
  private String id;
  private String name;
  private boolean nameOverriden;
  private boolean filterNotification;
  private List<Comment> comments;
  private Map<Integer,Boolean> hasCompletedPolls;
  
  
  /**
   * Constructor from group discussion.
   * 
   * @param groupDiscussion
   *            - group discussion
   * @param user 
   *            - user
   */
  public GroupDiscussionDTO(GroupDiscussion groupDiscussion, User user) {
    this(groupDiscussion);
    String userId = user.getId();
    filterNotification =  groupDiscussion.checkFilterNotification(user.getId());
    if (comments != null) {
      comments.stream().forEach(cm -> {
        if (cm.getMiniPoll() != null) {
          SPMiniPollResult result = cm.getMiniPoll().getResult();
          if (result != null) {
            List<Integer> list = result.getUserPollSelection().get(userId);
            if (list != null) {
              getHasCompletedPolls().put(cm.getCid(), true);
            }
          }  
        }
        
      });
    }
    
      
  }

  /**
   * Constructor from group discussion.
   * 
   * @param groupDiscussion
   *            - group discussion
   */
  public GroupDiscussionDTO(GroupDiscussion groupDiscussion) {
    BeanUtils.copyProperties(groupDiscussion, this);
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public List<Comment> getComments() {
    return comments;
  }
  
  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public boolean isNameOverriden() {
    return nameOverriden;
  }

  public void setNameOverriden(boolean nameOverriden) {
    this.nameOverriden = nameOverriden;
  }

  public boolean isFilterNotification() {
    return filterNotification;
  }

  public void setFilterNotification(boolean filterNotification) {
    this.filterNotification = filterNotification;
  }
  
  
  public void setHasCompletedPolls(Map<Integer, Boolean> hasCompletedPolls) {
    this.hasCompletedPolls = hasCompletedPolls;
  }
  
  public Map<Integer, Boolean> getHasCompletedPolls() {
    if(hasCompletedPolls == null){
      hasCompletedPolls = new HashMap<Integer, Boolean>();
    }
    return hasCompletedPolls;
    
  }
}
