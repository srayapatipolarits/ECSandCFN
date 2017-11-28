package com.sp.web.controller.todo;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.form.Operation;
import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.form.goal.PracticeAreaActionForm;
import com.sp.web.form.todo.TodoForm;
import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.TodoType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.todo.TodoFactoryCache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoControllerTest extends SPTestLoggedInBase {

  @Autowired
  private CompanyFactory companyFactory;

  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private TodoFactoryCache factoryCache;
  
  @Test
  public void testGetALL() {
    try {
      
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/todo/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.todoListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      
      // adding a todo 
      TodoRequest todoRequest = new TodoRequest("Some work to do.", LocalDateTime.now(), TodoType.Todo);
      todoFactory.addTodo(user, todoRequest);
      
      result = this.mockMvc
          .perform(
              post("/todo/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.todoListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao, Operation.ADD);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      todoFactory.addTodo(user, TodoType.OrgPlan, actionPlan.getPracticeAreaIdList().get(0),
          actionPlan.getId());
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      result = this.mockMvc
          .perform(
              post("/todo/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.todoListing", hasSize(2)))
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
      
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/todo/get")
              .param("refId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Ref id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/todo/get")
              .param("refId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Task not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      
      // adding a todo 
      TodoRequest todoRequest = new TodoRequest("Some work to do.", LocalDateTime.now(),
          TodoType.Todo);
      todoFactory.addTodo(user, todoRequest);

      result = this.mockMvc
          .perform(
              post("/todo/get")
              .param("refId", todoRequest.getRefId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.todo.refId").value(todoRequest.getRefId()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao, Operation.ADD);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      TodoRequest addTodo = todoFactory.addTodo(user, TodoType.OrgPlan, actionPlan.getPracticeAreaIdList().get(0),
          actionPlan.getId());
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      result = this.mockMvc
          .perform(
              post("/todo/get")
              .param("refId", addTodo.getParentRefId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.todo.parentRefId").value(addTodo.getParentRefId()))
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
      
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      
      ObjectMapper om = new ObjectMapper();
      
      TodoForm form = new TodoForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/todo/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Text is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setText("Todo number 1.");
      
      result = this.mockMvc
          .perform(
              post("/todo/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
      assertThat(userTodoRequests.getTodoTasks().size(), equalTo(1));

      form.setRefId("1");
      form.setText("Todo number 2.");
      form.setDueBy(LocalDateTime.now().plusDays(10));
      
      String content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/todo/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      userTodoRequests = factoryCache.getUserTodoRequests(user);
      assertThat(userTodoRequests.getTodoTasks().size(), equalTo(2));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      
      ObjectMapper om = new ObjectMapper();
      
      TodoForm form = new TodoForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/todo/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("RefId is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      form.setRefId("abc");
      
      result = this.mockMvc
          .perform(
              post("/todo/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Text is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setText("Todo updated to new.");
      
      result = this.mockMvc
          .perform(
              post("/todo/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Request not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      TodoRequest todo = new TodoRequest("A new todo added.", LocalDateTime.now().plusDays(10));
      User user = dbSetup.getUser();
      TodoRequest addTodo = todoFactory.addTodo(user, todo);
      form.setRefId(addTodo.getRefId());

      result = this.mockMvc
          .perform(
              post("/todo/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setDueBy(LocalDateTime.now().plusDays(20));

      result = this.mockMvc
          .perform(
              post("/todo/update")
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
      
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/todo/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Ref id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/todo/delete")
              .param("refId", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Request not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      TodoRequest todo = new TodoRequest("A new todo added.", LocalDateTime.now().plusDays(10));
      User user = dbSetup.getUser();
      TodoRequest addTodo = todoFactory.addTodo(user, todo);
      
      
      result = this.mockMvc
          .perform(
              post("/todo/delete")
              .param("refId", addTodo.getRefId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      UserTodoRequests userTodoRequests = factoryCache.getUserTodoRequests(user);
      assertThat(userTodoRequests.getTodoTasks().size(), equalTo(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
      
  private ActionPlan createActionPlan() throws Exception {
    CompanyDao company = companyFactory.getCompany("1");
    company.setActionPlanAdminEnabled(true);
    companyFactory.updateCompany(company);
    
    final ActionPlanForm actionPlanForm = new ActionPlanForm();
    actionPlanForm.setName("Test Action Plan");
    actionPlanForm.setCreatedByCompanyId("1");
    actionPlanForm.setDescription("Description for action plan.");
    actionPlanForm.setType(ActionPlanType.Company);
    actionPlanForm.setStepType(StepType.All);
    
    final List<PracticeAreaActionForm> practiceAreaList = new ArrayList<PracticeAreaActionForm>();
    PracticeAreaActionForm practiceArea = new PracticeAreaActionForm();
    practiceArea.setName("Test Practice Area");
    practiceArea.setDescription("Overview for the test practice area.");
    practiceArea.setStatus(GoalStatus.ACTIVE);
    practiceAreaList.add(practiceArea);
    actionPlanForm.setPracticeAreaList(practiceAreaList);
    List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory devStrategyActionCategory = new DSActionCategory();
    devStrategyActionCategoryList.add(devStrategyActionCategory);
    practiceArea.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    devStrategyActionCategory.setTitle("Do some action");
    devStrategyActionCategory.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action1 = new DSAction();
    action1.setTitle("Our Values");
    action1.setTimeInMins(83);
    action1.setType(DSActionType.Group);
    action1.setActive(true);
    action1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
        + "Mauris condimentum lacus nisl, eget place");
    devStrategyActionCategory.setActionList(actionList);
    final List<DSActionData> actionData = new ArrayList<DSActionData>();
    DSActionData actionData1 = new DSActionData();
    actionData1.setTitle("Comcast Announces Fifth Back-To-School Kickoff");
    actionData1.setType(DSActionDataType.Video);
    actionData1.setLinkText("Play Video");
    actionData1.setUrl("sprepo.surepeople.com/cdn/");
    actionData1.setImageLink("sprepo.surepeople.com/cdn/");
    actionData1.setAltText("Reimagining");
    actionData.add(actionData1);
    action1.setActionData(actionData);
    actionList.add(action1);
    Map<DSActionConfig, Boolean> actionDataPermissions = new HashMap<DSActionConfig, Boolean>();
    actionDataPermissions.put(DSActionConfig.Completion, Boolean.TRUE);
    actionData1.setPermissions(actionDataPermissions);
    DSActionData actionData2 = new DSActionData();
    actionData2.setTitle("View our Corporate Social Responsibility Report");
    actionData2.setType(DSActionDataType.Video);
    actionData2.setLinkText("Play Video");
    actionData2.setUrl("sprepo.surepeople.com/cdn/");
    actionData2.setImageLink("sprepo.surepeople.com/cdn/");
    Map<DSActionConfig, Boolean> actionDataPermissions2 = new HashMap<DSActionConfig, Boolean>();
    actionDataPermissions2.put(DSActionConfig.Completion, Boolean.TRUE);
    actionDataPermissions2.put(DSActionConfig.Note, Boolean.TRUE);
    actionData2.setPermissions(actionDataPermissions2);
    actionData.add(actionData2);

    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(actionPlanForm);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/admin/account/actionPlan/create")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<ActionPlan> all = dbSetup.getAll(ActionPlan.class);
    assertTrue(all.size() > 0);
    return all.get(0);
  }  
}
