/**
 * 
 */
package com.sp.web.controller.system.goal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.User;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.goals.SPGoalFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

/**
 * @author pradeepruhil
 *
 */
public class AdminGoalsControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory spGoalFactory;
  
  /**
   * Test method for
   * {@link com.sp.web.controller.admin.themes.AdminGoalsController#createNewGoal(com.sp.web.form.goal.GoalForm.GoalForm, org.springframework.security.authentication.Authentication)}
   * .
   */
  @Test
  public void testCreateNewGoal() throws Exception {
    
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAll("sPGoal");
    
    dbSetup.createProducts();
    dbSetup.createsPromotions();
    dbSetup.createCompanies();
    dbSetup.createAccounts();
    
    List<User> allUsers = dbSetup.getAll(User.class);
    allUsers.stream().filter(u -> u.getAnalysis() != null)
        .forEach(u -> spGoalFactory.addGoalsForUser(u));
    
    /* Create business goal to against 1 company */
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/goals/createGoal").param("name", "Goal1")
                .param("description", "Hello this is correct").param("isMandatory", "true")
                .param("status", "ACTIVE").param("category", "Business").param("accounts", "1")
                .param("allAccounts", "false").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* Test duplicate goals */
    /* Create business goal to against 1 company */
    MvcResult error = this.mockMvc
        .perform(
            post("/sysAdmin/goals/createGoal").param("name", "Goal1")
                .param("description", "Hello this is correct").param("isMandatory", "true")
                .param("status", "ACTIVE").param("category", "Business").param("accounts", "1")
                .param("allAccounts", "false").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal already exist, Please add different goal")).andReturn();
    log.debug("The MVC Response : " + error.getResponse().getContentAsString());
    
    /* create individual goal */
    /* Create business goal to against 1 company */
    MvcResult individual = this.mockMvc
        .perform(
            post("/sysAdmin/goals/createGoal").param("name", "GoalIndividual")
                .param("description", "Hello this is individual goal").param("isMandatory", "true")
                .param("status", "ACTIVE").param("category", "Individual").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + individual.getResponse().getContentAsString());
   
    /* All accounts */
    
    /* Create business goal to against 1 company */
    MvcResult allAccounts = this.mockMvc
        .perform(
            post("/sysAdmin/goals/createGoal").param("name", "GoalAllAccount")
                .param("description", "Hello this is correct").param("isMandatory", "true")
                .param("status", "ACTIVE").param("category", "Business").param("accounts", "1","2")
                .param("allAccounts", "true").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + allAccounts.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testInvalidateAndValidateGoal() {
    
    try {
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllPromotions();
      dbSetup.removeAllProducts();
      dbSetup.removeAll("sPGoal");
      
      dbSetup.createProducts();
      dbSetup.createsPromotions();
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      
      List<User> allUsers = dbSetup.getAll(User.class);
      allUsers.stream().filter(u -> u.getAnalysis() != null)
          .forEach(u -> spGoalFactory.addGoalsForUser(u));
      
      /* Create business goal to against 1 company */
      final String goalName = "Goal1";
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/createGoal").param("name", goalName)
                  .param("description", "Hello this is correct").param("isMandatory", "true")
                  .param("status", "ACTIVE").param("category", "Business").param("accounts", "1")
                  .param("allAccounts", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPGoal> all = dbSetup.getAll(SPGoal.class);
      Optional<SPGoal> findFirst = all.stream().filter(g -> g.getName().equals(goalName))
          .findFirst();
      
      assertTrue(findFirst.isPresent());
      SPGoal spGoal = findFirst.get();

      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/deactivate")
                .param("goalId", spGoal.getId())
                .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/activate")
                .param("goalId", spGoal.getId())
                .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      final String individualGoalName = "GoalIndividual";
      result = this.mockMvc
      .perform(
          post("/sysAdmin/goals/createGoal").param("name", "GoalIndividual")
              .param("description", "Hello this is individual goal").param("isMandatory", "true")
              .param("status", "ACTIVE").param("category", "Individual").contentType(MediaType.TEXT_PLAIN).session(session))
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$.success").exists())
      .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(SPGoal.class);
      findFirst = all.stream().filter(g -> g.getName().equals(individualGoalName))
          .findFirst();
      
      assertTrue(findFirst.isPresent());
      spGoal = findFirst.get();

      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/deactivate")
                .param("goalId", spGoal.getId())
                .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/activate")
                .param("goalId", spGoal.getId())
                .contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testGetAllGoal() throws Exception {
    
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    
    dbSetup.createProducts();
    dbSetup.createsPromotions();
    dbSetup.createCompanies();
    dbSetup.createAccounts();
    
    MvcResult individual = this.mockMvc
        .perform(
            post("/sysAdmin/goals/getAllGoals").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + individual.getResponse().getContentAsString());
   
  }

  @Test
  public void testUpdateGoal() {
    
    try {
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllPromotions();
      dbSetup.removeAllProducts();
      dbSetup.removeSpGoals();
      dbSetup.removeAllUserGoals();
      
      dbSetup.createProducts();
      dbSetup.createsPromotions();
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      dbSetup.createGoals();
      
      final String goalName = "GoalUpdateTest";
      MvcResult allAccounts = this.mockMvc
          .perform(
              post("/sysAdmin/goals/createGoal")
                  .param("name", goalName)
                  .param("description", "Hello this is correct")
                  .param("isMandatory", "true")
                  .param("status", "ACTIVE")
                  .param("category", "Business")
                  .param("allAccounts", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + allAccounts.getResponse().getContentAsString());
      
      List<SPGoal> all = dbSetup.getAll(SPGoal.class);
      Optional<SPGoal> findFirst = all.stream().filter(g -> g.getName().equals(goalName))
          .findFirst();
      
      assertTrue(findFirst.isPresent());
      
      SPGoal spGoal = findFirst.get();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal not found to update."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<User> allUsers = dbSetup.getAll(User.class);
      allUsers.stream().filter(u -> u.getAnalysis() != null)
          .forEach(u -> spGoalFactory.addGoalsForUser(u));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      // updating to an existing name
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", "GoalAllAccount")
              .param("category", GoalCategory.Individual + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal already exist, Please add different goal"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      String goalName2 = goalName + "Updated";
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Individual + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      // moving status to invalid
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Individual + "")
              .param("status", GoalStatus.INACTIVE + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      // moving status from individual to business
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Business + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      // moving status from business to accounts only
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Business + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .param("accounts", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // moving status from business change company
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Business + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .param("accounts", "2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // moving status from business to all business
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Business + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // moving status from business to inactive
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Business + "")
              .param("status", GoalStatus.INACTIVE + "")
              .param("allAccounts", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // moving status from business to Individual
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Individual + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // moving status from mandatory to optional
      result = this.mockMvc
          .perform(
              post("/sysAdmin/goals/updateGoal")
              .param("goalId", spGoal.getId())
              .param("name", goalName2)
              .param("category", GoalCategory.Individual + "")
              .param("status", GoalStatus.ACTIVE + "")
              .param("allAccounts", "true")
              .param("isMandatory", "false")
              .contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testGetGoalDetail() throws Exception {
    
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeSpGoals();
    
    dbSetup.createProducts();
    dbSetup.createsPromotions();
    dbSetup.createCompanies();
    dbSetup.createAccounts();
    dbSetup.createGoals();
    
    MvcResult getGoalDetail = this.mockMvc
        .perform(
            post("/sysAdmin/goals/getGoalDetail").param("goalId", "6").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + getGoalDetail.getResponse().getContentAsString());
   
    /* with company */
    MvcResult getGoalDetailreps = this.mockMvc
        .perform(
            post("/sysAdmin/goals/getGoalDetail").param("goalId", "7").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + getGoalDetailreps.getResponse().getContentAsString());
  }
  
}
