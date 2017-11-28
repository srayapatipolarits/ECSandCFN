package com.sp.web.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Error controller for configuring the error status.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class ErrorController {
  
  /**
   * Method for redirecting the user in case user is unauthorized.
   * 
   * @param request
   *          http servlet request.
   * @return the view to be shown.
   */
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @RequestMapping("/errors/unauthorised")
  public String unAuthorised(HttpServletRequest request) {
    return "redirect:/logout";
  }
  
  /**
   * This method will redirect user in case of status is 403.
   * 
   * @param request
   *          http servlet reuest
   * @return the redirect url.
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @RequestMapping("/errors/forbidden")
  public String forBidden(HttpServletRequest request) {
    return "redirect:/logout";
  }
}
