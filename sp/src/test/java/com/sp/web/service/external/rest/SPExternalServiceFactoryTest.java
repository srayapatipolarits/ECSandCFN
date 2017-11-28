package com.sp.web.service.external.rest;

import com.sp.web.controller.external.rest.SPExternalServiceFactory;
import com.sp.web.controller.external.rest.SPRestControllerHelper;
import com.sp.web.form.external.rest.UserForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.sse.ActionType;
import com.sp.web.utils.GenericUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SPExternalServiceFactoryTest extends SPTestLoggedInBase {
  
  @Autowired
  private SPExternalServiceFactory externalServiceFactory;
  
  @Autowired
  private PartnerFactory partnerFactory;
  
  @Autowired
  private SPRestControllerHelper controllerHelper;
  
  @Test
  public void testPrismUpdate() throws InterruptedException {
    dbSetup.removeAll("partnerAccount");
    dbSetup.removeAll("hiringUser");
    createPartnerAccount();
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    User user = GenericUtils.getUserFromAuthentication(authentication);
    
    UserForm userForm = new UserForm();
    userForm.setFirstName("Sam");
    userForm.setLastName("Santa");
    userForm.setUid("1114");
    userForm.setEmail("testmedixspclient@yopmail.com");
    Object[] params = new Object[] { userForm };
    controllerHelper.createUser(user, params);
    
    HiringUser hiringCandidate = dbSetup.getHiringCandidate(userForm.getEmail(),
        user.getCompanyId());
    
    partnerFactory.updatePartner(hiringCandidate, ActionType.PartnerPrismStatus);
    
    hiringCandidate.setUserStatus(UserStatus.ASSESSMENT_PROGRESS);
    dbSetup.addUpdate(hiringCandidate);
    partnerFactory.updatePartner(hiringCandidate, ActionType.PartnerPrismStatus);
    
    hiringCandidate.setUserStatus(UserStatus.VALID);
    hiringCandidate.setAnalysis(user.getAnalysis());
    dbSetup.addUpdate(hiringCandidate);
    partnerFactory.updatePartner(hiringCandidate, ActionType.PartnerPrismStatus,
        ActionType.PartnerPrismResult);
    
    Thread.currentThread().wait(5000);
    System.out.println("Completed");
    /* create a user */
    
  }
  
  private PartnerAccount createPartnerAccount() {
    return createPartnerAccount(RandomStringUtils.random(15, true, true), true, "1");
  }
  
  private PartnerAccount createPartnerAccount(String partnerId, boolean active, String companyId) {
    PartnerAccount partnerAccount = new PartnerAccount();
    partnerAccount.setPartnerId(partnerId);
    partnerAccount.setActive(true);
    partnerAccount.setCompanyId(companyId);
    partnerAccount.setActionProcessor("medixActionProcessor");
    
    dbSetup.addUpdate(partnerAccount);
    return partnerAccount;
  }
  
}
