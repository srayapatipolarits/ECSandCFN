package com.sp.web.controller.admin.user;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.response.UserResponse;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.Operation;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.Company;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.validation.Valid;

/**
 * @author Dax Abraham
 *
 *         The controller class for users. Has all the methods to manager the user information.
 */
@Controller
public class UserController {
  
  private static final Logger LOG = Logger.getLogger(UserController.class);
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private UserControllerHelper helper;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @RequestMapping(value = "/admin/account/member/assessment", method = RequestMethod.GET)
  public String updateProfileView() {
    return "updateProfile";
  }
  
  @RequestMapping(value = "/admin/account/profile/details", method = RequestMethod.GET)
  public String updateUserProfileView() {
    return "updateUserProfile";
  }
  
  /**
   * Method to get the user profile info.
   * 
   * @param userEmail
   *          - optional user email to get the user profile for if not present then sends the
   *          profile for the currently logged in user
   * @param token
   *          - to get the currently logged in user details
   * @return response indicating failure or success of the get profile action.
   */
  @RequestMapping(value = "/admin/user", method = RequestMethod.POST)
  @ResponseBody
  public UserResponse getUserInfo(@RequestParam(required = false) String userEmail,
      Authentication token) {
    
    UserResponse response = new UserResponse();
    
    try {
      
      // get the logged in user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // User profileUser = userFactory.getUserForGroupLead(userEmail,user);
      
      User profileUser = user;
      
      if (userEmail != null
          && user.hasAnyRole(RoleType.AccountAdministrator, RoleType.Administrator,
              RoleType.GroupLead)) {
        profileUser = userRepository.findByEmail(userEmail);
        
        if (profileUser == null) {
          throw new InvalidRequestException("User :" + userEmail + ": not found.");
        }
        
        // check if the logged in user and the user is from the same company
        profileUser.isSameCompany(user);
      }
      
      UserProfileForm profileForm = new UserProfileForm(profileUser);
      
      // add the user profile
      response.add("profileForm", profileForm);
      // add the address for the user
      response.add("address", profileUser.getAddress());
      // add the user group profile
      response.add("groupList", profileUser.getGroupAssociationList());
      // add the tags
      response.add("tagList", profileUser.getTagList());
      // add the user roles
      response.add(Constants.PARAM_ROLES, profileUser.getRoles());
      // add the user summary
      response.add("userSummary", new BaseUserDTO(profileUser));
      //Deactivated User
      response.add("deactivated", profileUser.isDeactivated());
      if (StringUtils.isNotBlank(user.getCompanyId())) {
        Company company = accountRepository.findCompanyById(user.getCompanyId());
        response.add("features", company.getFeatureList());
      }
      
      // add user certificate
      if (profileUser.hasRole(RoleType.IndividualAccountAdministrator)) {
        response.add("userCertificate", "SP" + profileUser.getCertificateNumber());
        if (profileUser.getCreatedOn() != null) {
          response.add("createdOn", MessagesHelper.formatDate(profileUser.getCreatedOn()));
        }
      }
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    // the response
    return response;
  }
  
  /**
   * This controller method updates the user profile with the given data.
   * 
   * @param userProfile
   *          - the user profile to update
   * @param address
   *          - the address to update
   * @param token
   *          - token to get currently logged in user
   * @return the response to the administrator update action
   */
  @RequestMapping(value = "/admin/user/update/profile", method = RequestMethod.POST)
  @ResponseBody
  public UserResponse updateUserProfile(UserProfileForm userProfile, AddressForm address,
      Authentication token) {
    
    UserResponse response = new UserResponse();
    
    try {
      
      // get the logged in user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      if (!user.equals(userProfile.getEmail())) {
        User userToUpdate = userRepository.findByEmail(userProfile.getEmail());
        if (userToUpdate == null) {
          throw new InvalidRequestException("User not found :" + userProfile.getEmail());
        }
        
        userToUpdate.isSameCompany(user);
      }
      
      // perform the update action
      userRepository.updateUserProfile(userProfile, address);
      
      // adding the updated user to the response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    // the response
    return response;
  }
  
  /**
   * This method updates the user information and sets or resets the administrator status of the
   * user depending on the operation passed.
   * 
   * @param userEmail
   *          - the user to update
   * @param isCompetencyAllowed
   *          - flag to set access to competency tool
   * @param token
   *          - get currently logged in user
   * @return the response to the administrator action
   */
  @RequestMapping(value = "/admin/user/updatePermissions", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updatePermissions(@RequestParam String userEmail,
      @RequestParam(defaultValue = "false") boolean isCompetencyAllowed,
      @RequestParam(defaultValue = "false") boolean isWorkspacePulseAllowed,
      @RequestParam(defaultValue = "false") boolean isSpectrumAllowed, Authentication token) {
    
    return process(helper::updatePermissions, token, userEmail, isCompetencyAllowed,
        isWorkspacePulseAllowed, isSpectrumAllowed);
  }
  
  /**
   * Method to add the tag to the user.
   * 
   * @param userEmail
   *          - optional user
   * @param tagName
   *          - tag name
   * @param op
   *          - Operation Add/Remove
   * @param token
   *          - current logged in user
   * @return the response for the tag operation
   */
  @RequestMapping(value = "/admin/user/tag", method = RequestMethod.POST)
  @ResponseBody
  public UserResponse manageTags(@RequestParam(required = false) String userEmail,
      @RequestParam String tagName, @RequestParam(defaultValue = "SET") Operation op,
      Authentication token) {
    
    UserResponse response = new UserResponse();
    
    try {
      // get the logged in user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      User userToModify = user;
      
      // check if user is sent along with the request
      // else modify the existing user
      if (userEmail != null) {
        userToModify = userRepository.findByEmailValidated(userEmail);
        userToModify.isSameCompany(user);
      }
      
      tagName = WordUtils.capitalizeFully(tagName);
      
      switch (op) {
      case SET:
        userRepository.addTag(userToModify, tagName);
        break;
      case REMOVE:
        userRepository.removeTag(userToModify, tagName);
        break;
      default:
        throw new InvalidRequestException("Do not know how to process operation :" + op);
      }
      
      response.add("tagList", userToModify.getTagList());
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    // the response
    return response;
  }
  
  /**
   * <code>updateProfile</code> method update the user profile.
   * 
   * @param userProfileForm
   *          contains the user information
   * @param addressForm
   *          of the user
   * @param bindingResult
   *          binding result in case of errors.
   * @return the SP Response
   */
  @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateProfile(@Valid UserProfileForm userProfileForm,
      @Valid AddressForm addressForm, BindingResult bindingResult) {
    SPResponse updateProfileResponse = new SPResponse();
    if (bindingResult.hasErrors()) {
      addBindingErrors(bindingResult, updateProfileResponse);
    } else {
      // validate the email
      User user = userRepository.updateUserProfile(userProfileForm, addressForm);
      
      /* update the user info in the spring security context */
      loginHelper.updateUser(user);
      updateProfileResponse.isSuccess();
      
      if (user.hasRole(RoleType.Hiring)) {
        HiringUser hiringUser = hiringUserFactory.getUserByEmail(user.getEmail(),
            user.getCompanyId());
        if (hiringUser != null) {
          userProfileForm.update(hiringUser);
          addressForm.update(hiringUser);
          hiringUserFactory.updateUser(hiringUser);
        }
      }
    }
    LOG.debug("Sending response :" + updateProfileResponse);
    return updateProfileResponse;
    
  }
  
  /**
   * Controller method to set or remove the linked in URL.
   * 
   * @param linkedInUrl
   *          - linked in url
   * @param op
   *          - operation
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/updateLinkedIn", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateLinkedIn(@RequestParam String linkedInUrl,
      @RequestParam(defaultValue = "SET") Operation op, Authentication token) {
    
    return process(helper::updateLinkedInUrl, token, linkedInUrl, op);
  }
  
  /**
   * Controller method to set or remove the linked in URL for the administrators.
   * 
   * @param linkedInUrl
   *          - linked in url
   * @param op
   *          - operation
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/admin/updateLinkedIn", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateLinkedIn(@RequestParam(required = false) String userEmail,
      @RequestParam String linkedInUrl, @RequestParam(defaultValue = "SET") Operation op,
      Authentication token) {
    
    return process(helper::updateLinkedInUrl, token, linkedInUrl, op, userEmail);
  }
  
  /**
   * Helper method to add the field errors to the response.
   * 
   * @param bindingResult
   *          - the binding result
   * @param resp
   *          - the response
   * @return the updated response
   */
  private SPResponse addBindingErrors(BindingResult bindingResult, SPResponse resp) {
    bindingResult.getAllErrors().forEach(err -> resp.addError((FieldError) err));
    return resp;
  }
  
  @RequestMapping(value = "/updateAutoLearning", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateAutoLearning(@RequestParam boolean autoLearning, Authentication token) {
    return ControllerHelper.process(helper::updateAutoLearning, token, autoLearning);
  }
  
  /**
   * Controller method to remove the message for the user.
   * 
   * @param uidList
   *          - id list of messages
   * @param token
   *          - logged in user
   * @return
   *    the response to the remove request
   */
  @RequestMapping(value = "/user/removeMessage", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeMessage(@RequestParam List<String> uidList, Authentication token) {
    return ControllerHelper.process(helper::removeMessage, token, uidList);
  }
  
}
