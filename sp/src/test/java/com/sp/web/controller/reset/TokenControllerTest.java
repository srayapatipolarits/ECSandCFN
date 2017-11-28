package com.sp.web.controller.reset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sp.web.Constants;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * 
 *         Token controller test.
 */
public class TokenControllerTest extends SPTestBase {
  @Test
  public void tokenResetPasswordRequestTest() throws Exception {
    Object[] addTokens = dbSetup.addTokens();
    List<Object> asList = Arrays.asList(addTokens);
    Object findFirst = asList.stream()
        .filter(t -> ((Token) t).getTokenProcessorType() == TokenProcessorType.RESET_PASSWORD)
        .findFirst().get();
    
    try {
      Token token = (Token) findFirst;
      
      ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
          .get("/processToken/" + token.getTokenId()).contentType(MediaType.TEXT_PLAIN)
          .session(session));
      perform.andExpect(MockMvcResultMatchers.view().name("changePassword"));
      
    } finally {
      dbSetup.removeTokens();
      testSmtp.stop();
    }
  }
  
  @Test
  public void testInvalidToken() throws Exception {
    MvcResult result;
    // invalid request without token
    result = mockMvc
        .perform(get("/processToken").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(status().is4xxClientError()).andReturn();
    
    String tokenStr = "dax";
    // invalid request with invalid token
    result = mockMvc
        .perform(
            get("/processToken/" + tokenStr).contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
        .andReturn();
    testSmtp.stop();
  }
  
  @Test
  public void testAddMemberTokenProcessor() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.removeAllTokens();
    // dbSetup.createAddMemberTokens();
    
    // in valid request user not found
    MvcResult result;
    // creating the token
    Token token = new Token();
    token.setCreatedTime(DateTime.now().getMillis());
    token.setId("1");
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_USER_EMAIL, "dax2@surepeople.com");
    paramsMap.put(Constants.PARAM_TIME_UNIT, "HOURS");
    paramsMap.put(Constants.PARAM_EXPIRES_TIME, "24");
    token.setParamsMap(paramsMap);
    // token.setTimeUnit(TimeUnit.HOURS);
    // token.setTokenExpiresTime(24);
    token.setTokenType(TokenType.TIME_BASED);
    token.setTokenProcessorType(TokenProcessorType.ADD_MEMBER);
    token.setTokenStatus(TokenStatus.ENABLED);
    token.setTokenId("1");
    
    dbSetup.addUpdate(token);
    
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404")).andReturn();
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    
    // token invalid check
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404")).andReturn();
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    
    // creating the users
    User user = new User();
    user.setEmail("dax2@surepeople.com");
    user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
    user.setCompanyId("1");
    dbSetup.addUpdate(user);
    
    // re-validate the token
    dbSetup.addUpdate(token);
    
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
        .andReturn();
    
    // token invalid check
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
        .andReturn();
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    
    // set the valid expected status
    user.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
    dbSetup.addUpdate(user);
    
    // re-validate the token
    dbSetup.addUpdate(token);
    
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("signupMember")).andReturn();
    
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    
    // token set time that is in the past for days
    
    token.addParam(Constants.PARAM_TIME_UNIT, "DAYS");
    token.addParam(Constants.PARAM_EXPIRES_TIME, "2");
    
    // token.setTokenExpiresTime(2);
    // token.setTimeUnit(TimeUnit.DAYS);
    token.setCreatedTime(DateTime.now().minusDays(3).getMillis());
    dbSetup.addUpdate(token);
    
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
        .andReturn();
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    
    // token set time that is in the past for minutes
    token.addParam(Constants.PARAM_TIME_UNIT, "MINUTES");
    token.addParam(Constants.PARAM_EXPIRES_TIME, "10");
    
    // token.setTokenExpiresTime(10);
    // token.setTimeUnit(TimeUnit.MINUTES);
    token.setCreatedTime(DateTime.now().minusMinutes(100).getMillis());
    dbSetup.addUpdate(token);
    
    result = mockMvc
        .perform(get("/processToken/1").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
        .andReturn();
    log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
    testSmtp.stop();
  }
  
}
