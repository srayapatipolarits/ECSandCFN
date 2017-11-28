package com.sp.web.assessment.questions;

import org.w3c.dom.Element;

/**
 * @author daxabraham
 * 
 *         The holding class for the rating information.
 */
public class RatingBean {
  
  private String name;
  private double factor;
  
  /**
   * Constructor from DOM Element.
   * 
   * @param ratingNode
   *          - rating node
   */
  public RatingBean(Element ratingNode) {
    this.name = ratingNode.getAttribute("Name");
    this.factor = Double.valueOf(ratingNode.getAttribute("Factor"));
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public double getFactor() {
    return factor;
  }
  
  public void setFactor(double factor) {
    this.factor = factor;
  }
  
}
