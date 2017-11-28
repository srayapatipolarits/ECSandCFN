package com.sp.web.controller.navigation;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 *
 *         The navigation controller.
 */
@Controller
public class NavigationController {

  @Autowired
  NavigationControllerHelper helper;
  
  /**
   * Controller method to get the navigation nodes for the currently logged in user.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get navigation request
   */
  @RequestMapping(value = "/navigation/get", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getNavigation(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getNavigation, token);
  }
}
