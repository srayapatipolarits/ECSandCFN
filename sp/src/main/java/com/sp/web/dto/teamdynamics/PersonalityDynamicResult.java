package com.sp.web.dto.teamdynamics;

import com.sp.web.model.teamdynamics.CategoryPairData;

public class PersonalityDynamicResult extends CategoryPairData {
  
  private boolean showStrength;
  
  private boolean showLibalities;
  
  private boolean isColor;
  
  private String color;
  
  private boolean showSpecialMessage;
  
  public boolean isShowStrength() {
    return showStrength;
  }
  
  public void setShowStrength(boolean showStrength) {
    this.showStrength = showStrength;
  }
  
  public boolean isShowLibalities() {
    return showLibalities;
  }
  
  public void setShowLibalities(boolean showLibalities) {
    this.showLibalities = showLibalities;
  }
  
  public boolean isColor() {
    return isColor;
  }
  
  public void setColor(boolean isColor) {
    this.isColor = isColor;
  }
  
  public String getColor() {
    return color;
  }
  
  public void setColor(String color) {
    this.color = color;
  }
  
  public void setShowSpecialMessage(boolean showSpecialMessage) {
    this.showSpecialMessage = showSpecialMessage;
  }
  
  public boolean isShowSpecialMessage() {
    return showSpecialMessage;
  }
  
}
