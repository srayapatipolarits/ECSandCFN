package com.sp.web.controller.hiring;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.Account;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.role.HiringRoleFactoryCache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HiringControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringRoleFactoryCache roleFactory;
  
  @Autowired
  HiringGroupFactory groupFactory;
  
  
  /**
   * Setup mock for mail sending.
   */
  @Before
  public void setUpNotificationController() {
    //helper.gateway = mock(CommunicationGateway.class);
    testSmtp.start();
  }
  
  /**
   * Cleanup.
   */
  @After
  public void cleanup() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }

  @Test
  public void testGetAvailableSubscriptions() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getAvailableSubscriptions")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      dbSetup.createCompanies();
      // invalid request no company
      result = this.mockMvc
          .perform(
              post("/hiring/getAvailableSubscriptions")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.availableHiringSubscripitons").value(20))
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
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringRole");
      dbSetup.removeAll("hiringGroup");

      final User user = dbSetup.getUser();
      final HiringGroupDTO addGroup = addGroup(user, "TestGroup");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getAll")
              .param("type", UserType.Member + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring").value(empty()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      hiringUser.addRole(RoleType.Hiring);
      dbSetup.addUpdate(hiringUser);
      
      addUserToGroup(user, addGroup, hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/getAll")
              .param("type", UserType.Member + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/getAll")
              .param("type", UserType.HiringCandidate + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring").value(empty()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      HiringUser hiringCandidate = addUser("Boom", "Shaka", "boom@yopmail.com", "Title",
          UserType.HiringCandidate, user.getCompanyId());
      
      HiringRole addRole = addRole(user, "Test Role");
      hiringCandidate.addHiringRole(addRole);
      dbSetup.addUpdate(hiringCandidate);
      
      addUserToGroup(user, addGroup, hiringCandidate);
      
      result = this.mockMvc
          .perform(
              post("/hiring/getAll")
              .param("type", UserType.HiringCandidate + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private void addUserToGroup(User user, final HiringGroupDTO group,
      HiringUser hiringUser) {
    HiringGroupForm form = new HiringGroupForm();
    form.setId(group.getId());
    List<String> userIds = new ArrayList<String>();
    userIds.add(hiringUser.getId());
    form.setUserIds(userIds);
    groupFactory.addUsers(user, form);
  }

  private HiringGroupDTO addGroup(User user, String string) {
    HiringGroupForm form = new HiringGroupForm();
    form.setName("TestGroup");
    final HiringGroupDTO create = groupFactory.create(user, form);
    return create;
  }
  
  private HiringRole addRole(User user, String name) {
    HiringRole role = new HiringRole();
    role.setName(name);
    role.setCompanyId(user.getCompanyId());
    roleFactory.save(role);
    return role;
  }

  @Test
  public void testGetAllMembers() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getAllMembers")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring").value(empty()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addHiringCandidate();
      User user = dbSetup.getUser();
      addUser("Boom", "Shaka", "boom@yopmail.com", "Title",
          UserType.HiringCandidate, user.getCompanyId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/getAllMembers")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private HiringUser addUser(String firstName, String lastName, String email, String title,
      UserType type, String companyId) {
    HiringUser user = new HiringUser();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setTitle(title);
    user.setType(type);
    user.setCompanyId(companyId);
    dbSetup.addUpdate(user);
    return user;
  }

  @Test
  public void testArchiveGetAll() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringUserArchive");

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/archiveGetAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring-archived").value(empty()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addHiringCandidateArchive();
      
      result = this.mockMvc
          .perform(
              post("/hiring/archiveGetAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiring-archived", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAllHiringRolesAndTags() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
      dbSetup.removeAll("hiringRole");

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getRolesAndTags")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoles", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      addRole(user, "TestRole");
      
      result = this.mockMvc
          .perform(
              post("/hiring/getRolesAndTags")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoles", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testRemoveHiringUser() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/delete")
              .param("userIds", "abc, def")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      
      Account account = dbSetup.getAccount("1");
      SPPlan plan = account.getPlan(SPPlanType.IntelligentHiring);
      final int numMember = plan.getNumMember();
      
      result = this.mockMvc
          .perform(
              post("/hiring/delete")
              .param("userIds", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      account = dbSetup.getAccount("1");
      plan = account.getPlan(SPPlanType.IntelligentHiring);
      assertThat("Hiring subscriptions", plan.getNumMember(),
          is(numMember + 1));
      
      hiringUser = addHiringCandidate();
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/delete")
              .param("userIds", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      account = dbSetup.getAccount("1");
      plan = account.getPlan(SPPlanType.IntelligentHiring);
      assertThat("Hiring subscriptions", plan.getNumMember(),
          is(numMember + 1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testRemoveHiringUserArchive() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/deleteArchiveUser")
              .param("userIds", "abc, def")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUserArchive hiringArchiveUser = addHiringCandidateArchive();
      
      result = this.mockMvc
          .perform(
              post("/hiring/deleteArchiveUser")
              .param("userIds", hiringArchiveUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUserArchive> all = dbSetup.getAll(HiringUserArchive.class);
      assertThat(all, hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testArchivePutBack() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();

      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/archivePutBack")
              .param("userIds", "aisha-archive@surepeople.com, def")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUserArchive hiringArchiveUser = addHiringCandidateArchive();
      
      result = this.mockMvc
          .perform(
              post("/hiring/archivePutBack")
              .param("userIds", hiringArchiveUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUserArchive> all = dbSetup.getAll(HiringUserArchive.class);
      assertThat(all, hasSize(0));
      
      List<HiringUser> all2 = dbSetup.getAll(HiringUser.class);
      assertThat(all2, hasSize(1));
      
      hiringArchiveUser = addHiringCandidateArchive();
      result = this.mockMvc
          .perform(
              post("/hiring/archivePutBack")
              .param("userIds", hiringArchiveUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User already in employee list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = all2.get(0);
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);
      result = this.mockMvc
          .perform(
              post("/hiring/archivePutBack")
              .param("userIds", hiringArchiveUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User already in candidate list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testArchiveHiringUser() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("hiringUserArchive");
      dbSetup.removeAll("activityLogMessage");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/archiveUser")
              .param("userIds", "abc, def")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();

      result = this.mockMvc
          .perform(
              post("/hiring/archiveUser")
              .param("userIds", hiringUser.getId())
              .param("isHired", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not completed assessment."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/archiveUser")
              .param("userIds", hiringUser.getId())
              .param("isHired", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUserArchive> archivedUsers = dbSetup.getAll(HiringUserArchive.class);
      assertThat(archivedUsers.size(), is(1));
      assertThat(archivedUsers.get(0).isHired(), equalTo(true));
      
      hiringUser = addHiringCandidate();
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      result = this.mockMvc
          .perform(
              post("/hiring/archiveUser")
              .param("userIds", hiringUser.getId())
              .param("isHired", "true")
              .param("tagList", "Test1, test2")
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
  public void testCheckHiringUser() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/checkCandidate")
              .param("candidateEmail", "dax@einstix.com, def")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addHiringCandidate();
      addHiringCandidateArchive();

      result = this.mockMvc
          .perform(
              post("/hiring/checkCandidate")
                  .param("candidateEmail",
                      "dax@einstix.com, dax@surepeople.com, admin@admin.com, aisha-archive@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
  
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAddHiringUserEmployee() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      final ObjectMapper om = new ObjectMapper();
      HiringAddForm form = new HiringAddForm();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Email required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<String> emails = new ArrayList<String>();
      emails.add("aisha@yopmail.com");
      form.setEmails(emails);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setType(UserType.Member);

      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.error").value("aisha@yopmail.com could not add users."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      assertThat(all.size(), equalTo(1));
      
      // adding multiple with other parameters.
      emails.clear();
      emails.add("user1@yopmail.com");
      emails.add("user2@yopmail.com");
      form.setDueBy(LocalDate.now().plusDays(10));
      Set<String> tags = new HashSet<String>();
      tags.add("Test1");
      tags.add("something");
      form.setTags(tags);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      all = dbSetup.getAll(HiringUser.class);
      assertThat(all.size(), equalTo(3));
      assertThat(all.get(1).getTagList().size(), equalTo(2));

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddHiringUserCandidate() {
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

      final ObjectMapper om = new ObjectMapper();
      
      HiringAddForm form = new HiringAddForm();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Email required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<String> emails = new ArrayList<String>();
      emails.add("aisha@yopmail.com");
      form.setEmails(emails);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setType(UserType.HiringCandidate);

      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Set<String> roleIds = new HashSet<String>();
      roleIds.add("abc");
      form.setRoleIds(roleIds);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringRole addRole = addRole(user, "TestRole");
      roleIds.clear();
      roleIds.add(addRole.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.error").value("aisha@yopmail.com could not add users."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      assertThat(all.size(), equalTo(1));
      
      // adding multiple with other parameters.
      emails.clear();
      emails.add("user1@yopmail.com");
      emails.add("user2@yopmail.com");
      form.setDueBy(LocalDate.now().plusDays(10));
      Set<String> tags = new HashSet<String>();
      tags.add("Test1");
      tags.add("something");
      form.setTags(tags);
      List<List<String>> referenceTypes = new ArrayList<List<String>>();
      referenceTypes.add(Arrays.asList("Ref1"));
      referenceTypes.add(Arrays.asList("Ref2"));
      form.setReferenceTypes(referenceTypes);
      form.setReferenceCheck(true);
      
      result = this.mockMvc
          .perform(
              post("/hiring/add")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      all = dbSetup.getAll(HiringUser.class);
      assertThat(all.size(), equalTo(3));
      assertThat(all.get(1).getTagList().size(), equalTo(2));

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testHireCandidate() {
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

      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("First name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", "abc")
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someone@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();

      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someone@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not a candidate."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someone@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Assessment pending."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      dbSetup.addUpdate(hiringUser);
      
      HiringUser addHiringCandidate = addHiringCandidate("someone@yopmail.com");
      
      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someone@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User already in employee list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addHiringCandidate.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(addHiringCandidate);

      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someone@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User already in candidate list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/hireCandidate")
              .param("id", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("title", "Title")
              .param("email", "someonenew@yopmail.com")
              .param("tags", "haha, hehe")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      hiringUser = all.get(1);
      assertThat(hiringUser.getType(), equalTo(UserType.Member));
      List<HiringUserArchive> all2 = dbSetup.getAll(HiringUserArchive.class);
      HiringUserArchive hiringUserArchive = all2.get(0);
      assertThat(hiringUserArchive.isHired(), equalTo(true));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testCompareProfile() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/compareProfile")
              .param("user1Id", "dax@einstix.com")
              .param("user2Id", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser1 = addHiringCandidate();
      
      // with the hiring candidate present
      
      result = this.mockMvc
          .perform(
              post("/hiring/compareProfile")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Analysis not found for user :dax@einstix.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addAnalysis(hiringUser1, PersonalityType.Refiner);
      
      // add the analysis for hiring candidate
      addAnaysisToHiring();
      
      result = this.mockMvc
      .perform(
          post("/hiring/compareProfile")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", "dax@surepeople.com")
          .contentType(MediaType.TEXT_PLAIN).session(session))
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$.error").exists())
      .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
      .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser2 = addHiringCandidate("someoneelse@yopmail.com");
      addAnalysis(hiringUser2, PersonalityType.Actuary);

      result = this.mockMvc
      .perform(
          post("/hiring/compareProfile")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", hiringUser2.getId())
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
  public void testCompareRelation() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/compareRelation")
              .param("user1Id", "dax@einstix.com")
              .param("user2Id", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser1 = addHiringCandidate();
      
      // with the hiring candidate present
      
      result = this.mockMvc
          .perform(
              post("/hiring/compareRelation")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Analysis not found for user :dax@einstix.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addAnalysis(hiringUser1, PersonalityType.Refiner);
      
      // add the analysis for hiring candidate
      addAnaysisToHiring();
      
      result = this.mockMvc
      .perform(
          post("/hiring/compareRelation")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", "dax@surepeople.com")
          .contentType(MediaType.TEXT_PLAIN).session(session))
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$.error").exists())
      .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
      .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser2 = addHiringCandidate("someoneelse@yopmail.com");
      addAnalysis(hiringUser2, PersonalityType.Actuary);

      result = this.mockMvc
      .perform(
          post("/hiring/compareRelation")
              .param("user1Id", hiringUser1.getId())
              .param("user2Id", hiringUser2.getId())
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
  public void testSendReminderCandidateEmployee() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAll("activityLogMessage");
      
      HiringUser hiringUser = addHiringCandidate();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringCandidateReminder.toString())
              .param(Constants.PARAM_MEMBER_LIST, hiringUser.getId())
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAddToErti() {
    try {
      testSmtp.start();
      
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = addHiringCandidate();
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not a member."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setType(UserType.Member);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Assessment pending."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setUserStatus(UserStatus.VALID);
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setEmail("admin@admin.com");
      dbSetup.addUpdate(hiringUser);

      List<User> allUsers = dbSetup.getAll(User.class);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User already part of Talent Development."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(dbSetup.getAll(User.class).size(), equalTo(allUsers.size()));

      final String email = "someone@yopmail.com";
      hiringUser.setEmail(email);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addToErti")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User newUser = dbSetup.getUser(email);
      assertNotNull(newUser);

      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      hiringUser = all.get(0);
      assertThat(hiringUser.isInErti(), equalTo(true));
      
      Thread.sleep(8000);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }
  
  @Test
  public void testAddAdministrator() {
    try {
      testSmtp.start();
      
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = addHiringCandidate();
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Assessment pending. dax@einstix.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setUserStatus(UserStatus.VALID);
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setEmail("admin@admin.com");
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not a member."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setType(UserType.Member);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("User already administrator."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser();
      user.removeRole(RoleType.Hiring);
      dbSetup.addUpdate(user);
      
      Account account = dbSetup.getAccount("1");
      SPPlan plan = account.getPlan(SPPlanType.IntelligentHiring);
      plan.setNumAdmin(0);
      dbSetup.addUpdate(account);

      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.SPException")
                  .value(
                      "com.sp.web.exception.SPException: Cannot send the invite, "
                      + "administrator subscription not availble for the plan"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      plan.setNumAdmin(2);
      dbSetup.addUpdate(account);
      
      List<User> allUsers = dbSetup.getAll(User.class);

      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(dbSetup.getAll(User.class).size(), equalTo(allUsers.size()));
      user = dbSetup.getUser();
      assertTrue(user.hasRole(RoleType.Hiring));

      final String email = "someone@yopmail.com";
      hiringUser.setEmail(email);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/addAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User newUser = dbSetup.getUser(email);
      assertNotNull(newUser);
      assertTrue(!newUser.hasRole(RoleType.User));

      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      hiringUser = all.get(0);
      assertThat(hiringUser.hasRole(RoleType.Hiring), equalTo(true));
      
      Thread.sleep(8000);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }  

  @Test
  public void testRemoveAdministrator() {
    try {
      testSmtp.start();
      
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();

      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())    
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = addHiringCandidate();
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Assessment pending. dax@einstix.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setUserStatus(UserStatus.VALID);
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setEmail("admin@admin.com");
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not a member."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringUser.setType(UserType.Member);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("User not an administrator."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.addRole(RoleType.Hiring);
      dbSetup.addUpdate(hiringUser);
      
      List<User> allUsers = dbSetup.getAll(User.class);
      for (User usr : allUsers) {
        if (usr.hasRole(RoleType.Hiring)) {
          usr.removeRole(RoleType.Hiring);
          dbSetup.addUpdate(usr);
        }
      }
       
      user = dbSetup.getUser();
      user.addRole(RoleType.Hiring);
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one administrator required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String email = "someone@yopmail.com";
      User newUser = new User(hiringUser);
      newUser.setEmail(email);
      newUser.addRole(RoleType.Hiring);
      newUser.addRole(RoleType.HiringEmployee);
      dbSetup.addUpdate(newUser);
      allUsers = dbSetup.getAll(User.class);

      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(dbSetup.getAll(User.class).size(), equalTo(allUsers.size()));
      user = dbSetup.getUser();
      assertTrue(user.hasRole(RoleType.Hiring));

      hiringUser.setEmail(email);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/removeAdministrator")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      newUser = dbSetup.getUser(email);
      assertNull(newUser);

      List<HiringUser> all = dbSetup.getAll(HiringUser.class);
      hiringUser = all.get(0);
      assertThat(hiringUser.hasRole(RoleType.Hiring), equalTo(false));
      
      //Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }  
  
  private void addAnalysis(User user, PersonalityType personality) {
    AnalysisBean analysisBean = new AnalysisBean();
    analysisBean.setAccuracy(BigDecimal.valueOf(0.93));
    HashMap<RangeType, PersonalityBeanResponse> personalityType = new HashMap<RangeType, PersonalityBeanResponse>();
    personalityType.put(RangeType.Primary, new PersonalityBeanResponse(personality, 1234));
    personalityType.put(RangeType.UnderPressure, new PersonalityBeanResponse(personality, 1234));
    analysisBean.setPersonality(personalityType);
    user.setAnalysis(analysisBean);
    dbSetup.addUpdate(user);
  }

  private void addAnaysisToHiring() {
    addAnaysisToHiring(PersonalityType.Designer);
  }
  
  private void addAnaysisToHiring(PersonalityType personality) {
    addAnaysisToHiring(personality,"dax@einstix.com");
  }
  
  private void addAnaysisToHiring(PersonalityType personalityType, String email) {
    HiringUser hiringUser = dbSetup.getHiringCandidate(email, "1");
    assertNotNull("Hiring user required !!!", hiringUser);
    addAnalysis(hiringUser, personalityType);
  }
  
}
