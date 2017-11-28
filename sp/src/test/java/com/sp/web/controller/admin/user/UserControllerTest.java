package com.sp.web.controller.admin.user;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.form.Operation;
import com.sp.web.model.Address;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserMessage;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.user.UserRepository;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The user controller tests.
 */
public class UserControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  UserRepository userRepository;
  
  @Test
  public void testUserCertificate() {
    User user = dbSetup.getUser("dax@surepeople.com");
    assertThat(user.getCertificateNumber(), is(nullValue()));
    userRepository.updateUserCertificate(user);
    assertThat(user.getCertificateNumber(), is(notNullValue()));
    log.debug("User certificate :" + user.getCertificateNumber());
  }
  
  @Test
  public void testGetUserInfo() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      
      // add a team
      dbSetup.createUsers();
      
      String adminEmail = "admin@admin.com";
      MvcResult result = this.mockMvc
          .perform(post("/admin/user").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.profileForm").exists())
          .andExpect(jsonPath("$.success.profileForm.email").value(adminEmail))
          .andExpect(jsonPath("$.success.groupList").doesNotExist()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.addGroups();
      
      // Request with invalid user
      String userEmail = "dax@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail + "Invalid")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax@surepeople.comInvalid: not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // proper request
      final String GroupName = "Executive";
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", GroupName)
                  .param("memberEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.profileForm").exists())
          .andExpect(jsonPath("$.success.profileForm.email").value(userEmail))
          .andExpect(jsonPath("$.success.groupList").exists())
          .andExpect(jsonPath("$.success.groupList[0].name").value(GroupName)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add tags and address to user
      List<String> tagList = new ArrayList<String>();
      String tag1 = "Super";
      tagList.add(tag1);
      tagList.add("Test");
      dbSetup.addTagsUser(userEmail, tagList);
      
      final User user = dbSetup.getUser(userEmail);
      Address address = new Address();
      address.setAddressLine1("address1");
      address.setAddressLine2("address2");
      address.setCity("New Delhi");
      address.setState("Delhi");
      address.setZipCode("12345");
      address.setCountry("India");
      user.setAddress(address);
      user.addRole(RoleType.IndividualAccountAdministrator);
      user.setCreatedOn(LocalDate.now().minusMonths(10));
      dbSetup.addUpdate(user);
      userRepository.updateUserCertificate(user);
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.profileForm").exists())
          .andExpect(jsonPath("$.success.profileForm.email").value(userEmail))
          .andExpect(jsonPath("$.success.groupList").exists())
          .andExpect(jsonPath("$.success.groupList[0].name").value(GroupName))
          .andExpect(jsonPath("$.success.tagList").exists())
          .andExpect(jsonPath("$.success.tagList[0]").value(tag1))
          .andExpect(jsonPath("$.success.userCertificate").exists())
          .andExpect(jsonPath("$.success.createdOn").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateUserProfile() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      
      // invalid update request no users in db
      
      String email = "dax@surepeople.com";
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/user/update/profile").param("firstName", "Dax")
                  .param("lastName", "Abraham").param("title", "Director").param("email", email)
                  .param("password", "abcd").param("company", "SurePeople").param("industry", "HR")
                  .param("noEmp", "10").param("country", "India").param("address1", "M-97, GK1")
                  .param("city", "Delhi").param("state", "Delhi").param("zip", "110048")
                  .param("phone", "9818399147").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User not found :dax@surepeople.com")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding users to the database
      dbSetup.createUsers();
      String firstName = "Dax";
      result = this.mockMvc
          .perform(
              post("/admin/user/update/profile").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director").param("email", email)
                  .param("day", "9").param("month", "9").param("year", "1977")
                  .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                  .param("state", "Delhi").param("zip", "110048")
                  .param("phoneNumber", "9818399147").param("gender", "M")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // validate the first name change
      firstName = "TestFirstName";
      result = this.mockMvc
          .perform(
              post("/admin/user/update/profile").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director").param("email", email)
                  .param("day", "9").param("month", "9").param("year", "1977")
                  .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                  .param("state", "Delhi").param("zip", "110048")
                  .param("phoneNumber", "9818399147").param("gender", "M")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser(email);
      assertThat("First name change !!!", user.getFirstName(), is(firstName));
      
      email = "admin@admin.com";
      result = this.mockMvc
          .perform(
              post("/admin/user/update/profile").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director").param("email", email)
                  .param("day", "9").param("month", "9").param("year", "1977")
                  .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                  .param("state", "Delhi").param("zip", "110048")
                  .param("phoneNumber", "9818399147").param("gender", "M")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(email);
      assertThat("First name change !!!", user.getFirstName(), is(firstName));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdatePermissions() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      String userEmail = "dax@surepeople.com";
      
      // invalid request with incorrect email.
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail + "11")
                  .param("isAdministrator", "false").param("isAccountAdministrator", "false")
                  .param("isHiringToolAllowed", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax@surepeople.com11: not found !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid request with correct email of the administrator
      
      userEmail = "admin@admin.com";
      result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail)
                  .param("isAdministrator", "true").param("isAccountAdministrator", "true")
                  .param("isHiringToolAllowed", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :admin@admin.com: is the current Administrator !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // Valid request with correct email
      dbSetup.createUsers();
      
      userEmail = "dax@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail)
                  .param("isAdministrator", "true").param("isAccountAdministrator", "true")
                  .param("isHiringToolAllowed", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.roles").value(
                  contains("User", "Administrator", "AccountAdministrator", "Hiring"))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // no account administrator
      result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail)
                  .param("isAdministrator", "true").param("isAccountAdministrator", "false")
                  .param("isHiringToolAllowed", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.roles").value(contains("User", "Administrator", "Hiring")))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // no administrator
      result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail)
                  .param("isAdministrator", "false").param("isAccountAdministrator", "true")
                  .param("isHiringToolAllowed", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.roles").value(contains("User", "Hiring"))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid remove request
      result = this.mockMvc
          .perform(
              post("/admin/user/updatePermissions").param("userEmail", userEmail)
                  .param("isAdministrator", "false").param("isAccountAdministrator", "false")
                  .param("isHiringToolAllowed", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/user").param("userEmail", userEmail).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.roles").value(contains("User"))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testSetTags() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      String userEmail = "dax@surepeople.com";
      
      // invalid request with incorrect email.
      String tagName = "TestTag";
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("userEmail", userEmail + "11")
                  .param("tagName", tagName).param("op", Operation.SET.toString())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax@surepeople.com11: not found !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      // Valid request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("userEmail", userEmail).param("tagName", tagName)
                  .param("op", Operation.SET.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tagList[0]").value(WordUtils.capitalizeFully(tagName)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // Valid repeat request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("userEmail", userEmail).param("tagName", tagName)
                  .param("op", Operation.SET.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User dax@surepeople.com already has the tag Testtag !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // Valid remove request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("userEmail", userEmail).param("tagName", tagName)
                  .param("op", Operation.REMOVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tagList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // Valid repeat remove request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("userEmail", userEmail).param("tagName", tagName)
                  .param("op", Operation.REMOVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User dax@surepeople.com does not have the tag Testtag !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // setting tag for the logged in user
      result = this.mockMvc
          .perform(
              post("/admin/user/tag").param("tagName", tagName).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testSetRemoveLinkedInUrl() {
    try {
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      
      // remove any previously created users
      final String userEmail = "dax@surepeople.com";
      
      String productId = "5";
      String accountId = "1";
      updateIndividualUser(productId, accountId);
      
      String linkedInUrl = "some linked in url";
      
      // Valid request with correct email
      MvcResult result = this.mockMvc
          .perform(
              post("/updateLinkedIn").param("linkedInUrl", linkedInUrl)
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), is(linkedInUrl));
      
      result = this.mockMvc
          .perform(
              post("/updateLinkedIn").param("linkedInUrl", linkedInUrl)
                  .param("op", Operation.REMOVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), isEmptyOrNullString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAdminSetRemoveLinkedInUrl() {
    try {
      // remove any previously created users
      String userEmail = "dax@surepeople.com";
      String linkedInUrl = "some linked in url";
      
      dbSetup.removeAllUsers();
      
      // invalid request with incorrect email.
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/updateLinkedIn").param("userEmail", userEmail)
                  .param("linkedInUrl", linkedInUrl).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax@surepeople.com: not found !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      dbSetup.createUsers();
      
      // Valid request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/updateLinkedIn").param("userEmail", userEmail)
                  .param("linkedInUrl", linkedInUrl).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), is(linkedInUrl));
      
      result = this.mockMvc
          .perform(
              post("/admin/updateLinkedIn").param("userEmail", userEmail)
                  .param("linkedInUrl", linkedInUrl).param("op", Operation.REMOVE.toString())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), isEmptyOrNullString());
      
      // for self
      authenticationHelper.authenticateUser(session);
      
      userEmail = "admin@admin.com";
      // Valid request with correct email
      result = this.mockMvc
          .perform(
              post("/admin/updateLinkedIn").param("linkedInUrl", linkedInUrl)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), is(linkedInUrl));
      
      result = this.mockMvc
          .perform(
              post("/admin/updateLinkedIn").param("linkedInUrl", linkedInUrl)
                  .param("op", Operation.REMOVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(userEmail);
      
      assertThat("Linked in URL set", user.getLinkedInUrl(), isEmptyOrNullString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAutoLeanring() throws Exception {
    // remove any previously created teams
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    
    // add a team
    dbSetup.createUsers();
    
    MvcResult result = this.mockMvc
        .perform(
            post("/updateAutoLearning").param("autoLearning", "true")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }

  @Test
  public void testRemoveMessage() throws Exception {
    // remove any previously created teams
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    
    // add a team
    dbSetup.createUsers();
    
    final String email = "admin@admin.com";
    User user = dbSetup.getUser(email);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/user/removeMessage")
            .param("uidList", "")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    result = this.mockMvc
        .perform(
            post("/user/removeMessage")
            .param("uidList", "123")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    user.addMessage(SPFeature.OrganizationPlan, "some message");
    UserMessage userMessage = user.getMessages().get(0);

    result = this.mockMvc
        .perform(
            post("/user/removeMessage")
              .param("uidList", "123, " + userMessage.getUid())
              .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    assertThat(user.getMessages(), is(empty()));
    user = dbSetup.getUser(email);
    assertThat(user.getMessages(), is(empty()));
    
  }
  
}
