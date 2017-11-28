package com.sp.web.controller.tutorial;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.dao.tutorial.SPTutorialDao;
import com.sp.web.form.tutorial.SPTutorialForm;
import com.sp.web.form.tutorial.TutorialStepForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.model.tutorial.TutorialActivityData;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.tutorial.SPTutorialFactoryCache;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminSPTutorialControllerTest extends SPTestLoggedInBase {

  @Autowired
  SPTutorialFactoryCache factoryCache;
  
  @Test
  public void testGetALL() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorialListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating a tutorial
      createTutorial();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorialListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("ID is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Tutorial not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      SPTutorial createTutorial = createTutorial();

      final String tutorialId = createTutorial.getId();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/get")
              .param("id", tutorialId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.tutorial.id").value(tutorialId))
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
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      
      ObjectMapper om = new ObjectMapper();
      
      SPTutorialForm form = new SPTutorialForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setName("Getting to know SurePeople.");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Description required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      form.setDescription("Description for Getting to know SurePeople.");

      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one step required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      form.setImgUrl("http://www.google.com");
      final List<TutorialStepForm> steps = new ArrayList<TutorialStepForm>();
      steps.add(createTutorialStep(1));
      steps.add(createTutorialStep(2));
      
      form.setSteps(steps);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
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

  private TutorialStepForm createTutorialStep(int index) {
    final TutorialStepForm stepForm = new TutorialStepForm();
    stepForm.setStatus(GoalStatus.ACTIVE);
    stepForm.setName("PRISM Portrait " + index);
    final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory category = new DSActionCategory();
    category.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action = new DSAction();
    action.setActive(true);
    action.setTitle("Understand the Dynamics of the Personality " + index);
    action.setDescription("Description for Understand the Dynamics of the Personality.");
    action.setMediaUrl("http://www.google.com");
    action.setMediaLinkText("Do something with prism. " + index);
    action.setMediaImageLink("http://www.google.com");
    action.setMediaAltText("Some video link.");
    Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Completion, true);
    action.setPermissions(permissions);
    actionList.add(action);
    DSAction action2 = new DSAction();
    action2.setActive(true);
    action2.setTitle("Understand the Dynamics of the Personality 2" + index);
    action2.setDescription("Description for Understand the Dynamics of the Personality.");
    action2.setMediaUrl("http://www.google.com");
    action2.setMediaLinkText("Do something with prism. 2" + index);
    action2.setMediaImageLink("http://www.google.com");
    action2.setMediaAltText("Some video link.");
    action2.setPermissions(permissions);
    actionList.add(action2);
    category.setActionList(actionList);
    devStrategyActionCategoryList.add(category);

    stepForm.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    return stepForm;
  }
  
  @Test
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      dbSetup.removeAll("userTutorialActivity");
      
      ObjectMapper om = new ObjectMapper();
      
      SPTutorialForm form = new SPTutorialForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("ID is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      form.setId("abc");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setName("Getting to know SurePeople.");
      form.setDescription("Description for Getting to know SurePeople.");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one step required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      List<TutorialStepForm> steps = new ArrayList<TutorialStepForm>();
      TutorialStepForm step = new TutorialStepForm();
      final String id = createTutorial.getId();
      SPTutorialDao tutorial = factoryCache.get(id, Constants.DEFAULT_LOCALE);
      SPGoal existingStep = tutorial.getSteps().get(0);
      BeanUtils.copyProperties(existingStep, step);
      steps.add(step);
      steps.add(createTutorialStep(2));
      form.setSteps(steps);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Tutorial not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setId(id);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      UserTutorialActivity userActivity = factoryCache.getUserActivity(user.getId());
      userActivity.addTutorial(id);
      
      SPTutorialDao spTutorialDao = factoryCache.get(id, Constants.DEFAULT_LOCALE);
      SPGoal stepData = spTutorialDao.getSteps().get(1);
      TutorialActivityData selectedActivity = userActivity.getSelectedActivity();
      selectedActivity.markComplete(stepData.getId(), stepData.getActionIds().stream().findFirst().get());
      factoryCache.save(userActivity);
      
      steps.remove(1);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
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
      
  @Test
  public void testDelete() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("ID is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/delete")
              .param("id", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Tutorial not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/tutorial/delete")
              .param("id", createTutorial.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPTutorial> all = dbSetup.getAll(SPTutorial.class);
      assertTrue(all.size() == 0);
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetSmartlingJson() {
    try {
      
      dbSetup.removeAll("sPTutorial");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/createTutorialJson")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPTutorial createTutorial = createTutorial();
      
      result = this.mockMvc
          .perform(
              get("/sysAdmin/createTutorialJson")
              .param("id", createTutorial.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
      
  private SPTutorial createTutorial() throws Exception {
    final Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Completion, true);
    
    final SPTutorialForm form = new SPTutorialForm();
    form.setName("Learn about yourself");
    form.setDescription("Explore ways to work with and support people who think, act and work differently than you.");
    form.setImgUrl("http://www.google.com");
    List<TutorialStepForm> steps = new ArrayList<TutorialStepForm>();
    form.setSteps(steps);
    final TutorialStepForm tStepForm1 = new TutorialStepForm();
    tStepForm1.setName("Step 1: Prism Portrait");
    tStepForm1.setStatus(GoalStatus.ACTIVE);
    final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory category = new DSActionCategory();
    category.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();

    DSAction action = new DSAction();
    action.setActive(true);
    action.setTitle("Understand the dynamics of Personality");
    action.setDescription("Start your personal journey by mastering an understanding of personality dynamics.");
    action.setMediaImageLink("http://www.google.com");
    final List<DSActionData> actionData = new ArrayList<DSActionData>();
    DSActionData actionData1 = new DSActionData();
    actionData1.setLinkText("Overview of the Ambassador Personality (2min)");
    actionData1.setUrl("http://www.google.com");
    actionData1.setPermissions(permissions);
    actionData.add(actionData1);
    action.setActionData(actionData);
    actionList.add(action);
    
    DSAction action2 = new DSAction();
    action2.setActive(true);
    action2.setTitle("Reflect on your Primary Personality");
    action2.setDescription("Get started learning about yourself by finding "
        + "out what it means to be an Ambassador and discover how this "
        + "Primary Personality appears to others.");
    action2.setMediaImageLink("http://www.google.com");
    final List<DSActionData> actionDataList = new ArrayList<DSActionData>();
    DSActionData actionData2 = new DSActionData();
    actionData2.setLinkText("Overview of the Ambassador Personality (2min)");
    actionData2.setUrl("http://www.google.com");
    actionData2.setPermissions(permissions);
    actionDataList.add(actionData2);
    action.setActionData(actionDataList);
    actionList.add(action2);
    
    category.setActionList(actionList);
    devStrategyActionCategoryList.add(category);
    tStepForm1.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    steps.add(tStepForm1);

    final TutorialStepForm tStepForm2 = new TutorialStepForm();
    tStepForm2.setName("Step 2: Prism Lens");
    tStepForm2.setStatus(GoalStatus.ACTIVE);
    final List<DSActionCategory> devStrategyActionCategoryList2 = new ArrayList<DSActionCategory>();
    DSActionCategory category2 = new DSActionCategory();
    category2.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList2 = new ArrayList<DSAction>();

    DSAction action12 = new DSAction();
    action12.setActive(true);
    action12.setTitle("Request a Prism Lens");
    action12.setDescription("Expand your self-understanding by finding out how others perceive your personality.");
    action12.setMediaImageLink("http://www.google.com");
    final List<DSActionData> actionDataList12 = new ArrayList<DSActionData>();
    DSActionData actionData12 = new DSActionData();
    actionData12.setLinkText("Request 2 Prism Lens reports");
    actionData12.setUrl("http://www.google.com");
    actionData12.setPermissions(permissions);
    actionDataList12.add(actionData12);
    action12.setActionData(actionDataList12);
    actionList2.add(action12);
    
    DSAction action22 = new DSAction();
    action22.setActive(true);
    action22.setTitle("Complete a Prism Lens for another");
    action22.setDescription("Provide your own perspective to another person to broaden "
        + "their self-understanding. Send a message or start a group discussion.");
    action22.setMediaImageLink("http://www.google.com");
    final List<DSActionData> actionDataList22 = new ArrayList<DSActionData>();
    DSActionData actionData22 = new DSActionData();
    actionData22.setLinkText("Complete a Prism Lens for another");
    actionData22.setUrl("http://www.google.com");
    actionData22.setPermissions(permissions);
    actionDataList22.add(actionData22);
    action22.setActionData(actionDataList22);
    actionList2.add(action22);
    
    category2.setActionList(actionList2);
    devStrategyActionCategoryList2.add(category2);
    tStepForm2.setDevStrategyActionCategoryList(devStrategyActionCategoryList2);
    steps.add(tStepForm2);    
    
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
