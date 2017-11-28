package com.sp.web.controller.hiring.match;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;
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
import java.util.Optional;
import java.util.Set;

public class HiringPortraitMatchControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  HiringPortraitMatchFactory factory;
  
  @Autowired
  AdminHiringPortraitMatchFactory adminFactory;
  
  @Autowired
  HiringPortraitMatchFactoryCache factoryCache;
  
  @Autowired
  HiringRoleFactory rolesFactory;
  
  @Autowired
  HiringGroupFactory groupsFactory;
  
  private void dbClean() {
    dbSetup.removeAll("hiringPortrait");
    dbSetup.removeAll("hiringRole");
    dbSetup.removeAll("hiringUser");
    dbSetup.removeAll("hiringGroup");
  }
  
  @Test
  public void testGetALL() {
    try {
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(post("/hiring/match/getAll").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      final AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
      
      result = this.mockMvc
          .perform(post("/hiring/match/getAll").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      adminFactory.assignPortraitToCompany(user, user.getCompanyId(), addPortrait.getId());
      
      result = this.mockMvc
          .perform(post("/hiring/match/getAll").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatchListing[0].roles", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringRoleDTO addRole = addRole(user, "TestRole");
      assignPortraitToRole(addRole.getId(), addPortrait.getId());
      
      result = this.mockMvc
          .perform(post("/hiring/match/getAll").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatchListing[0].roles", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/getAll").param("basePortrait", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatchListing", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatchListing[0].roles").doesNotExist())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private void assignPortraitToRole(String roleId, String portraitId) {
    HiringRole hiringRole = rolesFactory.get(roleId);
    hiringRole.setPortraitId(portraitId);
    rolesFactory.update(hiringRole);
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
    final AdminHiringPortriatDetailsDTO create = adminFactory.create(user, form);
    return create;
  }
  
  private void addDocumentUrl(User user, final AdminHiringPortriatDetailsDTO create) {
    HiringPortraitDao hiringPortraitDao = factoryCache.get(create.getId());
    hiringPortraitDao.addDocumentUrl(user.getCompanyId(), "http://somedocument.url");
    factoryCache.update(hiringPortraitDao);
  }
  
  private HiringRoleDTO addRole(User user, String roleName) {
    HiringRoleForm form = new HiringRoleForm();
    form.setName(roleName);
    form.setDescription("Test Role Description.");
    final HiringRoleDTO create = rolesFactory.create(user, form);
    return create;
  }
  
  private HiringGroupDTO addGroup(User user, HiringUser hiringUser) {
    HiringGroupForm form = new HiringGroupForm();
    form.setName("TestGroup");
    List<String> userIds = new ArrayList<String>(Arrays.asList(hiringUser.getId()));
    form.setUserIds(userIds);
    HiringGroupDTO create = groupsFactory.create(user, form);
    HiringGroup hiringGroup = groupsFactory.get(create.getId());
    hiringGroup.add(hiringUser);
    groupsFactory.update(hiringGroup);
    return create;
  }
  
  @Test
  public void testGet() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/match/get").param("portraitId", " ").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Portrait id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/get").param("portraitId", "abc")
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
              post("/hiring/match/get").param("portraitId", portraitId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      adminFactory.assignPortraitToCompany(user, user.getCompanyId(), addPortrait.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/get").param("portraitId", portraitId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.roles", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding roles
      HiringRoleDTO addRole = addRole(user, "TestRole");
      assignPortraitToRole(addRole.getId(), portraitId);
      addDocumentUrl(user, addPortrait);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/get").param("portraitId", portraitId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.roles", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.companyDocUrl").value(notNullValue()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testMatchPortrait() {
    try {
      
      dbClean();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Atleast one user or group required.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", "abc").param("userIds", "abc")
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
              post("/hiring/match/matchPortrait").param("id", portraitId).param("userIds", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      adminFactory.assignPortraitToCompany(user, user.getCompanyId(), addPortrait.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId).param("userIds", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException")
                  .value("User not found to process match.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.portrait.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").value(nullValue()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setAnalysis(user.getAnalysis());
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.portrait.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(0))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.getAnalysis().getPersonality().get(RangeType.Primary)
          .setPersonalityType(PersonalityType.Refiner);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.portrait.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringGroupDTO addGroup = addGroup(user, hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("groupIds", addGroup.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.portrait.id").value(portraitId))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testPortraitMatchPercentage() {
    try {
      
      dbClean();
      
      HiringUser hiringUser = addHiringCandidate();
      final String portraitId = portraitMatchSetup(hiringUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(50));
      addToMatchWeight(portraitId, BigDecimal.valueOf(.5), BigDecimal.valueOf(.5));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(80));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(70));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(90));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(67));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(95))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(63));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(59));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.Concrete, BigDecimal.valueOf(60));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(73))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.Processing, TraitType.External, BigDecimal.valueOf(60));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(93))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.DecisionMaking, TraitType.Outward, BigDecimal.valueOf(50));
      addToMatch(portraitId, CategoryType.Motivation, TraitType.AttainmentOfGoals,
          BigDecimal.valueOf(50));
      addToMatch(portraitId, CategoryType.LearningStyle, TraitType.Global, BigDecimal.valueOf(50));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(71))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testPortraitMatchFundamentalNeeds() {
    try {
      
      dbClean();
      
      HiringUser hiringUser = addHiringCandidate();
      final String portraitId = portraitMatchSetup(hiringUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatchWeight(portraitId, BigDecimal.valueOf(.5), BigDecimal.valueOf(.5));
      addToMatch(portraitId, CategoryType.FundamentalNeeds, TraitType.Control, (TraitType) null);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.FundamentalNeeds, TraitType.Significance,
          (TraitType) null);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(40), BigDecimal.valueOf(40),
          BigDecimal.valueOf(20));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(75))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.FundamentalNeeds, TraitType.Control, (TraitType) null);
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(40), BigDecimal.valueOf(20),
          BigDecimal.valueOf(40));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(33), BigDecimal.valueOf(33),
          BigDecimal.valueOf(34));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(40), BigDecimal.valueOf(50),
          BigDecimal.valueOf(10));
      addToMatch(portraitId, CategoryType.FundamentalNeeds, TraitType.Control,
          TraitType.Significance);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(50), BigDecimal.valueOf(40),
          BigDecimal.valueOf(10));
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(50), BigDecimal.valueOf(25),
          BigDecimal.valueOf(25));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(50), BigDecimal.valueOf(20),
          BigDecimal.valueOf(30));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(75))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.FundamentalNeeds, TraitType.Significance,
          TraitType.Control);
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(40), BigDecimal.valueOf(40),
          BigDecimal.valueOf(20));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(30), BigDecimal.valueOf(40),
          BigDecimal.valueOf(30));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateFundamentalNeeds(hiringUser, BigDecimal.valueOf(20), BigDecimal.valueOf(40),
          BigDecimal.valueOf(40));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testPortraitMatchConflictManagement() {
    try {
      
      dbClean();
      
      HiringUser hiringUser = addHiringCandidate();
      final String portraitId = portraitMatchSetup(hiringUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatchWeight(portraitId, BigDecimal.valueOf(.5), BigDecimal.valueOf(.5));
      addToMatch(portraitId, CategoryType.ConflictManagement, TraitType.Collaborate,
          (TraitType) null);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.ConflictManagement, TraitType.Accommodate,
          (TraitType) null);
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(20), BigDecimal.valueOf(20),
          BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(20));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(25), BigDecimal.valueOf(35),
          BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(0));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToMatch(portraitId, CategoryType.ConflictManagement, TraitType.Collaborate,
          TraitType.Accommodate);
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(20), BigDecimal.valueOf(20),
          BigDecimal.valueOf(20), BigDecimal.valueOf(30), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(25), BigDecimal.valueOf(25),
          BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(100))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(35), BigDecimal.valueOf(25),
          BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(0));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(20), BigDecimal.valueOf(20),
          BigDecimal.valueOf(20), BigDecimal.valueOf(30), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(20), BigDecimal.valueOf(30),
          BigDecimal.valueOf(20), BigDecimal.valueOf(20), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(15), BigDecimal.valueOf(30),
          BigDecimal.valueOf(35), BigDecimal.valueOf(10), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(60))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(15), BigDecimal.valueOf(30),
          BigDecimal.valueOf(35), BigDecimal.valueOf(20), BigDecimal.valueOf(0));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(50))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(35), BigDecimal.valueOf(10),
          BigDecimal.valueOf(35), BigDecimal.valueOf(10), BigDecimal.valueOf(10));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(90))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateConflictManagement(hiringUser, BigDecimal.valueOf(35), BigDecimal.valueOf(10),
          BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(35));
      
      result = this.mockMvc
          .perform(
              post("/hiring/match/matchPortrait").param("id", portraitId)
                  .param("userIds", hiringUser.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.portraitMatch.users", hasSize(1)))
          .andExpect(jsonPath("$.success.portraitMatch.users[0].matchResult").exists())
          .andExpect(
              jsonPath("$.success.portraitMatch.users[0].matchResult.matchPercent").value(75))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private void updateConflictManagement(HiringUser hiringUser, BigDecimal collaborate,
      BigDecimal accomodate, BigDecimal compromise, BigDecimal avoid, BigDecimal compete) {
    
    int totalScore = 0;
    AnalysisBean analysis = hiringUser.getAnalysis();
    Map<TraitType, BigDecimal> conflictManagement = analysis.getConflictManagement();
    
    totalScore += conflictManagement.put(TraitType.Collaborate, collaborate).intValue();
    totalScore += conflictManagement.put(TraitType.Accommodate, accomodate).intValue();
    totalScore += conflictManagement.put(TraitType.Compromise, compromise).intValue();
    totalScore += conflictManagement.put(TraitType.Avoid, avoid).intValue();
    totalScore += conflictManagement.put(TraitType.Compete, compete).intValue();
    assertTrue(totalScore == 100);
    dbSetup.addUpdate(hiringUser);
  }
  
  private void updateFundamentalNeeds(HiringUser hiringUser, BigDecimal control,
      BigDecimal significance, BigDecimal security) {
    int totalScore = 0;
    AnalysisBean analysis = hiringUser.getAnalysis();
    Map<TraitType, BigDecimal> fundamentalNeeds = analysis.getFundamentalNeeds();
    // adding control
    totalScore += fundamentalNeeds.put(TraitType.Control, control).intValue();
    totalScore += fundamentalNeeds.put(TraitType.Significance, significance).intValue();
    totalScore += fundamentalNeeds.put(TraitType.Security, security).intValue();
    assertTrue(totalScore == 100);
    dbSetup.addUpdate(hiringUser);
  }
  
  private void addToMatch(String portraitId, CategoryType categoryType, TraitType traitType,
      BigDecimal matchPercent) {
    HiringPortraitDao portrait = factory.getPortrait(portraitId);
    Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap = portrait
        .getCategoryDataMap();
    final Map<String, PortraitDataMatch> categoryMatcherMap = categoryDataMap.computeIfAbsent(
        categoryType, k -> new HashMap<String, PortraitDataMatch>());
    PortraitDataMatch matcherData = new PortraitDataMatch();
    matcherData.setEnabled(true);
    List<MatchCriteria> matchCriterias = new ArrayList<MatchCriteria>();
    MatchCriteria matchCriteria = new MatchCriteria();
    matchCriteria.setMatchPercent(matchPercent);
    matchCriterias.add(matchCriteria);
    matcherData.setMatchCriterias(matchCriterias);
    categoryMatcherMap.put(traitType + "", matcherData);
    factoryCache.update(portrait);
  }
  
  private void addToMatch(String portraitId, CategoryType categoryType, TraitType primary,
      TraitType secondary) {
    HiringPortraitDao portrait = factory.getPortrait(portraitId);
    Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap = portrait
        .getCategoryDataMap();
    final Map<String, PortraitDataMatch> categoryMatcherMap = categoryDataMap.computeIfAbsent(
        categoryType, k -> new HashMap<String, PortraitDataMatch>());
    addMatchData(Constants.PRIMARY, primary, categoryMatcherMap);
    Optional.ofNullable(secondary).ifPresent(
        s -> addMatchData(Constants.SECONDARY, s, categoryMatcherMap));
    factoryCache.update(portrait);
  }
  
  private void addMatchData(String key, TraitType trait,
      final Map<String, PortraitDataMatch> categoryMatcherMap) {
    PortraitDataMatch primaryMatchData = new PortraitDataMatch();
    primaryMatchData.setEnabled(true);
    List<MatchCriteria> matchCriterias = new ArrayList<MatchCriteria>();
    MatchCriteria matchCriteria = new MatchCriteria();
    matchCriteria.setKey(trait + "");
    matchCriterias.add(matchCriteria);
    primaryMatchData.setMatchCriterias(matchCriterias);
    categoryMatcherMap.put(key, primaryMatchData);
  }
  
  private String portraitMatchSetup(HiringUser hiringUser) {
    User user = dbSetup.getUser("admin@admin.com");
    AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "TestPortrait");
    final String portraitId = addPortrait.getId();
    adminFactory.assignPortraitToCompany(user, user.getCompanyId(), portraitId);
    hiringUser.setAnalysis(user.getAnalysis());
    hiringUser.setUserStatus(UserStatus.VALID);
    hiringUser.getAnalysis().getPersonality().get(RangeType.Primary)
        .setPersonalityType(PersonalityType.Refiner);
    dbSetup.addUpdate(hiringUser);
    return portraitId;
  }
  
  private void addToMatchWeight(String portraitId, BigDecimal essentialWeight,
      BigDecimal secondaryWeight) {
    HiringPortraitDao portrait = factory.getPortrait(portraitId);
    Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap = portrait
        .getCategoryPriorityWeightMap();
    categoryPriorityWeightMap.put(CategoryPriority.Essential, essentialWeight);
    categoryPriorityWeightMap.put(CategoryPriority.Secondary, secondaryWeight);
    factoryCache.update(portrait);
  }
}
