package com.sp.web.controller.systemadmin.company;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.partner.PartnerAccountDTO;
import com.sp.web.dto.partner.PartnerAccountListingDTO;
import com.sp.web.form.PartnerAccountForm;
import com.sp.web.model.User;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.mvc.SPResponse;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * PartnerAccountAdminControllerHelper is the helper class for the
 * {@link PartnerAccountAdminController}.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PartnerAccountAdminControllerHelper
    extends
    GenericControllerHelper<PartnerAccount, PartnerAccountListingDTO, PartnerAccountDTO, PartnerAccountForm, PartnerAccountFactory> {
  
  private static final String MODULE_NAME = "partnerAccount";
  
  @Inject
  public PartnerAccountAdminControllerHelper(PartnerAccountFactory factory) {
    super(MODULE_NAME, factory);
  }
  
  /**
   * update the action processor to the Partner Account.
   * 
   * @param user
   *          is the logged in user.
   * @param param
   *          contains the action processor type.
   * @return the action processor.
   */
  public SPResponse updateActionProcessor(User user, Object[] param) {
    String actionProcessorType = (String) param[0];
    String companyId = (String) param[1];
    factory.updateActionProcessor(actionProcessorType, companyId);
    return new SPResponse().isSuccess();
  }
}
