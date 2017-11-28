package com.sp.web.model.todo;

/**
 * @author Dax Abraham
 * 
 *         The enumeration to define the different types of Todo requests.
 */
public enum TodoType {
  OrgPlan("actionPlanDisplayProcessor"), PrismLens, Pulse, 
  Feedback("devFeedbackDisplayProcessor"), Todo("todoWithParentDisplayProcessor"), 
  BlueprintApproval, BlueprintFeedback, 
  CompetencyEvaluation("competencyEvaluationDisplayProcessor");
  
  private String displayProcessor;
  
  /**
   * Default constructor.
   */
  private TodoType() {
    this.displayProcessor = "defaultDisplayProcessor";
  }
  
  /**
   * The display processor.
   * 
   * @param displayProcessor
   *          - display processor
   */
  private TodoType(String displayProcessor) {
    this.displayProcessor = displayProcessor;
  }
  
  public String getDisplayProcessor() {
    return displayProcessor;
  }
}
