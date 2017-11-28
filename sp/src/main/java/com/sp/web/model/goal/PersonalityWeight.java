package com.sp.web.model.goal;

import com.sp.web.Constants;

import java.io.Serializable;

/**
 * PerosonalityWeight class hold the personality weigth information for the under pressure and
 * primary.
 * 
 * @author pradeepruhil
 *
 */
public class PersonalityWeight implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -4291668506456651802L;
  
  private int primaryWeight = Constants.DEFAULT_PERSONALITY_WEIGHT;
  
  private int underPressureWeight = Constants.DEFAULT_PERSONALITY_WEIGHT;
  
  public int getPrimaryWeight() {
    return primaryWeight;
  }
  
  public void setPrimaryWeight(int primaryWeight) {
    this.primaryWeight = primaryWeight;
  }
  
  public int getUnderPressureWeight() {
    return underPressureWeight;
  }
  
  public void setUnderPressureWeight(int underPressureWeight) {
    this.underPressureWeight = underPressureWeight;
  }
  
}
