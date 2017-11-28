package com.sp.web.assessment.questions;

/**
 * @author daxabraham
 * 
 *         The enumerator for the different traits type.
 */
public enum TraitType {
  
  Spontaneously, Compliance, Intuitive, Outward, Cognitive, Concrete, Rapid, TaskCompletion, 
  Control(3), Compliance_Most, Affiliation, Orderly, ExchangeOfIdeas, Collaborate(5), Submission_Least, 
  Dominance_Most, Careful, Internal, Analytical, Inducement_Most, Affective, Inaccurate, Global, 
  Compromise(3), Dominance_Least, Compete(1), Accurate, Significance(2), Power, Spontaneous, AttainmentOfGoals, 
  Submission_Most, Avoid(2), RecognitionForEffort, External, Security(1), Inward, Activity, Compliance_Least, 
  Inducement_Least, Consistency, Hygiene, Accomplishment, AffirmedByOthers, Accommodate(4), SelfAffirmed, 
  ReceiveDirection, Freedom, PrefersProcess, Submission, Inducement, Dominance, Powerfull, Versatile, 
  Precise, Adaptable, BigPicture, DetailOriented, MissionOriented, RelationOriented;
  
  private Integer priority;
  
  private TraitType() {
    this(0);
  }
  
  private TraitType(int priority) {
    this.priority = priority;
  }
  
  public Integer getPriority() {
    return priority;
  }
}
