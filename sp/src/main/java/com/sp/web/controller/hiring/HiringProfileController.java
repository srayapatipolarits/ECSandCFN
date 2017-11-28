package com.sp.web.controller.hiring;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.hiring.user.HiringReferenceForm;
import com.sp.web.form.hiring.user.HiringUserProfileForm;
import com.sp.web.model.SPMedia;
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

import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 * 
 *         This is the controller to maintain and manage the hiring candidate profiles.
 */
@Controller
public class HiringProfileController {
  
  @Autowired
  private HiringProfileControllerHelper helper;
  
  /**
   * Controller method to get the profile details of the hiring candidate.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get profile details
   */
  @RequestMapping(value = "/hiring/getProfileDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfileDetails(@RequestParam String userId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getProfileDetails, token, userId);
  }
  
  /**
   * This is the controller method to be called to complete the users profile details. From an
   * external user request for completing profile of candidate or employee.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get profile request
   */
  @RequestMapping(value = "/hiring/ext/getProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfileDetailsExt(HttpSession session, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getProfileDetailsExt, token, session);
  }
  
  /**
   * Controller method to get the details of the hiring candidate.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getArchivedUserProfileDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getArchivedUserProfileDetails(@RequestParam String userId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getArchivedUserProfileDetails, token, userId);
  }
  
  /**
   * Controller method to update the profile details.
   * 
   * @param form
   *          - form data to update
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/hiring/updateProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateProfile(@RequestBody HiringUserProfileForm form, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::updateProfile, token, form);
  }
  
  /**
   * Controller method for the external candidate or employee to complete their profile.
   * 
   * @param profile
   *          - profile
   * @param address
   *          - address
   * @param session
   *          - session
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/hiring/ext/updateProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateProfileExt(@RequestBody HiringUserProfileForm form, HttpSession session,
      Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::updateProfileExt, token, form, session);
  }
  
  /**
   * Controller method to add the hiring roles for the hiring user.
   * 
   * @param userId
   *          - hiring user id
   * @param roleId
   *          - hiring role
   * @param addMatch
   *          - flag to indicate if match needs to be sent or not
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/hiring/addRole", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addRole(@RequestParam String userId, @RequestParam String roleId,
      @RequestParam(defaultValue = "false") boolean addMatch, Authentication token) {
    
    return process(helper::addRole, token, userId, roleId, addMatch);
  }
  
  /**
   * Controller method to remove the hiring roles for the hiring user.
   * 
   * @param userId
   *          - hiring user id
   * @param roleId
   *          - hiring role to remove
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/hiring/removeRole", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeRole(@RequestParam String userId, @RequestParam String roleId,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::removeRole, token, userId, roleId);
  }
  
  /**
   * Controller method to update the taglist for the user.
   * 
   * @param userId
   *          - user id
   * @param tagList
   *          - tag list
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/hiring/updateTags", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateTags(@RequestParam String userId, @RequestParam List<String> tagList,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::updateTags, token, userId, tagList);
  }
  
  /**
   * Controller method to update the web url of the candidate.
   * 
   * @param userId
   *          - hiring user id
   * @param url
   *          - url to update to profile
   * @param profileUrl
   *          - flag to indicate if this is the profile URL.
   * 
   * @param token
   *          - logged in user
   * @return the response to the add update request
   */
  @RequestMapping(value = "/hiring/addUpdateUrl", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addUpdateUrl(@RequestParam String userId, SPMedia url,
      @RequestParam(defaultValue = "false") boolean profileUrl,
      @RequestParam(required = false, defaultValue = "-1") int index, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::addUpdateUrl, token, userId, url, profileUrl, index);
  }
  
  /**
   * Remove the given URL from the user's profile.
   * 
   * @param userId
   *          - hiring user id
   * @param url
   *          - url to remove
   * @param profileUrl
   *          - flag to indicate profile
   * @param token
   *          - logged in user
   * @return the response to the remove request
   */
  @RequestMapping(value = "/hiring/removeUrl", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeUrl(@RequestParam String userId,
      @RequestParam(required = false) String url,
      @RequestParam(defaultValue = "false") boolean profileUrl, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::removeUrl, token, userId, url, profileUrl);
  }
  
  /**
   * Controller method to add a comment.
   * 
   * @param userId
   *          - hiring user id
   * @param comment
   *          - comment text
   * @param token
   *          - logged in user
   * @return the response to add comment
   */
  @RequestMapping(value = "/hiring/addComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addComment(@RequestParam String userId, @RequestParam String comment,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::addComment, token, userId, comment);
  }
  
  /**
   * Controller method to remove the comments.
   * 
   * @param userId
   *          - hiring user id
   * @param cid
   *          - comment index
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/hiring/removeComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeComment(@RequestParam String userId, @RequestParam String cid,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::removeComment, token, userId, cid);
  }
  
  /**
   * The controller method to update the comment for the candidate.
   * 
   * @param userId
   *          - candidate email
   * @param cid
   *          - index
   * @param comment
   *          - comment to update
   * @param token
   *          - logged in user
   * @return the response to the update comment request
   */
  @RequestMapping(value = "/hiring/updateComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateComment(@RequestParam String userId, @RequestParam String cid,
      @RequestParam String comment, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::updateComment, token, userId, cid, comment);
  }
  
  /**
   * The controller method to invite the references for the candidate.
   * 
   * @param references
   *          - the request
   * @param session
   *          - session
   * @param token
   *          - logged in user
   * @return the response to the candidate reference invite request
   */
  @RequestMapping(value = "/hiring/ext/addReferences", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public SPResponse addReferences(@RequestBody HiringReferenceForm references, HttpSession session,
      Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::addReferences, token, references, session);
  }
  
  /**
   * The controller method to get the hiring users profile token.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getProfileShareId", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfileShareId(@RequestParam String userId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getProfileShareId, token, userId);
  }
  
  /**
   * The controller method to get the hiring users roles.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/hiring/getUserRoles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getUserRoles(@RequestParam String userId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getUserRoles, token, userId);
  }
  
}
