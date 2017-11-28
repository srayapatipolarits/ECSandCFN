package com.sp.web.controller.hiring.role;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.controller.hiring.match.AdminHiringPortriatDetailsDTO;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.dto.hiring.role.HiringRoleListingDTO;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.role.HiringRoleFactoryCache;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HiringRoleControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringRoleFactory factory;
  
  @Autowired
  HiringRoleFactoryCache factoryCache;
  
  @Autowired
  HiringPortraitMatchFactory portraitFactory;

  @Autowired
  AdminHiringPortraitMatchFactory adminPortraitFactory;
  
  @Test
  public void testGetALL() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");

      
      // adding a hiring group
      final HiringRoleDTO create = addRole(user, "TestRole");
      
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      addRoleToUser(create, hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/role/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleListing", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringRoleListing[0].count").value(1))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a hiring group
      addRole(user, "TestRole2");

      result = this.mockMvc
          .perform(
              post("/hiring/role/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleListing", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private void addRoleToUser(final HiringRoleDTO role,
      HiringUser hiringUser) {
    hiringUser.addHiringRole(role.getId());
    dbSetup.addUpdate(hiringUser);
  }

  private HiringRoleDTO addRole(User user, String roleName) {
    HiringRoleForm form = new HiringRoleForm();
    form.setName(roleName);
    form.setDescription("Test Role Description.");
    final HiringRoleDTO create = factory.create(user, form);
    return create;
  }

  @Test
  public void testGet() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      
      // adding a group
      final HiringRoleDTO addRole = addRole(user, "TestRole");

      final String roleId = addRole.getId();
      result = this.mockMvc
          .perform(
              post("/hiring/role/get")
              .param("id", roleId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRole.id").value(roleId))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      

      HiringUser hiringUser = new HiringUser(user);
      hiringUser.addRole(RoleType.Hiring);
      dbSetup.addUpdate(hiringUser);
      addRoleToUser(addRole, hiringUser);
      
      assignPortrait(addRole, "TestPortrait");

      result = this.mockMvc
          .perform(
              post("/hiring/role/get")
              .param("id", roleId)
              .param("addPrism", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRole.id").value(roleId))
          .andExpect(jsonPath("$.success.hiringRole.users", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private AdminHiringPortriatDetailsDTO assignPortrait(HiringRoleDTO addRole, String portraitName) {
    User user = dbSetup.getUser();
    AdminHiringPortriatDetailsDTO portrait = addPortrait(user, portraitName);
    HiringRole hiringRole = factoryCache.get(addRole.getId());
    hiringRole.setPortraitId(portrait.getId());
    factoryCache.save(hiringRole);
    return portrait;
  }

  @Test
  public void testCreate() {
    try {
      
      dbClean();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringRoleForm form = new HiringRoleForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String roleName = "TestRole";
      form.setName(roleName);
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRole").exists())
          .andExpect(jsonPath("$.success.hiringRole.name").value(roleName))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // adding same one again
      
      String content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setName(WordUtils.swapCase(roleName));
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role already exists."))
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
      
      dbClean();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringRoleForm form = new HiringRoleForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/update")
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
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String newRoleName = "TestRoleNew";
      form.setName(newRoleName);
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      final String roleName = "TestRole";
      HiringRoleDTO addRole = addRole(user, roleName);
      form.setId(addRole.getId());
      form.setPortraitId("abc");

      result = this.mockMvc
          .perform(
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "Test Portrait");
      form.setPortraitId(addPortrait.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringRole> all = dbSetup.getAll(HiringRole.class);
      HiringRole hiringRole = all.get(0);
      assertThat(hiringRole.getName(), equalTo(newRoleName));
      
      addRole(user, roleName);
      form.setName(roleName);
      result = this.mockMvc
          .perform(
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setName(WordUtils.swapCase(roleName));
      result = this.mockMvc
          .perform(
              post("/hiring/role/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role already exists."))
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
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/delete")
              .param("id", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      HiringRoleDTO addRole = addRole(user, "TestRole");
      
      HiringUser hiringUser = new HiringUser(user);
      dbSetup.addUpdate(hiringUser);
      
      addRoleToUser(addRole, hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/delete")
              .param("id", addRole.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<HiringRoleListingDTO> all = factory.getAll(user);
      assertThat(all.size(), equalTo(0));
      
      List<HiringUser> all2 = dbSetup.getAll(HiringUser.class);
      hiringUser = all2.get(0);
      assertThat(hiringUser.getHiringRoleIds().size(), equalTo(0));
      
      User user2 = dbSetup.getUser("pradeep2@surepeople.com");
      HiringRoleDTO addRole2 = addRole(user2, "TestRole");

      result = this.mockMvc
          .perform(
              post("/hiring/role/delete")
              .param("id", addRole2.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAssignPortrait() {
    try {
      
      dbClean();
      
      final ObjectMapper om = new ObjectMapper();
      HiringRoleForm form = new HiringRoleForm();
      
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/assignPortrait")
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
              post("/hiring/role/assignPortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portriat id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User user = dbSetup.getUser();
      form.setPortraitId("abc");

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/assignPortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringRoleDTO addGroup = addRole(user, "TestRole");
      form.setId(addGroup.getId());

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/assignPortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO portrait = addPortrait(user, "Test Portrait");
      form.setPortraitId(portrait.getId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/assignPortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringRoleDTO hiringRoleDTO = factory.get(user, form);
      assertThat(hiringRoleDTO.getPortrait().getId(), equalTo(portrait.getId()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testRemovePortrait() {
    try {
      
      dbClean();
      
      final ObjectMapper om = new ObjectMapper();
      HiringRoleForm form = new HiringRoleForm();
      
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/removePortrait")
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
              post("/hiring/role/removePortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Role not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User user = dbSetup.getUser();
      HiringRoleDTO addGroup = addRole(user, "TestRole");
      form.setId(addGroup.getId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/role/removePortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO portrait = assignPortrait(addGroup, "TestPortrait");
      HiringRoleDTO hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getPortrait().getId(), equalTo(portrait.getId()));

      result = this.mockMvc
          .perform(
              post("/hiring/role/removePortrait")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringGroupDTO = factory.get(user, form);
      assertThat(hiringGroupDTO.getPortrait(), nullValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetUserRolesAndMatch() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final User user = dbSetup.getUser();
      HiringRoleDTO addRole = addRole(user, "TestRole");
      addRoleToUser(addRole, hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].portrait").value(nullValue()))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult").value(nullValue()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO portrait = addPortrait(user, "Test Portrait");
      HiringRole hiringRole = factory.get(addRole.getId());
      hiringRole.setPortraitId(portrait.getId());
      factoryCache.save(hiringRole);

      // even if portrait is added the result is null as user analysis has
      // not been completed
      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].portrait").value(nullValue()))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult").value(nullValue()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].portrait").exists())
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult").exists())
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult.matchPercent").value(0))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.getAnalysis().getPersonality().get(RangeType.Primary).setPersonalityType(PersonalityType.Refiner);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/role/getUserRolesAndMatch")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch", hasSize(1)))
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].portrait").exists())
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult").exists())
          .andExpect(jsonPath("$.success.hiringRoleAndMatch[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetRolesWithoutPortrait() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/role/getRolesWithoutPortrait")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User user = dbSetup.getUser();
      HiringRoleDTO addRole = addRole(user, "TestRole");
      
      result = this.mockMvc
          .perform(
              post("/hiring/role/getRolesWithoutPortrait")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringRole hiringRole = factoryCache.get(addRole.getId());
      hiringRole.setPortraitId("abc");
      factoryCache.save(hiringRole);

      result = this.mockMvc
          .perform(
              post("/hiring/role/getRolesWithoutPortrait")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringRoleList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  

  private void dbClean() {
    dbSetup.removeAll("hiringRole");
    dbSetup.removeAll("hiringUser");
    dbSetup.removeAll("hiringPortrait");
  }
  
  private AdminHiringPortriatDetailsDTO addPortrait(User user, String name) {
    AdminHiringPortraitMatchForm form = new AdminHiringPortraitMatchForm();
    form.setName(name);
    form.setDescription("Some description.");
    Set<String> tags = new HashSet<String>(Arrays.asList("Tag1", "Tag2"));
    form.setTags(tags);
    final Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap = 
        new HashMap<CategoryType, Map<String, PortraitDataMatch>>();
    final Map<String, PortraitDataMatch> categoryMatchData = new HashMap<String, PortraitDataMatch>();
    PortraitDataMatch matchData = new PortraitDataMatch();
    matchData.setEnabled(true);
    List<MatchCriteria> matchCriterias = new ArrayList<MatchCriteria>();
    MatchCriteria criteria = new MatchCriteria();
    criteria.setKey(PersonalityType.Refiner + "");
    criteria.setMatchPercent(new BigDecimal(100));
    matchCriterias.add(criteria);
    matchData.setMatchCriterias(matchCriterias);
    categoryMatchData.put(RangeType.Primary + "", matchData);
    categoryDataMap.put(CategoryType.Personality, categoryMatchData);
    form.setCategoryDataMap(categoryDataMap);
    Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap = new HashMap<CategoryPriority, BigDecimal>();
    categoryPriorityWeightMap.put(CategoryPriority.Essential, BigDecimal.valueOf(1));
    form.setCategoryPriorityWeightMap(categoryPriorityWeightMap);
    form.validate();
    return adminPortraitFactory.create(user, form);
  }
  
}
