package com.sp.web.controller.system.goal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.form.admin.personality.PersonalitySwotForm;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SwotType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.goals.SPGoalFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The test cases for Admin practice areas.
 */
public class AdminPersonalityPracticeAreaControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory spGoalFactory;
  
  @Test
  public void testGetAllPersonalityPracticeAreas() {
    
    try {
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/personalityMapping/getAll").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testUpdatePersonalityPracticeAreas() {
    
    try {
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      // invalid personality type
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/personalityMapping/update")
                  .param("personalityType", PersonalityType.Invalid.toString())
                  .param("goalIds", "abc, def").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Personality practice area not found for :Invalid")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid goal ids
      result = this.mockMvc
          .perform(
              post("/sysAdmin/personalityMapping/update")
                  .param("personalityType", PersonalityType.Navigator.toString())
                  .param("goalIds", "abc, def").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Goal not found for id :abc"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      PersonalityPracticeArea personalityPracticeArea = getPersonalityPracticeArea();
      assertThat(personalityPracticeArea.getGoalIds().size(), is(2));
      
      // valid request
      result = this.mockMvc
          .perform(
              post("/sysAdmin/personalityMapping/update")
                  .param("personalityType", PersonalityType.Navigator.toString())
                  .param("goalIds", "1, 2, 3").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      personalityPracticeArea = getPersonalityPracticeArea();
      assertThat(personalityPracticeArea.getGoalIds().size(), is(3));
      
    } catch (Exception exp) {
      exp.printStackTrace();
      fail();
    }
  }

  private PersonalityPracticeArea getPersonalityPracticeArea() {
    List<PersonalityPracticeArea> all = dbSetup.getAll(PersonalityPracticeArea.class);
    Optional<PersonalityPracticeArea> findFirst = all.stream()
        .filter(p -> p.getPersonalityType() == PersonalityType.Navigator).findFirst();
    assertTrue(findFirst.isPresent());
    return findFirst.get();
  }

  
  @Test
  public void testGetAllSwotDetail() throws Exception {
      // create a new practice area to play around with
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/personalityMapping/swot/getSwotDetail")
                .contentType(MediaType.TEXT_PLAIN)
                .param("personalityType", PersonalityType.Navigator.toString()).session(
                session)).andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.swotTypes").isArray()).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void testUpdateSwot() throws Exception {
    dbSetup.removeAll("personalityPracticeArea");
    dbSetup.createPersonalityPracticeAreas();
    
    final PersonalitySwotForm swotForm = new PersonalitySwotForm();
    Map<SwotType, List<String>> swotProfileMap = new HashMap<SwotType, List<String>>();
    List<String> list = new ArrayList<String>();
    list.add("Test 1");
    list.add("Test 2");
    list.add("Test 3");
    swotProfileMap.put(SwotType.Strengths, list);
    swotProfileMap.put(SwotType.Weakness, list);
    swotProfileMap.put(SwotType.Opportunities, list);
    swotProfileMap.put(SwotType.Threats, list);
    
    swotForm.setPersonalityType(PersonalityType.Actuary);
    swotForm.setSwotProfileMap(swotProfileMap);
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(swotForm);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/personalityMapping/swot/update")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name not found."))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    // create a new practice area to play around with
     result = this.mockMvc
        .perform(
            post("/sysAdmin/personalityMapping/swot/getSwotDetail")
                .contentType(MediaType.TEXT_PLAIN)
                .param("personalityType", PersonalityType.Navigator.toString()).session(
                session)).andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.swotTypes").exists()).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
}
