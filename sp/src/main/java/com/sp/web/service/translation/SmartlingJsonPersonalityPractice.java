package com.sp.web.service.translation;

import com.sp.web.model.goal.PersonalityPracticeArea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartlingJsonPersonalityPractice {
  
  private Map<Object, Object> smartling;
  
  private List<PersonalityPracticeArea> data;
  
  public Map<Object, Object> getSmartling() {
    if(smartling == null){
      smartling = new HashMap<Object, Object>();
    }
    return smartling;
  }
  
  public void setSmartling(Map<Object, Object> smartling) {
    this.smartling = smartling;
  }
  
  public List<PersonalityPracticeArea> getData() {
    return data;
  }
  
  public void setData(List<PersonalityPracticeArea> data) {
    this.data = data;
  }
  
}
