package com.sp.web.controller.admin.member;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dto.UserValidationDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.MultiInviteUserForm;
import com.sp.web.model.Account;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The controller helper.
 */
@Component
@Scope("prototype")
public class MultiInviteControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(MultiInviteControllerHelper.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private MemberControllerHelper helper;
  
  /**
   * Helper method to add the file to the list of files.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add file request
   */
  public SPResponse multiInviteAddFile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the file to process
    MultipartFile userFile = (MultipartFile) params[0];
    
    // check the file size
    if (userFile.getSize() > Constants.HIRING_FILE_MAX_SIZE) {
      throw new InvalidRequestException("File size cannot be more than :"
          + (Constants.HIRING_FILE_MAX_SIZE / 1000000) + "mb");
    }
    
    String contentType = userFile.getOriginalFilename();
    if (!contentType.endsWith("csv")) {
      throw new SPException(MessagesHelper.getMessage("multiinvite.member.csvFileSupported",
          user.getLocale()));
    }
    
    // read the contents and process
    List<User> users = null;
    try {
      users = IOUtils.readLines(userFile.getInputStream()).stream().skip(1).map(mapToCsvUser)
          .collect(Collectors.toList());
    } catch (IOException e) {
      LOG.warn("Could not read the file content.", e);
      throw new InvalidRequestException("Could not read the file content.", e);
    }
    
    validateUsers(user, users, resp);
    
    return resp;
  }
  
  private boolean validateUsers(User user, List<User> users, SPResponse resp) {
    final int totalUsers = users.size();
    
    Assert.notEmpty(users,
        MessagesHelper.getMessage("multiinvite.member.csvFileSupported", user.getLocale()));
    boolean hasErros = false;
    Account companyAccount = accountRepository.findAccountByCompanyId(user.getCompanyId());
    if (companyAccount.getSpPlanMap().get(SPPlanType.Primary).getNumMember() < totalUsers) {
      throw new InvalidRequestException(MessagesHelper.getMessage(
          "admin.member.add.multiple.error.emailexceed", user.getLocale()));
    }
    
    List<UserValidationDTO> userDto = new ArrayList<UserValidationDTO>();
    for (User usr : users) {
      boolean valid = EmailValidator.getInstance().isValid(usr.getEmail());
      
      UserValidationDTO baseUserDTO = new UserValidationDTO(usr);
      if (valid) {
        User userExists = userFactory.getUserByEmail(usr.getEmail());
        if (userExists != null) {
          baseUserDTO.setUserStatus(UserStatus.ALREADY_EXISTS);
          baseUserDTO.setValidUser(false);
          baseUserDTO.setMessage(MessagesHelper.getMessage("multiinvite.member.alreadyexist",
              user.getLocale()));
          hasErros = true;
        } else {
          baseUserDTO.setUserStatus(UserStatus.INVITATION_NOT_SENT);
          baseUserDTO.setValidUser(true);
        }
        if (userDto.contains(baseUserDTO)) {
          baseUserDTO.setValidUser(false);
          
          /* Also update the already existing user */
          int indexOf = userDto.indexOf(baseUserDTO);
          UserValidationDTO userValidationDTO = userDto.get(indexOf);
          userValidationDTO.setValidUser(false);
          String duplicateMessage = MessagesHelper.getMessage("multiinvite.member.duplicateEmail",
              user.getLocale());
          userValidationDTO.setMessage(duplicateMessage);
          
          baseUserDTO.setMessage(duplicateMessage);
          hasErros = true;
        }
      } else {
        baseUserDTO.setValidUser(false);
        baseUserDTO.setMessage(MessagesHelper.getMessage("multiinvite.member.invalidEmail",
            user.getLocale()));
        hasErros = true;
        
      }
      
      userDto.add(baseUserDTO);
    }
    resp.add("users", userDto);
    return hasErros;
  }
  
  /**
   * Helper method to invite the candidates.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the invite request
   */
  @SuppressWarnings("unchecked")
  public SPResponse multiInvite(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the invite list
    List<MultiInviteUserForm> candidateInviteList = (List<MultiInviteUserForm>) params[0];
    
    List<User> users = candidateInviteList.stream().map(form -> {
      
      User newUser = new User(form.getEmail(), user.getCompanyId());
      newUser.setFirstName(form.getFirstName());
      newUser.setLastName(form.getLastName());
      newUser.setTitle(form.getTitle());
      return newUser;
      
    }).collect(Collectors.toList());
    
    if (validateUsers(user, users, resp)) {
      return resp;
    }
    
    // get the tag list
    List<String> tagListTemp = (List<String>) params[1];
    if (tagListTemp != null) {
      tagListTemp = tagListTemp.stream().map(WordUtils::capitalizeFully)
          .collect(Collectors.toList());
    }
    
    final List<String> tagList = tagListTemp;
    
    // get the group association list
    List<String> groupAssociationStrList = (List<String>) params[2];
    
    users.stream().forEach(usr -> {
      helper.addMember(user, tagList, groupAssociationStrList, false, usr);
    });
    /* remove the users added in the response as successfull add happened */
    resp.getSuccess().remove("users");
    return resp;
  }
  
  /**
   * Create a list of hiring users from the contents of the file.
   * 
   * @param fileContent
   *          - the file content
   * @return the list of hiring users from the file
   */
  
  private Function<String, User> mapToCsvUser = (line) -> {
    
    String[] hiringUserArray = line.split(",");
    User user = new User();
    user.setEmail(StringUtils.trimWhitespace(hiringUserArray[0].toLowerCase()));
    if (hiringUserArray.length > 1) {
      user.setFirstName(hiringUserArray[1]);
    }
    if (hiringUserArray.length > 2) {
      user.setLastName(hiringUserArray[2]);
    }
    
    if (hiringUserArray.length > 3) {
      user.setTitle(hiringUserArray[3]);
    }
    
    user.setUserStatus(UserStatus.INVITATION_NOT_SENT);
    
    return user;
  };
}