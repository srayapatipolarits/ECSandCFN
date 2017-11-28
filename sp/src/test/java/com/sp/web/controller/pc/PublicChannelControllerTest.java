package com.sp.web.controller.pc;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.personality.PrismPortraits;
import com.sp.web.form.CommentForm;
import com.sp.web.form.pchannel.PublicChannelForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.pc.PublicChannelFactory;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * PublicChannelControllerTest.
 * 
 * @author pradeepruhil
 *
 */
public class PublicChannelControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  PublicChannelFactory publicChannelFactory;
  /**
   * Test method for
   * {@link com.sp.web.controller.pc.PublicChannelController# getPublicChannel(java.lang.String, com.sp.web.model.SPFeature, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testGetPublicChannel() {
    fail("Not yet implemented");
  }
  
  
  /**
   * Test method for
   * {@link com.sp.web.controller.pc.PublicChannelController#createPublicChannel(com.sp.web.form.pchannel.PubliChannelForm, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testCreatePublicChannel() throws Exception {
    dbSetup.removeAll("publicChannel");
    dbSetup.removeAll("companyNewsFeed");
    
    
    PublicChannelForm form = new PublicChannelForm();
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(form);
    
    /* register the event for admin@admin.com user */
    User user1 = dbSetup.getUser("admin@admin.com");
    authenticationHelper.doAuthenticateWithoutPassword(session, user1);
    MvcResult sseResult = mockMvc.perform(get("/sse/push").session(session))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    /* register the event for dax@surepeople.com.com user */
    User user2 = dbSetup.getUser("pradeep1@surepeople.com");
    authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
    
    MvcResult sseResultUser2 = mockMvc.perform(get("/sse/push").session(session2))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/pubchannel/createPublicChannel").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.IllegalArgumentException").value(
                "No comment present, cannot create a public channel with no comment.")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* create a public channel for prism. */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    form.setComment(commentForm);
    form.setSpFeature(SPFeature.Prism);
    form.setSubModuleFeature(PrismPortraits.Personality.toString());
    form.setPcRefId(SPFeature.Prism.toString());
    request = om.writeValueAsString(form);
    log.debug("publicChannel request" + request);
    result = this.mockMvc
        .perform(
            post("/pubchannel/createPublicChannel").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    String contentAsString = sseResult.getResponse().getContentAsString();
    log.debug("The MVC Response : " + contentAsString);
    
    String userEvent2 = sseResultUser2.getResponse().getContentAsString();
//    String json = userEvent2.substring(5, userEvent2.length() - 2);
//    log.debug("The MVC Response userEvent2: " + json);

    /* create public channel for Practice area */
    testCreatePublicChannelGoals(sseResult, sseResultUser2);
    
  }
  
  private void testCreatePublicChannelGoals(MvcResult sseSession1, MvcResult sseSession2) throws Exception{
    dbSetup.removeArticles();
    dbSetup.createArticles();
    dbSetup.removeArticles();
    dbSetup.removeAll("personalityPracticeArea");
    dbSetup.removeSpGoals();
    dbSetup.createPersonalityPracticeAreas();
    dbSetup.createGoals();
    dbSetup.createArticles();
    
    dbSetup.removeAllUserGoals();
    String email = "pradeep1@surepeople.com";
    User user = dbSetup.getUser(email);
    addUserGoals(user);
    User user2 = dbSetup.getUser("admin@admin.com");
    addUserGoals(user2);
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user2);
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "2, 1, 3,10,4,9").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.info("The mvc results :" + result.getResponse().getContentAsString());
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "2, 1, 3,10,4,9").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.info("The mvc results :" + result.getResponse().getContentAsString());
    
    
    /* create the public channel */
    PublicChannelForm form = new PublicChannelForm();
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    form.setComment(commentForm);
    form.setSpFeature(SPFeature.Erti);
    form.setPcRefId("1");
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(form);
    log.info("request is " + request);
    result = this.mockMvc
        .perform(
            post("/pubchannel/createPublicChannel").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session2))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    String contentAsString = sseSession1.getResponse().getContentAsString();
    Assert.assertNotNull("Sse Emitter to admin user is empty", contentAsString);
    log.debug("The MVC Response : " + contentAsString);
    
    PublicChannel  pc = getPublicChannel("1",user2.getCompanyId() );
    for(int i =0; i< 15 ; i++){
      commentForm.setComment("Comment no :" + i);
      form.setId(pc.getId());
      commentForm.setCid(i);
      request = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/pubchannel/addComment").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();  
    }
    
    result = this.mockMvc
        .perform(
            post("/pubchannel/deleteComment").param("pcRefId","1").param("cid", "2")
                .contentType(MediaType.APPLICATION_JSON).session(session2))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();  
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    result = this.mockMvc
        .perform(
            post("/pubchannel/getPublicChannel").param("pcRefId","1")
                .contentType(MediaType.APPLICATION_JSON).session(session2))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    contentAsString = sseSession1.getResponse().getContentAsString();
    Assert.assertNotNull("Sse Emitter to admin user is empty", contentAsString);
    
    result = this.mockMvc
        .perform(
            post("/pubchannel/getAllComments").param("pcRefId","1")
                .contentType(MediaType.APPLICATION_JSON).session(session2))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    
  }
  
  /**
   * @param string
   * @param companyId
   * @return 
   */
  private PublicChannel getPublicChannel(String pcRefId, String companyId) {
    
    return publicChannelFactory.getPublicChannel(pcRefId, companyId);
  }

  /**
   * Test method for
   * {@link com.sp.web.controller.pc.PublicChannelController#followPublicChannel(boolean, java.lang.String, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testFollowPublicChannel() {
    fail("Not yet implemented");
  }
  
}
