package com.sp.web.controller.systemadmin.company;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.partner.PartnerAccountDTO;
import com.sp.web.dto.partner.PartnerAccountListingDTO;
import com.sp.web.form.PartnerAccountForm;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * PartnerAccountAdminController for partner id generation and creation and other partner related
 * service.
 * 
 * @author pradeepruhil
 *
 */
@Controller
@RequestMapping("/sysAdmin/partnerAccount")
public class PartnerAccountAdminController
    extends
    GenericController<PartnerAccount, PartnerAccountListingDTO, PartnerAccountDTO, PartnerAccountForm, PartnerAccountAdminControllerHelper> {
  
  @Inject
  public PartnerAccountAdminController(PartnerAccountAdminControllerHelper helper) {
    super(helper);
  }
  
  @RequestMapping(value = "/updateActionProcesssor", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateActionProcessor(@RequestParam String actionProcessor, String companyId,
      Authentication token) {
    return ControllerHelper.process(helper::updateActionProcessor, token, actionProcessor,
        companyId);
  }
  
}
