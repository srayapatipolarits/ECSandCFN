package com.sp.web.controller.tutorial;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.tutorial.SPTutorialForm;
import com.sp.web.form.tutorial.TutorialStepForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.model.tutorial.TutorialActivityData;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.tutorial.SPTutorialFactory;
import com.sp.web.service.tutorial.SPTutorialFactoryCache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPTutorialControllerTest extends SPTestLoggedInBase {

  @Autowired
  SPTutorialFactoryCache factoryCache;
  
  @Autowired
  SPTutorialFactory tutorialFactory;
  
  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      dbSetup.removeAll("userTutorialActivity");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/tutorial/get")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorial").doesNotExist())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      User user = dbSetup.getUser();
      UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      userTutorialActivity.addTutorial(createTutorial.getId());
      factoryCache.save(userTutorialActivity);
      
      result = this.mockMvc
          .perform(
              post("/tutorial/get")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorial").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetDetails() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      dbSetup.removeAll("userTutorialActivity");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/tutorial/getDetails")
              .param("tutorialId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Tutorial id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/tutorial/getDetails")
              .param("tutorialId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not assigned tutorial."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      User user = dbSetup.getUser();
      UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      final String tutorialId = createTutorial.getId();
      userTutorialActivity.addTutorial(tutorialId);
      factoryCache.save(userTutorialActivity);
      
      result = this.mockMvc
          .perform(
              post("/tutorial/getDetails")
              .param("tutorialId", tutorialId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorial").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      TutorialActivityData userActivityData = userTutorialActivity.getUserActivityData(tutorialId);
      String stepId = createTutorial.getStepIds().get(0);
      userActivityData.markComplete(stepId, "1");
      factoryCache.save(userTutorialActivity);
      
      result = this.mockMvc
          .perform(
              post("/tutorial/getDetails")
              .param("tutorialId", tutorialId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorial").exists())
          .andExpect(jsonPath("$.success.tutorial.completedCount").value(1))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testMarkComplete() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      dbSetup.removeAll("userTutorialActivity");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/tutorial/markComplete")
              .param("tutorialId", " ")
              .param("stepId", " ")
              .param("actionId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Tutorial id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/tutorial/markComplete")
              .param("tutorialId", "abc")
              .param("stepId", " ")
              .param("actionId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not assigned tutorial."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      User user = dbSetup.getUser();
      UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      final String tutorialId = createTutorial.getId();
      userTutorialActivity.addTutorial(tutorialId);
      factoryCache.save(userTutorialActivity);
      
      result = this.mockMvc
          .perform(
              post("/tutorial/markComplete")
              .param("tutorialId", tutorialId)
              .param("stepId", " ")
              .param("actionId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      TutorialActivityData selectedActivity = userTutorialActivity.getSelectedActivity();
      assertTrue(selectedActivity.getCount() == 0);
      
      String stepId = createTutorial.getStepIds().get(0);
      result = this.mockMvc
          .perform(
              post("/tutorial/markComplete")
              .param("tutorialId", tutorialId)
              .param("stepId", stepId)
              .param("actionId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
      selectedActivity = userTutorialActivity.getSelectedActivity();
      assertTrue(selectedActivity.getCount() == 1);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
      
  private SPTutorial createTutorial() throws Exception {
    final SPTutorialForm form = new SPTutorialForm();
    form.setName("Getting to know SurePeople.");
    form.setDescription("The description goes here.");
    form.setImgUrl("http://www.google.com");
    List<TutorialStepForm> steps = new ArrayList<TutorialStepForm>();
    form.setSteps(steps);
    final TutorialStepForm tForm = new TutorialStepForm();
    tForm.setName("Step1");
    tForm.setStatus(GoalStatus.ACTIVE);
    final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory category = new DSActionCategory();
    category.setTitle("Understand the dynamics of the Personality.");
    category.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action = new DSAction();
    action.setActive(true);
    action.setTitle("Understand the Dynamics of the Personality");
    action.setDescription("Description for Understand the Dynamics of the Personality.");
    action.setMediaUrl("http://www.google.com");
    action.setMediaLinkText("Do something with prism.");
    action.setMediaImageLink("http://www.google.com");
    action.setMediaAltText("Some video link.");
    Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Completion, true);
    action.setPermissions(permissions);
    actionList.add(action);
    category.setActionList(actionList);
    devStrategyActionCategoryList.add(category);
    tForm.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    steps.add(tForm);

    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(form);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/tutorial/create")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<SPTutorial> all = dbSetup.getAll(SPTutorial.class);
    assertTrue(all.size() > 0);
    return all.get(0);
  }
}
