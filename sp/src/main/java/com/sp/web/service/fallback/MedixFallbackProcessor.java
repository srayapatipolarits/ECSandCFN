package com.sp.web.service.fallback;

import com.sp.web.exception.FallbackFailException;
import com.sp.web.model.external.rest.PartnerRequest;
import com.sp.web.model.fallback.FallbackBean;
import com.sp.web.service.external.rest.PartnerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MedixFallabackProcessor will process the fallback request which failed during the normal process.
 * 
 * @author pradeepruhil
 *
 */
@Service
public class MedixFallbackProcessor implements FallbackProcessor {
  
  @Autowired
  private PartnerFactory partnerFactory;
  
  /**
   * processFallback method will process the fallback.
   */
  @Override
  public void processFallback(FallbackBean fallkbean) throws FallbackFailException {
    
    PartnerRequest partnerRequest = (PartnerRequest) fallkbean.getFallbackData();
    partnerRequest.addToRequest("fallbackRequest", true);
    partnerFactory.executeRequest(partnerRequest);
    
  }
}
