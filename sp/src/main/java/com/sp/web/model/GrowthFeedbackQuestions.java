/**
 * 
 */
package com.sp.web.model;

import com.sp.web.assessment.questions.QuestionType;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author pradeep
 *
 */
public class GrowthFeedbackQuestions implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2213731165683635242L;

  /**
   * Question id.
   */
  private String id;

  /**
   * Question number.
   */
  private int number;

  /**
   * QuestionType, wehther multipe choise, text box, true false.
   */
  private QuestionType type;

  /**
   * Description.
   */
  private String description;

  /**
   * <code>goalName</code> this question belongs to.
   */
  private String goalName;

  /**
   * OPtions list.
   */
  private List<String> optionsList;

  /**
   * QuestionId
   */
  private String questionId;

  /**
   * QuestionJsonSTring.
   */
  private String questionJSONString;

  private String optionalText;

  public GrowthFeedbackQuestions(GrowthFeedbackQuestions feedbackQuestions) {
    BeanUtils.copyProperties(feedbackQuestions, this);
  }

  public GrowthFeedbackQuestions() {
  }

  /**
   * 
   */
  private String question;

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the number
   */
  public int getNumber() {
    return number;
  }

  /**
   * @param number
   *          the number to set
   */
  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * @return the type
   */
  public QuestionType getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(QuestionType type) {
    this.type = type;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the optionsList
   */
  public List<String> getOptionsList() {
    return optionsList;
  }

  /**
   * @param optionsList
   *          the optionsList to set
   */
  public void setOptionsList(List<String> optionsList) {
    this.optionsList = optionsList;
  }

  /**
   * @return the questionJSONString
   */
  public String getQuestionJSONString() {
    return questionJSONString;
  }

  /**
   * @param questionJSONString
   *          the questionJSONString to set
   */
  public void setQuestionJSONString(String questionJSONString) {
    this.questionJSONString = questionJSONString;
  }

  /**
   * @param question
   *          the question to set
   */
  public void setQuestion(String question) {
    this.question = question;
  }

  /**
   * @return the question
   */
  public String getQuestion() {
    return question;
  }

  /**
   * @param goalName
   *          the goalName to set
   */
  public void setGoalName(String goalName) {
    this.goalName = goalName;
  }

  /**
   * @return the goalName
   */
  public String getGoalName() {
    return goalName;
  }

  /**
   * @param questionId
   *          the questionId to set
   */
  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  /**
   * @return the questionId
   */
  public String getQuestionId() {
    return questionId;
  }

  /**
   * @param optionalText
   *          the optionalText to set
   */
  public void setOptionalText(String optionalText) {
    this.optionalText = optionalText;
  }

  /**
   * @return the optionalText
   */
  public String getOptionalText() {
    return optionalText;
  }

}
