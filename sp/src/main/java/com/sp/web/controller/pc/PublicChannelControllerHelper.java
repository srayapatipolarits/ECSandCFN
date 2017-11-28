package com.sp.web.controller.pc;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PrismPortraits;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.pc.PublicChannelDTO;
import com.sp.web.dto.pc.PublicChannelNewsFeedDTO;
import com.sp.web.exception.SPException;
import com.sp.web.form.Operation;
import com.sp.web.form.pchannel.PublicChannelForm;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.pc.PublicChannelFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <code>PublicChanneControllerHelper</code> is the helper class for public channel request.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class PublicChannelControllerHelper {
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private PublicChannelFactory publicChannelFactory;
  
  @Autowired
  private NewsFeedFactory newsFactory;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PublicChannelHelper channelHelper;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationProcessor;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  /**
   * getPublicChannel will return the public channel for the passed ref Id. In case there are no
   * public channel a new public channel is created.
   * 
   * @param user
   *          is the logged in user.
   * @param param
   *          contains the pc ref id.
   * @return the SPResponse.
   */
  public SPResponse getPublicChannel(User user, Object[] param) {
    
    String pcRefId = (String) param[0];
    Assert.hasText(pcRefId, "PcRefId is not present ");
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    
    SPResponse response = new SPResponse();
    if (publicChannel != null) {
      PublicChannelDTO publicChannelDTO = new PublicChannelDTO(publicChannel,
          Constants.PUBLIC_CHANNEL_COMMENT_LIMIT, user);
      response.add(Constants.PARAM_PUBLIC_CHANNEL, publicChannelDTO);
    }
    
    return response;
  }
  
  /**
   * createPubliChannel method will create the public channel and add the public channel to users
   * news fead.
   * 
   * @param user
   *          logged user who has created the public channel.
   * @param params
   *          contains the public channel form.
   * @return the response.
   */
  public SPResponse createPubliChannel(User user, Object[] params) {
    
    PublicChannelForm channelForm = (PublicChannelForm) params[0];
    channelForm.validate();
    
    /* create the public channel */
    PublicChannel publicChannel = new PublicChannel(channelForm.getPcRefId(), user.getCompanyId(),
        channelForm.getSpFeature());
    Comment newComment = newComment(channelForm, publicChannel, user);
    publicChannel.addComment(newComment);
    publicChannel.getNotificationEmails().add(user.getId());
    publicChannel.setTitle(channelForm.getTitle());
    publicChannel.setCompanyId(user.getCompanyId());
    final String parentRefId = channelForm.getParentRefId();
    switch (channelForm.getSpFeature()) {
    case OrganizationPlan:
      ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(parentRefId);
      Assert.notNull(actionPlan, "Learning Program not found.");
      publicChannel.setName(actionPlan.getName());
      publicChannel.setText(actionPlan.getDescription());
      publicChannel.setParentRefId(parentRefId);
      /* find the users associated witht he organization plan. */
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          parentRefId, user.getCompanyId());
      if (capSettings == null) {
        throw new SPException("Invalid parentRefId sent for creating public channel name : "
            + channelForm.getTitle());
      }
      publicChannel.getMemberIds().addAll(capSettings.getMemberIds());
      break;
    case Erti:
      
      /* find all the users who are have this practice area as selected */
      List<String> memberIds = goalFactory.getUsersForGoals(publicChannel.getPcRefId(),
          user.getCompanyId());
      
      /* add the member id to the public channel so that it is avaiable to them. */
      publicChannel.getMemberIds().addAll(memberIds);
      break;
    
    case Competency:
      CompetencyProfileDao competencyProfile = competencyFactory.getCompetencyProfile(parentRefId);
      Assert.notNull(competencyProfile, "Competency profile not found.");
      publicChannel.setName(competencyProfile.getName());
      publicChannel.setParentRefId(parentRefId);
      List<String> users = userRepository
          .findAllMembersByCompetencyProfileId(publicChannel.getParentRefId()).stream()
          .map(User::getId).collect(Collectors.toList());
      publicChannel.getMemberIds().addAll(users);
      break;
    case Prism:
      
      /*
       * in case of Prism, the Prism will be the parent reference and refId will be the sub module
       * name.
       */
      publicChannel.setParentRefId(channelForm.getSpFeature().toString());
      PrismPortraits prismPortraitModule = PrismPortraits.valueOf(channelForm.getPcRefId());
      publicChannel.setPcRefId(prismPortraitModule.toString());
      publicChannel.setAllCompany(true);
      break;
    
    default:
      publicChannel.setPcRefId(channelForm.getSpFeature().toString());
      publicChannel.setAllCompany(true);
      break;
    }
    
    /* save the public channel to the database. */
    publicChannelFactory.updatePublicChannel(publicChannel);
    
    /* Add to the dashboard newsfeed */
    NewsFeedHelper newsFeedHelper = newsFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    newsFeedHelper.createNewsFeed(NewsFeedType.PublicChannel, publicChannel);
    
    /* send event to users */
    return new SPResponse().isSuccess();
  }
  
  /**
   * newComment method will create the new comment with id set as the index of the comment present
   * in the public channel.
   * 
   * @param channelForm
   *          public channel form containing the comment.
   * @param publicChannel
   *          of the user.
   * @return the comment.
   */
  private Comment newComment(PublicChannelForm channelForm, PublicChannel publicChannel, User user) {
    Comment newComment = channelForm.getComment().newComment(user);
    return newComment;
  }
  
  /**
   * <code>followPublicChannel</code> method will follow or unfollow user from public channel.
   * 
   * @param user
   *          logged in user who will follow or unfollow the user.
   * @param params
   *          contains the flag for follow or unfollow.
   * @return the response.
   */
  public SPResponse followUnfollowPublicChannel(User user, Object[] params) {
    boolean follow = (boolean) params[0];
    String pcRefId = (String) params[1];
    
    Assert.hasText(pcRefId, "Invalid pcRefId");
    
    channelHelper.followUnfollowPubliChannel(user, follow, pcRefId);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>addComment</code> method will add the comment to the public channel
   * 
   * @param user
   *          who is adding the comment
   * @param params
   *          contains the message form.
   * @return the spresponse.
   */
  public SPResponse addComment(User user, Object[] params) {
    
    PublicChannelForm form = (PublicChannelForm) params[0];
    
    form.validateUpdate();
    /* Fetch the pubilc channel */
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(form.getPcRefId(),
        user.getCompanyId());
    
    Assert.notNull(publicChannel,
        "No public channel found for to add comment for refernce " + form.getPcRefId());
    
    Comment newComment = newComment(form, publicChannel, user);
    final int cid = form.getComment().getCid();
    if (cid >= 0) {
      Comment existingComment = publicChannel.getComment(cid);
      existingComment.addComment(newComment);
      /* add the user to the notificationlist */
      publicChannel.getNotificationEmails().add(newComment.getUser().getId());
    } else {
      /* add the comment to the public channel */
      publicChannel.addComment(newComment);
      /* add the user to the notificationlist */
      publicChannel.getNotificationEmails().add(newComment.getUser().getId());
    }
    
    /* update the public channel */
    publicChannelFactory.updatePublicChannel(publicChannel);
    
    final Map<String, Object> notificationParams = new HashMap<String, Object>();
    notificationParams.put(Constants.PARAM_COMMENT, newComment);
    notificationParams.put(Constants.PARAM_USER, user);
    notificationParams.put(Constants.PARAM_PUBLIC_CHANNEL, publicChannel);
    
    /* check any tagged members for notification */
    if (!CollectionUtils.isEmpty(form.getTaggedMemberIds())) {
      
      form.getTaggedMemberIds().forEach(
          uSt -> {
            User u = userFactory.getUser(uSt);
            if (u != null) {
              notificationProcessor.process(NotificationType.PublicChannelCommentTag, user, u,
                  notificationParams);
            }
          });
    }
    
    /* send the notification to the member who has commented in the public channel. */
    
    /* get the follow member list */
    if (!CollectionUtils.isEmpty(publicChannel.getNotificationEmails())) {
      
      publicChannel.getNotificationEmails().forEach(
          uSt -> {
            if (form.getTaggedMemberIds() == null || !form.getTaggedMemberIds().contains(uSt)) {
              User u = userFactory.getUser(uSt);
              if (u != null && !u.getEmail().equalsIgnoreCase(user.getEmail())) {
                notificationParams.put(
                    Constants.PARAM_SUBJECT,
                    MessagesHelper.getMessage("notification.subject.PublicChannelComment",
                        u.getLocale(), user.getFullNameOrEmail(), publicChannel.getTitle()));
                notificationProcessor.process(NotificationType.PublicChannelComment, user, u,
                    notificationParams);
              }
            }
            
          });
    }
    
    /* update the news feed so that it appears at the top of the news feed. */
    NewsFeedHelper newsFeedHelper = newsFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    newsFeedHelper.updateNewsFeed(publicChannel);
    
    /* notify the updates to other users. */
    channelHelper.sendSseUpdate(publicChannel, Operation.ADD_COMMENT);
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_COMMENT, newComment);
    return response.isSuccess();
  }
  
  /**
   * Helper method to delete the dashboard message comment.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the message comment delete request
   */
  public SPResponse deleteComment(User user, Object[] params) {
    
    String pcRefId = (String) params[0];
    Assert.hasText(pcRefId, "PcRefId id required.");
    
    int cid = (int) params[1];
    int childCid = (int) params[2];
    
    /* Fetch the pubilc channel */
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    
    Assert.notNull(publicChannel, "No public channel found for to add comment for refernce "
        + pcRefId);
    
    Assert.isTrue(cid >= 0, "Comment id required.");
    Comment comment = publicChannel.getComment(cid);
    
    if (childCid >= 0) {
      Comment childComment = comment.getComment(childCid);
      isOwnerOrCommentOwner(childComment, user);
      comment.deleteComment(childComment);
    } else {
      isOwnerOrCommentOwner(comment, user);
      publicChannel.deleteComment(comment);
    }
    
    publicChannelFactory.updatePublicChannel(publicChannel);
    
    /* update the news feed so that it appears at the top of the news feed. */
    NewsFeedHelper newsFeedHelper = newsFactory.getCompanyNewsFeedHelper(user.getCompanyId());
    newsFeedHelper.updateNewsFeed(publicChannel);
    
    /* notify the updates to other users. */
    Map<String, Object> payLoadMap = new HashMap<String, Object>();
    payLoadMap.put(Constants.PARAM_DASHBOARD_MESSAGE_COMMENT_ID, cid);
    payLoadMap.put(Constants.PARAM_DASHBOARD_MESSAGE_CHILD_COMMENT_ID, childCid);
    payLoadMap.put(Constants.PARAM_USER_ID, user.getId());
    payLoadMap.put(Constants.PARAM_PUBLIC_CHANNEL_ID, publicChannel.getRefId());
    payLoadMap.put(Constants.PARAM_PUBLIC_CHANNEL_REF_ID, publicChannel.getPcRefId());
    channelHelper.sendSseUpdate(publicChannel, Operation.DELETE_COMMENT, payLoadMap);
    
    return new SPResponse().add(payLoadMap);
  }
  
  private void isOwnerOrCommentOwner(Comment childComment, User user) {
    Assert.isTrue(childComment.isOwner(user.getId()), "Unauthorized request.");
  }
  
  /**
   * getAllComments will return all the comments of the public channel.
   * 
   * @param user
   *          is the logged in user.
   * @param param
   *          contains the pc ref id.
   * @return the SPResponse.
   */
  public SPResponse getAllComments(User user, Object[] param) {
    
    String pcRefId = (String) param[0];
    boolean all = (boolean) param[1];
    Assert.hasText(pcRefId, "PcRefId is not present ");
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefId,
        user.getCompanyId());
    Assert.notNull(publicChannel, "Invalid request, Public Channel not found! ");
    SPResponse response = new SPResponse();
    
    List<Comment> collect = all ? publicChannel.getComments() : publicChannel.getComments()
        .stream().limit(50).collect(Collectors.toList());
    response.add(Constants.PARAM_COMMENT, collect);
    return response;
  }
  
  /**
   * getCommentsDetail method will return all the comments detail for the public channel.
   * 
   * @param user
   *          logged in user
   * @param param
   *          containis the parameter pcid.
   * @return the the SPResponse.
   */
  public SPResponse getCommentsDetails(User user, Object[] param) {
    
    String pcRefIdParam = (String) param[0];
    Assert.hasText(pcRefIdParam, "PCid is blank");
    
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(pcRefIdParam,
        user.getCompanyId());
    Assert.notNull(publicChannel, "No community form exist!!");
    
    SPResponse response = new SPResponse();
    /* check if public channel applicable for user */
    if (publicChannel != null && publicChannel.isApplicable(user.getId())) {
      PublicChannelNewsFeedDTO publicChannelNewsFeedDTO = new PublicChannelNewsFeedDTO(
          publicChannel, user, 50);
      switch (publicChannel.getSpFeature()) {
      case Erti:
        SPGoal goal = goalsFactory.getGoal(publicChannel.getPcRefId());
        publicChannelNewsFeedDTO.setText(goal.getDescription());
        break;
      case PrismLens:
        publicChannelNewsFeedDTO.setText(MessagesHelper
            .getMessage("dashboard.publicchannel.description." + publicChannel.getSpFeature()));
        break;
      case Prism:
        String pcRefId = publicChannel.getPcRefId();
        publicChannelNewsFeedDTO.setText(MessagesHelper
            .getMessage("dashboard.publicchannel.description." + pcRefId));
        publicChannelNewsFeedDTO.setTitle(MessagesHelper.getMessage("dashboard.publicchannel.name."
            + pcRefId));
        break;
      case RelationShipAdvisor:
        publicChannelNewsFeedDTO.setText(MessagesHelper
            .getMessage("dashboard.publicchannel.description." + publicChannel.getSpFeature()));
        publicChannelNewsFeedDTO.setTitle(MessagesHelper.getMessage("dashboard.publicchannel.name."
            + publicChannel.getSpFeature()));
        break;
      default:
        break;
      }
      if (!publicChannel.getUnfollowMemberIds().contains(user.getId())) {
        publicChannelNewsFeedDTO.setFollow(true);
      }
      response.add(Constants.PARAM_PUBLIC_CHANNEL, publicChannelNewsFeedDTO);
    } else {
      throw new SPException("Community form not applicable for user");
    }
    
    return response;
  }
}
