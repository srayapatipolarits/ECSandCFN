package com.sp.web.controller.discussion;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.audit.Audit;
import com.sp.web.form.discussion.GroupDiscussionForm;
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
 * <code>GroupDiscussionController</code> class will provide the interfaces for the user group
 * discussions.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class GroupDiscussionController {
  
  @Autowired
  private GroupDiscussionControllerHelper helper;
  
  /**
   * Controller method to get all the group discussions.
   * 
   * @param token
   *          - logged in user
   * @param isDashboardRequest
   *          - flag to indicate if it is a dashboard request
   * @return the list of group discussions for the user
   */
  @RequestMapping(value = "/gd/getAll", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse getAll(Authentication token,
      @RequestParam(defaultValue = "false") boolean isDashboardRequest) {
    return process(helper::getAll, token, isDashboardRequest);
  }
  
  /**
   * Controller method to get the details of the group discussion.
   * 
   * @param token
   *          - logged in user
   * @param gdId
   *          - group discussion id
   * @return the group discussion details
   */
  @RequestMapping(value = "/gd/getDetails", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse getDetails(Authentication token, @RequestParam String gdId) {
    return process(helper::getDetails, token, gdId);
  }
  
  /**
   * Controller method to update the group discussion.
   * 
   * @param form
   *          - form for gd update
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/gd/create", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse create(@RequestBody GroupDiscussionForm form, Authentication token) {
    return process(helper::create, token, form);
  }
  
  /**
   * Controller method to update the group discussion.
   * 
   * @param form
   *          - group discussion form
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/gd/update", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse update(@RequestBody GroupDiscussionForm form, Authentication token) {
    return process(helper::update, token, form);
  }
  
  /**
   * Controller method to add members to the group discussion.
   * 
   * @param newName
   *          - new name for the group
   * @param gdId
   *          - group discussion id
   * @param memberIds
   *          - member id list
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/gd/addMember", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse addMember(@RequestParam(required = false) String newName,
      @RequestParam String gdId, @RequestParam(required = false) List<String> memberIds,
      @RequestParam(defaultValue = "false") boolean nameCleared, Authentication token) {
    return process(helper::addMember, token, gdId, memberIds, newName, nameCleared);
  }
  
  /**
   * Controller method for user to leave the group discussion.
   * 
   * @param gdId
   *          - group discussion id
   * @param token
   *          - logged in user
   * @return the response to the leave request
   */
  @RequestMapping(value = "/gd/leaveGroupDiscussion", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse leaveGroupDiscussion(@RequestParam String gdId, Authentication token) {
    return process(helper::leaveGroupDiscussion, token, gdId);
  }
  
  /**
   * Controller method to get the count of unread messages across all the group discussions.
   * 
   * @param token
   *          - logged in user
   * @return the unread count
   */
  @RequestMapping(value = "/gd/getUnreadCount", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse getUnreadCount(Authentication token) {
    return process(helper::getUnreadCount, token);
  }
  
  /**
   * Controller method to change the group name.
   * 
   * @param gdId
   *          - group discussion id
   * @param name
   *          - new name
   * @param token
   *          - logged in user
   * @return the response to the change name request
   */
  @RequestMapping(value = "/gd/renameGroup", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse renameGroup(@RequestParam String gdId, @RequestParam String name,
      Authentication token) {
    return process(helper::renameGroup, token, gdId, name);
  }
  
  /**
   * Controller method to get the member details for a group discussion.
   * 
   * @param gdId
   *          - group discussion id
   * @param token
   *          - user
   * @return the result for the get group member details request
   */
  @RequestMapping(value = "/gd/groupMemberDetails", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse groupMemberDetails(@RequestParam String gdId, Authentication token) {
    return process(helper::groupMemberDetails, token, gdId);
  }
  
  /**
   * Add the user to the filter notification list if the do filter flag is true. Else remove the
   * user from the filter notifications list.
   * 
   * @param gdId
   *          - group discussion id
   * @param doFilter
   *          - do filter flag
   * @param token
   *          - logged in user
   * @return the response to the filter notification request
   */
  @RequestMapping(value = "/gd/filterNotification", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.GROUP_DISCUSSION)
  public SPResponse filterNotification(@RequestParam String gdId, @RequestParam boolean doFilter,
      Authentication token) {
    return process(helper::filterNotification, token, gdId, doFilter);
  }
  
  /**
   * answerMiniPoll method will answer the mini poll by the user.
   * 
   * @param gdId
   *          of the mini poll
   * @param selection
   *          contains the result.
   * @param authentication
   *          is the user who has answered the mini polls.
   * @return the sprespones.
   */
  @RequestMapping(value = "/gd/answerMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse answerMiniPoll(@RequestParam String gdId, @RequestParam int commentId, @RequestParam List<Integer> selection,
      Authentication authentication) {
    return process(helper::answerMiniPoll, authentication, gdId, commentId, selection);
  }
  
  /**
   * endMiniPoll method will update the mini poll by the user.
   * 
   * @param gdId
   *          of the mini poll 
   * @return the sprespones.
   */
  @RequestMapping(value = "/gd/endMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse endMiniPoll(@RequestParam String gdId, @RequestParam int commentId, Authentication authentication) {
    return process(helper::updateMiniPoll, authentication, gdId, commentId,"end");
  }
  
  /**
   * shareMiniPoll method will update the mini poll by the user.
   * 
   * @param gdId
   *          of the mini poll 
   * @return the sprespones.
   */
  @RequestMapping(value = "/gd/shareMiniPoll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareMiniPoll(@RequestParam String gdId, @RequestParam int commentId, Authentication authentication) {
    return process(helper::updateMiniPoll, authentication, gdId, commentId,"share");
  }
}
