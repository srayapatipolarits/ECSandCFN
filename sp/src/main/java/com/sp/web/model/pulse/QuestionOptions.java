package com.sp.web.model.pulse;

/**
 * @author Dax Abraham
 *
 *         The model class to store the question options and scoring.
 */
public class QuestionOptions {

  private String text;
  private double factor;

  /**
   * Constructor.
   * 
   * @param text
   *          - text
   * @param factor
   *          - factor
   */
  public QuestionOptions(String text, double factor) {
    this.text = text;
    this.factor = factor;
  }
  
  /**
   * Default Constructor.
   */
  public QuestionOptions() {}
  

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public double getFactor() {
    return factor;
  }

  public void setFactor(double factor) {
    this.factor = factor;
  }
}
