package com.sp.web.service.pc;

import com.sp.web.Constants;
import com.sp.web.dto.pc.PublicChannelDTO;
import com.sp.web.form.Operation;
import com.sp.web.model.User;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PublicChannelHelper class is the helper class for the public channel.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PublicChannelHelper {
  
  @Autowired
  private PublicChannelFactory publicChannelFactory;
  
  @Autowired
  private NewsFeedFactory newsFactory;
  
  @Autowired
  EventGateway eventGateway;
  
  /**
   * followUnfollowPubliChannel method will follow or unflollow the list of users from the public
   * channel.
   * 
   * @param userIds
   *          list of users.
   * @param follow
   *          flag whether follow or unfollow
   * @param publicChannel
   *          public channel.
   */
  public void followUnfollowPubliChannel(List<String> userIds, boolean follow,
      PublicChannel publicChannel) {
    /**
     * add the user ids in case the public channel is only for some users, if pc is for all members
     * of the company then no need to enter.
     */
    if (follow) {
      /* remove from the unfollow list in case user has followed it previously unfollowed. */
      userIds.forEach(publicChannel::addMember);
    } else {
      userIds.forEach(publicChannel::unfollow);
    }
    
    /* update the public channel. */
    publicChannelFactory.updatePublicChannel(publicChannel);
  }
  
  /**
   * followUnfollow the user from the public channel.
   * 
   * @param user
   *          to be followed or unfoolowed
   * @param follow
   *          true or false
   * @param pcRefId
   *          pcRefId.
   */
  public void followUnfollowPubliChannel(User user, boolean follow, String pcRefId) {
    
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    
    /* no public channel exist so far, return. */
    if (publicChannel == null) {
      return;
    }
    
    List<String> list = new ArrayList<>();
    list.add(user.getId());
    followUnfollowPubliChannel(list, follow, publicChannel);
  }
  
  /**
   * followUnfollow the user from the public channel.
   * 
   * @param user
   *          to be followed or unfoolowed
   * @param follow
   *          true or false
   * @param parentRefId
   *          pcRefId.
   */
  public void followUnfollowPubliChannelByParent(User user, boolean follow, String parentRefId) {
    
    List<PublicChannel> pcs = publicChannelFactory.getPubliChannelsByParentNoCache(parentRefId,
        user.getCompanyId());
    final List<String> list = new ArrayList<>();
    list.add(user.getId());
    if (CollectionUtils.isNotEmpty(pcs)) {
      pcs.stream().forEach(pc -> followUnfollowPubliChannel(list, follow, pc));
    }
  }
  
  /**
   * deletePubliChannel will delete all the public channel associated with the parent reference Id.
   * 
   * @param companyId
   *          comapnyId
   * @param parentRefId
   *          parent public channel.
   */
  public void deletePublicChannelByParent(String companyId, String parentRefId) {
    List<PublicChannel> pcs = publicChannelFactory.getPubliChannelsByParentNoCache(parentRefId,
        companyId);
    if (CollectionUtils.isNotEmpty(pcs)) {
      NewsFeedHelper newsFeedHelper = newsFactory.getCompanyNewsFeedHelper(companyId);
      pcs.stream().forEach(pc -> deletePublicChannel(newsFeedHelper, pc));
    }
  }
  
  /**
   * Delete the public channels with the given parent reference id.
   * 
   * @param parentRefId
   *          - parent reference id
   * @return the list of public channels
   */
  public void deletePublicChannelByParent(String parentRefId) {
    List<PublicChannel> pcList = publicChannelFactory.getPubliChannelsByParentNoCache(parentRefId);
    pcList.forEach(this::deletePublicChannel);
  }
  
  /**
   * Delete all the public channels for the given pc reference id.
   * 
   * @param pcRefId
   *          - public channel reference id
   */
  public void deletePublicChannel(String pcRefId) {
    List<PublicChannel> pcList = publicChannelFactory.getPubliChannelsNoCache(pcRefId);
    pcList.forEach(this::deletePublicChannel);
  }
  
  /**
   * deletePubliChannel will delete all the public channel associated with the parent reference Id.
   * 
   * @param companyId
   *          comapnyId
   * @param pcRefId
   *          public channel.
   */
  public void deletePublicChannel(String companyId, String pcRefId) {
    PublicChannel pc = publicChannelFactory.getPubliChannelsNoCache(pcRefId, companyId);
    if (pc == null) {
      return;
    }
    deletePublicChannel(pc);
  }
  
  /**
   * Delete the given public channel.
   * 
   * @param pc
   *          - public channel
   */
  private void deletePublicChannel(PublicChannel pc) {
    NewsFeedHelper newsFeedHelper = newsFactory.getCompanyNewsFeedHelper(pc.getCompanyId());
    deletePublicChannel(newsFeedHelper, pc);
  }
  
  /**
   * delete the public channel.
   * 
   * @param newsFeedHelper
   *          to update the news feed.
   * @param pc
   *          public channel.
   */
  public void deletePublicChannel(NewsFeedHelper newsFeedHelper, PublicChannel pc) {
    publicChannelFactory.deletePublicChannel(pc);
    /* delete the news feed associated with the public channel */
    newsFeedHelper.deleteNewsFeed(pc);
    sendSseUpdate(pc, Operation.DELETE);
  }
  
  /**
   * sendUpdate will send the update to the all the other member.
   * 
   * @param publicChannel
   *          in which message was added.
   * @param op
   *          to be performed on the event.
   */
  public void sendSseUpdate(PublicChannel publicChannel, Operation op) {
    sendSseUpdate(publicChannel, op, null);
  }
  
  /**
   * sendUpdate will send the update to the all the other member.
   * 
   * @param publicChannel
   *          in which message was added.
   * @param op
   *          to be performed on the event.
   */
  public void sendSseUpdate(PublicChannel publicChannel, Operation op,
      Map<String, Object> payLoadMap) {
    
    HashMap<String, Object> payload = getMessagePayload(publicChannel, op);
    if (payLoadMap != null) {
      payload.putAll(payLoadMap);
    }
    MessageEventRequest newEvent;
    if (publicChannel.isAllCompany()) {
      newEvent = MessageEventRequest.newEvent(ActionType.PublicChannel,
          publicChannel.getCompanyId(), payload);
      
    } else {
      newEvent = MessageEventRequest.newEvent(ActionType.PublicChannel,
          publicChannel.getMemberIds(), payload, publicChannel.getCompanyId());
    }
    
    try {
      newEvent.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
    } catch (Exception ex) {
      /* catching illegal state exception in case request is coming from thread. */
    }
    
    eventGateway.sendEvent(newEvent);
  }
  
  /**
   * Send the SSE for the given user.
   * 
   * @param user
   *          - user
   * @param publicChannel
   *          - public channel
   * @param op
   *          - operation
   */
  private void sendSseUpdate(User user, PublicChannel publicChannel, Operation op) {
    HashMap<String, Object> payload = getMessagePayload(publicChannel, op);
    
    MessageEventRequest newEvent = MessageEventRequest.newEvent(ActionType.PublicChannel, payload,
        user);
    eventGateway.sendEvent(newEvent);
  }
  
  private HashMap<String, Object> getMessagePayload(PublicChannel publicChannel, Operation op) {
    HashMap<String, Object> payload = new HashMap<>();
    payload.put(Constants.PARAM_OPERATION, op);
    /* check if event sent to all members of a company or closed useres in the public channel */
    switch (op) {
    case NEW_PUBLIC_CHANNEL:
      payload.put(Constants.PARAM_PUBLIC_CHANNEL, new PublicChannelDTO(publicChannel,
          Constants.PUBLIC_CHANNEL_COMMENT_LIMIT));
      break;
    case ADD_COMMENT:
      payload.put(Constants.PARAM_COMMENT, publicChannel.getComments().getFirst());
      payload.put(Constants.PARAM_PUBLIC_CHANNEL_ID, publicChannel.getRefId());
      payload.put(Constants.PARAM_PUBLIC_CHANNEL_REF_ID, publicChannel.getPcRefId());
      break;
    case DELETE:
      payload.put(Constants.PARAM_PUBLIC_CHANNEL_ID, publicChannel.getRefId());
      payload.put(Constants.PARAM_PUBLIC_CHANNEL_REF_ID, publicChannel.getPcRefId());
      break;
    case ADD:
      payload.put(Constants.PARAM_PUBLIC_CHANNEL, new PublicChannelDTO(publicChannel,
          Constants.PUBLIC_CHANNEL_COMMENT_LIMIT));
      break;
    case DELETE_COMMENT:
      
      // sending back the response
      payload.put(Constants.PARAM_OPERATION, Operation.DELETE_COMMENT);
      
      break;
    
    default:
      break;
    }
    return payload;
  }
  
  /**
   * Removing the given user from the public channel.
   * 
   * @param member
   *          - member
   * @param parentRefId
   *          - parent reference id
   */
  public void removeByParentUser(User member, String parentRefId) {
    List<PublicChannel> pcs = publicChannelFactory.getPubliChannelsByParentNoCache(parentRefId,
        member.getCompanyId());
    pcs.forEach(pc -> removeUser(member, pc));
  }
  
  /**
   * Remove the given user from the public channel.
   * 
   * @param user
   *          - user
   * @param pcRefId
   *          - public channel reference
   */
  public void removeUser(User user, String pcRefId) {
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    
    /* no public channel exist so far, return. */
    if (publicChannel == null) {
      return;
    }
    
    removeUser(user, publicChannel);
  }
  
  private void removeUser(User user, PublicChannel publicChannel) {
    final String userId = user.getId();
    if (publicChannel.hasUser(userId)) {
      publicChannel.removeUser(userId);
      publicChannelFactory.updatePublicChannel(publicChannel);
      sendSseUpdate(user, publicChannel, Operation.DELETE);
    }
  }
  
  /**
   * Check if the user is present in the public channel.
   * 
   * @param user
   *          - user
   * @param pcRefId
   *          - pc reference id
   * @return true if present else false
   */
  public boolean hasUser(User user, String pcRefId) {
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    if (publicChannel != null) {
      return publicChannel.hasUser(user.getId());
    }
    return false;
  }
  
  /**
   * Add the user to all the public channels for the given parent reference id.
   * 
   * @param user
   *          - user
   * @param parentRefId
   *          - parent reference id
   */
  public void addByParentUser(User user, String parentRefId) {
    List<PublicChannel> pcs = publicChannelFactory.getPubliChannelsByParentNoCache(parentRefId,
        user.getCompanyId());
    pcs.forEach(pc -> addUser(user, pc));
  }
  
  /**
   * Add the given user to the public channel if the user is not present.
   * 
   * @param user
   *          - user
   * @param pcRefId
   *          - public channel reference id
   */
  public void addUser(User user, String pcRefId) {
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    
    /* no public channel exist so far, return. */
    if (publicChannel == null) {
      return;
    }
    addUser(user, publicChannel);
  }
  
  private void addUser(User user, PublicChannel publicChannel) {
    final String userId = user.getId();
    if (!publicChannel.hasUser(userId)) {
      publicChannel.addMember(userId);
      publicChannelFactory.updatePublicChannel(publicChannel);
      sendSseUpdate(user, publicChannel, Operation.ADD);
    }
  }
}
