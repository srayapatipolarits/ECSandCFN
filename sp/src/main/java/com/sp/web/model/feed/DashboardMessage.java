package com.sp.web.model.feed;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.service.feed.NewsFeedSupport;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The dashboard message entity.
 */
public class DashboardMessage implements NewsFeedSupport {
  
  private static final long serialVersionUID = 3393207898745567083L;
  private String id;
  private String srcId;
  private Comment message;
  private String ownerId;
  private List<Comment> comments;
  private Set<String> likedByMemberIds;
  private List<UserMarkerDTO> taggedMembers;
  private List<String> taggedMemberIds;
  private boolean allCompany;
  private List<String> memberIds;
  private List<String> unfollowMemberIds;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private DashboardMessageType type;
  private String companyId;
  private Map<String, UserMarkerDTO> likedByMembers;
  private Set<String> notificationEmails;
  private boolean blockCommenting;
  private int cidCounter;
  
  public String getRefId() {
    return id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Comment getMessage() {
    return message;
  }
  
  public void setMessage(Comment message) {
    this.message = message;
  }
  
  public List<Comment> getComments() {
    return comments;
  }
  
  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }
  
  public Set<String> getLikedByMemberIds() {
    return likedByMemberIds;
  }
  
  public void setLikedByMemberIds(Set<String> likedByMemberIds) {
    this.likedByMemberIds = likedByMemberIds;
  }
  
  public List<UserMarkerDTO> getTaggedMembers() {
    return taggedMembers;
  }
  
  public void setTaggedMembers(List<UserMarkerDTO> taggedMembers) {
    this.taggedMembers = taggedMembers;
  }
  
  public List<String> getMemberIds() {
    return memberIds;
  }
  
