package com.sp.web.model;

import java.io.Serializable;

public class IntroSetting implements Serializable {
  
  /**
   * @author Prasanna Venkatesh
   * 
   *         This class holds the IntroSettings for each flag for Individual User
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private boolean flagValue;
  
  private int count;
  
  public boolean isFlagValue() {
    return flagValue;
  }
  
  public void setFlagValue(boolean flagValue) {
    this.flagValue = flagValue;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
}