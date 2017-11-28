package com.sp.web.controller.demoaccount;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.form.DemoAccountForm;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.demoaccount.DemoUser;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TokenRequest;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DemoAccountControllerHelper method is the helper method for the demo account.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class DemoAccountControllerHelper {
  
  /**
   * 
   */
  private static final String DEMO_ACCOUNT_EMAIL_SUBJECT = "demoAccount.email.subject";
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(DemoAccountControllerHelper.class);
  
  /** Account repository to create the demo account for the user. */
  @Autowired
  private AccountRepository accountRepository;
  
  /** Communication Gateway. */
  @Autowired
  private CommunicationGateway communicationGateway;
  
  /** SPToken Factory for creating the token. */
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  private Environment environment;
  
  /**
   * <code>createDemoAccount</code> method will create the demo account and will send the demo link.
   * 
   * @param param
   *          contains the demo account user form.
   * @return the succes or failure email.
   */
  public SPResponse createDemoAccount(Object[] param) {
    
    DemoAccountForm accountForm = (DemoAccountForm) param[0];
    
    LOG.debug("Demo account form :" + accountForm);
    /* create a demo user */
    DemoUser demoUser = new DemoUser(accountForm);
    
    accountRepository.createDemoAccount(demoUser);
    
    TokenRequest tokenRequest = new TokenRequest(TokenType.TIME_BASED);
    /*
     * user exist in the system, create a token and send the reset email to the uesr
     */
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.PARAM_EXPIRES_TIME, 2);
    params.put(Constants.PARAM_TIME_UNIT, TimeUnit.DAYS);
    tokenRequest.setParamsMap(params);
    Token token = spTokenFactory.getToken(tokenRequest, TokenProcessorType.DEMO_ACCOUNT);
    
    /* send the email link to the user to access the demo account */
    EmailParams emailParams = new EmailParams(NotificationType.DemoAccount.getTemplateName(), demoUser.getEmail(),
        MessagesHelper.getMessage(DEMO_ACCOUNT_EMAIL_SUBJECT), Constants.DEFAULT_LOCALE);
    
    emailParams.addParam("token", token.getTokenUrl());
    emailParams.addParam("email", demoUser.getEmail());
    emailParams.addParam("name", demoUser.getFirstName());
    
    communicationGateway.sendMessage(emailParams);
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
}
