package com.sp.web.controller.demoaccount;

import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Demo Account Token Processor is the class for handling the demo account.
 * 
 * @author pradeepruhil
 *
 */
@Component("demoAccountInviteTokenProcessor")
public class DemoAccountTokenProcessor implements TokenProcessor {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  /**
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    
    if (!token.isValid()) {
      throw new InvalidTokenException("Token has expired", token);
    }
    
    User demoUser = userRepository.findByEmail("demouser@ace.com");
    loginHelper.authenticateUserAndSetSession(demoUser);
    
    token.setRedirectToView("redirect:/home");
  }
}
