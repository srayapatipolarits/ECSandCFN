package com.sp.web.controller.external.rest;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class JsonView {
  
  /**
   * returns the view.
   * 
   * @param model
   * @param response
   * @return
   */
  public static ModelAndView Render(Object model, HttpServletResponse response) {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    
    MediaType jsonMimeType = MediaType.APPLICATION_JSON;
    
    try {
      jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
    } catch (HttpMessageNotWritableException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }
}