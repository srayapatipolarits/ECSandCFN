package com.sp.web.controller.hiring.group;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.dto.hiring.group.HiringGroupListingDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.group.HiringGroupFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HiringGroupControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringGroupFactory factory;
  
  @Test
  public void testGetALL() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");

      
      // adding a hiring group
      final HiringGroupDTO create = addGroup(user, "TestGroup");
      
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      addUserToGroup(user, create, hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/group/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringGroupListing[0].count").value(1))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a hiring group
      addGroup(user, "TestGroup2");

      result = this.mockMvc
          .perform(
              post("/hiring/group/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(2)))
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
    factory.addUsers(user, form);
  }

  private HiringGroupDTO addGroup(User user, String name) {
    HiringGroupForm form = new HiringGroupForm();
    form.setName(name);
    final HiringGroupDTO create = factory.create(user, form);
    return create;
  }

  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      
      // adding a group
      final HiringGroupDTO addGroup = addGroup(user, "TestGroup");

      final String groupId = addGroup.getId();
      result = this.mockMvc
          .perform(
              post("/hiring/group/get")
              .param("id", groupId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroup.id").value(groupId))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      

      HiringUser hiringUser = new HiringUser(user);
      hiringUser.getAnalysis().setCreatedOn(LocalDateTime.now());
      hiringUser.addRole(RoleType.Hiring);
      dbSetup.addUpdate(hiringUser);
      addUserToGroup(user, addGroup, hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/group/get")
              .param("id", groupId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroup.id").value(groupId))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testCreate() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      ObjectMapper om = new ObjectMapper();
      
      HiringGroupForm form = new HiringGroupForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String groupName = "TestGroup";
      form.setName(groupName);
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroup").exists())
          .andExpect(jsonPath("$.success.hiringGroup.name").value(groupName))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // adding same one again
      
      String content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setName("tEsTgRoUp");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      ObjectMapper om = new ObjectMapper();
      
      HiringGroupForm form = new HiringGroupForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      form.setId("abc");
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String newGroupName = "TestGroupNew";
      form.setName(newGroupName);
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      final String groupName = "TestGroup";
      HiringGroupDTO addGroup = addGroup(user, groupName);
      form.setId(addGroup.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringGroup> all = dbSetup.getAll(HiringGroup.class);
      HiringGroup hiringGroup = all.get(0);
      assertThat(hiringGroup.getName(), equalTo(newGroupName));
      
      addGroup(user, groupName);
      form.setName(groupName);

      result = this.mockMvc
          .perform(
              post("/hiring/group/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group already exists."))
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
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/delete")
              .param("id", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringGroupDTO addGroup = addGroup(user, "TestGroup");
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/delete")
              .param("id", addGroup.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringGroupListingDTO> all = factory.getAll(user);
      assertThat(all.size(), equalTo(0));
      
      User user2 = dbSetup.getUser("pradeep2@surepeople.com");
      HiringGroupDTO addGroup2 = addGroup(user2, "TestGroup");

      result = this.mockMvc
          .perform(
              post("/hiring/group/delete")
              .param("id", addGroup2.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAddUsers() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      final ObjectMapper om = new ObjectMapper();
      HiringGroupForm form = new HiringGroupForm();
      
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/addUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setId("abc");
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/addUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User ids required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      List<String> userIds = new ArrayList<String>();
      userIds.add("abc");
      userIds.add(hiringUser.getId());
      form.setUserIds(userIds);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/addUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringGroupDTO addGroup = addGroup(user, "TestGroup");
      form.setId(addGroup.getId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/addUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringGroupDTO hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getUsers().size(), equalTo(1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testRemoveUsers() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      final ObjectMapper om = new ObjectMapper();
      HiringGroupForm form = new HiringGroupForm();
      
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/removeUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setId("abc");
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/removeUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User ids required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      List<String> userIds = new ArrayList<String>();
      userIds.add("abc");
      userIds.add(hiringUser.getId());
      form.setUserIds(userIds);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/removeUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringGroupDTO addGroup = addGroup(user, "TestGroup");
      form.setId(addGroup.getId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/group/removeUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringGroupDTO hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getUsers().size(), equalTo(0));
      
      addUserToGroup(user, addGroup, hiringUser);
      hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getUsers().size(), equalTo(1));

      result = this.mockMvc
          .perform(
              post("/hiring/group/removeUsers")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getUsers().size(), equalTo(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testUserGroups() {
    try {

      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");

      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/userGroups")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroup", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringGroupDTO addGroup = addGroup(user, "TestGroup");
      
      HiringUser hiringUser = new HiringUser(user);
      hiringUser.addRole(RoleType.Hiring);
      dbSetup.addUpdate(hiringUser);
      addUserToGroup(user, addGroup, hiringUser);
      
      addGroup(user, "TestGroup2");
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/userGroups")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroup", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetPortraits() {
    try {
      
      dbSetup.removeAll("hiringGroup");
      dbSetup.removeAll("hiringUser");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/group/getPortraits")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      
      // adding a hiring group
      final HiringGroupDTO create = addGroup(user, "TestGroup");
      
      result = this.mockMvc
          .perform(
              post("/hiring/group/getPortraits")
              .param("id", create.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      addUserToGroup(user, create, hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/group/getPortraits")
              .param("id", create.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringGroupListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
}
