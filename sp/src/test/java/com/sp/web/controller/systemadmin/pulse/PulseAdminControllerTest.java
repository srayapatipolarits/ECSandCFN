package com.sp.web.controller.systemadmin.pulse;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

public class PulseAdminControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAll() {
    try {
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseQuestion", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testUpdateStatus() {
    try {
      List<PulseQuestionSet> pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      PulseQuestionSet pulseQuestionSet = pulseQuestionSetList.get(0);
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/updateStatus")
              .param("pulseQuestionSetId", pulseQuestionSet.getId())
              .param("status", PulseQuestionSetStatus.InActive + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      pulseQuestionSet = pulseQuestionSetList.get(0);
      assertThat(pulseQuestionSet.getStatus(), is(equalTo(PulseQuestionSetStatus.InActive)));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/updateStatus")
              .param("pulseQuestionSetId", pulseQuestionSet.getId())
              .param("status", PulseQuestionSetStatus.Active + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      pulseQuestionSet = pulseQuestionSetList.get(0);
      assertThat(pulseQuestionSet.getStatus(), is(equalTo(PulseQuestionSetStatus.Active)));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCreate() {
    try {
      File pulseQuestionFile = new File("src/test/java/testPulseQuestions.json");
      FileReader fileReader = new FileReader(pulseQuestionFile);
      StringBuffer sb = new StringBuffer();
      int readCount = 0;
      char[] charBuff = new char[1000];
      while (readCount >= 0) {
        readCount = fileReader.read(charBuff);
        sb.append(charBuff);
        charBuff = new char[1000];
      }
      fileReader.close();

      MockMultipartFile file = new MockMultipartFile("pulseQuestionSet", "testPulseQuestions",
          "text/json", sb.toString().getBytes());

      MockMultipartFile badFile = new MockMultipartFile("pulseQuestionSet", "testPulseQuestions",
          "text/json", "This is a bad file".getBytes());
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(fileUpload("/sysAdmin/pulse/createPulseFile").file(badFile).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Could not load default pulse question set from file."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(fileUpload("/sysAdmin/pulse/createPulseFile").file(file).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String pulseName = "testPulse";
      List<PulseQuestionSet> all = dbSetup.getAll(PulseQuestionSet.class);
      Optional<PulseQuestionSet> findFirst = all.stream().filter(pqs -> pqs.getName().equals(pulseName)).findFirst();
      if (findFirst.isPresent()) {
        dbSetup.remove(findFirst.get());
      }
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/createPulse")
              .param("name", pulseName)
              .param("questionSetType", QuestionSetType.Company + "")
              .param("status", PulseQuestionSetStatus.Active + "")
              .param("companyId", "1, 2")
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
  
  @Test
  public void testGetDetails() {
    try {
      List<PulseQuestionSet> pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      PulseQuestionSet pulseQuestionSet = pulseQuestionSetList.get(0);
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/getDetails")
              .param("pulseQuestionSetId", pulseQuestionSet.getId())
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

  @Test
  public void testDownloadJson() {
    try {
      List<PulseQuestionSet> pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      PulseQuestionSet pulseQuestionSet = pulseQuestionSetList.get(0);
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/pulse/downloadJson")
              .param("pulseQuestionSetId", pulseQuestionSet.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
 
  @Test
  public void testUpdatePulse() {
    try {
      List<PulseQuestionSet> pulseQuestionSetList = dbSetup.getAll(PulseQuestionSet.class);
      PulseQuestionSet pulseQuestionSet = pulseQuestionSetList.get(0);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/pulse/update")
              .param("pulseQuestionSetId", pulseQuestionSet.getId())
              .param("type", QuestionSetType.Company + "")
              .param("companyIds", "1, 2")
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
