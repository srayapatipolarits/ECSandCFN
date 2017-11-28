package com.sp.web.model.discussion;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The group discussion entity.
 */
public class GroupDiscussion implements Serializable {
  
  private static final long serialVersionUID = -7931159021344898705L;
  private String id;
  private String name;
  private String companyId;
  private boolean nameOverriden;
  private List<String> memberIds;
  private LinkedList<Comment> comments;
  private Set<String> filterNotifications;
  
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
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public boolean isNameOverriden() {
    return nameOverriden;
  }
  
  public void setNameOverriden(boolean nameOverriden) {
    this.nameOverriden = nameOverriden;
  }
  
  public List<String> getMemberIds() {
    return memberIds;
  }
  
  public void setMemberIds(List<String> memberIds) {
    this.memberIds = memberIds;
  }
  
  public LinkedList<Comment> getComments() {
    return comments;
  }
  
  public void setComments(LinkedList<Comment> comments) {
    this.comments = comments;
  }

  public Set<String> getFilterNotifications() {
    return filterNotifications;
  }

  public void setFilterNotifications(Set<String> filterNotifications) {
    this.filterNotifications = filterNotifications;
  }
  
  /**
   * Add the comment to the comments list.
   * 
   * @param comment
   *          - comment
   * @param user 
   *          - user
   * @return 
   *      the new comment added
   */
  public Comment addComment(Comment comment, User user) {
    addComment(comment, user, false);
    return comment;
  }

  /**
   * Add the comment to the comment list.
   * 
   * @param comment
   *          - comment to add
   * @param user
   *          - user
   * @param isUpdateName
   *          - flag to update the name
   */
  public synchronized void addComment(Comment comment, User user, boolean isUpdateName) {
    if (comments == null) {
      comments = new LinkedList<Comment>();
    }
    comment.setCid(comments.size());
    comment.setCreatedOn(LocalDateTime.now());
    comment.setUser(new UserMarkerDTO(user));
    comments.add(0, comment);
    if (isUpdateName && !nameOverriden) {
      if (name != null) {
        name += "," + user.getFirstName();
      } else {
        name = user.getFirstName();
      }
    }
  }
  
  /**
   * Add member to the member id list.
   * 
   * @param memberId
   *          - member id
   */
  public synchronized void addMember(String memberId) {
    if (memberIds == null) {
      memberIds = new ArrayList<String>();
    }
    memberIds.add(memberId);
  }

  /**
   * Validate if the user is part of the group discussion.
   * 
   * @param userId
   *          - user id
   * @return
   *    true if user present else false
   */
  public boolean validateUser(String userId) {
    if (CollectionUtils.isEmpty(memberIds)) {
      return false;
    }
    return memberIds.contains(userId);
  }

  /**
   * Remove the member from the group discussion.
   * 
   * @param memberId
   *          - member id
   */
  public void removeMember(String memberId) {
    if (!CollectionUtils.isEmpty(memberIds)) {
      memberIds.remove(memberId);
    }
    if (!CollectionUtils.isEmpty(filterNotifications)) {
      filterNotifications.remove(memberId);
    }
  }

  /**
   * Updating the name for the group discussion.
   * 
   * @param user
   *          - user
   * @param newName
   *          - new name
   */
  public void updateName(User user, String newName) {
    name = newName;
    if (StringUtils.isBlank(name)) {
      nameOverriden = false;
      name = null;
      addComment(Comment.newCommment(MessagesHelper.getMessage("group.discussion.user.blankName",
          user.getFirstName(), user.getLastName())), user);
    } else {
      nameOverriden = true;
      addComment(Comment.newCommment(MessagesHelper.getMessage("group.discussion.user.changeName",
          user.getFirstName(), user.getLastName(), newName)), user);
    }
  }

  /**
   * Add the given user to the filter notifications list.
   * 
   * @param userId
   *          - user id
   */
  public synchronized void addtoFilterNotifications(String userId) {
    if (filterNotifications == null) {
      filterNotifications = new HashSet<String>();
    }
    filterNotifications.add(userId);
  }

  /**
   * Remove the given user from the filter notifications list.
   * 
   * @param userId
   *          - user id
   */
  public void removeFromFilterNotifications(String userId) {
    if (filterNotifications != null) {
      filterNotifications.remove(userId);
    }
  }

  /**
   * Check if the current user has filter notification enabled.
   * 
   * @param userId
   *          - user id
   * @return
   *    true if user has turned on filter notification else false
   */
  public boolean checkFilterNotification(String userId) {
    if (!CollectionUtils.isEmpty(filterNotifications)) {
      return filterNotifications.contains(userId);
    }
    return false;
  }
  
  /**
   * Check if the user has filter notification enabled.
   * 
   * @param user
   *          - user
   * @return
   *    true if user has turned on filter notification else false
   */
  public boolean checkFilterNotification(User user) {
    return checkFilterNotification(user.getId());
  }
}
