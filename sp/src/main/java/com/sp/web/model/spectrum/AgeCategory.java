/**
 * 
 */
package com.sp.web.model.spectrum;

/**
 * @author pradeepruhil
 *
 */
public enum AgeCategory {
  
  YOUNG(0, 21),
  
  MILIENIAL(22,34),
  
  GEN_X(35,44),
  
  BOOMERS(45,54),
  
  OLD(55, 64),

  SILENT (65,200);
  
  private int lower;
  
  private int higher;
  
  private AgeCategory(int lower, int higher) {
    this.lower = lower;
    this.higher = higher;
  }
  
  /**
   * Returns the higher range value.
   * @return the higher.
   */
  public int getHigher() {
    return higher;
  }
  
  /**
   * Returs the lower range value.
   * @return the lower.
   */
  public int getLower() {
    return lower;
  }
  
}
