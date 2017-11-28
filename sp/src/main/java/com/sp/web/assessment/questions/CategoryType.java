package com.sp.web.assessment.questions;

/**
 * @author Dax Abraham
 *
 *         The different categories.
 */
public enum CategoryType {
  /** Conflict Management with respective traits Type. */
  ConflictManagement("conflictManagementPortraitMatchProcessor", "conflictTeamDynamicsProcessor",
      TraitType.Collaborate, TraitType.Compromise, TraitType.Accommodate, TraitType.Avoid,
      TraitType.Compete),
  
  /** Fundamental needs with respective traits type. */
  FundamentalNeeds("fundamentalNeedsPortraitMatchProcessor", "fundamentalTeamDynamicsProcessor",
      TraitType.Control, TraitType.Significance, TraitType.Security),
  
  /** Motivation (how, what why traits type). */
  Motivation(TraitType.AttainmentOfGoals, TraitType.RecognitionForEffort, TraitType.Power,
      TraitType.Compliance, TraitType.Activity, TraitType.Affiliation, TraitType.SelfAffirmed,
      TraitType.AffirmedByOthers, TraitType.ExchangeOfIdeas, TraitType.ReceiveDirection,
      TraitType.Freedom, TraitType.Consistency, TraitType.TaskCompletion, TraitType.PrefersProcess),
//  Motivation(TraitType.AttainmentOfGoals, TraitType.RecognitionForEffort, TraitType.Power,
//      TraitType.Compliance, TraitType.Activity, TraitType.Affiliation, TraitType.SelfAffirmed,
//      TraitType.AffirmedByOthers, TraitType.ExchangeOfIdeas, TraitType.ReceiveDirection,
//      TraitType.Freedom, TraitType.Consistency, TraitType.TaskCompletion, TraitType.PrefersProcess,
//      TraitType.Hygiene, TraitType.Accomplishment),

      
  /** Processing blueprint traits type. */
  Processing(TraitType.External, TraitType.Internal, TraitType.Concrete, TraitType.Intuitive,
      TraitType.Affective, TraitType.Cognitive, TraitType.Orderly, TraitType.Spontaneous),
  
  /** Learning style traits type. */
  LearningStyle(TraitType.Global, TraitType.Analytical),
  
  /** Personality primary traits type. */
  Personality("personalityPortraitMatchProcessor", "personalityTeamDynamicsProcessor",
      CategoryPriority.Essential, TraitType.Powerfull, TraitType.Versatile, TraitType.Adaptable,
      TraitType.Precise),
  
  UnderPresssure("personalityPortraitMatchProcessor", "personalityTeamDynamicsProcessor",
      TraitType.Powerfull, TraitType.Versatile, TraitType.Precise, TraitType.Adaptable),
  
  /** Accuracy cateogry type. */
  Accuracy,
  
  /** Decision making trait type. */
  DecisionMaking(TraitType.Inward, TraitType.Outward, TraitType.Rapid, TraitType.Careful),
  
  MotivationWhy(TraitType.AttainmentOfGoals, TraitType.RecognitionForEffort, TraitType.Power,
      TraitType.Compliance, TraitType.Activity, TraitType.Affiliation),
  
  MotivationHow(TraitType.SelfAffirmed, TraitType.AffirmedByOthers, TraitType.ExchangeOfIdeas,
      TraitType.ReceiveDirection, TraitType.Freedom, TraitType.Consistency,
      TraitType.TaskCompletion, TraitType.PrefersProcess), 
  
  MotivationWhat(TraitType.Hygiene, TraitType.Accomplishment);
  
  private TraitType[] traitTypes;
  private String portraitMatchProcessor;
  private String teamDynamicsProcessor;
  private CategoryPriority priority;
  
  /**
   * Constructor initializing the traits.
   */
  private CategoryType(TraitType... traitTypes) {
    this("percentagePortraitMatchProcessor", "categoryPairTeamDynamicsProcessor", traitTypes);
  }
  
  /**
   * Constructor initializing the traits.
   */
  private CategoryType(String portraitMatchProcessor, String teamDynamicsProcessor,
      TraitType... traitTypes) {
    this(portraitMatchProcessor, teamDynamicsProcessor, CategoryPriority.Secondary, traitTypes);
  }
  
  /**
   * Constructor initializing the traits.
   */
  private CategoryType(String portraitMatchProcessor, String teamDynamicsProcessor,
      CategoryPriority priority, TraitType... traitTypes) {
    this.traitTypes = traitTypes;
    this.portraitMatchProcessor = portraitMatchProcessor;
    this.priority = priority;
    this.teamDynamicsProcessor = teamDynamicsProcessor;
  }
  
  public void setTraitTypes(TraitType[] traitTypes) {
    this.traitTypes = traitTypes;
  }
  
  public TraitType[] getTraitTypes() {
    return traitTypes;
  }
  
  public String getPortraitMatchProcessor() {
    return portraitMatchProcessor;
  }
  
  public CategoryPriority getPriority() {
    return priority;
  }
  
  public void setTeamDynamicsProcessor(String teamDynamicsProcessor) {
    this.teamDynamicsProcessor = teamDynamicsProcessor;
  }
  
  public String getTeamDynamicsProcessor() {
    return teamDynamicsProcessor;
  }
}
