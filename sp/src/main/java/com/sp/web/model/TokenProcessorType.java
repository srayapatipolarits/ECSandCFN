package com.sp.web.model;

/**
 * Token Processor Type.
 * 
 * @author pruhil
 */
public enum TokenProcessorType {
  
  /** Reset password processor. */
  RESET_PASSWORD("resetPasswordProcessor"), 
  ADD_MEMBER("addMemberTokenProcessor"), 
  HIRING_REFERENCE("hiringReference"), 
  HIRING_CANDIDATE("hiringCandidate"), 
  GROWTH_INVITE("growthInviteTokenProcessor"), 
  FEEDBACK_INVITE("feedbackInviteTokenProcessor"), 
  DEMO_ACCOUNT("demoAccountInviteTokenProcessor"), 
  COPY_PROFILE("copyProfileTokenProcessor"), 
  ADD_INDIVIDUAL_MEMBER("addIndividualMemberTokenProcessor"),
  REQUEST_FEEDBACK("developmentFeedbackTokenProcessor"), 
  BLUEPRINT_SHARE("blueprintTokenProcessor"), 
  BLUEPRINT_PUBLISH_SHARE("blueprintPublishShareTokenProcessor"), 
  COMPETENCY_EVALUATE("competencyEvaluationProcessor"), 
  HIRING_LENS("hiringLensTokenProcessor"), 
  HIRING_SHARE_PORTRAIT("hiringSharePortriatTokenProcessor"), 
 COMPETENCY_MANAGER_RESULT(
      "competencyManagerResultProcessor"), DOCRAPTOR_PDF("docraptorPdfTokenProcessor");
  
  /** processor type. */
  private String processorName;
  
  /**
   * Constructor for the processor.
   * 
   * @param processorName
   *          processor name
   */
  private TokenProcessorType(String processorName) {
    this.processorName = processorName;
  }
  
  /**
   * the processor name.
   * 
   * @return processor name
   */
  public String getProcessorName() {
    return processorName;
  }
}
