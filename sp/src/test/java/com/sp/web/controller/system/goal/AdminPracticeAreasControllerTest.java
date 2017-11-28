package com.sp.web.controller.system.goal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
 * @author Dax Abraham
 *
 *         The test cases for Admin practice areas.
 */
public class AdminPracticeAreasControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory spGoalFactory;
  
  @Test
  public void testGetAllPracticeAreas() {
    
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      MvcResult individual = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pratcieAreas[0].status").value("ACTIVE"))
          .andReturn();
      
      log.debug("The MVC Response : " + individual.getResponse().getContentAsString());
      
    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
    
  }
  
  @Test
  public void testActivateDeactivatePracticeAreas() {
    
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      // invalid goal id
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/activateDeactivate").param("practiceAreaId", "abcd")
                  .param("status", GoalStatus.ACTIVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Practice area not found with id :abcd")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // same status
      String goalId = "1";
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/activateDeactivate").param("practiceAreaId", goalId)
                  .param("status", GoalStatus.ACTIVE.toString()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Practice area already set to :ACTIVE")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // change status
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/activateDeactivate").param("practiceAreaId", goalId)
                  .param("status", GoalStatus.INACTIVE.toString())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPGoal> all = dbSetup.getAll(SPGoal.class);
      Optional<SPGoal> findFirst = all.stream().filter(g -> g.getId().equals(goalId)).findFirst();
      assertTrue(findFirst.isPresent());
      assertEquals(GoalStatus.INACTIVE, findFirst.get().getStatus());
      
    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCreateNewPracticeArea() {
    
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      // development strategy missing
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", "Abcd")
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Practice area development strategy, minimum 3 required."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", "Abcd")
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Development strategy description required."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", "Abcd")
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Development strategy id required."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", "Abcd")
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // trying again
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", "Abcd")
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Error creating the Practice Area."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
  }
    
  @Test
  public void testUpdatePracticeArea() {
    
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();

      // create a new practice area to play around with
      final String goalName = "Abcd";
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/create")
                  .param("name", goalName)
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPGoal spGoal = getGoal(goalName);
      
      // invalid goal id
      final String invalidGoalId = spGoal.getId() + "1";
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/update")
                  .param("practiceAreaId", invalidGoalId)
                  .param("name", goalName)
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Practice area not found with id :" + invalidGoalId))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      // invalid goal data
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/update")
                  .param("practiceAreaId", spGoal.getId())
                  .param("name", goalName)
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Development strategy description required."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      // invalid goal data
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/update")
                  .param("practiceAreaId", spGoal.getId())
                  .param("name", goalName)
                  .param("description", "Ha ha ha ha ha.")
                  .param("status", GoalStatus.ACTIVE.toString())
                  .param("developmentStrategyList[0].dsDescription", "DS 1 : Ha ha ha")
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Development strategy id required."))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      String updateKey = "new";
      // invalid goal data
      result = this.mockMvc
          .perform(
              post("/sysAdmin/practiceArea/update")
                  .param("practiceAreaId", spGoal.getId())
                  .param("name", spGoal.getName() + updateKey)
                  .param("description", spGoal.getDescription() + updateKey)
                  .param("status", GoalStatus.INACTIVE.toString())
                  .param("developmentStrategyList[0].id", "0")
                  .param("developmentStrategyList[0].dsDescription",
                      spGoal.getDevelopmentStrategyList().get(0).getDsDescription() + updateKey)
                  .param("developmentStrategyList[0].isActive", "false")    
                  .param("developmentStrategyList[1].id", "1")
                  .param("developmentStrategyList[1].dsDescription", "DS 2 : Ha ha ha")
                  .param("developmentStrategyList[1].isActive", "true")
                  .param("developmentStrategyList[2].id", "2")
                  .param("developmentStrategyList[2].dsDescription", "DS 3 : Ha ha ha")
                  .param("developmentStrategyList[1].isActive", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      SPGoal spGoal2 = getGoal(goalName + updateKey);
      assertThat(spGoal2.getName(), is(spGoal.getName() + updateKey));
      assertThat(spGoal2.getDescription(), is(spGoal.getDescription() + updateKey));
      assertThat(spGoal2.getStatus(), is(GoalStatus.INACTIVE));
      assertThat(spGoal2.getDevelopmentStrategyList().get(0).getDsDescription(), is(spGoal
          .getDevelopmentStrategyList().get(0).getDsDescription()
          + updateKey));
      assertThat(spGoal2.getDevelopmentStrategyList().get(0).isActive(), is(false));

    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
  }

  private SPGoal getGoal(final String goalName) {
    List<SPGoal> all = dbSetup.getAll(SPGoal.class);
    Optional<SPGoal> findFirst = all.stream().filter(g -> g.getName().equals(goalName)).findFirst();
    assertTrue(findFirst.isPresent());
    SPGoal spGoal = findFirst.get();
    return spGoal;
  }
}
