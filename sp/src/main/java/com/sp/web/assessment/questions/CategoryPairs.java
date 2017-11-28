package com.sp.web.assessment.questions;

/**
 * CategoryPair enum contains the pair of Trait for each {@link CategoryType}.
 * 
 * @author pradeepruhil
 *
 */
public enum CategoryPairs {
  
  /* Processing Pairs Start. */
  ExternalInternal(CategoryType.Processing, TraitType.Internal, TraitType.External),
  
  ConcreteIntuitive(CategoryType.Processing, TraitType.Concrete, TraitType.Intuitive),
  
  AffectiveCognitive(CategoryType.Processing, TraitType.Affective, TraitType.Cognitive),
  
  OrderlySpontaneous(CategoryType.Processing, TraitType.Spontaneous, TraitType.Orderly),
  /* Processing Pairs End. */
  
  /* Conflict Management start */
  Collaborate(CategoryType.ConflictManagement, TraitType.Collaborate),
  
  Compromise(CategoryType.ConflictManagement, TraitType.Compromise),
  
  Accommodate(CategoryType.ConflictManagement, TraitType.Accommodate),
  
  Avoid(CategoryType.ConflictManagement, TraitType.Avoid),
  
  Compete(CategoryType.ConflictManagement, TraitType.Compete),
  /* Conflict Management end */
  
  /* Fundamental needs start */
  Control(CategoryType.FundamentalNeeds, TraitType.Control),
  
  Significance(CategoryType.FundamentalNeeds, TraitType.Significance),
  
  Security(CategoryType.FundamentalNeeds, TraitType.Security),
  /* fundamental needs end */
  
  /* Motivation start */
  AttainmentGoalsRecognitionEffort(CategoryType.Motivation, TraitType.AttainmentOfGoals,
      TraitType.RecognitionForEffort),
  
  PowerCompliance(CategoryType.Motivation, TraitType.Power, TraitType.Compliance),
  
  ActivityAffiliation(CategoryType.Motivation, TraitType.Activity, TraitType.Affiliation),
  
  SelfAffirmedAffirmedByOthers(CategoryType.Motivation, TraitType.SelfAffirmed,
      TraitType.AffirmedByOthers),
  
  ExchangeOfIdeaReceiveDirection(CategoryType.Motivation, TraitType.ExchangeOfIdeas,
      TraitType.ReceiveDirection),
  
  FeedomConsitency(CategoryType.Motivation, TraitType.Freedom, TraitType.Consistency),
  
  TaskCompletionPreferProcess(CategoryType.Motivation, TraitType.TaskCompletion,
      TraitType.PrefersProcess),
  
  HygieneAccomplishment(CategoryType.Motivation, TraitType.Hygiene, TraitType.Accomplishment),
  /* Motivation end */
  
  /* Personality start */
  Powerfull(CategoryType.Personality, TraitType.Powerfull),
  
  Versatile(CategoryType.Personality, TraitType.Versatile),
  
  Adaptable(CategoryType.Personality, TraitType.Adaptable),
  
  Precise(CategoryType.Personality, TraitType.Precise),
  /* Personality end */
  
  /* UnderPressurePersonality start */
  PowerfullUnderPressure(CategoryType.UnderPresssure, TraitType.Powerfull),
  
  VersatileUnderPressure(CategoryType.UnderPresssure, TraitType.Versatile),
  
  AdaptableUnderPressure(CategoryType.UnderPresssure, TraitType.Adaptable),
  
  PreciseUnderPressure(CategoryType.UnderPresssure, TraitType.Precise),
  /* UnderPressurePersonality end */
  
  /* Decision Making start */
  InwardOutward(CategoryType.DecisionMaking, TraitType.Inward, TraitType.Outward),
  
  RapidCareful(CategoryType.DecisionMaking, TraitType.Rapid, TraitType.Careful),
  /* Decision Making end */
  
  GlobalAnalytical(CategoryType.LearningStyle, TraitType.Global, TraitType.Analytical);
  
  private CategoryType categoryType;
  private TraitType traitType1;
  
  private TraitType traitType2;
  
  private CategoryPairs(CategoryType categoryType, TraitType traitType1, TraitType traitType2) {
    
    this.categoryType = categoryType;
    this.traitType1 = traitType1;
    this.traitType2 = traitType2;
  }
  
  private CategoryPairs(CategoryType categoryType, TraitType traitType1) {
    this.categoryType = categoryType;
    this.traitType1 = traitType1;
  }
  
  public CategoryType getCategoryType() {
    return categoryType;
  }
  
  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }
  
  public TraitType getTraitType1() {
    return traitType1;
  }
  
  public void setTraitType1(TraitType traitType1) {
    this.traitType1 = traitType1;
  }
  
  public TraitType getTraitType2() {
    return traitType2;
  }
  
  public void setTraitType2(TraitType traitType2) {
    this.traitType2 = traitType2;
  }
  
  /**
   * get the categoryPair for the trait type passed.
   * 
   * @param traitType
   *          for which Category pair is to be find.
   * @return the category pair.
   */
  public static CategoryPairs getCategoryPairForTrait(TraitType traitType) {
    CategoryPairs categoryPairs = null;
    CategoryPairs[] values = CategoryPairs.values();
    for (CategoryPairs categoryPair : values) {
      
      if (categoryPair.getTraitType1() == traitType || categoryPair.getTraitType2() == traitType) {
        categoryPairs = categoryPair;
        break;
      }
    }
    return categoryPairs;
  }
}
