package com.sp.web.controller.profile;

import com.sp.web.Constants;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 *
 *         The token processor to handle copy profile.
 */
@Component("copyProfileTokenProcessor")
public class CopyProfileTokenProcessor implements TokenProcessor {

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  /* (non-Javadoc)
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    String userId = (String) token.getParam(Constants.PARAM_USER_ID);
    UserType userType = UserType.valueOf((String)token.getParam(Constants.PARAM_USER_TYPE));
    
    User user = null;
    if (userType == UserType.HiringCandidate) {
      user = hiringRepository.findById(userId);
    } else {
      user = userRepository.findUserById(userId);
    }
    
    if (user != null && user.getAnalysis() == null) {
      token.addParam(Constants.PARAM_USER, user);
      token.setRedirectToView("copyProfileAuthorize");
    } else {
      token.setRedirectToView(Constants.VIEW_TOKEN_ERROR);
    }
    
  }

}
