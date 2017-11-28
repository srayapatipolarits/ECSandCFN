package com.sp.web.controller.reset;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.icegreen.greenmail.util.GreenMailUtil;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.test.setup.AuthenticationHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.mail.Message;

public class ResetControllerTest extends SPTestBase {
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  private boolean isMockSmtp;
  
  @Autowired
  protected AuthenticationHelper authenticationHelper;
  
  @Before
  public void setup() {
    testSmtp.start();
    isMockSmtp = true;
  }
  
  @After
  public void stop() {
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }
  
  @Test
  public void testSendResetEmail() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.createUsers();
    final String username = "dax@surepeople.com";
    ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/sendResetEmail")
        .param("email", username).contentType(MediaType.TEXT_PLAIN).session(session));

    perform.andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    if (isMockSmtp) {
      testSmtp.waitForIncomingEmail(1);
      Message[] messages = testSmtp.getReceivedMessages();
      
      // assertEquals("Reset Password", messages[0].getSubject());
      String body = GreenMailUtil.getBody(messages[0]).replaceAll("\r\n\r\n", "");
      
      String tokenUrl = body.substring((body.indexOf("/sp")+3), body.indexOf(">RESET")-1);
      
      ResultActions performToken = mockMvc.perform(MockMvcRequestBuilders.get(tokenUrl)
          .contentType(MediaType.TEXT_PLAIN).session(session));
      
      performToken.andExpect(MockMvcResultMatchers.view().name("changePassword"));
      
      ResultActions setNewPassword = mockMvc.perform(MockMvcRequestBuilders.post("/newPassword")
          .param("password", "password").contentType(MediaType.TEXT_PLAIN).session(session));
      
      setNewPassword.andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      tokenUrl = tokenUrl + "Invalid";
      
      ResultActions invalidToken = mockMvc.perform(MockMvcRequestBuilders.get(tokenUrl)
          .contentType(MediaType.TEXT_PLAIN).session(session));
      
      invalidToken.andExpect(MockMvcResultMatchers.view().name("redirect:http://www.surepeople.com/?status=404"));
      session.removeAttribute("admin@admin.comtoken");
      ResultActions setPasswordInvalidSession = mockMvc.perform(MockMvcRequestBuilders
          .post("/newPassword").param("password", "password").contentType(MediaType.TEXT_PLAIN)
          .session(session));
      
      setPasswordInvalidSession
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidTokenException").value(
                  "Token is invalid")).andReturn();
    }
  }
  
  @Test
  public void testChangePassword() throws Exception{
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.createUsers();
    dbSetup.createCompanies();
    
    User user = new User();
    user.setEmail("individual@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
   MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/changePassword")
                .param("oldPassword", "password")
                .param("newPassword", "surepeople").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
  }
  
}
