package com.sp.web.controller.resume;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Before;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author pradeepruhil
 *
 */
public class SPResumeControllerTest extends SPTestLoggedInBase {
  
  /**
   * (non-Javadoc)
   * 
   * @see com.sp.web.mvc.test.setup.SPTestLoggedInBase#setUp()
   */
  @Override
  @Before
  public void setUp() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.createUsers();
    dbSetup.createCompanies();
    
    User user = new User();
    user.setEmail("individual@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.resume.SPResumeController#getResumeList(org.springframework.security.authentication.Authentication)}
   * .
   */
//  @Test
  public void testGetResumeList() throws Exception {
    dbSetup.removeAll("sPResume");
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/resume/getAllResumes").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.resume").isArray())
        .andExpect(jsonPath("$.success.resume", hasSize(0))).andReturn();
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/resume/createResume")
                .param("content", "<div> Your Resume is created</div>")
                .param("role", "Finance Manager").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/resume/getAllResumes").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.resume").exists())
        .andExpect(jsonPath("$.success.resume", hasSize(1))).andReturn();
    
  }
  
//  @Test
  public void createResume() throws Exception {
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/resume/createResume")
                .param("content", "<div> Your Resume is created</div>")
                .param("role", "Finance Manager").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
  }
  
}
