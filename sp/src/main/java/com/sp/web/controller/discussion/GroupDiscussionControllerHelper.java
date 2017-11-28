package com.sp.web.controller.discussion;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.discussion.GroupDiscussionDTO;
import com.sp.web.dto.discussion.GroupDiscussionListingDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.Operation;
import com.sp.web.form.discussion.GroupDiscussionForm;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.User;
import com.sp.web.model.bm.ta.ToneRequestType;
import com.sp.web.model.discussion.GroupDiscussion;
import com.sp.web.model.discussion.UserGroupDiscussion;
import com.sp.web.model.poll.SPMiniPoll;
import com.sp.web.model.poll.SPMiniPollResult;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.bm.ToneAnalyserFactory;
import com.sp.web.service.discussion.GroupDiscussionFactory;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class for the group discussion controller.
 */
@Component
public class GroupDiscussionControllerHelper {
  
  private static final Logger log = Logger.getLogger(GroupDiscussionControllerHelper.class);
  
  @Autowired
  GroupDiscussionFactory groupDiscussionFactory;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationProcessor;
  
  @Autowired
  EventGateway eventGateway;
  
  @Autowired
  ToneAnalyserFactory toneAnalyserFactory;
  
  /**
   * Helper method to get all the users group discussions.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the user group discussions
   */
  public SPResponse getAll(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    boolean isDashboardRequest = (boolean) params[0];
    
    // getting the users group discussions
    UserGroupDiscussion userGroupDiscussion = groupDiscussionFactory.getUserGroupDiscussion(user);
    
    // get all the group discussions for the user
    final List<String> groupDiscussionIds = userGroupDiscussion.getGroupDiscussionIds();
    
    // if any group discussion exists then add the same to the response
    if (!CollectionUtils.isEmpty(groupDiscussionIds)) {
      // the map of the unread counts
      final Map<String, Integer> unreadCountMap = userGroupDiscussion.getUnreadCountMapOrCreate();
      final List<GroupDiscussionListingDTO> gdListing = new ArrayList<GroupDiscussionListingDTO>();
      // adding the group discussions to the GD list
      for (String gdId : groupDiscussionIds) {
        final GroupDiscussion groupDiscussion = groupDiscussionFactory.getGroupDiscussion(gdId);
        gdListing.add(new GroupDiscussionListingDTO(groupDiscussion, unreadCountMap.get(gdId)));
      }
      Collections.sort(gdListing);
      resp.add(Constants.PARAM_GROUP_DISCUSSION_LISTING, gdListing);
      
      // if dashboard request add the details for the first group discussion
      if (isDashboardRequest) {
        GroupDiscussionListingDTO gdListingDTO = gdListing.get(0);
        addGroupDiscussionDetailsToResponse(resp, userGroupDiscussion,
            groupDiscussionFactory.getGroupDiscussion(gdListingDTO.getId()), user);
      }
    } else {
      resp.isSuccess();
    }
    return resp;
  }
  
  /**
   * Add the given group discussion details to the response and also reset the unread count.
   * 
   * @param resp
   *          - response
   * @param userGroupDiscussion
   *          - user group discussion
   * @param groupDiscussion
   *          - group discussion
   * @param user
   *          - user
   * @return the response
   */
  private SPResponse addGroupDiscussionDetailsToResponse(final SPResponse resp,
      UserGroupDiscussion userGroupDiscussion, GroupDiscussion groupDiscussion, User user) {
    userGroupDiscussion.resetUnreadCount(groupDiscussion.getId());
    groupDiscussionFactory.updateUserGroupDiscussion(userGroupDiscussion);
    resp.add(Constants.PARAM_GROUP_DISCUSSION, new GroupDiscussionDTO(groupDiscussion, user));
    return resp;
  }
  
  /**
   * Helper method to get the details of the group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the group discussion details
   */
  public SPResponse getDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id required.");
    
