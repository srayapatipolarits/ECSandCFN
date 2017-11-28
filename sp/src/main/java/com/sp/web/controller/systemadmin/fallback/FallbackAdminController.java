package com.sp.web.controller.systemadmin.fallback;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * FallbackAdminController will execute the fallbacks in the system.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class FallbackAdminController {
  
  @Autowired
  private FallbackAdminControllerHelper helper;
  
  /**
   * executeFallbck method will execute the fallback.
   * 
   * @param companyId
   *          execute fallbacks only for a company
   * @param auth
   *          is the system admin user.
   * @return the response.
   */
  @RequestMapping(value = "/sysAdmin/fallback/processFallback", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse executeFallback(@RequestParam(required = false) String companyId,
      Authentication auth) {
    return ControllerHelper.process(helper::executeFallback, auth, companyId);
  }
}
