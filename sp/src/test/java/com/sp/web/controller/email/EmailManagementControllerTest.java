package com.sp.web.controller.email;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.form.email.EmailManagementForm;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.email.EmailManagementFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailManagementControllerTest extends SPTestLoggedInBase {

  @Autowired
  EmailManagementFactory emailManagementFactory;
  
  @Test
  public void testGetCompanies() {
    try {
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      List<Company> all = dbSetup.getAll(Company.class);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/getCompanies")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companies", hasSize(all.size())))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addEmailManagement(Constants.DEFAULT_COMPANY_ID);
      addEmailManagement("1");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/getCompanies")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companies", hasSize(all.size() - 1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAll() {
    try {
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding email management
      addEmailManagement(Constants.DEFAULT_COMPANY_ID);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding email management
      addEmailManagement("1");

      result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGet() {
    try {
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/emailManagement/get")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding email management
      addEmailManagement(Constants.DEFAULT_COMPANY_ID);
      
      emailManagementFactory.reset();
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/get")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getDefaultUser();
      addEmailManagement(user.getCompanyId());

      emailManagementFactory.reset();
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/get")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.emailManagement").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetNotifications() {
    try {
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/emailManagement/getNotifications")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  

  @Test
  public void testCreateUpdate() {
    try {
      
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      final ObjectMapper om = new ObjectMapper();
      
      EmailManagementForm form = new EmailManagementForm();
      form.setCompanyId("1");
      String content = om.writeValueAsString(form);
      
      // type is required
      MvcResult result = this.mockMvc
          .perform(
              post("/emailManagement/createUpdate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setType(NotificationType.AddMember);
      content = om.writeValueAsString(form);
      
      // content is required
      result = this.mockMvc
          .perform(
              post("/emailManagement/createUpdate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Content is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<String, String> contentMap = new HashMap<String, String>();
      contentMap.put("something", "and something more.");
      form.setContent(contentMap);
      content = om.writeValueAsString(form);

      result = this.mockMvc
          .perform(
              post("/emailManagement/createUpdate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<EmailManagement> all = dbSetup.getAll(EmailManagement.class);
      
      assertThat(all.size(), is(greaterThan(0)));
      
      addEmailManagement(Constants.DEFAULT_COMPANY_ID);
      
      EmailManagement emailManagement = emailManagementFactory.get("2");
      assertThat(emailManagement, is(not(nullValue())));

      form.setCompanyId("2");
      content = om.writeValueAsString(form);

      result = this.mockMvc
          .perform(
              post("/emailManagement/createUpdate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testDelete() {
    try {
      
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      final ObjectMapper om = new ObjectMapper();
      
      EmailManagementForm form = new EmailManagementForm();
      final String companyId = "1";
      form.setCompanyId(companyId);
      String content = om.writeValueAsString(form);
      
      // type is required
      MvcResult result = this.mockMvc
          .perform(
              post("/emailManagement/delete")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setType(NotificationType.AddMember);
      content = om.writeValueAsString(form);
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/delete")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addEmailManagement(companyId);

      List<EmailManagement> all = dbSetup.getAll(EmailManagement.class);
      assertThat(all.size(), is(equalTo(1)));
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/delete")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(EmailManagement.class);
      assertThat(all.size(), is(equalTo(0)));      
      
      addEmailManagement(companyId);
      all = dbSetup.getAll(EmailManagement.class);
      EmailManagement emailManagement = all.get(0);
      emailManagement.getContent().put(NotificationType.AccountExpiredAccount, new HashMap<String, String>());
      emailManagementFactory.put(emailManagement);
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/delete")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(EmailManagement.class);
      assertThat(all.size(), is(equalTo(1)));      

      form.setType(null);
      form.setDeleteAll(true);
      content = om.writeValueAsString(form);
      
      result = this.mockMvc
          .perform(
              post("/emailManagement/delete")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(EmailManagement.class);
      assertThat(all.size(), is(equalTo(0)));      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testCompanyPermission() {
    try {
      
      // remove any previously created users
      dbSetup.removeAll("emailManagement");
      
      final ObjectMapper om = new ObjectMapper();
      
      EmailManagementForm form = new EmailManagementForm();
      String content = om.writeValueAsString(form);
      
      // company not found
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/companyPermission")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      form.setCompanyId("abc");
      content = om.writeValueAsString(form);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/companyPermission")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      addEmailManagement(Constants.DEFAULT_COMPANY_ID);
      
      final String companyId = "1";
      form.setCompanyId(companyId);
      content = om.writeValueAsString(form);
      
//      result = this.mockMvc
//          .perform(
//              post("/sysAdmin/emailManagement/companyPermission")
//              .content(content)
//              .contentType(MediaType.APPLICATION_JSON).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(jsonPath("$.error.InvalidRequestException").value("Email management not set for company."))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      addEmailManagement(companyId);
      emailManagementFactory.reset();
      form.setCompanyAllowed(true);
      content = om.writeValueAsString(form);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/emailManagement/companyPermission")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
      
      
  private void addEmailManagement(String companyId) {
    // adding email management
    EmailManagement emailManagement = new EmailManagement();
    emailManagement.setCompanyId(companyId);
    final Map<NotificationType, Map<String, String>> content = 
              new HashMap<NotificationType, Map<String, String>>();
    Map<String, String> value = new HashMap<String, String>();
    value.put("Template", "Some template");
    value.put("Subject", "Some subject");
    value.put("Section1", "section 1 content.");
    content.put(NotificationType.AddMember, value);
    emailManagement.setContent(content);
    dbSetup.addUpdate(emailManagement);
  }
}
