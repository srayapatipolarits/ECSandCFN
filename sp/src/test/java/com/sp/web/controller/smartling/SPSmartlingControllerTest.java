package com.sp.web.controller.smartling;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;

public class SPSmartlingControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testUploadFiles() throws Exception {
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/translation/upload").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
  }
  
  @Test
  public void testUplaodFile() throws Exception {
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/translation/upload/messages")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
  }
  
  @Test
  public void testUplaodPracticeAreaJson() throws Exception {
    byte[] englishContent = FileUtils.readFileToByteArray(new File("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/src/test/java/PracticeArea_en-US.json"));
    byte[] spanishContent = FileUtils.readFileToByteArray(new File("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/src/test/java/PracticeArea_es-LA.json"));
    
    
    MockMultipartFile englishFile = new MockMultipartFile("json", "", "application/json", englishContent);
    
    MockMultipartFile spFile = new MockMultipartFile("json", "", "application/json", spanishContent);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/translation/updatePracticeAreaJson")
                    .file("spanishPracticeAreaJson",spanishContent)
                    .file("practiceAreaJson",englishContent)
                    .param("locale", "es_LA").session(session))
                    .andExpect(jsonPath("$.success").exists())
                    .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
  }
 
  @Test
  public void testSPGoalJson() throws Exception {
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/sysAdmin/createJsonDataTranslableSource")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(status().is2xxSuccessful())        
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andReturn();
    log.info("The mvc result is :" + result.getResponse().getContentAsString());
  }
  
}
