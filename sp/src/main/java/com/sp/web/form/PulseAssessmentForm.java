package com.sp.web.form;

/**
 * @author Dax Abraham
 *
 *         This is the form for getting the pulse assessment for the members.
 */
public class PulseAssessmentForm {

  private String categoryName;
  private int[] questionSelectionIndex;

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public int[] getQuestionSelectionIndex() {
    return questionSelectionIndex;
  }

  public void setQuestionSelectionIndex(int[] questionSelectionIndex) {
    this.questionSelectionIndex = questionSelectionIndex;
  }
}
