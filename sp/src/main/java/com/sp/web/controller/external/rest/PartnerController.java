package com.sp.web.controller.external.rest;

import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.external.rest.PartnerRequest;
import com.sp.web.model.external.rest.PartnerResponse;
import com.sp.web.service.external.rest.PartnerFactory;
import com.sp.web.service.sse.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class PartnerController {
  
  @Autowired
  private PartnerFactory partnerFactory;
  
  /**
   * partnerAction is the request which handles all the request for the partners.
   * 
   * @param partnerId
   *          is the partner id.
   * @param uid
   *          is the extenrnal system id.
   * @param actionType
   *          is the action to be performed for the partner.
   * @return the response.
   */
  @RequestMapping(value = "/partner")
  public Object partnerAction(@RequestParam String partnerId, @RequestParam String uid,
      @RequestParam(required = false, defaultValue = "PartnerPrism") ActionType actionType,
      @RequestParam(required = false, defaultValue = "IntelligentHiring") SPPlanType planType,
      HttpServletResponse response) {
    
    PartnerRequest partnerRequest = new PartnerRequest();
    partnerRequest.setPartnerId(partnerId);
    partnerRequest.addToRequest("uid", uid);
    partnerRequest.addToRequest("planType", planType);
    /* Converting enum to string as from mongo enum to string conversion is happening */
    partnerRequest.addToRequest("action", actionType.toString());
    
    PartnerResponse processAction = partnerFactory.executeRequest(partnerRequest);
    if (processAction.isJsonResponse()) {
      return JsonView.Render(processAction.getResponse(), response);
    } else {
      return processAction.getModelView();
    }
  }
}
