package com.sp.web.alexa;

import com.sp.web.model.User;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AlexaRequestController will handle all the request from the Alexa device.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class AlexaRequestController {
  
  @Autowired
  private AlexaRequestControllerHelper requestControllerHelper;
  
  @RequestMapping(value = "/rest/alexa/request", method = RequestMethod.POST)
  public void getRelationShipRequest(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    User user = GenericUtils.getUserFromAuthentication(authentication);
    requestControllerHelper.getAlexaResponse(user, request, response);
  }
}
