package com.sp.web.controller.feedback;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.goal.GoalsControllerHelper;
import com.sp.web.form.FeedbackForm;
import com.sp.web.form.ReferencesForm;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * <code>FeedbackController</code> class will provide the feedback for the user.
 * 
 * @author pradeep
 *
 */
@Controller
public class FeedbackController {

  /** Feedbak controller helper. */
  @Autowired
  private FeedbackControllerHelper feedbackControllerHelper;

  @Autowired
  private GoalsControllerHelper goalControllerHelper;

  /**
   * <code>getFeedbackTeam</code> method will return the feedback team for the current user.
   * 
   * @param token
   *          logged in user.
   * @return the growth feedback team.
   */
  @RequestMapping(value = "/feedback/getFeedbackTeam", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getFeedbackTeam(Authentication token,
      @RequestParam(required = false) String groupMemberEmail,
      @RequestParam(defaultValue = "false") boolean accountDashboardRequest) {
    return ControllerHelper.process(feedbackControllerHelper::getFeedbackTeam, token,
        groupMemberEmail, accountDashboardRequest);
  }

  /**
   * <code>getDashboardFeedbackRequest</code> method will return the feedback team for the current user.
   * 
   * @param token
   *          logged in user.
   * @return the growth feedback team.
   */
  @RequestMapping(value = "/dashboard/feedback/myrequests", method = RequestMethod.GET)
  @ResponseBody
  @Audit(type = ServiceType.DASHBOARD)
  public SPResponse getDashboardFeedbackRequest(Authentication token,
      @RequestParam(required = false) String groupMemberEmail,
      @RequestParam(defaultValue = "false") boolean accountDashboardRequest) {
    return ControllerHelper.process(feedbackControllerHelper::getFeedbackTeam, token,
        groupMemberEmail, accountDashboardRequest);
  }
  
  /**
   * <code>getAllMembersToInvite</code> method will return the all members present in the same company for that user.
   * 
   * @param token
   *          user name password token
   * @return the all the members present in the company
   */
  @RequestMapping(value = "/feedback/getAllFeedbackMembers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse inviteMembersList(Authentication token,
      @RequestParam(required = false) String groupMemberEmail) {
    return ControllerHelper.process(feedbackControllerHelper::getAllForInviteMembers, token,
        groupMemberEmail);
  }

  /**
   * Controller method to get the feedback status for the given member.
   * 
   * @param token
   *          - logged in user
   * @param userId
   *          - user id 
   * @return
   *    the response to the feedback status
   */
  @RequestMapping(value = "/feedback/getFeedbackStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getFeedbackStatus(Authentication token,
      @RequestParam String userId) {
    return ControllerHelper.process(feedbackControllerHelper::getFeedbackStatus, token,
        userId);
  }
  
  /**
   * <code>sendRequestForFeedback</code> method will send the feedbackRequest to the user.
   * 
   * @param feedbackForm
   *          FeedbackForm for the request.
   * @param token
   *          logged in user.
   * @return the logged in response.
   */
  @RequestMapping(value = "/feedback/submitFeedbackRequest", method = RequestMethod.POST)
  @ResponseBody
  @Audit(actionType = LogActionType.FeedbackInvite, type = ServiceType.PRISM_360)
  public SPResponse inviteUserForFeedback(@Valid FeedbackForm feedbackForm, @Valid ReferencesForm referencesForm,
      Authentication token, @RequestParam(required = false) String groupMemberEmail) {
    return ControllerHelper.process(feedbackControllerHelper::sendFeedbackRequest, token, feedbackForm, referencesForm,
        groupMemberEmail);
  }

  /**
   * <code>getFeedbackDetail</code> method will return the feedbackDetail for the user.
   * 
   * @param token
   *          authenticated user
   * @param feedbackRequestId
   *          feedback requested by id user.
   * @return the feedback user.
   */
  @RequestMapping(value = "/feedback/getFeedbackDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getFeedbackDetail(Authentication token, @RequestParam String feadbackUserId,
      @RequestParam(required = false) String groupMemberEmail) {
    return ControllerHelper.process(feedbackControllerHelper::getFeedbackDetail, token, feadbackUserId,
        groupMemberEmail);
  }

  /**
   * <code>archvieFeedback</code> method will archive the feedback.
   * 
   * @param feedbackUserId
   *          archive feedbackUser id id
   * @param token
   *          logged in user.
   * @return the archived Feedback.
   */
  @RequestMapping(value = "/feedback/archiveFeedback", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse archiveFeedback(@RequestParam String feedbackUserId, Authentication token) {
    return ControllerHelper.process(feedbackControllerHelper::archiveFeedback, token, feedbackUserId);
  }

  @RequestMapping(value = "/feedback/getAllArchiveFeedbacks", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllArchiveFeedbacks(Authentication token) {
    return ControllerHelper.process(feedbackControllerHelper::getAllArchivedFeedbacks, token);
  }

  @RequestMapping(value = "/feedback/feedbackArchive/getRequestDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getArchiveRequestDetails(@RequestParam String feedbackUserArchiveId,
      Authentication token) {
    return ControllerHelper.process(feedbackControllerHelper::getArchiveRequestDetails, token, feedbackUserArchiveId);
  }

  /**
   * View For Home Page.
   * 
   */
  @RequestMapping(value = "/feedback", method = RequestMethod.GET)
  public String validateFeedbackHomeGroup(Authentication token) {
    return "feedbackListing";
  }

  /**
   * View For Submit request.
   * 
   */
  @RequestMapping(value = "/feedback/submitRequest", method = RequestMethod.GET)
  public String validateFeedbackSubmitRequest(Authentication token) {
    return "feedbackSubmitRequest";
  }

  /**
   * View For archiveMemberScreen.
   * 
   */
  @RequestMapping(value = "/feedback/archiveMemberScreen", method = RequestMethod.GET)
  public String validatearchiveMemberScreen(Authentication token) {

    return "archiveFeedbackMemberScreen";
  }

  /**
   * View For archiveListing.
   * 
   */
  @RequestMapping(value = "/feedback/archive", method = RequestMethod.GET)
  public String validatearchiveListing(Authentication token) {
    return "archiveListing";
  }

  /**
   * View For archiveDetail.
   * 
   */
  @RequestMapping(value = "/feedback/archiveDetail", method = RequestMethod.GET)
  public String validatearchiveDetail(Authentication token,
      @RequestParam(required = false) String theme) {

    return "archiveFeedbackDetail";
  }

  /**
   * View For archiveDetail.
   * 
   */
  @RequestMapping(value = "/feedback/themeView", method = RequestMethod.GET)
  public String validateThemeView(Authentication token,
      @RequestParam(required = false) String theme) {
    return "themeDetail";
  }

  /**
   * View For archiveDetail.
   * 
   */
  @RequestMapping(value = "/feedback/reminder/feedsendReminder", method = RequestMethod.GET)
  public String validatefeedsendReminder(Authentication token,
      @RequestParam(required = false) String theme) {
    return "reminderfeed";
  }
  
  @RequestMapping(value = "/feedback/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteFeedbackRequest(@RequestParam String feedbackUserId,
      Authentication authentication) {
    return ControllerHelper.process(
        feedbackControllerHelper::deleteFeedbackRequest, authentication, feedbackUserId);
  }
}
