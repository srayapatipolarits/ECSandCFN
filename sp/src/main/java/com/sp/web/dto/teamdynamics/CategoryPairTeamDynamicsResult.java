package com.sp.web.dto.teamdynamics;

import com.sp.web.model.teamdynamics.CategoryPairData;

/**
 * CategoryPairTeamDynamics holds the information for what data to be shown for category pair prims
 * module.
 * 
 * @author pradeepruhil
 *
 */
public class CategoryPairTeamDynamicsResult extends CategoryPairData {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7820385751559751847L;

  private boolean showInBalanceStrengths;
  
  private boolean showLeaningStrengths;
  
  private boolean showLibalitesAndAdvice;
  
  private String color;
  
  public boolean isShowInBalanceStrengths() {
    return showInBalanceStrengths;
  }
  
  public void setShowInBalanceStrengths(boolean showInBalanceStrengths) {
    this.showInBalanceStrengths = showInBalanceStrengths;
  }
  
  public boolean isShowLeaningStrengths() {
    return showLeaningStrengths;
  }
  
  public void setShowLeaningStrengths(boolean showLearningStrengths) {
    this.showLeaningStrengths = showLearningStrengths;
  }
  
  public boolean isShowLibalitesAndAdvice() {
    return showLibalitesAndAdvice;
  }
  
  public void setShowLibalitesAndAdvice(boolean showLibalitesAndAdvice) {
    this.showLibalitesAndAdvice = showLibalitesAndAdvice;
  }
  
  public String getColor() {
    return color;
  }
  
  public void setColor(String color) {
    this.color = color;
  }
}
