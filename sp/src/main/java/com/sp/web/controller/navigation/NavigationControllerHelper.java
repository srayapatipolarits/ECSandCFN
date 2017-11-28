package com.sp.web.controller.navigation;

import com.sp.web.Constants;
import com.sp.web.dto.navigation.NavigationDTO;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.navigation.NavigationFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 *
 *         The navigation controller helper.
 */
@Component
public class NavigationControllerHelper {

  @Autowired
  NavigationFactory navigationFactory;
  
  /**
   * The helper method to get the navigation for the currently logged in user.
   * 
   * @param user
   *          - user
   * @return
   *      the response to the get navigation
   */
  public SPResponse getNavigation(User user) {
    final SPResponse resp = new SPResponse();
    NavigationDTO navigation = navigationFactory.getNavigation(user);
    return resp.add(Constants.PARAM_NAVIGATION, navigation);
  }
}
