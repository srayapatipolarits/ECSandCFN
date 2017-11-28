package com.sp.web.dto.discussion;

import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.discussion.GroupDiscussion;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the group discussion listing.
 */
public class GroupDiscussionListingDTO implements Serializable, Comparable<GroupDiscussionListingDTO> {
  private static final long serialVersionUID = -9219111460306769320L;
  private String id;
  private String name;
  private boolean nameOverriden;
  private int unreadCount;
  private Comment lastComment;
  private boolean hasReference;
  
  /**
   * Constructor from group discussion and unread count.
   * 
   * @param groupDiscussion
   *            - group discussion
   * @param unreadCount
   *            - unread count
   */
  public GroupDiscussionListingDTO(GroupDiscussion groupDiscussion, Integer unreadCount) {
    this(groupDiscussion);
    if (unreadCount != null) {
      this.unreadCount = unreadCount;
    }
  }

  /**
   * Constructor from Group Discussion.
   * 
   * @param groupDiscussion
   *            - group discussion
   */
  public GroupDiscussionListingDTO(GroupDiscussion groupDiscussion) {
    BeanUtils.copyProperties(groupDiscussion, this);
    final List<Comment> comments = groupDiscussion.getComments();
    if (!CollectionUtils.isEmpty(comments)) {
      lastComment = comments.get(0);
      ContentReference contentReference = lastComment.getContentReference();
      hasReference = contentReference != null && contentReference.isValid();
    }
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
  
  public int getUnreadCount() {
    return unreadCount;
  }
  
  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public Comment getLastComment() {
    return lastComment;
  }

  public void setLastComment(Comment lastComment) {
    this.lastComment = lastComment;
  }

  @Override
  public int compareTo(GroupDiscussionListingDTO obj) {
    if (obj == null) {
      return -1;
    }
    return -1 * lastComment.getCreatedOn().compareTo(obj.getLastComment().getCreatedOn());
  }

  public boolean isNameOverriden() {
    return nameOverriden;
  }

  public void setNameOverriden(boolean nameOverriden) {
    this.nameOverriden = nameOverriden;
  }

  public boolean isHasReference() {
    return hasReference;
  }

  public void setHasReference(boolean hasReference) {
    this.hasReference = hasReference;
  }
}
