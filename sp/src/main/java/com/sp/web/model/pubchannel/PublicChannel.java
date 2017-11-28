package com.sp.web.model.pubchannel;

import com.sp.web.model.Comment;
import com.sp.web.model.SPFeature;
import com.sp.web.service.feed.NewsFeedSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * PublicChannel is the public channel model for holding the public conversation for the user.
 * 
 * @author pradeepruhil
 *
 */
public class PublicChannel implements NewsFeedSupport {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -1779170988876862006L;
  
  private String id;
  private LinkedList<Comment> comments;
  private String title;
  private String pcRefId;
  private SPFeature spFeature;
  private String companyId;
  private String parentRefId;
  private List<String> memberIds;
  private List<String> unfollowMemberIds;
  private boolean allCompany;
  private String name;
  private String text;
  private Set<String> notificationEmails;
  
  private int cidCounter;
  
  /**
   * Default constructor for the model.
   */
  public PublicChannel() {
    
  }
  
  /**
   * Constrcutor creating object with pcRefId and companyId.
   * 
   * @param pcRefId
   *          publicChannelReferenceId
   * @param companyId
   *          companyId.
   * @param feature
   *          spfeature for which this public channel belongs.
   */
  public PublicChannel(String pcRefId, String companyId, SPFeature feature) {
    this.pcRefId = pcRefId;
    this.companyId = companyId;
    this.spFeature = feature;
    
  }
  
