package com.sp.web.controller.admin.competency;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.form.competency.CompetencyForm;
import com.sp.web.form.competency.CompetencyProfileForm;
import com.sp.web.form.competency.ManageCompetencyForm;
import com.sp.web.model.Company;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.SPRating;
import com.sp.web.model.SPRatingScore;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.article.Article;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationByProfile;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.competency.RatingConfigurationType;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.competency.UserEvaluationResult;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.library.ArticlesFactory;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author vikram
 *
 *         The test cases for admin services of blueprint.
 */
public class AdminCompetencyControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  CompetencyFactory competencyFactory;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Test
  public void testGetCompanies() throws Exception {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getCompanies").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companyList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Company> all = dbSetup.getAll(Company.class);
      assertThat(all.size(), is(greaterThan(0)));
      Company company = all.get(0);
      company.addFeature(SPFeature.Competency);
      companyFactory.updateCompany(company);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getCompanies").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companyList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetAll() throws Exception {
    try {
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companyCompetencyList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      createCompetencyProfile("1");
      createCompetencyProfile("2");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companyCompetencyList", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCreateUpdate() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAll("competencyProfile");
      
      resetCompanyEvaluationStatus("1");
      
      final ObjectMapper om = new ObjectMapper();
      final CompetencyProfileForm profileForm = new CompetencyProfileForm();
      String content = om.writeValueAsString(profileForm);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      profileForm.setName("Some new name");
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String companyId = "1";
      profileForm.setCompanyId(companyId);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one competency required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final List<CompetencyForm> competencyList = new ArrayList<CompetencyForm>();
      CompetencyForm competency = new CompetencyForm();
      competencyList.add(competency);
      profileForm.setCompetencyList(competencyList);
      content = om.writeValueAsString(profileForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Rating configuration required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      RatingConfiguration ratingConfiguration = new RatingConfiguration();
      profileForm.setRatingConfiguration(ratingConfiguration);

      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Rating configuration type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ratingConfiguration.setType(RatingConfigurationType.Numeric);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Rating configuration size at least 2."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      ratingConfiguration.setSize(3);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency name is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competency.setName("Some competency name.");
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Rating not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      SPRating rating = new SPRating();
      competency.setRating(rating);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Rating not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final List<SPRatingScore> ratingList = new ArrayList<SPRatingScore>();
      SPRatingScore ratingScore = new SPRatingScore();
      ratingScore.setTitle("Terrible");
      ratingScore.setScore(1);
      ratingScore.setDescription("How terrible.");
      ratingList.add(ratingScore);
      SPRatingScore ratingScore2 = new SPRatingScore();
      ratingScore2.setTitle("Intermediate Terrible");
      ratingScore2.setScore(3);
      ratingScore2.setDescription("How intermediate terrible.");
      ratingList.add(ratingScore2);
      SPRatingScore ratingScore3 = new SPRatingScore();
      ratingScore3.setTitle("Super Terrible");
      ratingScore3.setScore(10);
      ratingScore3.setDescription("How super terrible.");
      ratingList.add(ratingScore3);
      rating.setRatingList(ratingList);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one category required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
      DSActionCategory actionCategory = new DSActionCategory();
      devStrategyActionCategoryList.add(actionCategory);
      competency.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Category name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      actionCategory.setTitle("action category name");
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one strategy required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<DSAction> actionList = new ArrayList<DSAction>();
      DSAction action =  new DSAction();
      actionList.add(action);
      actionCategory.setActionList(actionList);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Strategy title is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      action.setTitle("Strategy name");
      List<String> mandatoryArticles = new ArrayList<String>();
      mandatoryArticles.add("1");
      competency.setMandatoryArticles(mandatoryArticles);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Competency profile already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<CompetencyProfile> all = dbSetup.getAll(CompetencyProfile.class);
      assertThat(all.size(), is(greaterThan(0)));
      CompetencyProfile competencyProfile = all.get(0);
      profileForm.setId(competencyProfile.getId());
      List<String> deletedCompetencyList = new ArrayList<String>();
      deletedCompetencyList.add("123");
      profileForm.setDeletedCompetencyList(deletedCompetencyList);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Competency with the same name already exists."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String competencyId = competencyProfile.getCompetencyIdList().get(0);
      competency.setId(competencyId);
      deletedCompetencyList.add(competencyId);
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Need at least one competency."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPGoal competencyToDelete = new SPGoal();
      competencyToDelete.setName("Competency to Delete");
      competencyToDelete.setCategory(GoalCategory.Competency);
      dbSetup.addUpdate(competencyToDelete);
      
      CompetencyProfileDao competencyToUpdate = competencyFactory.getCompetencyProfile(competencyProfile.getId());
      competencyToUpdate.getCompetencyIdList().add(competencyToDelete.getId());
      competencyFactory.updateCompetencyProfile(competencyToUpdate);
      deletedCompetencyList.clear();
      deletedCompetencyList.add(competencyToDelete.getId());
      content = om.writeValueAsString(profileForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the ordering test
      CompetencyForm competency2 = new CompetencyForm();
      BeanUtils.copyProperties(competency, competency2);
      competency2.setName("This is the second competency");
      competency2.setId(null);
      List<DSActionCategory> devStrategyActionCategoryList2 = competency2.getDevStrategyActionCategoryList();
      devStrategyActionCategoryList2.stream().forEach(ds -> {
          ds.setUid(null);
          ds.getActionList().stream().forEach(act -> act.setUid(null));
        });
      competencyList.add(0, competency2);
      content = om.writeValueAsString(profileForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyDao company = companyFactory.getCompany(companyId);
      company.setEvaluationInProgress(true);
      companyFactory.updateCompanyDao(company);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/createUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
      
  @Test
  public void testGet() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.createGoals();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/get")
              .param("competencyProfileId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompetencyProfile competencyProfile = createCompetencyProfile("1");
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/get")
              .param("competencyProfileId", competencyProfile.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.removeRole(RoleType.SuperAdministrator);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      result = this.mockMvc
          .perform(
              post("/admin/competency/get")
              .param("competencyProfileId", competencyProfile.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testDelete() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/delete")
              .param("competencyProfileId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompetencyProfile competencyProfile = createCompetencyProfile("1");
      CompanyDao company = companyFactory.getCompany("1");
      company.setEvaluationInProgress(true);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/delete")
              .param("competencyProfileId", competencyProfile.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile in evaluation period."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/delete")
              .param("competencyProfileId", competencyProfile.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetArticles() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeArticles();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getArticles")
              .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String companyId = "1";
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getArticles")
              .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.articles", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createGoals();
      dbSetup.createArticles();
      articlesFactory.load();
      articlesFactory.resetAllCache();
      
      List<Article> all = dbSetup.getAll(Article.class);
      
      final int articlesListSize = all.size();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getArticles")
              .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.articles", hasSize(articlesListSize)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPGoal goal = new SPGoal();
      goal.setName("Newly added goal.");
      goal.setCategory(GoalCategory.Business);
      List<String> accounts = new ArrayList<String>();
      accounts.add(companyId);
      goal.setAccounts(accounts);
      goal.setStatus(GoalStatus.ACTIVE);
      dbSetup.addUpdate(goal);
      
      Article article = new Article();
      article.setArticleLinkLabel("Newly added article.");
      Set<String> goals = new HashSet<String>();
      goals.add(goal.getId());
      article.setGoals(goals);
      dbSetup.addUpdate(article);
      articlesFactory.resetAllCache();

      result = this.mockMvc
          .perform(
              post("/sysAdmin/competency/getArticles")
              .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.articles", hasSize(articlesListSize + 1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
   
  @Test
  public void testGetUserCompetency() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/getUserCompetency")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = "1";
      // adding the competency and assigning to a user
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/getUserCompetency")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAssignCompetency() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeArticles();
      dbSetup.createArticles();
      final ManageCompetencyForm competencyForm = new ManageCompetencyForm();
      
      final ObjectMapper om = new ObjectMapper();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/assignCompetency")
                .content(om.writeValueAsString(competencyForm))
                  .contentType(MediaType.APPLICATION_JSON)
//                .param("competencyProfileId", "abc")
//                .param("memberIdList", "1, 2")
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      final String competencyProfileId = competencyProfile.getId();
      List<String> competencyProfileIds = new ArrayList<String>();
      competencyProfileIds.add(competencyProfileId);
      competencyForm.setCompetencyProfileId(competencyProfileIds);
      
      
       result = this.mockMvc
          .perform(
              post("/admin/competency/assignCompetency")
                .content(om.writeValueAsString(competencyForm))
                  .contentType(MediaType.APPLICATION_JSON)
//                .param("competencyProfileId", "abc")
//                .param("memberIdList", "1, 2")
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User or group list not found in request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("abc");
      userIdList.add("def");
      competencyForm.setUserIdList(userIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/assignCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getCompetencyProfileId(), is(nullValue()));
      
      CompanyDao company = companyFactory.getCompany(companyId);
      company.setEvaluationInProgress(true);
      companyFactory.updateCompanyDao(company);

      result = this.mockMvc
          .perform(
              post("/admin/competency/assignCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/assignCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getCompetencyProfileId(), is(competencyProfileId));
      
      stopMail();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testRemoveCompetency() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      // reset company evaluation status
      resetCompanyEvaluationStatus("1");
      
      final ManageCompetencyForm competencyForm = new ManageCompetencyForm();
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("1");
      userIdList.add("2");
      competencyForm.setUserIdList(userIdList);
      final ObjectMapper om = new ObjectMapper();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/removeCompetency")
                .content(om.writeValueAsString(competencyForm))
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      final String competencyProfileId = competencyProfile.getId();
      List<String> competencyProfileIds = new ArrayList<String>();
      competencyProfileIds.add(competencyProfileId);
      competencyForm.setCompetencyProfileId(competencyProfileIds);
      userIdList.clear();
      userIdList.add("abc");
      userIdList.add("def");
      competencyForm.setUserIdList(userIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/removeCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      
      CompanyDao company = companyFactory.getCompany(companyId);
      company.setEvaluationInProgress(true);
      companyFactory.updateCompanyDao(company);

      result = this.mockMvc
          .perform(
              post("/admin/competency/removeCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/removeCompetency")
              .content(om.writeValueAsString(competencyForm))
                .contentType(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getCompetencyProfileId(), is(nullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  private void resetCompanyEvaluationStatus(String companyId) {
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setEvaluationInProgress(false);
    company.setCompetencyEvaluationId(null);
    companyFactory.updateCompanyDao(company);
  }

  @Test
  public void testActivateEvaluation() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No competency profiles set for company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Competency not assigned to any user in company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate =  formatter.format(LocalDateTime.now().minusDays(2));

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "End date cannot be before today."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      endDate =  formatter.format(LocalDateTime.now().plusDays(2));
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one evaluation type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      String requiredEvaluationListStr = EvaluationType.Manager + "," + EvaluationType.Peer + "," + EvaluationType.Self;
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(
              jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testActivateEvaluationSelf() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");

      // reset company evaluation status
      resetCompanyEvaluationStatus("1");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No competency profiles set for company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Competency not assigned to any user in company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate =  formatter.format(LocalDateTime.now().minusDays(2));

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "End date cannot be before today."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      endDate =  formatter.format(LocalDateTime.now().plusDays(2));
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one evaluation type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      String requiredEvaluationListStr = EvaluationType.Self + "" ;
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(
              jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testActivateEvaluationManager() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");

      // reset company evaluation status
      resetCompanyEvaluationStatus("1");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No competency profiles set for company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Competency not assigned to any user in company."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", "")
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate =  formatter.format(LocalDateTime.now().minusDays(2));

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "End date cannot be before today."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      endDate =  formatter.format(LocalDateTime.now().plusDays(2));
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", "")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one evaluation type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      String requiredEvaluationListStr = EvaluationType.Manager + "" ;
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(
              jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/admin/competency/activateEvaluation")
                .param("endDate", endDate)
                .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testAdminGetAll() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding competency profile to company
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the user to the competency
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers[0].userEvaluationMap.*", hasSize(1)))
          .andExpect(jsonPath("$.success.evaluationInProgress").value(false))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // start an evaluation 
      final CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);

      result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers[0].userEvaluationMap.*", hasSize(1)))
          .andExpect(jsonPath("$.success.evaluationInProgress").value(true))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final Map<String, CompetencyEvaluationByProfile> evaluationMap = 
                        competencyEvaluation.getEvaluationMap();
      CompetencyEvaluationByProfile competencyEvaluationByProfile = new CompetencyEvaluationByProfile();
      competencyEvaluationByProfile.setCompetencyProfile(new CompetencyProfileSummaryDTO(
          competencyFactory.getCompetencyProfile(competencyProfile.getId())));
      final Map<String, UserEvaluationResult> userEvaluationMap = 
                          new HashMap<String, UserEvaluationResult>();
      UserEvaluationResult userCompetencyEvaluationResult = new UserEvaluationResult();
      userCompetencyEvaluationResult.setMember(new BaseUserDTO(user));
      BaseCompetencyEvaluationScore selfEvaluation = new BaseCompetencyEvaluationScore();
      selfEvaluation.setScore(4.5f);
      userCompetencyEvaluationResult.setSelfEvaluation(selfEvaluation);
      UserCompetencyEvaluationScore managerEvaluation = new UserCompetencyEvaluationScore();
      managerEvaluation.setScore(2.5f);
      managerEvaluation.setReviewer(new BaseUserDTO(user));
      userCompetencyEvaluationResult.setManagerEvaluation(managerEvaluation);
      List<UserCompetencyEvaluationScore> peerEvaluationList = new ArrayList<UserCompetencyEvaluationScore>();
      UserCompetencyEvaluationScore peerEvaluation = new UserCompetencyEvaluationScore();
      peerEvaluation.setScore(3.5f);
      peerEvaluation.setReviewer(new BaseUserDTO(user));
      peerEvaluationList.add(peerEvaluation);
      UserCompetencyEvaluationScore peerEvaluation2 = new UserCompetencyEvaluationScore();
      peerEvaluation2.setScore(5.0f);
      peerEvaluation2.setReviewer(new BaseUserDTO(user));;
      peerEvaluationList.add(peerEvaluation2);
      userCompetencyEvaluationResult.setPeerEvaluation(peerEvaluationList);
      userEvaluationMap.put(user.getId(), userCompetencyEvaluationResult);
      competencyEvaluationByProfile.setUserEvaluationMap(userEvaluationMap);
      evaluationMap.put(competencyProfile.getId(), competencyEvaluationByProfile);
      competencyFactory.update(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers[0].userEvaluationMap.*", hasSize(1)))
          .andExpect(jsonPath("$.success.evaluationInProgress").value(true))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // ending the evaluation
      competencyEvaluation.setEndedOn(LocalDateTime.now());
      competencyEvaluation.setCompleted(true);
      competencyFactory.update(competencyEvaluation);
      CompanyDao company = companyFactory.getCompany(companyId);
      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/getAll")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluationUsers[0].userEvaluationMap.*", hasSize(1)))
          .andExpect(jsonPath("$.success.evaluationInProgress").value(false))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testSendReminder() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeAllActivityLogs();

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation not in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User competency not set."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);

      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      startMail();
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", userId)
                .param("isSelf", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", userId)
                .param("isSelf", "false")
                .param("reviewUserId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feedback user not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setEmail("dax@yopmail.com");
      fbUser.setCompanyId(user.getCompanyId());
      fbUser.setFeatureType(FeatureType.Competency);
      dbSetup.addUpdate(fbUser);
      
      result = this.mockMvc
          .perform(
              post("/admin/competency/sendReminder")
                .param("userId", userId)
                .param("isSelf", "false")
                .param("reviewUserId", fbUser.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetStats() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeAllActivityLogs();

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      CompanyDao company = companyFactory.getCompany(companyId);
      company.setEvaluationInProgress(true);
      companyFactory.updateCompanyDao(company);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/competency/getStats")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Evaluation in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      company.setEvaluationInProgress(false);
      companyFactory.updateCompanyDao(company);
      
      List<User> all = dbSetup.getAll(User.class);
      List<User> companyUsers = all.stream().filter(u -> checkCompany(u, companyId))
          .collect(Collectors.toList());
      int allActiveMember = (int) companyUsers.stream()
          .filter(u -> u.getUserStatus() == UserStatus.VALID).count();
      
      final int allUserCount = companyUsers.size();
      result = this.mockMvc
          .perform(
              post("/admin/competency/getStats")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.membersWithCompetency").value(0))
          .andExpect(jsonPath("$.success.activeMemberCount").value(allActiveMember))
          .andExpect(jsonPath("$.success.totalMemberCount").value(allUserCount))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId("1");
      dbSetup.addUpdate(user);
      result = this.mockMvc
          .perform(
              post("/admin/competency/getStats")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
                  .andExpect(content().contentType("application/json;charset=UTF-8"))          
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.membersWithCompetency").value(1))
          .andExpect(jsonPath("$.success.activeMemberCount").value(allActiveMember))
          .andExpect(jsonPath("$.success.totalMemberCount").value(allUserCount))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
      
  private boolean checkCompany(User user, String companyId) {
    String userCompanyId = user.getCompanyId();
    if (userCompanyId != null && userCompanyId.equalsIgnoreCase(companyId)) {
      return true;
    }
    return false;
  }

  private CompetencyEvaluation createCompetencyEvaluation(final String companyId) {
    CompetencyEvaluation competencyEvaluation = new CompetencyEvaluation();
    competencyEvaluation.setCompanyId(companyId);
    competencyEvaluation.setStartDate(LocalDateTime.now());
    competencyEvaluation.setEndDate(LocalDateTime.now().plusDays(2));
    dbSetup.addUpdate(competencyEvaluation);
    // adding the competency profiles
    competencyFactory.getCompanyCompetencyProfiles(companyId).stream()
        .map(c -> competencyFactory.getCompetencyProfile(c.getId()))
        .forEach(competencyEvaluation::addCompetencyProfile);

    // iterate over all the users and add them to the competency profiles user map
    List<User> memberList = accountRepository.getAllMembersForCompany(companyId);
    memberList.stream().filter(m -> m.getCompetencyProfileId() != null)
        .forEach(competencyEvaluation::addUser);
    
    competencyFactory.update(competencyEvaluation);
    
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setCompetencyEvaluationId(competencyEvaluation.getId());
    company.setEvaluationInProgress(true);
    companyFactory.updateCompanyDao(company);
    return competencyEvaluation;
  }  
  
  private CompetencyProfile createCompetencyProfile(String companyId) {
    CompetencyProfile competencyProfile = new CompetencyProfile();
    competencyProfile.setActive(true);
    competencyProfile.setName("Some competency name");
    competencyProfile.setDescription("Some competency description.");
    competencyProfile.setCompanyId(companyId);
    SPGoal competency = new SPGoal();
    competency.setName("Some competency name.");
    SPRating rating = new SPRating();
    competency.setRating(rating);
    final List<SPRatingScore> ratingList = new ArrayList<SPRatingScore>();
    SPRatingScore ratingScore = new SPRatingScore();
    ratingScore.setTitle("Terrible");
    ratingScore.setScore(1);
    ratingScore.setDescription("How terrible.");
    ratingList.add(ratingScore);
    SPRatingScore ratingScore2 = new SPRatingScore();
    ratingScore2.setTitle("Intermediate Terrible");
    ratingScore2.setScore(3);
    ratingScore2.setDescription("How intermediate terrible.");
    ratingList.add(ratingScore2);
    SPRatingScore ratingScore3 = new SPRatingScore();
    ratingScore3.setTitle("Super Terrible");
    ratingScore3.setScore(10);
    ratingScore3.setDescription("How super terrible.");
    ratingList.add(ratingScore3);
    rating.setRatingList(ratingList);
    List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory actionCategory = new DSActionCategory();
    devStrategyActionCategoryList.add(actionCategory);
    competency.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    actionCategory.setTitle("action category name");
    List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action =  new DSAction();
    actionList.add(action);
    actionCategory.setActionList(actionList);
    action.setTitle("Strategy name");
    List<String> mandatoryArticles = new ArrayList<String>();
    mandatoryArticles.add("1");
    competency.setMandatoryArticles(mandatoryArticles);
    dbSetup.addUpdate(competency);
    List<String> competencyIdList = new ArrayList<String>();
    competencyIdList.add(competency.getId());
    competencyProfile.setCompetencyIdList(competencyIdList);
    dbSetup.addUpdate(competencyProfile);
    return competencyProfile;
  }
  
}