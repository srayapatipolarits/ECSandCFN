package com.sp.web.controller.hiring;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.form.hiring.user.HiringReferenceForm;
import com.sp.web.form.hiring.user.HiringUserProfileForm;
import com.sp.web.model.Address;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPMedia;
import com.sp.web.model.SPMediaType;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.model.hiring.user.HiringComment;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.role.HiringRoleFactoryCache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HiringProfileControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringRoleFactoryCache roleFactory;
  
  @Test
  public void testGetProfileDetails() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // invalid request no candidate
      String userId = "abc";
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getProfileDetails")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      userId = hiringUser.getId();
      
      result = this.mockMvc
          .perform(
              post("/hiring/getProfileDetails")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringUser").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add the references to the hiring user
      addComments(hiringUser);
      result = this.mockMvc
          .perform(
              post("/hiring/getProfileDetails")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringUser").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setType(UserType.Member);
      dbSetup.addUpdate(hiringUser);
      result = this.mockMvc
          .perform(
              post("/hiring/getProfileDetails")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringUser").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final User user = dbSetup.getUser();
      HiringUser hiringUser2 = new HiringUser(user);
      user.addRole(RoleType.Hiring);
      user.addRole(RoleType.HiringEmployee);
      dbSetup.addUpdate(hiringUser2);

      result = this.mockMvc
          .perform(
              post("/hiring/getProfileDetails")
              .param("userId", hiringUser2.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringUser").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateProfileDetails() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      ObjectMapper om = new ObjectMapper();
      HiringUserProfileForm form = new HiringUserProfileForm();
      
      // invalid request no candidate
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/updateProfile")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setId("abc");
      
      result = this.mockMvc
          .perform(
              post("/hiring/updateProfile")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      form.setId(hiringUser.getId());
      form.setFirstName("Aisha");
      form.setLastName("Gupta");
      form.setTitle("SuperStar");
      form.setGender(Gender.F);
      form.setPhoneNumber("9818399147");
      form.setDob(LocalDate.now().minusYears(10));
      Address address = new Address();
      address.setCountry("India");
      address.setAddressLine1("M-97, GK1");
      address.setAddressLine2("Arya samaj mandir");
      address.setCity("New Delhi");
      address.setState("Delhi");
      address.setZipCode("110048");
      form.setAddress(address);
      
      result = this.mockMvc
          .perform(
              post("/hiring/updateProfile")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Last name changed !!!", hiringUser.getLastName(), is("Gupta"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testGetProfileDetailsExtEmployee() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      addEmployee("aisha@yopmail.com");
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      MvcResult result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("hiringCompleteProfile"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/getProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
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
  public void testUpdateProfileDetailsExtEmployee() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      addEmployee("aisha@yopmail.com");
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      MvcResult result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("hiringCompleteProfile"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/getProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final ObjectMapper om = new ObjectMapper();
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/updateProfile")
              .param("firstName", "Aisha")
              .param("lastName", "Gupta")
              .param("email", "aisha@yopmail.com")
              .param("gender", "F")
              .param("dob", om.writeValueAsString(LocalDate.now().minusYears(10)))
              .param("country", "India")
              .param("address1", "M-97, GK1")
              .param("city", "New Delhi")
              .param("state", "Delhi")
              .param("zip", "110048")              
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      assertThat(all, hasSize(1));
      assertThat(all.get(0).getUserStatus(), equalTo(UserStatus.ASSESSMENT_PENDING));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  private void addEmployee(String email) throws Exception {
    final ObjectMapper om = new ObjectMapper();
    HiringAddForm form = new HiringAddForm();
    final List<String> emails = new ArrayList<String>();
    emails.add(email);
    form.setEmails(emails);
    form.setType(UserType.Member);

    MvcResult result = this.mockMvc
        .perform(
            post("/hiring/add")
            .content(om.writeValueAsString(form))
            .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }

  @Test
  public void testGetProfileDetailsExtCandidate() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAll("hiringRole");

      addCandidate("aisha@yopmail.com");
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      MvcResult result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("hiringCompleteProfile"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/getProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring.type").value("HiringCandidate"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  

  @Test
  public void testUpdateProfileDetailsExtCandidate() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAll("hiringRole");

      addCandidate("aisha@yopmail.com");
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      MvcResult result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("hiringCompleteProfile"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/getProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring.type").value("HiringCandidate"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final ObjectMapper om = new ObjectMapper();
      HiringUserProfileForm form = new HiringUserProfileForm();
      form.setFirstName("Aisha");
      form.setLastName("Gupta");
      form.setTitle("SuperStar");
      form.setGender(Gender.F);
      form.setPhoneNumber("9818399147");
      form.setDob(LocalDate.now().minusYears(10));
      Address address = new Address();
      address.setCountry("India");
      address.setAddressLine1("M-97, GK1");
      address.setAddressLine2("Arya samaj mandir");
      address.setCity("New Delhi");
      address.setState("Delhi");
      address.setZipCode("110048");
      form.setAddress(address);      
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/updateProfile")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      assertThat(all, hasSize(1));
      assertThat(all.get(0).getUserStatus(), equalTo(UserStatus.ADD_REFERENCES));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testAddReferenceExtCandidate() {
    try {
      // remove any previously created users
      dbSetup.removeAllFeedbackUsers();

      testUpdateProfileDetailsExtCandidate();

      final ObjectMapper om = new ObjectMapper();
      HiringReferenceForm form = new HiringReferenceForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("References required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final List<HiringLensForm> referenceList = new ArrayList<HiringLensForm>();
      HiringLensForm lensForm = new HiringLensForm();
      referenceList.add(lensForm);
      form.setReferenceList(referenceList);
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Reference count mismatch."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      referenceList.add(lensForm);

      result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("First name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      referenceList.clear();
      referenceList.add(lensForm);
      lensForm.setFirstName("Aisha");
      lensForm.setLastName("Abraham");
      lensForm.setEmail("aisha@yopmail.com");
      lensForm.setPhoneNumber("9818399147");
      lensForm.setReferenceType("Ref1");
      
      final HiringLensForm lensForm2 = new HiringLensForm();
      lensForm2.setFirstName("Aisha2");
      lensForm2.setLastName("Abraham2");
      lensForm2.setEmail("aisha1@yopmail.com");
      lensForm2.setPhoneNumber("9818399147");
      lensForm2.setReferenceType("Rif3");
      referenceList.add(lensForm2);
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot add self as reference."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      lensForm.setEmail("aisha2@yopmail.com");
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Reference types mismatch."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      lensForm2.setReferenceType("Ref2");
      
      log.debug("Request content:" + om.writeValueAsString(form));
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/addReferences")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      assertThat(all, hasSize(1));
      assertThat(all.get(0).getUserStatus(), equalTo(UserStatus.ASSESSMENT_PENDING));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }    
  
  private void addCandidate(String email) throws Exception {
    final ObjectMapper om = new ObjectMapper();
    HiringAddForm form = new HiringAddForm();
    final List<String> emails = new ArrayList<String>();
    emails.add(email);
    form.setEmails(emails);
    form.setType(UserType.HiringCandidate);
    Set<String> roleIds = new HashSet<String>();
    form.setRoleIds(roleIds);
    User user = dbSetup.getUser();
    HiringRole addRole = addRole(user, "TestRole");
    roleIds.clear();
    roleIds.add(addRole.getId());
    Set<String> tags = new HashSet<String>();
    tags.add("Test1");
    tags.add("something");
    form.setTags(tags);
    List<List<String>> referenceTypes = new ArrayList<List<String>>();
    referenceTypes.add(Arrays.asList("Ref1", "Ref2"));
    referenceTypes.add(Arrays.asList("Ref2"));
    form.setReferenceTypes(referenceTypes);
    form.setReferenceCheck(true);

    MvcResult result = this.mockMvc
        .perform(
            post("/hiring/add")
            .content(om.writeValueAsString(form))
            .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }  

  private HiringRole addRole(User user, String name) {
    HiringRole role = new HiringRole();
    role.setName(name);
    role.setCompanyId(user.getCompanyId());
    roleFactory.save(role);
    return role;
  }

  
  @Test
  public void testGetArchivedUserProfileDetails() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
      
      // invalid request no candidate
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getArchivedUserProfileDetails")
              .param("userId", "abcd")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with candidate
      HiringUserArchive archiveUser = addHiringCandidateArchive();
      
      result = this.mockMvc
          .perform(
              post("/hiring/getArchivedUserProfileDetails")
              .param("userId", archiveUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringUser").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private void addComments(HiringUser hiringUser) {
    User adminUser = dbSetup.getUser("admin@admin.com");
    User memberUser = dbSetup.getUser("dax@surepeople.com");
    List<HiringComment> commentList = new ArrayList<HiringComment>();
    commentList.add(new HiringComment(adminUser, "Test comment 1 !!!"));
    commentList.add(new HiringComment(memberUser, "Test comment 2 !!!"));
    commentList.add(new HiringComment(adminUser, "Test comment 3 !!!"));
    hiringUser.setComments(commentList);
    dbSetup.addUpdate(hiringUser);
  }

  @Test
  public void testAddRoles() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringRole");
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/addRole")
              .param("userId", hiringUser.getId())
              .param("roleId", "Role1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringRole addRole = addRole(user, "TestRole");
      
      result = this.mockMvc
          .perform(
              post("/hiring/addRole")
              .param("userId", hiringUser.getId())
              .param("roleId", addRole.getId())
              .param("addMatch", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertTrue(hiringUser.getHiringRoleIds().contains(addRole.getId()));

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testRemoveRoles() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringRole");
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/removeRole")
              .param("userId", hiringUser.getId())
              .param("roleId", "Role1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringRole addRole = addRole(user, "TestRole");
      hiringUser.getHiringRoleIds().clear();
      hiringUser.addHiringRole(addRole);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeRole")
              .param("userId", hiringUser.getId())
              .param("roleId", addRole.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat(hiringUser.getHiringRoleIds(), hasSize(0));

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testUpdateTags() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/updateTags")
              .param("userId", hiringUser.getId())
              .param("tagList", "Tag1, Tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Tags updated !!!", hiringUser.getTagList(), contains("Tag1", "Tag2"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateUrl() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      hiringUser.getProfileUrls().clear();
      dbSetup.addUpdate(hiringUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/addUpdateUrl")
              .param("userId", hiringUser.getId())
              .param("name", "Test Url")
              .param("url", "http://www.blah.com")
              .param("mediaType", SPMediaType.MSDOC + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      SPMedia spMedia = hiringUser.getUrls().get(0);
      assertThat("Web url updated !!!", spMedia.getUrl(), equalTo("http://www.blah.com"));
      assertThat("Profile Url updated !!!", hiringUser.getProfileUrls(), hasSize(0));
      
      result = this.mockMvc
          .perform(
              post("/hiring/addUpdateUrl")
              .param("userId", hiringUser.getId())
              .param("url", "http://www.linkedin.com/aisha")
              .param("profileUrl", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Profile Urls updated !!!", hiringUser.getProfileUrls().get(0).getUrl(), is("http://www.linkedin.com/aisha"));
      
      result = this.mockMvc
          .perform(
              post("/hiring/addUpdateUrl")
              .param("userId", hiringUser.getId())
              .param("url", "http://www.linkedin.com/pradeep")
              .param("profileUrl", "true")
              .param("index", "0")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Profile Urls updated !!!", hiringUser.getProfileUrls().get(0).getUrl(), is("http://www.linkedin.com/pradeep"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testRemoveUrl() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/removeUrl")
              .param("userId", hiringUser.getId())
              .param("url","http://linkedin.com/aishaAbraham")
              .param("profileUrl", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Profile Url updated !!!", hiringUser.getProfileUrls(), hasSize(0));
      final String urlStr = "http://www.blah.com";
      final SPMedia webUrl = new SPMedia("Test URL", urlStr, SPMediaType.MSDOC);
      hiringUser.addUrl(webUrl);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeUrl")
              .param("userId", hiringUser.getId())
              .param("url", urlStr)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Web url updated !!!", hiringUser.getUrls(), hasSize(0));
      assertThat("Profile Url updated !!!", hiringUser.getProfileUrls(), hasSize(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testAddComments() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      final String commentText = "She is really good, this is so cool.";
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/addComment")
              .param("userId", hiringUser.getId())
              .param("comment", commentText)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Roles updated !!!", hiringUser.getComments().get(0).getComment(),
          is(commentText));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeleteComments() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      final String userId = hiringUser.getId();
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/removeComment")
              .param("userId", userId)
              .param("cid", "0")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/addComment")
              .param("userId", userId)
              .param("comment", "She is really good, this is so cool.")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      String cid = hiringUser.getComments().get(0).getCid();
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeComment")
              .param("userId", userId)
              .param("cid", cid)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())              
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat("Roles updated !!!", hiringUser.getComments().size(),
          is(0));
      
      HiringUser dummyUser = new HiringUser();
      dummyUser.setId("123123");
      HiringComment addComment = hiringUser.addComment(dummyUser, "This is not coool !!!");
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeComment")
              .param("userId", userId)
              .param("cid", addComment.getCid())
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
  public void testUpdateComments() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringRole");
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      String userId = hiringUser.getId();
      
      String commentText = "This is the updated comment.";
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/updateComment")
              .param("userId", userId)
              .param("cid", "0")
              .param("comment", commentText)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/addComment")
              .param("userId", userId)
              .param("comment", "She is really good, this is so cool.")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      String cid = hiringUser.getComments().get(0).getCid();
      
      result = this.mockMvc
          .perform(
              post("/hiring/updateComment")
              .param("userId", userId)
              .param("cid", cid)
              .param("comment", commentText)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser = dbSetup.getHiringCandidate("dax@einstix.com", "1");
      assertThat(hiringUser.getComments().get(0).getComment(),
          is(commentText));
      
      HiringUser dummyUser = new HiringUser();
      dummyUser.setId("123123");
      HiringComment addComment = hiringUser.addComment(dummyUser, "This is not coool !!!");
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/updateComment")
              .param("userId", userId)
              .param("cid", addComment.getCid())
              .param("comment", commentText)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not comment owner."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testGetPublicProfileId() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getProfileShareId")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringPublicId").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testSharePortrait() {
    try {
      // remove any previously created users
      dbSetup.removeAllHiringUsers();

      testSmtp.start();
      
      final HiringUser hiringUser = addHiringCandidate();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringSharePortrait.toString())
              .param(Constants.PARAM_MEMBER_LIST, "abc")
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringSharePortrait.toString())
              .param(Constants.PARAM_MEMBER_LIST, hiringUser.getId())
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User analysis not found."))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      final AnalysisBean analysis = user.getAnalysis();
      Map<TraitType, BigDecimal> processing = analysis.getProcessing();
      processing.put(TraitType.External, BigDecimal.valueOf(40));
      processing.put(TraitType.Internal, BigDecimal.valueOf(60));
      Map<TraitType, BigDecimal> motivationWhy = analysis.getMotivationWhy();
      motivationWhy.put(TraitType.AttainmentOfGoals, BigDecimal.valueOf(50));
      motivationWhy.put(TraitType.RecognitionForEffort, BigDecimal.valueOf(50));
      hiringUser.setAnalysis(analysis);
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringSharePortrait.toString())
              .param(Constants.PARAM_MEMBER_LIST, hiringUser.getId())
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(8000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }  
}
