package com.sp.web.controller.admin.member;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.SignupForm;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         This is the controller to manage the members.
 */
@Controller
public class MemberController {
  
  @Autowired
  MemberControllerHelper memberControllerHelper;
  
  /**
   * Validates the email from the given email list, checks if the users are present already.
   * 
   * @param memberList
   *          - member list
   * @param token
   *          - logged in user
   * @return - the validate request
   */
  @RequestMapping(value = "/signup/validateEmails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse validateEmails(@RequestParam List<String> memberList, Authentication token) {
    
    // process the validate the user emails from user controller helper
    return process(memberControllerHelper::validateEmails, token, memberList);
  }
  
  /**
   * Adds a single user for the company and sends an email to the user to finish the registration
   * process.
   * 
   * @param addUserForm
   *          - the user details to add
   * @param tagList
   *          - the tags for the user
   * @param groupAssociationList
   *          - the groups for the user
   * @param isAdministrator
   *          - flag to indicate if the user is an administrator
   * @param token
   *          - get the logged in user
   * @return the response for the add action
   */
  @Audit("")
  @RequestMapping(value = "/admin/member/addSingle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addSingle(SignupForm addUserForm,
      @RequestParam(required = false) List<String> tagList,
      @RequestParam(required = false) List<String> groupAssociationList,
      @RequestParam(required = false, defaultValue = "false") boolean isAdministrator,
      Authentication token) {
    
    // process the add single user from user controller helper
    return process(memberControllerHelper::addSingle, token, addUserForm, tagList,
        groupAssociationList, isAdministrator);
  }
  
  /**
   * Controller method to add the list of members to the company account.
   * 
   * @param memberList
   *          - list of members
   * @param tagList
   *          - list of tags
   * @param groupAssociationList
   *          - list of group associations
   * @param token
   *          - logged in user
   * @return the response for the user add action
   */
  @RequestMapping(value = "/admin/member/addMultiple", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addMultiple(@RequestParam List<String> memberList,
      @RequestParam(required = false) List<String> tagList,
      @RequestParam(required = false) List<String> groupAssociationList, Authentication token) {
    
    // process the add multiple user from user controller helper
    return process(memberControllerHelper::addMultiple, token, memberList, tagList,
        groupAssociationList);
  }
  
  /**
   * Controller method to return the list of members for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response for the get member list request
   */
  @RequestMapping(value = "/admin/member", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMembers(Authentication token) {
    
    // process the list of members from user controller helper
    return process(memberControllerHelper::getMembers, token);
  }
  
  /**
   * Controller method to delete a member from the company.
   * 
   * @param memberEmail
   *          - the member to delete
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/admin/member/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteMember(@RequestParam String memberEmail, Authentication token) {
    
    // process the delete single member from user controller helper
    return process(memberControllerHelper::deleteMember, token, memberEmail);
  }
  
  /**
   * Deletes the list of members.
   * 
   * @param memberEmails
   *          - list of members to delete
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/admin/member/deleteMulti", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteMembers(@RequestParam List<String> memberEmails, Authentication token) {
    
    // process the delete multiple members from user controller helper
    return process(memberControllerHelper::deleteMembers, token, memberEmails);
  }
  
  /**
   * Controller method to get all the tags for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get tags request
   */
  @RequestMapping(value = "/admin/member/getAllTags", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllTags(Authentication token) {
    
    // process the delete multiple members from user controller helper
    return process(memberControllerHelper::getAllTags, token);
  }
  
  // This is being handled in the front end so
  // function no longer required.
  // /**
  // * The controller search action method.
  // *
  // * @param searchString
  // * - the search string
  // * @param token
  // * - logged in user
  // * @return the response for the search action
  // */
  // @RequestMapping(value = "/admin/member/search", method = RequestMethod.POST)
  // @ResponseBody
  // public SPResponse searchMembers(@RequestParam String searchString,
  // Authentication token) {
  //
  // // process the delete multiple members from user controller helper
  // return process(memberControllerHelper::searchMembers, token, searchString);
  // }
  
  /**
   * <code>getAllValidMembers</code> method will return the valid members.
   * 
   * @param token
   *          logged in user
   * @return the vall valid members
   */
  @RequestMapping(value = "/allUsers", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllValidMembers(
      @RequestParam(defaultValue = "false") boolean isAccountAdmin, Authentication token) {
    // process the delete multiple members from user controller helper
    return process(memberControllerHelper::getAllValidMembers, token, isAccountAdmin);
  }
  
  /**
   * Controller method to return the list of members for the company.
   * 
   * @param token
   *          - logged in user
   * @return the response for the get member list request
   */
  @RequestMapping(value = "/admin/getMembersForAdmins/", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMembersForAdmins(@RequestParam SPPlanType planType, Authentication token) {
    // process the list of members from user controller helper
    return process(memberControllerHelper::getMembersForAdmins, token, planType);
  }
  
  /**
   * getPlanFeatures method will return all the spFeatures associated with the plan.
   * 
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/admin/addAdminMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addAdminMember(@RequestParam String userEmail,
      @RequestParam SPPlanType planType, Authentication token) {
    return ControllerHelper.process(memberControllerHelper::addAdminMember, token, userEmail,
        planType);
  }
  
  /**
   * getPlanFeatures method will return all the spFeatures associated with the plan.
   * 
   * @param token
   *          of the user.
   * @return the
   */
  @RequestMapping(value = "/admin/removeAdminMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeAdminMember(@RequestParam String userEmail,
      @RequestParam SPPlanType planType, Authentication token) {
    return ControllerHelper.process(memberControllerHelper::removeAdminMember, token, userEmail,
        planType);
  }
  
  /**
   * Controller method to block the user.
   * 
   * @param userList
   *          - user list
   * @param blockUser
   *          - flag to block or unblock user
   * @param token
   *          - logged in user
   * @return the response to the block user request
   */
  @RequestMapping(value = "/admin/blockUser", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse blockUser(@RequestParam List<String> userList, @RequestParam boolean blockUser,
      Authentication token) {
    return ControllerHelper.process(memberControllerHelper::blockUser, token, userList, blockUser);
  }
  
  /**
   * <code>getGroupMemberLndView</code> method will return the lnd view by a group lead or account
   * administrator.
   * 
   * @param token
   *          logged in user.
   * @return json response.
   */
  @RequestMapping(value = "/goals/groupView/getMemberLndCompDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGroupMemberLndView(@RequestParam String memberEmail, Authentication token) {
    return ControllerHelper.process(memberControllerHelper::getMemberLndCompDetails, token,
        memberEmail);
  }
}