    UserGroupDiscussion userGroupDiscussion = groupDiscussionFactory.getUserGroupDiscussion(user);
    Assert.isTrue(userGroupDiscussion.partOfGroupDiscussion(gdId), "Unauthorized request.");
    
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    return addGroupDiscussionDetailsToResponse(resp, userGroupDiscussion, groupDiscussion, user);
  }
  
  /**
   * Helper method to update the group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return Response to the update request
   */
  public SPResponse create(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form to process and validate the same
    GroupDiscussionForm form = (GroupDiscussionForm) params[0];
    form.validateCreate();
    
    // Create a new group discussion from the form data
    final GroupDiscussion groupDiscussion = form.newGroupDiscussion(user);
    groupDiscussionFactory.createGroupDiscussion(groupDiscussion);
    
    // get the unread messages count
    final List<String> memberIds = form.getMemberIds();
    memberIds.remove(user.getId());
    final int unreadCount = 1;
    
    // update all the users
    updateGroupDiscussionUsers(user, groupDiscussion, Operation.CREATE, memberIds, unreadCount);
    
    // adding to the owner user
    updateUser(Operation.ADD_OWNER, user, groupDiscussion, 0);
    
    // doing tone analysis
    final String commentText = form.getComment().getComment();
    if (!StringUtils.isBlank(commentText)) {
      toneAnalyserFactory.process(ToneRequestType.GroupDiscussionCreate, commentText, user);
    }
    
    return resp.add(Constants.PARAM_GROUP_DISCUSSION, new GroupDiscussionDTO(groupDiscussion));
  }
  
  /**
   * Controller method to update the group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse update(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form to process and validate the same
    GroupDiscussionForm form = (GroupDiscussionForm) params[0];
    form.validateUpdate();
    
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(form.getGdId(), user);
    
    // validate if user part of group discussion
    Assert.isTrue(groupDiscussion.validateUser(user.getId()), "Unauthorised request.");
    
    // add the comment to the group discussion
    form.addComment(user, groupDiscussion);
    
    // saving the group discussion in the DB
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    // get the list of members to send the update to
    List<String> memberIds = new ArrayList<String>(groupDiscussion.getMemberIds());
    memberIds.remove(user.getId());
    
    // updating the unread counts and sending notifications to the users
    updateGroupDiscussionUsers(user, groupDiscussion, Operation.UPDATE, memberIds, 1);
    
    // doing tone analysis
    final String commentText = form.getComment().getComment();
    if (!StringUtils.isBlank(commentText)) {
      toneAnalyserFactory.process(ToneRequestType.GroupDiscussionComment, commentText, user);
    }
    
    return resp
        .add(Constants.PARAM_GROUP_DISCUSSION, new GroupDiscussionDTO(groupDiscussion, user));
  }
  
  /**
   * Controller method to add members to the group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add member request
   */
  @SuppressWarnings("unchecked")
  public SPResponse addMember(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id is required.");
    
    List<String> memberIds = (List<String>) params[1];
    // Assert.notEmpty(memberIds, "Member not found to add.");
    
    // the new name for the group
    String newName = (String) params[2];
    
    // flag to indicate if the name was cleared
    boolean nameCleared = (boolean) params[3];
    
    int updateUnreadCount = 0;
    
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    if (!StringUtils.isBlank(newName)) {
      groupDiscussion.updateName(user, newName);
      updateUnreadCount++;
    }
    
    if (nameCleared) {
      groupDiscussion.updateName(user, null);
      updateGroupDiscussionName(groupDiscussion, false);
      updateUnreadCount++;
    }
    
    // get the list of members to send the update to
    List<String> membersToUpdate = null;
    
    if (!CollectionUtils.isEmpty(memberIds)) {
      // the number of updates to the group discussion
      int unreadCount = memberIds.size() + groupDiscussion.getComments().size();
      
      // adding the members to the group discussion
      updateGroupDiscussionUsers(user, groupDiscussion, Operation.ADD, memberIds, unreadCount);
      
      // getting the list of members to update
      membersToUpdate = new ArrayList<String>(groupDiscussion.getMemberIds());
      
      // removing all the added members
      membersToUpdate.removeAll(memberIds);
      // adding to the update unread count
      updateUnreadCount += memberIds.size();
    } else {
      // getting the list of members to update
      membersToUpdate = new ArrayList<String>(groupDiscussion.getMemberIds());
    }
    
    // removing self
    membersToUpdate.remove(user.getId());
    
    if (!CollectionUtils.isEmpty(membersToUpdate) && updateUnreadCount > 0) {
      // adding the members to the group discussion
      updateGroupDiscussionUsers(user, groupDiscussion, Operation.UPDATE, membersToUpdate,
          updateUnreadCount);
    }
    
    // sending the updated group discussion
    return resp
        .add(Constants.PARAM_GROUP_DISCUSSION, new GroupDiscussionDTO(groupDiscussion, user));
  }
  
  /**
   * Controller method for user to leave the group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the leave group request
   */
  public SPResponse leaveGroupDiscussion(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id is required.");
    
    leaveGroupDiscussion(user, gdId);
    
    return resp.isSuccess();
  }
  
  /**
   * Method for user to leave the group discussion.
   * 
   * @param user
   *          - user
   * @param gdId
   *          - group discussion id
   */
  public void leaveGroupDiscussion(User user, String gdId) {
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    // removing the user from the group discussion
    updateUser(Operation.LEAVE_GROUP, user, groupDiscussion, 0);
    
    // updating the group discussion
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    final List<String> memberIds = groupDiscussion.getMemberIds();
    
    // check if the group is empty then remove the group
    if (CollectionUtils.isEmpty(memberIds)) {
      groupDiscussionFactory.removeGroupDiscussion(groupDiscussion);
    } else {
      if (!groupDiscussion.isNameOverriden()) {
        updateGroupDiscussionName(groupDiscussion);
      }
      // notifying the other users of the update
      // updateGroupDiscussionUsers(user, groupDiscussion, Operation.UPDATE, memberIds, 1);
    }
  }
  
  /**
   * Helper method to get the count of unread messages across all the group discussions.
   * 
   * @param user
   *          - user
   * @return the unread messages count
   */
  public SPResponse getUnreadCount(User user) {
    final SPResponse resp = new SPResponse();
    
    UserGroupDiscussion userGroupDiscussion = groupDiscussionFactory.getUserGroupDiscussion(user);
    
    int count = 0;
    Map<String, Integer> unreadCountMap = userGroupDiscussion.getUnreadCountMap();
    if (!CollectionUtils.isEmpty(unreadCountMap)) {
      count = unreadCountMap.values().stream().mapToInt(i -> i).sum();
    }
    
    return resp.add(Constants.PARAM_GROUP_DISCUSSION_UNREAD_COUNT, count);
  }
  
  /**
   * Helper method to change the group name.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the change name request
   */
  public SPResponse renameGroup(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id is required.");
    
    String name = (String) params[1];
    Assert.hasText(name, "Name is required.");
    
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    // setting the new name and saving the group discussion
    groupDiscussion.updateName(user, name);
    if (groupDiscussion.getName() == null) {
      updateGroupDiscussionName(groupDiscussion);
    }
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    // notify all the users except the current one
    // get the list of members to send the update to
    List<String> membersToUpdate = new ArrayList<String>(groupDiscussion.getMemberIds());
    // removing self
    membersToUpdate.remove(user.getId());
    
    if (!CollectionUtils.isEmpty(membersToUpdate)) {
      // adding the members to the group discussion
      updateGroupDiscussionUsers(user, groupDiscussion, Operation.UPDATE, membersToUpdate, 1);
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get the member details for a group discussion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the result for the get group member details request
   */
  public SPResponse groupMemberDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id is required.");
    
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    // notify all the users except the current one
    // get the list of members to send the update to
    List<String> membersToUpdate = new ArrayList<String>(groupDiscussion.getMemberIds());
    // removing self
    membersToUpdate.remove(user.getId());
    
    // adding the member details to the response
    if (!CollectionUtils.isEmpty(membersToUpdate)) {
      resp.add(Constants.PARAM_MEMBER_LIST, membersToUpdate.stream().map(userFactory::getUser)
          .filter(u -> u != null).map(UserMarkerDTO::new).collect(Collectors.toList()));
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to filter notifications for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the filter notifications request
   */
  public SPResponse filterNotification(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String gdId = (String) params[0];
    Assert.hasText(gdId, "Group discussion id is required.");
    
    boolean doFilter = (boolean) params[1];
    
    // get the group discussion
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    final String userId = user.getId();
    if (doFilter) {
      groupDiscussion.addtoFilterNotifications(userId);
    } else {
      groupDiscussion.removeFromFilterNotifications(userId);
    }
    return resp.isSuccess();
  }
  
  /**
   * Get the group discussion and also validate if the user is authorized.
   * 
   * @param gdId
   *          - group discussion id
   * @param user
   *          - user
   * @return the group discussion
   */
  private GroupDiscussion getGroupDiscussion(String gdId, User user) {
    GroupDiscussion groupDiscussion = groupDiscussionFactory.getGroupDiscussion(gdId);
    Assert.notNull(groupDiscussion, "Group discussion not found.");
    Assert.isTrue(groupDiscussion.validateUser(user.getId()), "Unauthorized request.");
    return groupDiscussion;
  }
  
  /**
   * Send an update to all the users for the group discussion.
   * 
   * @param user
   *          - request owner
   * @param groupDiscussion
   *          - group discussion
   * @param op
   *          - update operation
   * @param memberIds
   *          - list of members to update
   * @param unreadCount
   *          - unread count
   */
  private void updateGroupDiscussionUsers(User user, GroupDiscussion groupDiscussion, Operation op,
      List<String> memberIds, int unreadCount) {
    
    // creating the payload
    final HashMap<String, Object> payload = new HashMap<String, Object>();
    final GroupDiscussionListingDTO gdListingDTO = new GroupDiscussionListingDTO(groupDiscussion);
    payload.put(Constants.PARAM_GROUP_DISCUSSION_COMMENT, gdListingDTO);
    payload.put(Constants.PARAM_OPERATION, op);
    
    // iterating of the all the members of the group discussion
    // and performing the operation
    final ArrayList<User> userList = new ArrayList<User>();
    for (String userId : memberIds) {
      
      // get the user if user not found then add to remove list
      User member = userFactory.getUser(userId);
      if (member == null) {
        log.warn("User not found :" + userId);
        continue;
      }
      
      // performing the operation
      updateUser(op, member, groupDiscussion, unreadCount);
      
      // creating a list of valid members
      userList.add(member);
    }
    
    // no user action taken
    boolean sendNotification = !CollectionUtils.isEmpty(userList);
    
    // updating the group discussion
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    switch (op) {
    case CREATE:
    case ADD:
      // update the group name
      if (!groupDiscussion.isNameOverriden()) {
        updateGroupDiscussionName(user, userList, groupDiscussion);
        groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
      }
      
      if (sendNotification) {
        // sending the notification to all the users
        final Map<String, Object> notificationParams = new HashMap<String, Object>();
        notificationParams.put("name", groupDiscussion.getName());
        notificationParams.put("nameOverride", groupDiscussion.isNameOverriden());
        final Comment value = groupDiscussion.getComments().get(0);
        ContentReference contentReference = value.getContentReference();
        notificationParams.put("lastComment", value);
        notificationParams.put("hasReference",
            contentReference != null && contentReference.isValid());
        notificationParams.put("id", groupDiscussion.getId());
        if (value.getMiniPoll() != null) {
          userList.forEach(u -> notificationProcessor.process(NotificationType.MiniPollGdCreated,
              user, u, notificationParams));
        } else {
          userList.forEach(u -> notificationProcessor.process(NotificationType.GroupDiscussionAdd,
              user, u, notificationParams));
        }
        
        // updating the payload with the new group name
        payload.put(Constants.PARAM_GROUP_DISCUSSION_COMMENT, gdListingDTO);
      }
      break;
    case UPDATE:
      if (sendNotification) {
        // sending the notification to all the users
        final Map<String, Object> notificationParams = new HashMap<String, Object>(payload);
        userList
            .stream()
            .filter(u -> !groupDiscussion.checkFilterNotification(u))
            .forEach(
                u -> {
                  /*
                   * check if the comment is a poll or not. If it is a poll, then send the invite
                   * email.
                   */
                  if (gdListingDTO.getLastComment().getMiniPoll() != null) {
                    notificationProcessor.process(NotificationType.MiniPollGdCreated, user, u,
                        notificationParams);
                  } else {
                    notificationProcessor.process(NotificationType.GroupDiscussionComment, user, u,
                        notificationParams);
                  }
                  
                });
      }
      break;
    default:
      break;
    }
    
    if (sendNotification) {
      // sending the event to all the logged in users
      eventGateway.sendEvent(MessageEventRequest.newEvent(ActionType.GroupDiscussion, memberIds,
          payload, user.getCompanyId()));
    }
  }
  
  private void updateUser(Operation op, User member, GroupDiscussion groupDiscussion,
      int unreadCount) {
    // get the user group discussion
    UserGroupDiscussion userGroupDiscussion = groupDiscussionFactory.getUserGroupDiscussion(member);
    // add the group discussion to the users group discussion
    final String gdId = groupDiscussion.getId();
    
    switch (op) {
    case ADD_OWNER:
      // adding the group discussion to the users group discussion list
      userGroupDiscussion.addGroupDiscussion(gdId);
      
      break;
    
    case ADD:
      // adding the group discussion to the users group discussion list
      userGroupDiscussion.addGroupDiscussion(gdId);
      // adding the comment that new member is added to the group discussion
      groupDiscussion
          .addComment(getNewComment("group.discussion.user.added", member), member, true);
      
      // adding the member to the group discussion
      groupDiscussion.addMember(member.getId());
      
      break;
    
    case CREATE:
      // adding the group discussion to the users group discussion list
      userGroupDiscussion.addGroupDiscussion(gdId);
      
      // adding the member to the group discussion
      groupDiscussion.addMember(member.getId());
      
      break;
    
    case LEAVE_GROUP:
      userGroupDiscussion.removeGroupDiscussion(gdId);
      groupDiscussion.removeMember(member.getId());
      // adding the comment that new member is added to the group discussion
      groupDiscussion
          .addComment(getNewComment("group.discussion.user.left", member), member, false);
      break;
    default:
      break;
    }
    
    // update unread count if greater than zero
    if (unreadCount > 0) {
      // update the unread count
      userGroupDiscussion.updateUnreadCount(gdId, unreadCount);
    }
    
    // update the user group discussion and group discussion in DB
    groupDiscussionFactory.updateUserGroupDiscussion(userGroupDiscussion);
    
  }
  
  /**
   * Update the group discussion name and update the DB.
   * 
   * @param groupDiscussion
   *          - group discussion
   */
  private void updateGroupDiscussionName(GroupDiscussion groupDiscussion) {
    updateGroupDiscussionName(groupDiscussion, true);
  }
  
  /**
   * Update the group discussion name.
   * 
   * @param groupDiscussion
   *          - group discussion
   * @param updateDb
   *          - flag to update db
   */
  private void updateGroupDiscussionName(GroupDiscussion groupDiscussion, boolean updateDb) {
    List<User> collect = groupDiscussion.getMemberIds().stream().map(userFactory::getUser)
        .filter(u -> u != null).collect(Collectors.toList());
    updateGroupDiscussionName(collect.remove(0), collect, groupDiscussion);
    if (updateDb) {
      groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    }
  }
  
  /**
   * Generate the name for the group from the group discussion user list.
   * 
   * @param user
   * 
   * @param userList
   *          - group discussion user list
   * @param groupDiscussion
   *          - group discussion
   */
  private void updateGroupDiscussionName(User user, List<User> userList,
      GroupDiscussion groupDiscussion) {
    final StringBuffer sb = new StringBuffer();
    sb.append(user.getFirstName());
    userList.forEach(u -> sb.append(", ").append(u.getFirstName()));
    groupDiscussion.setName(sb.toString());
  }
  
  /**
   * Get the comment for the user added to group discussion.
   * 
   * @param key
   *          - key from the messages props for the comment
   * @param user
   *          - added user
   * @return the new comment
   */
  private Comment getNewComment(String key, User user) {
    Comment comment = new Comment();
    comment.setText(MessagesHelper.getMessage(key, user.getFirstName(), user.getLastName()));
    return comment;
  }
  
  /**
   * <code>answerMiniPoll</code> method capture the answer of the minipoll from the user.
   * 
   * @param user
   *          who is giving the mini poll
   * @param params
   *          contains the message id and answer selections.
   * @return the spresponse.
   */
  public SPResponse answerMiniPoll(User user, Object[] params) {
    String gdId = (String) params[0];
    
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    int commentId = (int) params[1];
    
    Assert.isTrue(commentId < groupDiscussion.getComments().size(), "Invalid Comment Id");
    Comment comment = groupDiscussion.getComments().stream().filter(cm -> cm.getCid() == commentId)
        .findFirst().get();
    
    /*
     * get the miniPoll *.
     */
    SPMiniPoll miniPoll = comment.getMiniPoll();
    Assert.notNull(miniPoll, "Invaild mesasgeId");
    
    SPMiniPollResult result = miniPoll.getResult();
    
    if (result == null) {
      /* first user who is doing the poll */
      result = new SPMiniPollResult(miniPoll.getOptions().size());
      miniPoll.setResult(result);
    }
    
    /* get the options */
    List<Integer> options = (List<Integer>) params[2];
    Assert.notEmpty(options, "No answer is present");
    result.updateResult(options, user.getId());
    
    // updating the message
    // saving the group discussion in the DB
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    final Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.PARAM_OPERATION, Operation.VOTE_MINIPOLL);
    payload.put(Constants.PARAM_GROUP_DISCUSSION, groupDiscussion.getId());
    payload.put(Constants.PARAM_COMMENTID, comment.getCid());
    payload.put(Constants.PARAM_MINIPOLL_RESULT, result);
    
    // get the list of members to send the update to
    List<String> memberIds = new ArrayList<String>(groupDiscussion.getMemberIds());
    memberIds.remove(user.getId());
    
    eventGateway.sendEvent(MessageEventRequest.newEvent(ActionType.GroupDiscussion, memberIds,
        payload, user.getCompanyId()));
    
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_MINIPOLL_RESULT, result);
    return response;
    
  }
  
  /**
   * <code>updateMiniPoll</code> method capture the update the minipoll from the user.
   * 
   * @param user
   *          who is giving the mini poll
   * @param params
   *          contains the message id and answer selections.
   * @return the spresponse.
   */
  public SPResponse updateMiniPoll(User user, Object[] params) {
    String gdId = (String) params[0];
    
    GroupDiscussion groupDiscussion = getGroupDiscussion(gdId, user);
    
    int commentId = (int) params[1];
    
    Assert.isTrue(commentId < groupDiscussion.getComments().size(), "Invalid Comment Id");
    Comment comment = groupDiscussion.getComments().get(commentId);
    
    /*
     * get the miniPoll *.
     */
    SPMiniPoll miniPoll = comment.getMiniPoll();
    Assert.notNull(miniPoll, "Invaild mesasgeId");
    
    SPMiniPollResult result = miniPoll.getResult();
    
    if (result == null) {
      /* first user who is doing the poll */
      result = new SPMiniPollResult(miniPoll.getOptions().size());
      miniPoll.setResult(result);
    }
    
    String updateType = (String) params[2];
    
    final Map<String, Object> payload = new HashMap<String, Object>();
    switch (updateType) {
    case "end":
      miniPoll.setEndDate(LocalDateTime.now());
      payload.put(Constants.PARAM_END_DATE, miniPoll.getEndDate());
      break;
    case "share":
      miniPoll.setHideResults(false);
      payload.put(Constants.PARAM_MINIPOLL_SHARE, miniPoll.isHideResults());
      
    default:
      break;
    }
    // updating the message
    // saving the group discussion in the DB
    groupDiscussionFactory.updateGroupDiscussion(groupDiscussion);
    
    payload.put(Constants.PARAM_OPERATION, Operation.VOTE_MINIPOLL);
    payload.put(Constants.PARAM_GROUP_DISCUSSION, groupDiscussion.getId());
    payload.put(Constants.PARAM_COMMENTID, comment.getCid());
    payload.put(Constants.PARAM_MINIPOLL_RESULT, result);
    
    // get the list of members to send the update to
    List<String> memberIds = new ArrayList<String>(groupDiscussion.getMemberIds());
    memberIds.remove(user.getId());
    
    eventGateway.sendEvent(MessageEventRequest.newEvent(ActionType.GroupDiscussion, memberIds,
        payload, user.getCompanyId()));
    
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_MINIPOLL_RESULT, result);
    return response;
    
  }
  
}
