package com.sp.web.controller.external.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.external.rest.UserForm;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.test.setup.TestOauthHelper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SPRestExternalControllerTest extends SPTestBase {
  
  @Autowired
  private TestOauthHelper authHelper;
  
  @Test
  public void testCreateUser() throws Exception {
    
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.createUsers();
    dbSetup.createCompanies();
    
    UserForm userForm = new UserForm();
    userForm.setEmail("testing@yopmail.com");
    userForm.setUid("1003");
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(userForm);
    System.out.println(request);
    User user = dbSetup.getUser("admin@admin.com");
    String accesToken = obtainAccessToken("admin@admin.com","admin");
    
//    MvcResult result = mockMvc
//        .perform(
//            MockMvcRequestBuilders.post("/rest/user/create").session(session).with(")
//                .content(request).contentType(MediaType.APPLICATION_JSON))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//        .andExpect(jsonPath("$.success").exists())
//        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
  }
  
  private String obtainAccessToken(String username, String password) throws Exception {
    
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "test");
    params.add("username", username);
    params.add("password", password);
    
    ResultActions result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/oauth/token").params(params)
                .with(httpBasic("test", "test"))
                .accept("application/json;charset=UTF-8")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"));
    
    String resultString = result.andReturn().getResponse().getContentAsString();
    
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }
}