  public void setMemberIds(List<String> memberIds) {
    this.memberIds = memberIds;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public int getLikeCount() {
    return (likedByMemberIds != null) ? likedByMemberIds.size() : 0;
  }
  
  public List<String> getTaggedMemberIds() {
    return taggedMemberIds;
  }
  
  public void setTaggedMemberIds(List<String> taggedMemberIds) {
    this.taggedMemberIds = taggedMemberIds;
  }
  
  public boolean isAllCompany() {
    return allCompany;
  }
  
  public void setAllCompany(boolean allCompany) {
    this.allCompany = allCompany;
  }
  
  public String getOwnerId() {
    return ownerId;
  }
  
  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * Add the given member to the member ids list.
   * 
   * @param memberId
   *          - member id
   */
  public void addMember(String memberId) {
    if (!memberIds.contains(memberId)) {
      memberIds.add(memberId);
    }
  }
  
  public List<String> getUnfollowMemberIds() {
    return unfollowMemberIds;
  }
  
  public void setUnfollowMemberIds(List<String> unfollowMemberIds) {
    this.unfollowMemberIds = unfollowMemberIds;
  }
  
  public DashboardMessageType getType() {
    return type;
  }
  
  public void setType(DashboardMessageType type) {
    this.type = type;
  }
  
  public String getSrcId() {
    return srcId;
  }
  
  public void setSrcId(String srcId) {
    this.srcId = srcId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  /**
   * Validated if the user has access to this dashboard message.
   * 
   * @param user
   *          - user to check
   */
  public void authoriseUser(User user) {
    Assert.isTrue(user.getCompanyId().equalsIgnoreCase(companyId),
        MessagesHelper.getMessage("service.growl.message8", user.getLocale()));
    if (!user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysMessagePost)) {
      if (!allCompany) {
        Assert.isTrue(memberIds.contains(user.getId()),
            MessagesHelper.getMessage("service.growl.message8", user.getLocale()));
     //Requriment Change by keshava People can still comment/like even if they unfollowed  :)
        // } else {
       // Assert.isTrue(!unfollowMemberIds.contains(user.getId()), "Unauthorized access.");
      }
    }
  }
  
  /**
   * Helper method to create a new dashboard message.
   * 
   * @param newCommment
   *          - new comment
   * @param companyId
   *          - company id
   * @return the newly created dashboard message
   */
  public static DashboardMessage newMessage(Comment newCommment, String companyId) {
    return newMessage(newCommment, companyId, true);
  }
  
  /**
   * Helper method to create a new dashboard message.
   * 
   * @param newCommment
   *          - new comment
   * @param companyId
   *          - company id
   * @param allCompany
   *          - flag for all company
   * @return the newly created dashboard message
   */
  public static DashboardMessage newMessage(Comment newCommment, String companyId,
      boolean allCompany) {
    return newMessage(newCommment, companyId, allCompany, DashboardMessageType.Default);
  }
  
  /**
   * Get the dashboard message for the given message post.
   * 
   * @param companyId
   *          - company id
   * @param messagePost
   *          - message post
   * @return the dashboard message
   */
  public static DashboardMessage newMessage(String companyId, SPMessagePost messagePost) {
    DashboardMessage newMessage = newMessage(messagePost.getMessage(), companyId,
        messagePost.isAllMember(), DashboardMessageType.SurePeoplePost);
    newMessage.setSrcId(messagePost.getId());
    return newMessage;
  }
  
  /**
   * Create a new dashboard message for the given user activity.
   * 
   * @param companyId
   *          - company id
   * @param activityTracking
   *          - activity tracking
   * @return the new dashboard message
   */
  public static DashboardMessage newMessage(String companyId, ActivityTracking activityTracking) {
    return newMessage(activityTracking.getMessage(), companyId, true,
        DashboardMessageType.UserActivity);
  }
  
  /**
   * Create a new dashboard message for the given SP activity feed.
   * 
   * @param companyId
   *          - company id
   * @param activityFeed
   *          - activity feed
   * @return the new dashboard message
   */
  public static DashboardMessage newMessage(String companyId, SPActivityFeed activityFeed) {
    return newMessage(Comment.newCommment(activityFeed.getText()), companyId, true,
        DashboardMessageType.SPActivityFeed);
  }
  
  /**
   * Create a new dashboard message.
   * 
   * @param newComment
   *          - new comment
   * @param companyId
   *          - company id
   * @param allCompany
   *          - flag for all company
   * @param type
   *          - type
   * @return the new dashboard message
   */
  private static DashboardMessage newMessage(Comment newComment, String companyId,
      boolean allCompany, DashboardMessageType type) {
    DashboardMessage message = new DashboardMessage();
    message.setMessage(newComment);
    final UserMarkerDTO user = newComment.getUser();
    if (user != null) {
      message.setOwnerId(user.getId());
    }
    if (allCompany) {
      message.setAllCompany(true);
    } else {
      message.setMemberIds(new ArrayList<String>());
    }
    message.setUnfollowMemberIds(new ArrayList<String>());
    message.setCreatedOn(LocalDateTime.now());
    message.setUpdatedOn(LocalDateTime.now());
    message.setType(type);
    message.setCompanyId(companyId);
    return message;
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
    comments.add(0, newCommment);
  }
  
  /**
   * Remove the given member id.
   * 
   * @param memberId
   *          - member id
   */
  public void removeTaggedMember(String memberId) {
    if (!CollectionUtils.isEmpty(taggedMemberIds)) {
      taggedMemberIds.remove(memberId);
    }
    
    if (!CollectionUtils.isEmpty(taggedMembers)) {
      taggedMembers.removeIf(u -> u.getId().equals(memberId));
    }
  }
  
  /**
   * Add the tagged members to the existing tagged members list.
   * 
   * @param taggedMembersToAdd
   *          - tagged members to add
   */
  public synchronized void addTaggedMembers(List<UserMarkerDTO> taggedMembersToAdd) {
    taggedMembersToAdd.forEach(this::addTaggedMember);
  }
  
  /**
   * Add the given user as the tagged member.
   * 
   * @param user
   *        - user
   */
  public synchronized void addTaggedMember(User user) {
    if (user != null) {
      addTaggedMember(new UserMarkerDTO(user));
    }
  }
  
  /**
   * Adding the user as a tagged member to the message.
   * 
   * @param user
   *          - tagged user
   */
  public synchronized void addTaggedMember(UserMarkerDTO user) {
    if (taggedMembers == null) {
      taggedMembers = new ArrayList<UserMarkerDTO>();
    }
    
    if (taggedMemberIds == null) {
      taggedMemberIds = new ArrayList<String>();
    }
    
    // add the tagged member
    String memberId = user.getId();
    if (!taggedMemberIds.contains(memberId)) {
      taggedMembers.add(user);
      taggedMemberIds.add(memberId);
      unfollowMemberIds.remove(memberId);
      if (!allCompany) {
        addMember(memberId);
      }
    }
    
  }
  
  /**
   * Check if the given member id is the owner of the message.
   * 
   * @param memberId
   *          - member id
   * @return true if owner else false
   */
  public boolean isOwner(String memberId) {
    if (ownerId == null) {
      return false;
    }
    return (StringUtils.equals(ownerId, memberId));
  }
  
  /**
   * Add to the liked by member count.
   * 
   * @param user
   *          - member
   * @param childCid 
   *          - child comment id
   * @param cid 
   *          - comment id
   * @param isLike 
   *          - flag to indicate if it is like or unlike
   */
  public synchronized int updatedLike(User user, int cid, int childCid, MutableBoolean isLike) {
    
    if (cid >= 0) {
      // getting the comment
      Comment comment = getComment(cid);
      Assert.notNull(comment, "Comment not found.");
      return comment.updateLike(user, childCid, isLike);
    } else {
      if (likedByMemberIds == null) {
        likedByMemberIds = new HashSet<String>();
        likedByMembers = new HashMap<String, UserMarkerDTO>();
      }
      
      String memberId = user.getId();
      if (likedByMemberIds.contains(memberId)) {
        likedByMemberIds.remove(memberId);
        likedByMembers.remove(memberId);
        isLike.setFalse();
      } else {
        likedByMemberIds.add(memberId);
        likedByMembers.put(memberId, new UserMarkerDTO(user));
      }
      return likedByMemberIds.size();
    }
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
    if (unfollowMemberIds.contains(memberId)) {
      return false;
    }
    
    // if not all company and the member not part of member list
    if (!allCompany && !memberIds.contains(memberId)) {
      return false;
    }
    
    // the message is applicable to the user
    return true;
  }
  
  /**
   * Method to support follow or unfollow to a dashboard message.
   * 
   * @param memberId
   *          - member id
   * @param follow
   *          - flag to follow or unfollow
   */
  public void follow(String memberId, boolean follow) {
    // check if not all company then
    // member part of the members applicable
    // for the group
    if (!allCompany) {
      if (!memberIds.contains(memberId)) {
        return;
      }
    }
    
    // check if follow then remove from
    // the unfollowed members list
    if (follow) {
      unfollowMemberIds.remove(memberId);
    } else {
      // adding to unfollow member list
      unfollowMemberIds.add(memberId);
    }
  }
  
  /**
   * Check if the given user id is tagged in the message.
   * 
   * @param memberId
   *          - member id
   * @return true if member tagged else false
   */
  public boolean isUserTagged(String memberId) {
    if (!CollectionUtils.isEmpty(taggedMemberIds)) {
      return taggedMemberIds.contains(memberId);
    }
    return false;
  }
  
  /**
   * Checks if the message has been liked by the user.
   * 
   * @param memberId
   *          - member id
   * @return flag true if liked by member
   */
  public boolean isLikedByUser(String memberId) {
    return (CollectionUtils.isEmpty(likedByMemberIds)) ? false : likedByMemberIds
        .contains(memberId);
  }
  
  @Override
  public String getFeedRefId() {
    return id;
  }
  
  /**
   * Check if the given member id is tagged in the message.
   * 
   * @param memberId
   *          - member id
   * @return true if tagged else false
   */
  public boolean isTagged(String memberId) {
    return CollectionUtils.isEmpty(taggedMemberIds) ? false : taggedMemberIds.contains(memberId);
  }

  public Map<String, UserMarkerDTO> getLikedByMembers() {
    return likedByMembers;
  }

  public void setLikedByMembers(Map<String, UserMarkerDTO> likedByMembers) {
    this.likedByMembers = likedByMembers;
  }

  /**
   * Check if the user is the owner or is tagged in the post.
   * 
   * @param userId
   *          - user id
   * @return
   *    true if is owner or tagged
   */
  public boolean filterByUser(String userId) {
    // check if the message type is default
    if (type == DashboardMessageType.Default) {
      // check if the user is the owner
      if (ownerId.equals(userId)) {
        return true;
      }
      
      // check if the user is tagged
      if (isTagged(userId)) {
        // check if the user has unfollowed this post
        if (!unfollowMemberIds.contains(userId)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Add the user email to the notification email's set.
   * 
   * @param user
   *          - user
   */
  public synchronized void addToNotifications(User user) {
    if (getNotificationEmails() == null) {
      setNotificationEmails(new HashSet<String>());
    }
    getNotificationEmails().add(user.getEmail());
  }

  public Set<String> getNotificationEmails() {
    return notificationEmails;
  }

  public void setNotificationEmails(Set<String> notificationEmails) {
    this.notificationEmails = notificationEmails;
  }

  /**
   * Get the comment from the given comment id.
   * 
   * @param cid
   *          - comment id
   * @return
   *    the comment found else null
   */
  public Comment getComment(int cid) {
    Assert.isTrue(!CollectionUtils.isEmpty(comments), "Comment not found.");
    Optional<Comment> findFirst = comments.stream().filter(c -> c.getCid() == cid).findFirst();
    Assert.isTrue(findFirst.isPresent(), "Comment not found.");
    return findFirst.get();
  }

  /**
   * Check if the user is currently un-following the message thread.
   * 
   * @param user
   *          - user
   * @return
   *    true if unfollowing else false
   */
  public boolean unfollowed(User user) {
    if (!CollectionUtils.isEmpty(unfollowMemberIds)) {
      return unfollowMemberIds.contains(user.getId());
    }
    return false;
  }

  /**
   * Delete the given child comment.
   * 
   * @param comment
   *            - comment to delete
   */
  public void deleteComment(Comment comment) {
    if (!CollectionUtils.isEmpty(comments)) {
      comments.remove(comment);
      final String email = comment.getUser().getEmail();
      Optional<Comment> findFirst = comments.stream()
          .filter(c -> c.getUser().sameEmail(email)).findFirst();
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

  public boolean isBlockCommenting() {
    return blockCommenting;
  }

  public void setBlockCommenting(boolean blockCommenting) {
    this.blockCommenting = blockCommenting;
  }

  public int getCidCounter() {
    return cidCounter;
  }

  public void setCidCounter(int cidCounter) {
    this.cidCounter = cidCounter;
  }

  /**
   * Method to indicate if the message can be displayed to the user.
   * 
   * @param memberId
   *          - member id
   * @return
   *    true if can be displayed else false
   */
  public boolean canShow(String memberId) {
    // if not all company and the member not part of member list
    if (!allCompany && !memberIds.contains(memberId)) {
      return false;
    }
    
    // the message is applicable to the user
    return true;
  }
}
