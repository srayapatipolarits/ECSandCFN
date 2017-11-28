package com.sp.web.controller.admin.member;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.exception.SignInFailedException;
import com.sp.web.exception.SignInFailedException.SignInFailReason;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

public class MemberControllerTest extends SPTestLoggedInBase {

  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  MemberControllerHelper helper;
  private boolean isProxyEmail;

  @Before
  public void setUp1() throws Exception {
    isProxyEmail = true;
    testSmtp.start();
  }
  
  @After
  public void after() {
    testSmtp.stop();
  }

  @Test
  public void testGetTagList() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();

      // invalid request existing user
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/getAllTags")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tagList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      User user = dbSetup.getUser("admin@admin.com");
      ArrayList<String> tagList = new ArrayList<String>();
      tagList.add("Tag1");
      tagList.add("Tag2");
      user.setTagList(tagList);
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/admin/member/getAllTags")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tagList", hasSize(tagList.size())))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("dax@surepeople.com");
      tagList.add("Tag3");
      user.setTagList(tagList);
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/admin/member/getAllTags")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tagList", hasSize(tagList.size())))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
      
  @Test
  public void testAddMemberSingle() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.createUsers();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllLogs();

      String userEmail = "dax@surepeople.com";
      String firstName = "Dax";

      // invalid request existing user
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "An account already exists with the given email address.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat("No messages.", testSmtp.getReceivedMessages().length, is(0));
      
      //verify(helper.gateway, times(0)).sendMessage(any(EmailParams.class));

      // valid request existing user
      userEmail = "dax@einstix.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(5000);

      if (isProxyEmail) {
        assertThat("No messages.", testSmtp.getReceivedMessages().length, is(1));
      }

      // repeat request existing user
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "An account already exists with the given email address.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", "individual@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException")
                  .value(MessagesHelper.getMessage("exception.add.member.exists")))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      if (isProxyEmail) {
        assertThat("No messages.", testSmtp.getReceivedMessages().length, is(1));
      }

      // invalid request existing user and tags and incorrect group
      // associations
      userEmail = "dax2@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "tag1:false, tag2:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :tag1 found in company :1")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      dbSetup.addGroups();

      // valid request existing user and tags and group associations
      userEmail = "dax2@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "Executive:false, AnotherGroup:true")
                  .param("isAdministrator", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      Thread.sleep(5000);

      if (isProxyEmail) {
        assertThat("No messages.", testSmtp.getReceivedMessages().length, is(2));
      }

      // valid request new user but account exhausted
      dbSetup.exhaustAccount();
      userEmail = "dax3@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle").param("firstName", firstName)
                  .param("lastName", "Abraham").param("title", "Director")
                  .param("email", userEmail).param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "Executive:false, AnotherGroup:true")
                  .param("isAdministrator", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No subscriptions left for company :1")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testValidateEmails() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();

      String userEmail = "dax@surepeople.com";
      MvcResult result = this.mockMvc
          .perform(
              post("/signup/validateEmails")
                  .param("memberList", userEmail, "test@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.successList", hasSize(2)))
          .andExpect(jsonPath("$.success.failureList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // creating the users and testing one of the emails will already
      // exist
      dbSetup.createUsers();
      result = this.mockMvc
          .perform(
              post("/signup/validateEmails")
                  .param("memberList", userEmail, "test@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.successList", hasSize(1)))
          .andExpect(jsonPath("$.success.failureList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAddMemberMulti() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.createUsers();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeAllLogs();

      String userEmail = "dax@surepeople.com";

      // valid request with one existing user
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/addMultiple")
                  .param("memberList", userEmail, "test@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.successList", hasSize(1)))
          .andExpect(jsonPath("$.success.failureList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // repeat request with one existing user
      result = this.mockMvc
          .perform(
              post("/admin/member/addMultiple")
                  .param("memberList", userEmail, "test@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.successList", hasSize(0)))
          .andExpect(jsonPath("$.success.failureList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // add request with tagList and invalid group association
      result = this.mockMvc
          .perform(
              post("/admin/member/addMultiple")
                  .param("memberList", "test2@surepeople.com, test3@surepeople.com")
                  .param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "tag1:false, tag2:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :tag1 found in company :1"))
          .andExpect(jsonPath("$.success.successList", hasSize(0)))
          .andExpect(jsonPath("$.success.failureList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // add request with tagList and valid group association
      dbSetup.addGroups();

      result = this.mockMvc
          .perform(
              post("/admin/member/addMultiple")
                  .param("memberList", "dax@einstix.com, test3@surepeople.com")
                  .param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "Executive:false, AnotherGroup:false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.successList", hasSize(2)))
          .andExpect(jsonPath("$.success.failureList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(8000);

      // valid request new user but account exhausted
      dbSetup.exhaustAccount();
      userEmail = "dax3@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/addSingle")
                  .param("memberList", "dax@einstix.com, test3@surepeople.com")
                  .param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "Executive:false, AnotherGroup:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No subscriptions left for company :1")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetMembers() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.createUsers();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.addGroups();


      int memberCount = (int) dbSetup.getMemberCount("1");
      
      // valid request with one existing user
      MvcResult result = this.mockMvc
          .perform(post("/admin/member").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList").exists())
          .andExpect(jsonPath("$.success.memberList", hasSize(memberCount))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // add one more user and see if the count has
      // gone up to 3
      result = this.mockMvc
          .perform(
              post("/admin/member/addMultiple")
                  .param("memberList", "test2@surepeople.com, test3@surepeople.com")
                  .param("tagList", "tag1, tag2")
                  .param("groupAssociationList", "Executive:false, AnotherGroup:false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.successList", hasSize(2)))
          .andExpect(jsonPath("$.success.failureList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      memberCount += 2;
      
      result = this.mockMvc
          .perform(post("/admin/member").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList").exists())
          .andExpect(jsonPath("$.success.memberList", hasSize(memberCount))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testRemoveMember() {
    try {
      dbSetup.removeAllGroups();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();

      String memberToDelete = "abc";
      // valid request with one existing user
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :abc: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // user from different company
      memberToDelete = "dax2@surepeople.com";
      User userUnderTest = new User();
      userUnderTest.setEmail(memberToDelete);
      userUnderTest.setCompanyId("ABC Company");
      dbSetup.addUpdate(userUnderTest);

      result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Unauthroized request !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      userUnderTest.setCompanyId("1");
      dbSetup.addUpdate(userUnderTest);
      result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      userUnderTest.setCompanyId("1");
      dbSetup.addUpdate(userUnderTest);
      dbSetup.addGroups();
      // add user to group
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", "Executive")
                  .param("memberEmail", memberToDelete).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // remove the user
      result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      userUnderTest.setCompanyId("1");
      dbSetup.addUpdate(userUnderTest);

      // add user to group as group lead
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", "Executive")
                  .param("memberEmail", memberToDelete).param("isGroupLead", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // remove the user
      result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // remove the logged in user
      memberToDelete = "admin@admin.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/delete").param("memberEmail", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Cannot delete yourself !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testRemoveMembersMultiple() {
    try {
      dbSetup.removeAllGroups();
      String memberToDelete = "abc";
      // valid request with one existing user
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/deleteMulti").param("memberEmails", memberToDelete, "def")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :abc: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // user from different company
      memberToDelete = "dax2@surepeople.com";
      User userUnderTest = new User();
      userUnderTest.setEmail(memberToDelete);
      userUnderTest.setCompanyId("ABC Company");
      dbSetup.addUpdate(userUnderTest);
      result = this.mockMvc
          .perform(
              post("/admin/member/deleteMulti").param("memberEmails", memberToDelete, "def")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Unauthroized request !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      userUnderTest.setCompanyId("1");
      dbSetup.addUpdate(userUnderTest);
      result = this.mockMvc
          .perform(
              post("/admin/member/deleteMulti").param("memberEmails", memberToDelete, "def")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :def: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      dbSetup.addUpdate(userUnderTest);
      User user2 = new User();
      user2.setEmail("dax3@surepeople.com");
      user2.setCompanyId("1");
      dbSetup.addUpdate(user2);
      result = this.mockMvc
          .perform(
              post("/admin/member/deleteMulti")
                  .param("memberEmails", memberToDelete, user2.getEmail())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // remove the logged in user
      memberToDelete = "admin@admin.com";
      result = this.mockMvc
          .perform(
              post("/admin/member/deleteMulti").param("memberEmails", memberToDelete)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Cannot delete yourself !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetMembersForAdmins() throws Exception {
    
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.createCompanies();
    dbSetup.createProducts();
    dbSetup.createAccounts();
    dbSetup.createPaymentInstruments();
    
    MvcResult result = this.mockMvc
        .perform(
            post("/admin/getMembersForAdmins")
                .param("planType", "IntelligentHiring").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.memberList", Matchers.hasSize(0))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    dbSetup.createUsers();
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/removeAdminMember")
                .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                .param("planType", "IntelligentHiring").session(session).contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/members").param("companyId", "7")
                .param("planType", "IntelligentHiring").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.memberList", Matchers.hasSize(1))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
//  @Test
//  public void testSearchMembers() {
//    try {
//      MvcResult result = this.mockMvc
//          .perform(
//              post("/admin/member/search").param("searchString", "")
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.InvalidRequestException").value("Search query not found !!!"))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//
//      result = this.mockMvc
//          .perform(
//              post("/admin/member/search").param("searchString", "a")
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//
//      result = this.mockMvc
//          .perform(
//              post("/admin/member/search").param("searchString", "a a")
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//
//      result = this.mockMvc
//          .perform(
//              post("/admin/member/search").param("searchString", "a p")
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//
//      result = this.mockMvc
//          .perform(
//              post("/admin/member/search").param("searchString", "z")
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail("Error !!!");
//    }
//  }

  /*
   * @Test public void searchResearch() {
   * 
   * DBObject condition1 = new BasicDBObject("lastName", new BasicDBObject( "$regex",
   * "^a").append("$options", "i")); DBObject condition2 = new BasicDBObject("firstName", new
   * BasicDBObject("$regex", "(^d)|(^p)") .append("$options", "i"));
   * 
   * DBObject[] queryArray = new DBObject[] {condition1, condition2};
   * 
   * BasicDBObject query = new BasicDBObject("$or", Arrays.asList(queryArray));
   * 
   * DBObject projections = new BasicDBObject("firstName", "1"); projections.put("lastName", "1");
   * 
   * DBCollection collection = mongoTemplate.getCollection("user"); DBCursor cursor =
   * collection.find(query, projections); //cursor.addSpecial("lastName", "-1"); //cursor.sort(new
   * BasicDBObject("lastName", "-1")); while(cursor.hasNext()) {
   * log.debug("The user list ->>>>>>>>>>> "+cursor.next()); }
   * 
   * List<User> userList = mongoTemplate.find( query((new Criteria().orOperator(where(
   * "lastName").regex("^a", "i"), where("firstName").regex("(^p)", "i")))).with(new
   * Sort(Sort.Direction.DESC, "lastName")), User.class);
   * 
   * try { Query q = new Query(); q.addCriteria(new Criteria().orOperator(
   * where("lastName").regex("^a", "i"), where("firstName").regex("(^p)", "i"))); q.with(new
   * Sort(Sort.Direction.DESC, "lastName")); q.fields().include("firstName").include("lastName");
   * userList = mongoTemplate.find(q, User.class); } catch (Exception e) { e.printStackTrace(); } }
   */
  
  @Test
  public void testBlockUser() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.createUsers();
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.addGroups();
      dbSetup.removeAllHiringUsersArchive();

      // valid request with one existing user
      MvcResult result = this.mockMvc
          .perform(post("/admin/blockUser")
          .param("userList", "a, b")
          .param("blockUser", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(post("/admin/blockUser")
          .param("userList", "dax@surepeople.com, b")
          .param("blockUser", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User userToAuth = new User();
      userToAuth.setEmail("dax@surepeople.com");
      userToAuth.setPassword("pwd123");
      
      try {
        // trying to login user
        authenticationHelper.doAuthenticate(session2, userToAuth);
        fail("Authentication should have failed.");
        
      } catch (SignInFailedException e) {
        e.printStackTrace();
        assertThat(e.getReason(), is(SignInFailReason.IndividualBlocked));
      }
      
      // now bring it back to life
      result = this.mockMvc
          .perform(post("/admin/blockUser")
          .param("userList", "dax@surepeople.com, b")
          .param("blockUser", "false")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      try {
        // trying to login user
        authenticationHelper.doAuthenticate(session2, userToAuth);
      } catch (SignInFailedException e) {
        e.printStackTrace();
        fail("Authentication should have passed.");
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testSomething() {
    try {
      User user = dbSetup.getUser();
      userFactory.getAllBasicUserInfo(user.getCompanyId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
}
