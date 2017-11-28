package com.sp.web.model.assessment;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The model to store the Assessment Response.
 */
public class AssessmentResponse {
  
  private int questionNum;
  private List<String> selection;
  private long ts;
  
  
  /**
   * Default Constructor. 
   */
  public AssessmentResponse() {
    super();
  }

  /**
   * Constructor from question number and answer.
   * 
   * @param questionNumber
   *            - quesiton number
   * @param answer
   *            - answer
   */
  public AssessmentResponse(int questionNumber, List<String> answer) {
    this.questionNum = questionNumber;
    this.selection = answer;
    this.ts = System.currentTimeMillis();
  }

  public int getQuestionNum() {
    return questionNum;
  }
  
  public void setQuestionNum(int questionNum) {
    this.questionNum = questionNum;
  }
  
  public List<String> getSelection() {
    return selection;
  }
  
  public void setSelection(List<String> selection) {
    this.selection = selection;
  }
  
  public long getTs() {
    return ts;
  }
  
  public void setTs(long ts) {
    this.ts = ts;
  }
  
}
