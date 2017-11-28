package com.sp.web.controller.admin.member;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.MultiInviteUserForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The session based controller for the multiple invite requests.
 */
@Controller
@Scope("session")
public class MultiInviteController {
  
  @Autowired
  MultiInviteControllerHelper helper;
  
  /**
   * Controller method to add a file to the list of files.
   * 
   * @param token
   *          - logged in user.
   * @return the response to the add file request
   */
  @RequestMapping(value = "/admin/member/multiInviteAddFile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse multiInviteAddFile(@RequestParam MultipartFile userFile, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::multiInviteAddFile, token, userFile);
  }
  
  /**
   * The controller method to invite multiple candidates.
   * 
   * @param invitedUsers
   *          - multi invited users
   * @param token
   *          - logged in user
   * @return the response to the invite request
   */
  @RequestMapping(value = "/admin/member/multiInvite", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse multiInvite(@RequestParam(required = false) List<String> tagList,
      @RequestParam(required = false) List<String> groupAssociationList,
      @RequestBody List<MultiInviteUserForm> invitedUsers, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::multiInvite, token, invitedUsers, tagList, groupAssociationList);
  }
  
}