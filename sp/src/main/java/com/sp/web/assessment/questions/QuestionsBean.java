package com.sp.web.assessment.questions;

import java.util.List;
import java.util.Optional;

/**
 * @author daxabraham
 * 
 *         This class is the enclosing class for the questions.
 */
public class QuestionsBean {

  //private static final Logger LOGGER = Logger.getLogger(QuestionsBean.class);
  
  private int number;
  private int categoryNumber;
  private QuestionType type;
  private String description;
  private List<OptionsBean> optionsList;

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

  public int getCategoryNumber() {
    return categoryNumber;
  }

  public void setCategoryNumber(int categoryNumber) {
    this.categoryNumber = categoryNumber;
  }

  public List<OptionsBean> getOptionsList() {
    return optionsList;
  }

  public void setOptionsList(List<OptionsBean> optionsList) {
    this.optionsList = optionsList;
  }

  public Optional<OptionsBean> findAnswer(String key) {
    return optionsList.stream().filter(o -> o.getId().equals(key)).findFirst();
  }
}
