package com.sp.web.controller.demoaccount;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.DemoAccountForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * DemoAccountController contains the request for demo account. Account creeation and sending the
 * demo account link to the user.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class DemoAccountController {
  
  @Autowired
  private DemoAccountControllerHelper demoAccountControllerHelper;
  
  /**
   * getDemoAccountForm method will return the demo account form to be displayed on the page.
   * 
   * @return tiles name for the showing the demo account form.
   */
  @RequestMapping(value = "/demoAccount/form", method = RequestMethod.GET)
  public String getDemoAccountForm() {
    return "demoAccountForm";
  }
  
  /**
   * <code>createDemoAccount</code> method will create demo account for the user.
   * 
   * @param demoAccountForm
   *          demo account for the user.
   * @return the demo account.
   */
  @RequestMapping(value = "/demoAccount/createDemoAccount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createDemoAccount(@Valid DemoAccountForm demoAccountForm) {
    return ControllerHelper.doProcess(demoAccountControllerHelper::createDemoAccount,
        demoAccountForm);
  }
  
}
