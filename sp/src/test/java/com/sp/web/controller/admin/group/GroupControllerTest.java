package com.sp.web.controller.admin.group;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.teamdynamics.TeamDynamicForm;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.test.setup.AuthenticationHelper;
import com.sp.web.user.UserFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

public class GroupControllerTest extends SPTestBase {
  
  private static final String DEFAULT_GROUP_NAME = "Executive";
  
  @Autowired
  AuthenticationHelper authenticationHelper;
  
  boolean isAuthenticationDone = false;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * Setup.
   * 
   * @throws Exception
   *           - error setting up
   */
  @Before
  public void setUp() throws Exception {
    
    // login the user
    if (!isAuthenticationDone) {
      authenticationHelper.authenticateUser(session);
      isAuthenticationDone = true;
    }
  }
  
  @Test
  public void testNoTeams() throws Exception {
    dbSetup.removeAllGroups();
    
    MvcResult result = this.mockMvc
        .perform(post("/admin/group/all").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.groupInfoList", hasSize(0))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void testGroupPresent() {
    try {
      // remove any previously created teams
      dbSetup.removeAllGroups();
      
      // add a team
      int groupsAddedSize = dbSetup.addGroups();
      
      MvcResult result = this.mockMvc
          .perform(post("/admin/group/all").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfoList", hasSize(groupsAddedSize)))
          // .andExpect(jsonPath("$.success.groupInfoList[0].numOfMembers").value(1))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateGroupName() {
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    MvcResult result;
    try {
      String newGroupName = "TestGroup";
      result = this.mockMvc
          .perform(
              post("/admin/group/updateName").param("name", newGroupName)
                  .param("oldName", "Executive").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add a team
      dbSetup.addGroups();
      
      // proper request
      String existingGroupName = "AnotherGroup";
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", existingGroupName)
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          // .andExpect(jsonPath("$.error").exists())
          // .andExpect(
          // jsonPath("$.error.InvalidRequestException").value(
          // "User :dax@surepeople.com: already part of group :AnotherGroup !!!"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      
      existingGroupName = "Developer";
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", existingGroupName)
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // proper request
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", existingGroupName)
                  .param("memberEmailList", "admin@admin.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/updateName").param("name", newGroupName)
                  .param("oldName", existingGroupName).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfoList", hasSize(1)))
          .andExpect(jsonPath("$.success.groupInfoList[0].numOfMembers").value(2))
          .andExpect(jsonPath("$.success.groupInfoList[0].name").value(newGroupName)).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      GroupAssociation ga = new GroupAssociation(newGroupName);
      assertTrue("Update group association for user", user.getGroupAssociationList().contains(ga));
      user = dbSetup.getUser("admin@admin.com");
      assertTrue("Update group association for user", user.getGroupAssociationList().contains(ga));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testDeleteGroup() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    try {
      // add a groups
      dbSetup.addGroups();
      MvcResult result;
      result = this.mockMvc
          .perform(
              post("/admin/group/delete").param("name", "Executive1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive1 found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/delete").param("name", "Executive")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      assertThat("No group associations.", user.getGroupAssociationList(), hasSize(0));
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "AnotherGroup")
                  .param("memberEmailList", "dax@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/delete").param("name", "AnotherGroup")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("dax@surepeople.com");
      assertThat("No group associations.", user.getGroupAssociationList(), hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAddMemberToGroup() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      MvcResult result;
      
      // invalid group name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive1")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive1 found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax1@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax1@surepeople.com: not found in the system !!!")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // proper request
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add again same user
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          // .andExpect(jsonPath("$.error").exists())
          // .andExpect(
          // jsonPath("$.error.InvalidRequestException").value(
          // "User :dax@surepeople.com: already part of group :Executive !!!"))
          .andExpect(jsonPath("$.success").exists())
          // .andExpect(
          // jsonPath("$.success.numMembers").value(2))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add user as team lead
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          // .andExpect(
          // jsonPath("$.success.numMembers").value(2))
          // .andExpect(jsonPath("$.error").exists())
          // .andExpect(
          // jsonPath("$.error.InvalidRequestException").value(
          // "User :admin@admin.com: already team lead of the group :Executive !!!"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeMember").param("name", "Executive")
                  .param("memberEmail", "admin@admin.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add same user as team lead
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          // .andExpect(
          // jsonPath("$.success.numMembers").value(1))
          // .andExpect(jsonPath("$.error").exists())
          // .andExpect(
          // jsonPath("$.error.InvalidRequestException").value(
          // "User :dax@surepeople.com: already team lead of the group :Executive !!!"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid remove member request
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeMember").param("name", "Executive")
                  .param("memberEmail", "dax@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // add user success
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").param("isGroupLead", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "pradeep3@surepeople.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "admin@admin.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :pradeep3@surepeople.com: already team lead of the group :Executive !!!"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testRemoveMemberFromGroup() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      MvcResult result;
      
      // adding a proper user to the group to remove
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid group name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeMember").param("name", "Executive1")
                  .param("memberEmail", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive1 found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid member name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeMember").param("name", "Executive")
                  .param("memberEmail", "dax1@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax1@surepeople.com: not part of the "
                      + "group :Executive in company :1 !!!")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid member name but not team lead but sent as team lead
      
      // result = this.mockMvc
      // .perform(
      // post("/admin/group/removeMember").param("name", "Executive")
      // .param("memberEmail", "dax@surepeople.com").param("isGroupLead", "true")
      // .contentType(MediaType.TEXT_PLAIN).session(session))
      // .andExpect(content().contentType("application/json;charset=UTF-8"))
      // .andExpect(jsonPath("$.error").exists())
      // .andExpect(
      // jsonPath("$.error.InvalidRequestException").value(
      // "User :dax@surepeople.com: not group lead for "
      // + "the group :Executive in company :1 !!!")).andReturn();
      //
      // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid remove member request
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeMember").param("name", "Executive")
                  .param("memberEmail", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetTeamMembers() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      MvcResult result;
      
      // get group member list invalid group name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/groupDetails").param("name", "Executive1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive1 found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with no team members
      
      result = this.mockMvc
          .perform(
              post("/admin/group/groupDetails").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(DEFAULT_GROUP_NAME))
          .andExpect(jsonPath("$.success.groupInfo.memberList").doesNotExist()).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a proper user to the group
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with no team members
      
      result = this.mockMvc
          .perform(
              post("/admin/group/groupDetails").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(DEFAULT_GROUP_NAME))
          // .andExpect(jsonPath("$.success.groupInfo.memberSummaryList", hasSize(1)))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCreateTeam() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      MvcResult result;
      
      // valid group create w/o team lead
      
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(DEFAULT_GROUP_NAME))
          .andExpect(jsonPath("$.success.groupInfo.groupLeadEmail").doesNotExist()).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create group with same name w/o team lead
      
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Group already exists."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create group with team lead
      
      String testGroup = "TestGroup";
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", testGroup)
                  .param("groupLead", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(testGroup))
          .andExpect(jsonPath("$.success.groupInfo.groupLead.email").value("dax@surepeople.com"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create duplicate group with team lead
      
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", testGroup)
                  .param("groupLead", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Group already exists."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", "testgroup")
                  .param("groupLead", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Group already exists."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testMemberSummaryGroup() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      
      MvcResult result;
      
      // valid group create w/o team lead or any members
      
      result = this.mockMvc
          .perform(
              post("/admin/group/memberSummary").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.groupSummaryList").doesNotExist()).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a proper user to the group
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", DEFAULT_GROUP_NAME)
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid group create w/o team lead 1 team member
      
      result = this.mockMvc
          .perform(
              post("/admin/group/memberSummary").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupSummaryList", hasSize(1)))
          .andExpect(
              jsonPath("$.success.groupSummaryList[0].user.email").value("dax@surepeople.com"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetGroupLeadMemberAndGroups() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      
      MvcResult result;
      
      // valid group create w/o team lead or any members
      
      result = this.mockMvc
          .perform(
              post("/groupLead/getMembersAndGroups").contentType(MediaType.TEXT_PLAIN).session(
                  session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Need to be a group lead to "
                      + "access this tool. Please contact your administrator.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the group association to admin
      User groupLead = dbSetup.getUser("admin@admin.com");
      groupLead.addGroupAssociation(new GroupAssociation("Executive", true));
      dbSetup.addUpdate(groupLead);
      
      result = this.mockMvc
          .perform(
              post("/groupLead/getMembersAndGroups").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupList", hasSize(1)))
          .andExpect(jsonPath("$.success.memberList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/groupLead/getMembersAndGroups").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupList", hasSize(1)))
          .andExpect(jsonPath("$.success.memberList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "AnotherGroup")
                  .param("memberEmailList", "admin@admin.com").param("isGroupLead", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Developer")
                  .param("memberEmailList", "pradeep1@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/groupLead/getMembersAndGroups").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupList", hasSize(2)))
          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testMemberUpdateGroupMulti() {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      
      MvcResult result;
      
      // valid group create w/o team lead or any members
      String userEmail = "dax@surepeople.com";
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "Executive:false, Accounting:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Accounting found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "Executive:false, AnotherGroup:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
          // .andExpect(jsonPath("$.success.groupList[0].groupLead").value(true))
          // .andExpect(jsonPath("$.success.groupList[1].groupLead").value(false))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups removing group lead from another group
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "Executive:false, AnotherGroup:false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
          .andExpect(jsonPath("$.success.groupList[0].groupLead").value(false))
          .andExpect(jsonPath("$.success.groupList[1].groupLead").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups adding group lead for another group
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "Executive:false, AnotherGroup:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
          .andExpect(jsonPath("$.success.groupList[0].groupLead").value(true))
          .andExpect(jsonPath("$.success.groupList[1].groupLead").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups adding group lead for another group
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "AnotherGroup:true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
          .andExpect(jsonPath("$.success.groupList[0].groupLead").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups removing group lead for another group
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "Executive:false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
          .andExpect(jsonPath("$.success.groupList[0].groupLead").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid groups removing all associations
      result = this.mockMvc
          .perform(
              post("/admin/group/memberGroupUpdate").param("memberEmail", userEmail)
                  .param("groupAssociationList", "").contentType(MediaType.TEXT_PLAIN)
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
          .andExpect(jsonPath("$.success.groupList").doesNotExist()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetALlGroupsWithMembersCount() throws Exception {
    
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    dbSetup.addGroups();
    
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllGroups();
    dbSetup.removeAll("profileBalance");
    
    dbSetup.createUsers();
    dbSetup.createCompanies();
    dbSetup.addGroups();
    User user = dbSetup.getUser("dax@surepeople.com");
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    MvcResult result;
    
    // valid group create w/o team lead or any members
    result = this.mockMvc
        .perform(
            post("/spectrum/groups/memberCount").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void testRemoveGroupLead() {
    // remove any previously created teams
    dbSetup.removeAllGroups();
    
    // add a groups
    try {
      dbSetup.addGroups();
      
      MvcResult result;
      
      // Invalid GroupLead
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("groupNameList", "Executive")
                  .param("memberEmailList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid group name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeGroupLead").param("name", "Executive1")
                  .param("groupLeadEmail", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No group :Executive1 found in company :1")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid member name
      
      result = this.mockMvc
          .perform(
              post("/admin/group/removeGroupLead").param("name", "Executive")
                  .param("groupLeadEmail", "dax1@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax1@surepeople.com: not part of the "
                      + "group :Executive in company :1 !!!")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // check no member preseent in the group
      result = this.mockMvc
          .perform(
              post("/admin/group/groupDetails").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(DEFAULT_GROUP_NAME))
          .andExpect(jsonPath("$.success.groupInfo.memberList").doesNotExist()).andReturn();
      
      // Valid Remove group lead request
      result = this.mockMvc
          .perform(
              post("/admin/group/removeGroupLead").param("name", "Executive")
                  .param("groupLeadEmail", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // Check if group lead added back to the member list
      result = this.mockMvc
          .perform(
              post("/admin/group/groupDetails").param("name", DEFAULT_GROUP_NAME)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(DEFAULT_GROUP_NAME))
          // .andExpect(jsonPath("$.success.groupInfo.memberList").exists())
          .andReturn();
    } catch (Exception ex) {
      log.error("Exception occurreed", ex);
      fail();
    }
    
  }
  
  @Test
  public void testGetMemberPortraits() {
    // remove any previously created teams
    dbSetup.removeAllUsers();
    
    // add a groups
    try {
      dbSetup.createUsers();
      
      MvcResult result;
      
      TeamDynamicForm dynamicForm = new TeamDynamicForm();
      
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(dynamicForm);
      result = this.mockMvc
          .perform(
              post("/groupLead/getMemberPortraits").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No user present to perform the team dynamics.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<User> allMembersForCompany = userFactory.getAllMembersForCompany("1");
      List<String> users = allMembersForCompany.stream()
          .filter(usr -> usr.getUserStatus() == UserStatus.VALID).map(uid -> uid.getId())
          .collect(Collectors.toList());
      dynamicForm.setUserIds(users);
      request = om.writeValueAsString(dynamicForm);
      result = this.mockMvc
          .perform(
              post("/groupLead/getMemberPortraits").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      //
      // result = this.mockMvc
      // .perform(
      // post("/groupLead/getMemberPortraits").param("groupId", "2")
      // .contentType(MediaType.TEXT_PLAIN).session(session))
      // .andExpect(content().contentType("application/json;charset=UTF-8"))
      // .andExpect(jsonPath("$.success").exists())
      // .andExpect(jsonPath("$.success.groupList", hasSize(2))).andReturn();
      // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception ex) {
      log.error("Exception occurreed", ex);
      fail();
    }
  }
}
