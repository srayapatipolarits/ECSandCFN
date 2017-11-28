package com.sp.web.controller.dashboard;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.audit.Audit;
import com.sp.web.form.feed.DashboardMessageForm;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The controller for all the dashboard related functionalities.
 */
@Controller
@Audit(type = ServiceType.DASHBOARD)
public class DashboardController {
  
  @Autowired
  DashboardControllerHelper helper;
  
  /**
   * Controller method to get all the tasks for the logged in user.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get tasks request
   */
  @RequestMapping(value = "/dashboard/getTasks", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.PRISM_360)
  public SPResponse getTasks(Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::getTasks, token);
  }
  
  /**
   * Controller method to get the top five notes and feedback for the logged in user.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get notes and feedback list
   */
  @RequestMapping(value = "/dashboard/getNotesAndFeedback", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getNotesAndFeedback(Authentication token) {
    // process the get for the notes and feedback for the dashboard
    return process(helper::getNotesAndFeedback, token);
  }
  
  /**
   * Controller method to get the all the development feedbacks requested by the user.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get notes and feedback list
   */
  @RequestMapping(value = "/dashboard/getDevelopmentFeedbacks", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDevelopmentFeedbacks(Authentication token) {
    // process the get for the getDevelopmentFeedbacks and feedback for the dashboard
    return process(helper::getDevelopmentFeedbacks, token);
  }
  
  /**
   * Controller method to get the users latest news feed.
   * 
   * @param token
   *          - logged in user
   * @return the news feed for the user
   */
  @RequestMapping(value = "/dashboard/getNewsFeed", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getNewsFeed(@RequestParam(defaultValue = "0") int startIdx, Authentication token) {
    return process(helper::getNewsFeed, token, startIdx);
  }
  
  /**
   * Get the user's news feed.
   * 
   * @param startIdx
   *          - staring index
   * @param userEmail
   *          - user email
   * @param token
   *          - logged in user
   * @return the response to the get user news feed request
   */
  @RequestMapping(value = "/dashboard/getUserNewsFeed", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserNewsFeed(@RequestParam(defaultValue = "0") int startIdx,
      @RequestParam(required = false) String userEmail, Authentication token) {
    return process(helper::getUserNewsFeed, token, startIdx, userEmail);
  }
  
  /**
   * The controller method for the user profile feed.
   * 
   * @param startIdx
   *          - start index
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the user profile feed
   */
  @RequestMapping(value = "/dashboard/getUserProfileFeed", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserProfileFeed(@RequestParam(defaultValue = "0") int startIdx,
      @RequestParam String userId, Authentication token) {
    return process(helper::getUserProfileFeed, token, startIdx, userId);
  }
  
  /**
   * Get the announcements for the given company.
   * 
   * @param startIdx
   *          - start index
   * @param token
   *          - logged in user
   * @return the response to the get announcements
   */
  @RequestMapping(value = "/dashboard/getAnnouncements", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAnnouncements(@RequestParam(defaultValue = "0") int startIdx,
      Authentication token) {
    return process(helper::getAnnouncements, token, startIdx);
  }
  
  /**
   * Controller method to create a new dashboard message.
   * 
   * @param form
   *          - message form
   * @param token
   *          - logged in user
   * @return the response to the add message
   */
  @RequestMapping(value = "/dashboard/message/new", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse newDashboardMessage(@RequestBody DashboardMessageForm form, Authentication token) {
    // process the get for the notes and feedback for the dashboard
    return process(helper::newDashboardMessage, token, form);
  }
  
  /**
   * Controller method to get the dashboard message details.
   * 
   * @param messageId
   *          - message id
   * @param commentsOnly
   *          - flag for comments only
   * @param token
   *          - logged in user
   * @return the response to the get details call
   */
  @RequestMapping(value = "/dashboard/message/details", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getDashboardMessageDetails(@RequestParam String messageId,
      @RequestParam(defaultValue = "false") boolean commentsOnly,
      @RequestParam(defaultValue = "false") boolean msgDetailsPage, Authentication token) {
    return process(helper::getDashboardMessageDetails, token, messageId, commentsOnly,
        msgDetailsPage);
  }
  
  /**
   * Controller method to update the dashboard message.
   * 
   * @param form
   *          - dashboard message form
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/dashboard/message/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateDashboardMessage(@RequestBody DashboardMessageForm form,
      Authentication token) {
    return process(helper::updateDashboardMessage, token, form);
  }
  
  /**
   * Controller method to add a comment to the dashboard message.
   * 
   * @param form
   *          - message form
   * @param token
   *          - logged in user
   * @return the response to the add comment request
   */
  @RequestMapping(value = "/dashboard/message/addComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addCommentDashboardMessage(@RequestBody DashboardMessageForm form,
      Authentication token) {
    return process(helper::addCommentDashboardMessage, token, form);
  }
  
  /**
   * Controller method to like a dashboard message.
   * 
   * @param messageId
   *          - message id
   * @param doLike
   *          - flag to indicate like or remove like
   * @param cid
   *          - comment id
   * @param childCid
   *          - Child comment id
   * @param token
   *          - logged in user
   * @return the response to the message like
   */
  @RequestMapping(value = "/dashboard/message/like", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse likeDashboardMessage(@RequestParam String messageId,
      @RequestParam(defaultValue = "true") boolean doLike,
      @RequestParam(defaultValue = "-1") int cid, @RequestParam(defaultValue = "-1") int childCid,
      Authentication token) {
    final DashboardMessageForm form = new DashboardMessageForm(messageId);
    form.setDoLike(doLike);
    form.setCid(cid);
    form.setChildCid(childCid);
    return process(helper::likeDashboardMessage, token, form);
  }
  
  /**
   * Controller method to get the list of users who have liked the message.
   * 
   * @param messageId
   *          - message id
   * @param cid
   *          - comment id
   * @param childCid
   *          - Child comment id
   * @param token
   *          - user
   * @return the response to the liked users
   */
  @RequestMapping(value = "/dashboard/message/likeUserInfo", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse dashboardMessageLikeUserInfo(@RequestParam String messageId,
      @RequestParam(defaultValue = "-1") int cid, @RequestParam(defaultValue = "-1") int childCid,
      Authentication token) {
    return process(helper::dashboardMessageLikeUserInfo, token, messageId, cid, childCid);
  }
  
  /**
   * Delete the comment.
   * 
   * @param messageId
   *          - message id
   * @param cid
   *          - comment id
   * @param childCid
   *          - child comment id
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/dashboard/message/deleteComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteComment(@RequestParam String messageId,
      @RequestParam(defaultValue = "-1") int cid, @RequestParam(defaultValue = "-1") int childCid,
      Authentication token) {
    return process(helper::deleteComment, token, messageId, cid, childCid);
  }
  
  /**
   * Controller method to delete a dashboard message.
   * 
   * @param messageId
   *          - message id
   * @param token
   *          - logged in user
   * @return the response to the message delete request
   */
  @RequestMapping(value = "/dashboard/message/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteDashboardMessage(@RequestParam String messageId, Authentication token) {
    return process(helper::deleteDashboardMessage, token, new DashboardMessageForm(messageId));
  }
  
  /**
   * Controller method to follow unfollow dashboard messages.
   * 
   * @param messageId
   *          - message id
   * @param follow
   *          - flag to indicate follow or unfollow
   * @param token
   *          - logged in user
   * @return the response to the follow unfollow request
   */
  @RequestMapping(value = "/dashboard/message/followUnfollow", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse followUnfollowDashboardMessage(@RequestParam String messageId,
      @RequestParam boolean follow, Authentication token) {
    return process(helper::followUnfollowDashboardMessage, token, messageId, follow);
  }
  
  /**
   * Controller method to block unblock commenting.
   * 
   * @param messageId
   *          - message id
   * @param blockCommenting
   *          - flag to indicate block or unblock
   * @param token
   *          - logged in user
   * @return the response to the block unblock request
   */
  @RequestMapping(value = "/dashboard/message/blockCommenting", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse blockCommenting(@RequestParam String messageId,
      @RequestParam boolean blockCommenting, Authentication token) {
    return process(helper::blockCommenting, token, messageId, blockCommenting);
  }
  
  /**
   * View For Home Page.
   * 
   */
  @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
  public String getDashboardView(Authentication token) {
    return "dashboard";
  }
  
  /**
   * View For Message Detail Page.
   * 
   */
  @RequestMapping(value = "/dashboard/message", method = RequestMethod.GET)
  public String getMessageDetailPage(Authentication token) {
    return "messageDetail";
  }
  
  /**
   * getCommentsDetails will return comments for the public channels.
   * 
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/dashboard/nfpc", method = RequestMethod.GET)
  public String getCommentsDetails(Authentication authentication) {
    return "pcComments";
  }
  
  /**
   * profile page.
   * 
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/dashboard/profile", method = RequestMethod.GET)
  public String getProfileDetailDashboard(Authentication authentication) {
    return "dashboardProfileDetail";
  }
  
  /**
   * View For Group Page.
   * 
   */
  @RequestMapping(value = "/dashboardUserAnalysis", method = RequestMethod.GET)
  public String getDashboardUser(Authentication token) {
    return "dashboardUser";
  }
  
  /**
   * View For Listing Page.
   * 
   */
  @RequestMapping(value = "/dashboard/groups", method = RequestMethod.GET)
  public String getDashboardListingPage(Authentication token) {
    return "dashboardList";
  }
  
  /**
   * View For Notification POP UP.
   * 
   */
  @RequestMapping(value = "/notification/email", method = RequestMethod.GET)
  public String getNotificationEmail(Authentication token) {
    return "notificationEmail";
  }
  
  @RequestMapping(value = "/dashboard/feedbackReply", method = RequestMethod.GET)
  public String getfeedbackReply(Authentication token) {
    return "feedbackReply";
  }
  
  /**
   * answerMiniPoll method will answer the mini poll by the user.
   * 
   * @param messageId
   *          of the mini poll
   * @param selection
   *          contains the result.
   * @param authentication
   *          is the user who has answered the mini polls.
   * @return the sprespones.
   */
  @RequestMapping(value = "/dashboard/message/answerMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse answerMiniPoll(@RequestParam String messageId,
      @RequestParam List<Integer> selection, Authentication authentication) {
    return process(helper::answerMiniPoll, authentication, messageId, selection);
  }
  
  /**
   * endMiniPoll method will answer the end poll by the user.
   * 
   * @param messageId
   *          of the mini poll
   * @param authentication
   *          is the user who has answered the mini polls.
   * @return the sprespones.
   */
  @RequestMapping(value = "/dashboard/message/endMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse endMiniPoll(@RequestParam String messageId, Authentication authentication) {
    return process(helper::updateMiniPoll, authentication, messageId, "end");
  }
  
  /**
   * shareMiniPoll method will answer the end poll by the user.
   * 
   * @param messageId
   *          of the mini poll
   * @param authentication
   *          is the user who has answered the mini polls.
   * @return the sprespones.
   */
  @RequestMapping(value = "/dashboard/message/shareMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareMiniPoll(@RequestParam String messageId, Authentication authentication) {
    return process(helper::updateMiniPoll, authentication, messageId, "share");
  }
  
  /**
   * Get the article for the user from the users prism portrait.
   * 
   * @param authentication
   *          - logged in user
   * @return the article for the user
   */
  @RequestMapping(value = "/dashboard/learning/getArticle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getArticle(Authentication authentication) {
    return process(helper::getPrismArticle, authentication);
  }
  
  /**
   * getGoalsInProgress service will return the goals in progress for the user.
   * 
   * @param authentication
   *          - logged in user.
   * @return the spresponse.
   */
  @RequestMapping(value = "/dashboard/goalsInProgress",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGoalsInProgress(Authentication authentication) {
    return process(helper::getGoalsInProgress,authentication);
  }
}
