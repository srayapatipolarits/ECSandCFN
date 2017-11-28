package com.sp.web.model;

import java.io.Serializable;

/**
 * <code>GrowthFeedbackResponse</code> class contains the growth feedback.
 * 
 * @author pradeep
 *
 */
public class GrowthFeedbackResponse implements Serializable {

  /**
   * Default serial verison id.
   */
  private static final long serialVersionUID = -6526925595529784024L;

  /** feedback response given by use. */
  private String response;

  /** question id for which feedback response given. */
  private String questionId;

  /** Any comment for the question. */
  private String comment;

  private String question;

  /**
   * Response string is returned.
   * 
   * @return the response
   */
  public String getResponse() {
    return response;
  }

  /**
   * set the response.
   * 
   * @param response
   *          the response to set
   */
  public void setResponse(String response) {
    this.response = response;
  }

  /**
   * question id to be returned.
   * 
   * @return the questionId
   */
  public String getQuestionId() {
    return questionId;
  }

  /**
   * set the questionid.
   * 
   * @param questionId
   *          the questionId to set
   */
  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  /**
   * comments returned.
   * 
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Comment to be set.
   * 
   * @param comment
   *          the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
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
}
