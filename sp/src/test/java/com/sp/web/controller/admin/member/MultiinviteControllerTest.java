package com.sp.web.controller.admin.member;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.account.AccountRepository;
import com.sp.web.form.MultiInviteUserForm;
import com.sp.web.model.Account;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiinviteControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  private AccountRepository accountRepository;
  private String originalFilename = "member-multi.csv";
  int numberOfCandidatesInFile = 7;
  
  @Test
  public void testMultiInviteAddFile() {
    
    try {
      
      File candidateFile = new File("src/test/java/member-multi.csv");
      FileReader fileReader = new FileReader(candidateFile);
      StringBuffer sb = new StringBuffer();
      int readCount = 0;
      char[] charBuff = new char[1000];
      while (readCount >= 0) {
        readCount = fileReader.read(charBuff);
        sb.append(charBuff);
        charBuff = new char[1000];
      }
      fileReader.close();
      
      MockMultipartFile file = new MockMultipartFile("userFile", originalFilename, "text/csv", sb
          .toString().getBytes());
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(fileUpload("/admin/member/multiInviteAddFile").file(file).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Not enough subscriptions left for company :1")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Account account = accountRepository.findValidatedAccountByAccountId("1");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNumMember(50);
      accountRepository.updateAccount(account);
      
      result = this.mockMvc
          .perform(fileUpload("/admin/member/multiInviteAddFile").file(file).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<MultiInviteUserForm> users = IntStream.range(0, 5).mapToObj(i -> {
        MultiInviteUserForm userFOrm = new MultiInviteUserForm();
        userFOrm.setEmail("sampletest" + i + "@yopmail.com");
        userFOrm.setFirstName("sampletest" + i);
        userFOrm.setLastName("last" + i);
        return userFOrm;
      }).collect(Collectors.toList());
      
      ObjectMapper om = new ObjectMapper();
      String writeValueAsString = om.writeValueAsString(users);
      
      result = this.mockMvc
          .perform(
              post("/admin/member/multiInvite")
                  .content(writeValueAsString).session(session)
                  .contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      fail();
    }
  }
  
  @Test
  public void testMultiInvite() {
    fail("Not yet implemented");
  }
  
}
