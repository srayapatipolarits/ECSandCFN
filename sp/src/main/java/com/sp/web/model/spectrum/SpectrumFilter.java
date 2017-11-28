package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>SpectrumFilter</code> class contains the filters for the spectrum.
 * 
 * @author pradeepruhil
 *
 */
public class SpectrumFilter implements Serializable{
  
  private static final long serialVersionUID = -2974245842258238932L;
  private Map<String, Object> filters;
  
  /**
   * Return the filters map.
   * @return the filter map.
   */
  public Map<String, Object> getFilters() {
    if (filters == null) {
      filters = new HashMap<String, Object>();
    }
    
    return filters;
  }
  
  public void setFilters(Map<String, Object> filters) {
    this.filters = filters;
  }
  
}
