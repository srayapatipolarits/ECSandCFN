package com.sp.web.service.fallback;

import com.sp.web.exception.FallbackFailException;
import com.sp.web.model.fallback.FallbackBean;

/**
 * FallbackProcessor is the fallback processor which will process the fallback.
 * 
 * @author pradeepruhil
 *
 */
public interface FallbackProcessor {
  
  /**
   * processFallback method will pocess the fallback.
   * 
   * @param fallbean
   *          contains the fallback bean with fallback data.
   * @throws FallbackFailException
   *           if fallback failes.
   */
  public void processFallback(FallbackBean fallbean) throws FallbackFailException;
}
