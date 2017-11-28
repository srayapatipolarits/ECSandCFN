package com.sp.web.model.pulse;

/**
 * @author Dax Abraham
 * 
 *         The model to store the users selections.
 */
public class PulseSelection {

  private int questionIndex;
  private int selectionIndex;

  /**
   * Constructor to create the pulse selection.
   * 
   * @param questionIndex
   *            - question index
   * @param selectionIndex
   *            - selection index
   */
  public PulseSelection(int questionIndex, int selectionIndex) {
    this.questionIndex = questionIndex;
    this.selectionIndex = selectionIndex;
  }
  
  /**
   * Default Constructor.
   */
  public PulseSelection() {}

  public int getQuestionIndex() {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex) {
    this.questionIndex = questionIndex;
  }

  public int getSelectionIndex() {
    return selectionIndex;
  }

  public void setSelectionIndex(int selectionIndex) {
    this.selectionIndex = selectionIndex;
  }
}
