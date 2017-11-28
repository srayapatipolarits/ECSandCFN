package com.sp.web.controller.external.rest;

import com.sp.web.controller.hiring.HiringFactory;
import com.sp.web.controller.systemadmin.company.PartnerAccountFactory;
import com.sp.web.form.external.rest.UserForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.ThirdPartyUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.repository.external.ThirdPartyRepository;
import com.sp.web.service.external.rest.PartnerFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.sse.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SPExternalServiceFactory {
  
  @Autowired
  private HiringFactory hiringFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  @Qualifier("thirdPartyRepository")
  private ThirdPartyRepository thirdPartyRepository;
  
  @Autowired
  private PartnerFactory partnerFactory;
  
  @Autowired
  private PartnerAccountFactory partnerAccountFactory;
  
  /**
   * createUser method will create the user.
   * 
   * @param userForm
   *          external user form.
   * @param user
   *          is the company for the user.
   */
  public void createUser(UserForm userForm, User user) {
    
    /* create the hiringUserForm from the userForm */
    /* Since in this case only 1 user is created at a time. */
    HiringAddForm hiringAddForm = userForm.createHiringAddForm();
    hiringAddForm.getEmails().forEach(
        email -> {
          HiringUser hiringUser = hiringFactory.addHiringUser(user, email, hiringAddForm,
              hiringAddForm.getType(), false);
          
          /* Create the third party user for the request */
          hiringUser.setThirdParty(true);
          hiringUser.setFirstName(userForm.getFirstName());
          hiringUser.setGender(userForm.getGender());
          hiringUser.setLastName(userForm.getLastName());
          hiringUser.setUserStatus(UserStatus.ASSESSMENT_PENDING);
          hiringUser.addRole(RoleType.PartnerCandidate);
          hiringUserFactory.updateUser(hiringUser);
          ThirdPartyUser thUser = ThirdPartyUser.createUser(userForm, hiringUser);
          thirdPartyRepository.save(thUser);
          partnerFactory.updatePartner(hiringUser, ActionType.PartnerPrismStatus);
        });
    
  }
  
  /**
   * deleteUser method will delete the user.
   * 
   * @param email
   *          is the hiring user email.
   * @param user
   *          is the logged in user administrator.
   */
  public void deleteUser(String email, User user) {
    HiringUser userByEmail = hiringUserFactory.getUserByEmail(email, user.getCompanyId());
    Assert.notNull(userByEmail, "Error 1002: Invalid Request");
    
    hiringUserFactory.delete(userByEmail);
  }
}
