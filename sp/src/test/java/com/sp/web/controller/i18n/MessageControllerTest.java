package com.sp.web.controller.i18n;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class MessageControllerTest extends SPTestBase {

  @Test
  public void testGetMessage() throws Exception {
    /* test single key success message */
    this.mockMvc
        .perform(
            post("/message").param("key", "group.assessment.percentMessage").param("params", "20")
                .param("locale", "en_US").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(
            MockMvcResultMatchers
                .content()
                .json(
                    "{\"success\":{\"group.assessment.percentMessage\":\"20% Complete\",\"Success\":\"true\"},\"error\":null}"))
        .andReturn();

    /* test failed message */
    this.mockMvc
        .perform(
            post("/message").param("key", "group.assessment").param("params", "20")
                .param("locale", "en_US").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(status().is(200))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.NoSuchMessageException").value(
                "No message found under code 'group.assessment' for locale 'en_US'.")).andReturn();

    /* test default locale */

    this.mockMvc
        .perform(
            post("/message").param("key", "group.assessment").param("params", "20")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.NoSuchMessageException").value(
                "No message found under code 'group.assessment' for locale 'en'.")).andReturn();
  }

  @Test
  public void testGetAllMessages() throws Exception {
    MvcResult result = this.mockMvc
        .perform(
            post("/messages").param("url", "/sp/group/assessment")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(
            MockMvcResultMatchers
                .content()
                .json(
                    "{\"success\":{\"group.assessment.progressMessage\":\"{0} out {1} Assessment reports received.\""
                    + ",\"Success\":\"true\"},\"error\":null}"))
        .andReturn();
    log.debug("Result received mvc response :" + result.getResponse().getContentAsString());
    
    // test the additional keys as well
    result = this.mockMvc
    .perform(
        post("/messages")
        .param("url", "/sp/group/assessment")
        .param("additionalKeys", "admin.group")
        .contentType(MediaType.TEXT_PLAIN)
        .session(session))
    .andExpect(content().contentType("application/json;charset=UTF-8"))
    .andExpect(jsonPath("$.success").exists())
        .andExpect(
            MockMvcResultMatchers
                .content()
                .json(
                    "{\"success\":{\"group.assessment.progressMessage\":\"{0} out {1} Assessment reports received.\""
                    + ",\"Success\":\"true\"},\"error\":null}"))
    .andReturn();
    log.debug("Result received mvc response :" + result.getResponse().getContentAsString());

    result = this.mockMvc
    .perform(
        post("/messages")
        .param("url", "/sp/group/assessment")
        .param("additionalKeys", "")
        .contentType(MediaType.TEXT_PLAIN)
        .session(session))
    .andExpect(content().contentType("application/json;charset=UTF-8"))
    .andExpect(jsonPath("$.success").exists())
        .andExpect(
            MockMvcResultMatchers
                .content()
                .json(
                    "{\"success\":{\"group.assessment.progressMessage\":\"{0} out {1} Assessment reports received.\""
                    + ",\"Success\":\"true\"},\"error\":null}"))
    .andReturn();
    
    result = this.mockMvc
        .perform(
            post("/messages")
            .param("url", "/sp/sigin")
            .param("additionalKeys", "").param("localStorage", "true")
            .contentType(MediaType.TEXT_PLAIN)
            .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
            .andExpect(
                MockMvcResultMatchers
                    .content()
                    .json(
                        "{\"success\":{\"group.assessment.progressMessage\":\"{0} out {1} Assessment reports received.\""
                        + ",\"Success\":\"true\"},\"error\":null}"))
        .andReturn();
    log.debug("Result received mvc response :" + result.getResponse().getContentAsString());
  }

}
