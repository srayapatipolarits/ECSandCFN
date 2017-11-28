package com.sp.web.service.translation;

import com.sp.web.model.tutorial.SPTutorial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartlingJsonTutorial {
  
  private Map<Object, Object> smartling;
  private List<SPTutorial> data;
  
  /**
   * @return 
   *  - gets the Smartling settings map if not present then an empty map is returned.
   */
  public Map<Object, Object> getSmartling() {
    if (smartling == null) {
      smartling = new HashMap<Object, Object>();
    }
    return smartling;
  }
  
  public void setSmartling(Map<Object, Object> smartling) {
    this.smartling = smartling;
  }
  
  public List<SPTutorial> getData() {
    return data;
  }
  
  public void setData(List<SPTutorial> data) {
    this.data = data;
  }
  
}