  public String getRefId() {
    return id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Return the comments for the public channel.
   * 
   * @return the linked list of comments.
   */
  public LinkedList<Comment> getComments() {
    if (this.comments == null) {
      this.comments = new LinkedList<>();
    }
    return comments;
  }
  
  public void setComments(LinkedList<Comment> comments) {
    this.comments = comments;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getPcRefId() {
    return pcRefId;
  }
  
  public void setPcRefId(String pcRefId) {
    this.pcRefId = pcRefId;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }
  
  public void setMemberIds(List<String> memberIds) {
    this.memberIds = memberIds;
  }
  
  public boolean isAllCompany() {
    return allCompany;
  }
  
  public void setUnfollowMemberIds(List<String> unfollowMemberIds) {
    this.unfollowMemberIds = unfollowMemberIds;
  }
  
  /**
   * @see com.sp.web.service.feed.NewsFeedSupport#getUnfollowMemberIds()
   */
  @Override
  public List<String> getUnfollowMemberIds() {
    if (unfollowMemberIds == null) {
      unfollowMemberIds = new ArrayList<String>();
    }
    return unfollowMemberIds;
  }
  
  /**
   * 
   * @see com.sp.web.service.feed.NewsFeedSupport#getMemberIds()
   */
  @Override
  public List<String> getMemberIds() {
    if (memberIds == null) {
      memberIds = new ArrayList<String>();
    }
    return memberIds;
  }
  
  public void setAllCompany(boolean allCompany) {
    this.allCompany = allCompany;
  }
  
  /**
   * Check if the message is applicable for the user.
   * 
   * @param memberId
   *          - member id
   * @return true if applicable else false
   */
  public boolean isApplicable(String memberId) {
    
    // if user is part of the unfollow list
    if (unfollowMemberIds != null && unfollowMemberIds.contains(memberId)) {
      return false;
    }
    
    // if not all company and the member not part of member list
    if (!allCompany && !memberIds.contains(memberId)) {
      return false;
    }
    
    // the message is applicable to the user
    return true;
  }
  
  @Override
  public String getFeedRefId() {
    return pcRefId;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * Update the title if it is different.
   * 
   * @param title
   *          - title to update
   * @return true if updated else false
   */
  public boolean updateTitle(String title) {
    if (!StringUtils.equals(this.title, title)) {
      this.title = title;
      return true;
    }
    return false;
  }
  
  /**
   * Update the name if it is different.
   * 
   * @param name
   *          - name
   * @return true if updated else false
   */
  public boolean updateName(String name) {
    if (!StringUtils.equals(this.name, name)) {
      this.name = name;
      return true;
    }
    return false;
  }
  
  /**
   * Check if user is present in the public channel.
   * 
   * @param userId
   *          - user id
   * @return true if present else false
   */
  public boolean hasUser(String userId) {
    return (!CollectionUtils.isEmpty(memberIds)) ? memberIds.contains(userId) : false;
  }
  
  /**
   * Remove the give user id from the public channel.
   * 
   * @param userId
   *          - user id
   */
  public void removeUser(String userId) {
    getMemberIds().remove(userId);
    getUnfollowMemberIds().remove(userId);
  }
  
  /**
   * Add the user to the given public channel.
   * 
   * @param userId
   *          - user id
   */
  public synchronized void addMember(String userId) {
    getUnfollowMemberIds().remove(userId);
    if (!allCompany) {
      if (memberIds != null) {
        if (!memberIds.contains(userId)) {
          memberIds.add(userId);
        }
      } else {
        memberIds = new ArrayList<String>();
        memberIds.add(userId);
      }
    }
  }
  
  /**
   * Unfollow the given public channel.
   * 
   * @param userId
   *          - user id
   */
  public synchronized void unfollow(String userId) {
    if (unfollowMemberIds != null) {
      if (!unfollowMemberIds.contains(userId)) {
        unfollowMemberIds.add(userId);
      }
    } else {
      unfollowMemberIds = new ArrayList<String>();
      unfollowMemberIds.add(userId);
    }
  }
  
  /**
   * Get the status if the user is currently following the public channel.
   * 
   * @param memberId
   *          - member id
   * @return true if following else false
   */
  public boolean followStatus(String memberId) {
    return (unfollowMemberIds != null) ? !unfollowMemberIds.contains(memberId) : true;
  }
  
  /**
   * Add a new comments to the comments list.
   * 
   * @param newCommment
   *          - new comment
   */
  public synchronized void addComment(Comment newCommment) {
    if (comments == null) {
      comments = new LinkedList<Comment>();
    }
    newCommment.setCid(cidCounter++);
    comments.addFirst(newCommment);
    
  }
  
  /**
   * Get the comment from the given comment id.
   * 
   * @param cid
   *          - comment id
   * @return the comment found else null
   */
  public Comment getComment(int cid) {
    Assert.isTrue(!CollectionUtils.isEmpty(comments), "Comment not found.");
    Optional<Comment> findFirst = comments.stream().filter(c -> c.getCid() == cid).findFirst();
    Assert.isTrue(findFirst.isPresent(), "Comment not found.");
    return findFirst.get();
  }
  
  /**
   * Delete the given child comment.
   * 
   * @param comment
   *          - comment to delete
   */
  public void deleteComment(Comment comment) {
    if (!CollectionUtils.isEmpty(comments)) {
      comments.remove(comment);
      final String email = comment.getUser().getEmail();
      Optional<Comment> findFirst = comments.stream().filter(c -> c.getUser().sameEmail(email))
          .findFirst();
      if (!findFirst.isPresent()) {
        removeFromNotifications(email);
      }
      
      removeFromNotifications(comment.getUser().getEmail());
    }
  }
  
  /**
   * Remove the given email from the email notifications list.
   * 
   * @param email
   *          - email to remove
   */
  private void removeFromNotifications(String email) {
    if (!CollectionUtils.isEmpty(notificationEmails)) {
      notificationEmails.remove(email);
    }
  }
  
  public void setNotificationEmails(Set<String> notificationEmails) {
    this.notificationEmails = notificationEmails;
  }
  
  public Set<String> getNotificationEmails() {
    if (notificationEmails == null) {
      notificationEmails = new HashSet<String>();
    }
    return notificationEmails;
  }
  
  public void setCidCounter(int cidCounter) {
    this.cidCounter = cidCounter;
  }
  
  public int getCidCounter() {
    return cidCounter;
  }
}
