package com.sp.web.assessment.processing;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The holding class for all the analysis.
 * 
 * @author Dax Abraham
 * 
 */
public class AnalysisBean implements Serializable {
  
  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 7349923351244567522L;
  private static final ConflictManagementComparator comparator = new ConflictManagementComparator();
  
  private BigDecimal accuracy;
  private Map<TraitType, BigDecimal> processing;
  private Map<TraitType, BigDecimal> conflictManagement;
  private Map<TraitType, BigDecimal> learningStyle;
  private Map<TraitType, BigDecimal> motivationWhy;
  private Map<TraitType, BigDecimal> motivationHow;
  private Map<TraitType, BigDecimal> motivationWhat;
  private HashMap<RangeType, PersonalityBeanResponse> personality;
  private Map<TraitType, BigDecimal> fundamentalNeeds;
  private Map<TraitType, BigDecimal> decisionMaking;
  private LocalDateTime createdOn;
  private Map<CategoryType, List<TraitType>> traitPriorities;
  private Set<CategoryType> completedCategories; 
  
  public BigDecimal getAccuracy() {
    return accuracy;
  }
  
  public void setAccuracy(BigDecimal accuracy) {
    this.accuracy = accuracy;
  }
  
  public Map<TraitType, BigDecimal> getProcessing() {
    return processing;
  }
  
  public void setProcessing(Map<TraitType, BigDecimal> processingMap) {
    this.processing = processingMap;
  }
  
  public Map<TraitType, BigDecimal> getConflictManagement() {
    return conflictManagement;
  }
  
  public void setConflictManagement(Map<TraitType, BigDecimal> conflictManagementMap) {
    this.conflictManagement = conflictManagementMap;
  }
  
  public Map<TraitType, BigDecimal> getLearningStyle() {
    return learningStyle;
  }
  
  public void setLearningStyle(Map<TraitType, BigDecimal> learningStyleMap) {
    this.learningStyle = learningStyleMap;
  }
  
  public Map<TraitType, BigDecimal> getMotivationWhy() {
    return motivationWhy;
  }
  
  public void setMotivationWhy(Map<TraitType, BigDecimal> motivationWhyMap) {
    this.motivationWhy = motivationWhyMap;
  }
  
  public Map<TraitType, BigDecimal> getMotivationHow() {
    return motivationHow;
  }
  
  public void setMotivationHow(Map<TraitType, BigDecimal> motivationHowMap) {
    this.motivationHow = motivationHowMap;
  }
  
  public Map<TraitType, BigDecimal> getMotivationWhat() {
    return motivationWhat;
  }
  
  public void setMotivationWhat(Map<TraitType, BigDecimal> motivationWhatMap) {
    this.motivationWhat = motivationWhatMap;
  }
  
  public HashMap<RangeType, PersonalityBeanResponse> getPersonality() {
    return personality;
  }
  
  /**
   * Get the personality for the given personality type.
   * 
   * @param type
   *         the personality type
   * @return
   *    the personality
   */
  public PersonalityType getPersonality(RangeType type) {
    return getPersonality().get(type).getPersonalityType();
  }
  
  public void setPersonality(HashMap<RangeType, PersonalityBeanResponse> personalityType) {
    this.personality = personalityType;
  }
  
  public Map<TraitType, BigDecimal> getFundamentalNeeds() {
    return fundamentalNeeds;
  }
  
  public void setFundamentalNeeds(Map<TraitType, BigDecimal> fundamentalNeedsMap) {
    this.fundamentalNeeds = fundamentalNeedsMap;
  }
  
  public Map<TraitType, BigDecimal> getDecisionMaking() {
    return decisionMaking;
  }
  
  public void setDecisionMaking(Map<TraitType, BigDecimal> decisionMakingMap) {
    this.decisionMaking = decisionMakingMap;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public Map<CategoryType, List<TraitType>> getTraitPriorities() {
    return traitPriorities;
  }
  
  public void setTraitPriorities(Map<CategoryType, List<TraitType>> traitPriorities) {
    this.traitPriorities = traitPriorities;
  }
  
  /**
   * Compute the trait priorities for scales like conflict management and fundamental needs.
   */
  public void updatePriorities() {
    if (traitPriorities == null) {
      traitPriorities = new HashMap<CategoryType, List<TraitType>>();
    }
    
    traitPriorities.put(CategoryType.ConflictManagement, getPriorityList(conflictManagement));
    traitPriorities.put(CategoryType.FundamentalNeeds, getPriorityList(fundamentalNeeds));
  }

  /**
   * Process the priority list.
   * 
   * @param map
   *          - the map to process
   * @return
   *    the top two priorities
   */
  private List<TraitType> getPriorityList(Map<TraitType, BigDecimal> map) {
    return map.entrySet().stream()
        .sorted(comparator.reversed()).limit(2).map(Entry::getKey).collect(Collectors.toList());
  }

  public Set<CategoryType> getCompletedCategories() {
    return completedCategories;
  }

  public void setCompletedCategories(Set<CategoryType> completedCategories) {
    this.completedCategories = completedCategories;
  }

  /**
   * Update the completed categories for the users analysis.
   * 
   * @param categories
   *          - categories to update
   */
  public void updateCompletedCategories(List<CategoryType> categories) {
    if (completedCategories == null) {
      completedCategories = new HashSet<CategoryType>();
    }
    completedCategories.addAll(categories);
  }
  
  public List<TraitType> traitPriorities(CategoryType type) {
    return (traitPriorities != null) ? traitPriorities.get(type) : null;
  }
}
