package com.sp.web.model.pulse;

import com.sp.web.assessment.questions.QuestionType;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The entity class for the pulse question.
 */
public class PulseQuestionBean {

  private int number;
  private QuestionType type;
  private String description;
  private List<QuestionOptions> optionsList;

  /**
   * Constructor.
   * 
   * @param number
   *          - question number
   * @param type
   *          - type
   * @param description
   *          - description
   * @param optionsList
   *          - option list
   */
  public PulseQuestionBean(int number, QuestionType type, String description,
      List<QuestionOptions> optionsList) {
    this.number = number;
    this.type = type;
    this.description = description;
    this.optionsList = optionsList;
  }
  
  /**
   * Default Constructor.
   */
  public PulseQuestionBean() {}

  public List<QuestionOptions> getOptionsList() {
    return optionsList;
  }

  public void setOptionsList(List<QuestionOptions> optionsList) {
    this.optionsList = optionsList;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
