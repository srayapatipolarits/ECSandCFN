package com.sp.web.controller.utility;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UtilitiesControllerTest extends SPTestLoggedInBase {

  @Test
  public void testOG() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Url not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", "www.youtube.com/watch?v=x94QcfbTngc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.MalformedURLException")
                  .value(
                      "java.net.MalformedURLException: no protocol: www.youtube.com/watch?v=x94QcfbTngc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", "http://www.checkupdown.com/accounts/grpb/B1394343/")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IOException")
                  .value(
                      "java.io.IOException: Server returned HTTP response code: 403 for URL: http://www.checkupdown.com/accounts/grpb/B1394343/"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String videoUrl = "https://www.youtube.com/watch?v=x94QcfbTngc";
      result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", videoUrl)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.contentRef.title").value("Bang Bang Shrimp with Michael&#39;s Home Cooking"))
//          .andExpect(jsonPath("$.success.contentRef.media.url").value(videoUrl))
//          .andExpect(jsonPath("$.success.contentRef.media.mediaType").value("Video"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String webUrl = "http://www.pepperfry.com/three-door-wardrobe-in-wenge-finish-by-mintwud-1193831.html";
      result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", webUrl)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.contentRef.title")
                  .value(
                      "Buy Kiyo Three Door Wardrobe In Wenge Finish by Mintwud  Online "
                      + " - Modern - Wardrobes - Pepperfry"))
//          .andExpect(jsonPath("$.success.contentRef.media.url").value(webUrl))
//          .andExpect(jsonPath("$.success.contentRef.media.mediaType").value(SPMediaType.Web.toString()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String internalUrl = "http://localhost:8080/sp/profile";
      result = this.mockMvc
          .perform(
              post("/utility/og")
              .param("url", internalUrl)
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


