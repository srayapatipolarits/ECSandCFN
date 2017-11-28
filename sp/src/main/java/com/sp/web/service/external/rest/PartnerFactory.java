package com.sp.web.service.external.rest;

import com.sp.web.controller.systemadmin.company.PartnerAccountFactory;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.external.rest.PartnerRequest;
import com.sp.web.model.external.rest.PartnerResponse;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.service.sse.ActionType;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * BullhornFactory class provides the interaction with the BH.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PartnerFactory {
  
  private static final Logger log = Logger.getLogger(PartnerFactory.class);
  
  @Autowired
  @Lazy
  private PartnerAccountFactory partnerAccountFactory;
  
  /**
   * executeRequest method will execute the request for the partner.
   * 
   * @param request
   *          is the
   * @return the Partner Response
   */
  public PartnerResponse executeRequest(PartnerRequest request) {
    
    String partnerId = request.getPartnerId();
    
    PartnerAccount partnerAccount = partnerAccountFactory.findByPartnerId(partnerId);
    if (partnerAccount != null) {
      PartnerActionProcesssor actionProcessor = (PartnerActionProcesssor) ApplicationContextUtils
          .getBean(partnerAccount.getActionProcessor());
      return actionProcessor.processAction(request);
    }
    /* Error occurred */
    PartnerResponse response = new PartnerResponse();
    response.setModelView("invalidPartner");
    return response;
    
  }
  
  /**
   * updatePartner method will update the status of the user.
   * 
   * @param user
   *          is the hiringUser
   */
  @Async
  public void updatePartner(User user, ActionType... actionType) {
    if (user instanceof HiringUser && ((HiringUser) user).isThirdParty()) {
      if (log.isDebugEnabled()) {
        log.debug("Updating the partner Status");
      }
      
      PartnerRequest partnerRequest = new PartnerRequest();
      PartnerAccount partnerAccount = partnerAccountFactory.findPartnerAccountByCompany(user
          .getCompanyId());
      if (partnerAccount != null) {
        for (ActionType acType : actionType) {
          partnerRequest.setPartnerId(partnerAccount.getPartnerId());
          partnerRequest.setCompanyId(user.getCompanyId());
          partnerRequest.addToRequest("action", acType.toString());
          partnerRequest.addToRequest("user", user);
          executeRequest(partnerRequest);
        }
        
      } else {
        log.error("PartnerAccount not set for the company : " + user.getCompanyId());
      }
      
    }
  }
}
