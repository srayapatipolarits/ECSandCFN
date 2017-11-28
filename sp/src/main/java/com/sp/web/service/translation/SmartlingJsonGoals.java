package com.sp.web.service.translation;

import com.sp.web.model.goal.SPGoal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartlingJsonGoals {
  
  private Map<Object, Object> smartling;
  
  private List<SPGoal> data;
  
  public Map<Object, Object> getSmartling() {
    if(smartling == null){
      smartling = new HashMap<Object, Object>();
    }
    return smartling;
  }
  
  public void setSmartling(Map<Object, Object> smartling) {
    this.smartling = smartling;
  }
  
  public List<SPGoal> getData() {
    return data;
  }
  
  public void setData(List<SPGoal> data) {
    this.data = data;
  }
  
}
