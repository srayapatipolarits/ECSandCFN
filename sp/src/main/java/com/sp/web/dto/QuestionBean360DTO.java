package com.sp.web.dto;

import com.sp.web.assessment.questions.OptionsBean;
import com.sp.web.assessment.questions.QuestionType;
import com.sp.web.assessment.questions.QuestionsBean;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for questions.
 */
public class QuestionBean360DTO {
  
  private int number;
  private QuestionType type;
  private String description;
  private List<OptionsBean> optionsList;
  //private List<String> userOptions;
  
  /**
   * Constructor.
   * 
   * @param questionsBean
   *          - question bean
   * @param formatValues
   *          - values to apply
   * @param firstName
   *          - first name
   */
  public QuestionBean360DTO(QuestionsBean questionsBean, Object[] formatValues, String firstName) {
    this.number = questionsBean.getNumber();
    this.type = questionsBean.getType();
    this.description = questionsBean.getDescription();
    this.optionsList = questionsBean.getOptionsList();
    
    // process the formatted values for description and options
    if (!StringUtils.isBlank(description)) {
      description = MessageFormat.format(description, firstName, formatValues[0], formatValues[1],
          formatValues[2], formatValues[3], formatValues[4], formatValues[5], formatValues[6]);
    }
    
//    userOptions = optionsList
//        .stream()
//        .map(
//            s -> MessageFormat.format(s.getText(), firstName, formatValues[0], formatValues[1],
//                formatValues[2], formatValues[3], formatValues[4], formatValues[5], formatValues[6]))
//        .collect(Collectors.toList());
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
  
  public List<OptionsBean> getOptionsList() {
    return optionsList;
  }
  
//  public List<String> getUserOptions() {
//    return userOptions;
//  }
}
