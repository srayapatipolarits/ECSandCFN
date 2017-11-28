package com.sp.web.navigation;

import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dto.navigation.NavigationDTO;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NavigationFactoryTest extends SPTestLoggedInBase {
  
  @Autowired
  NavigationFactory navigationFactory;
  
  @Test
  public void testNavigationIndividual() {
    User user = new User();
    
    user.addRole(RoleType.User);
    user.addRole(RoleType.IndividualAccountAdministrator);
    
    try {
      NavigationDTO navigation = navigationFactory.getNavigation(user);
      log.debug("Navigation nodes.");
      log.debug(navigation.getNavigationList());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testNavigationMember() {
    User user = new User();
    
    user.addRole(RoleType.User);
    user.addRole(RoleType.Erti);
    user.addRole(RoleType.SysAdminMemberRole);
    user.addRole(RoleType.SysMedia);
    
    try {
      NavigationDTO navigation = navigationFactory.getNavigation(user);
      log.debug("Navigation nodes.");
      ObjectMapper om = new ObjectMapper();
      log.debug(om.writeValueAsString(navigation));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testNavigationMemberGroupLead() {
    User user = new User();
    
    user.addRole(RoleType.User);
    user.addRole(RoleType.GroupLead);
    
    try {
      NavigationDTO navigation = navigationFactory.getNavigation(user);
      log.debug("Navigation nodes.");
      log.debug(navigation.getNavigationList());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testNavigationMemberGroupLeadAdministrator() {
    User user = new User();
    
    user.addRole(RoleType.User);
    user.addRole(RoleType.GroupLead);
    user.addRole(RoleType.Administrator);
    
    try {
      NavigationDTO navigation = navigationFactory.getNavigation(user);
      log.debug("Navigation nodes.");
      log.debug(navigation.getNavigationList());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testNavigationMemberGroupLeadAccountAdministrator() {
    User user = new User();
    
    user.addRole(RoleType.User);
    user.addRole(RoleType.GroupLead);
    user.addRole(RoleType.Administrator);
    user.addRole(RoleType.AccountAdministrator);
    
    try {
      NavigationDTO navigation = navigationFactory.getNavigation(user);
      log.debug("Navigation nodes.");
      log.debug(navigation.getNavigationList());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

}
