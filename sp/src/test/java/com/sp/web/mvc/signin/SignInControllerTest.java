/**
 * 
 */
package com.sp.web.mvc.signin;

import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.test.setup.DBSetup;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

/**
 * @author Pradeep Ruhil
 * 
 */
public class SignInControllerTest extends SPTestBase {

  private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void requiresAuthentication() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/signin")).andExpect(
        MockMvcResultMatchers.view().name("homeNotSignedIn"));
  }

  @Test
  public void userAuthenticates() throws Exception {
    DBSetup dbSetup = new DBSetup(mongoTemplate, passwordEncoder);
    dbSetup.removeAllUsers();
    dbSetup.createUsers();
    dbSetup.removeAllCompanies();
    dbSetup.createCompanies();
    
    final String username = "admin@admin.com";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/loginProcess").param("email", username)
                .param("password", "admin"))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/home"))
        .andExpect(new ResultMatcher() {
          public void match(MvcResult mvcResult) throws Exception {
            HttpSession session = mvcResult.getRequest().getSession();
            SecurityContext securityContext = (SecurityContext) session
                .getAttribute(SEC_CONTEXT_ATTR);
            Assert.assertEquals(
                ((User) (securityContext.getAuthentication().getPrincipal())).getEmail(), username);
          }
        });
  }

  @Test
  public void userAuthenticateFails() throws Exception {
    final String username = "admin@admin.com";
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/loginProcess").param("email", username)
                .param("password", "test"))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/signin?login_error=1"))
        .andExpect(new ResultMatcher() {
          public void match(MvcResult mvcResult) throws Exception {
            HttpSession session = mvcResult.getRequest().getSession();
            SecurityContext securityContext = (SecurityContext) session
                .getAttribute(SEC_CONTEXT_ATTR);
            Assert.assertNull(securityContext);
          }
        });
  }

}