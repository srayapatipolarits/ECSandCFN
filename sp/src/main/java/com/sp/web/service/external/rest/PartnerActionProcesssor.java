package com.sp.web.service.external.rest;

import com.sp.web.model.external.rest.PartnerRequest;
import com.sp.web.model.external.rest.PartnerResponse;

/**
 * PartnerActionProcessor interface will provides the action to be processed for the partners.
 * 
 * @author pradeepruhil
 *
 */
public interface PartnerActionProcesssor {
  
  /**
   * PartnerAction method will process the action for the partner.
   * 
   * @param partnerRequest
   *          is the parter request.
   * @return the Partner Response.
   */
  public PartnerResponse processAction(PartnerRequest partnerRequest);
}
