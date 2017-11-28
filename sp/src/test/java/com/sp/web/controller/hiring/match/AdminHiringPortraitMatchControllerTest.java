package com.sp.web.controller.hiring.match;

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
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactoryCache;
import com.sp.web.service.hiring.role.HiringRoleFactory;

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

public class AdminHiringPortraitMatchControllerTest extends SPTestLoggedInBase {

  @Autowired
  AdminHiringPortraitMatchFactory factory;
  
  @Autowired
  HiringPortraitMatchFactoryCache factoryCache;
  
  @Autowired
  HiringRoleFactory rolesFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  private void dbClean() {
    dbSetup.removeAll("hiringPortrait");
    dbSetup.removeAll("hiringRole");
  }
  
  @Test
  public void testGetALL() {
    try {
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(addPortrait.getId());
      hiringPortraitDao.addCompany(user.getCompanyId());
      factoryCache.update(hiringPortraitDao);

      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatchListing[0].companies", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
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
    return factory.create(user, form);
  }
  
  @Test
  public void testGet() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");

      final String portraitId = addPortrait.getId();
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/get")
              .param("id", portraitId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.id").value(portraitId))
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
      
      dbClean();
      
      ObjectMapper om = new ObjectMapper();
      
      AdminHiringPortraitMatchForm form = new AdminHiringPortraitMatchForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String name = "TestPortrait";
      form.setName(name);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Description required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setDescription("Some description.");

      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait match data required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap = 
                      new HashMap<CategoryType, Map<String,PortraitDataMatch>>();
      categoryDataMap.put(CategoryType.Personality, null);
      form.setCategoryDataMap(categoryDataMap);

      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Category portrait match data required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
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
      final Map<String, PortraitDataMatch> fundamentalMatchData = new HashMap<String, PortraitDataMatch>();
      PortraitDataMatch controlMatchData = new PortraitDataMatch();
      controlMatchData.setEnabled(false);
      List<MatchCriteria> controlMatchCriterias = new ArrayList<MatchCriteria>();
      MatchCriteria controlMatchCriteria = new MatchCriteria();
      controlMatchCriteria.setMatchPercent(new BigDecimal(65));
      controlMatchCriterias.add(controlMatchCriteria);
      controlMatchData.setMatchCriterias(controlMatchCriterias);
      fundamentalMatchData.put(TraitType.Control + "", controlMatchData);
      categoryDataMap.put(CategoryType.FundamentalNeeds, fundamentalMatchData);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait match weight data required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap = new HashMap<CategoryPriority, BigDecimal>();
      categoryPriorityWeightMap.put(CategoryPriority.Essential, BigDecimal.valueOf(0.6d));
      categoryPriorityWeightMap.put(CategoryPriority.Secondary, BigDecimal.valueOf(0.4d));
      form.setCategoryPriorityWeightMap(categoryPriorityWeightMap);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // adding same one again
      String content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setName(name.toLowerCase());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait already exists."))
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
      
      AdminHiringPortraitMatchForm form = new AdminHiringPortraitMatchForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/update")
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
              post("/hiring/admin/match/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String newPortraitName = "TestPortraitNew";
      form.setName(newPortraitName);
      form.setDescription("Some description.");
      User user = dbSetup.getUser();
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      form.setCategoryDataMap(addPortrait.getCategoryDataMap());

      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait match weight data required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap = new HashMap<CategoryPriority, BigDecimal>();
      categoryPriorityWeightMap.put(CategoryPriority.Essential, BigDecimal.valueOf(0.6d));
      categoryPriorityWeightMap.put(CategoryPriority.Secondary, BigDecimal.valueOf(0.4d));
      form.setCategoryPriorityWeightMap(categoryPriorityWeightMap);

      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setId(addPortrait.getId());
      form.setName(newPortraitName);
      addPortrait(user, newPortraitName);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String newPortraitName2 = newPortraitName + "1";
      form.setName(newPortraitName2);
      Set<String> tags = new HashSet<String>();
      tags.add("Test");
      form.setTags(tags);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(form.getId());
      assertThat(hiringPortraitDao.getName(), equalTo(newPortraitName2));

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
              post("/hiring/admin/match/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/delete")
              .param("id", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      HiringRoleDTO addRole = addRole(user, "TestRole");
      
      HiringRole hiringRole = rolesFactory.get(addRole.getId());
      final String portraitId = addPortrait.getId();
      hiringRole.setPortraitId(portraitId);
      dbSetup.addUpdate(hiringRole);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/delete")
              .param("id", portraitId)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringRole = rolesFactory.get(addRole.getId());
      assertThat(hiringRole.getPortraitId(), equalTo(null));

      assertThat(factoryCache.getAll().size(), equalTo(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private HiringRoleDTO addRole(User user, String roleName) {
    HiringRoleForm form = new HiringRoleForm();
    form.setName(roleName);
    form.setDescription("Test Role Description.");
    final HiringRoleDTO create = rolesFactory.create(user, form);
    return create;
  }
  
  @Test
  public void testAssignPortrait() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/assignPortrait")
              .param("id", " ")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/assignPortrait")
              .param("id", "abc")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/assignPortrait")
              .param("id", "abc")
              .param("companyId", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final User user = dbSetup.getUser();
      
      final String companyId = user.getCompanyId();
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/assignPortrait")
              .param("id", "abc")
              .param("companyId", companyId)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      final String portraitId = addPortrait.getId();
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), equalTo(null));
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/assignPortrait")
              .param("id", portraitId)
              .param("companyId", companyId)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), hasSize(1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testRemovePortrait() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removePortrait")
              .param("id", " ")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removePortrait")
              .param("id", "abc")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final User user = dbSetup.getUser();

      final String companyId = user.getCompanyId();
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removePortrait")
              .param("id", "abc")
              .param("companyId", companyId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      final String portraitId = addPortrait.getId();
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), nullValue());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removePortrait")
              .param("id", portraitId)
              .param("companyId", companyId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), nullValue());
      
      hiringPortraitDao.addCompany(companyId);
      factoryCache.update(hiringPortraitDao);
      
      HiringRoleDTO addRole = addRole(user, "TestRole");
      HiringRole hiringRole = rolesFactory.get(addRole.getId());
      hiringRole.setPortraitId(portraitId);
      dbSetup.addUpdate(hiringRole);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removePortrait")
              .param("id", portraitId)
              .param("companyId", companyId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), hasSize(0));
            
      hiringRole = rolesFactory.get(addRole.getId());
      assertThat(hiringRole.getPortraitId(), nullValue());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testAddDocumentUrl() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", " ")
              .param("companyId", " ")
              .param("documentUrl", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", "abc")
              .param("companyId", " ")
              .param("documentUrl", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", "abc")
              .param("companyId", "abc")
              .param("documentUrl", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Document URL required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", "abc")
              .param("companyId", "abc")
              .param("documentUrl", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final User user = dbSetup.getUser();
      
      final String documentUrl = "abc";
      final String companyId = user.getCompanyId();
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", "abc")
              .param("companyId", companyId)
              .param("documentUrl", documentUrl)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      final String portraitId = addPortrait.getId();
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyIds(), equalTo(null));
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/addDocumentUrl")
              .param("id", portraitId)
              .param("companyId", companyId)
              .param("documentUrl", documentUrl)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyDocumentUrl(companyId), equalTo(documentUrl));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testRemoveDocumentUrl() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removeDocumentUrl")
              .param("id", " ")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removeDocumentUrl")
              .param("id", "abc")
              .param("companyId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User user = dbSetup.getUser();
      
      final String documentUrl = "abc";
      final String companyId = user.getCompanyId();
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removeDocumentUrl")
              .param("id", "abc")
              .param("companyId", companyId)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      final String portraitId = addPortrait.getId();
      
      HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
      hiringPortraitDao.addDocumentUrl(companyId, documentUrl);
      factoryCache.update(hiringPortraitDao);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/removeDocumentUrl")
              .param("id", portraitId)
              .param("companyId", companyId)
              .param("documentUrl", documentUrl)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      hiringPortraitDao = factoryCache.get(portraitId);
      assertThat(hiringPortraitDao.getCompanyDocumentUrl(companyId), nullValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAllCompanies() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/admin/match/getCompanies")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      company.addFeature(SPFeature.Hiring);
      companyFactory.updateCompany(company);
      
      result = this.mockMvc
          .perform(
              post("/hiring/admin/match/getCompanies")
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
}
