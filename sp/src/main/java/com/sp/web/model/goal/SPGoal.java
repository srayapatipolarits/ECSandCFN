package com.sp.web.model.goal;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.dto.todo.ActionPlanStepActionTodoDTO;
import com.sp.web.model.SPRating;
import com.sp.web.service.translation.Translable;

import org.springframework.data.annotation.Transient;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * <code>SPGoal</code> class holds the goals associated with the users.
 * 
 * @author pradeep
 */
public class SPGoal implements Serializable {
  
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = -1367786124484740780L;
  
  @Translable
  private String name;
  
  @Translable
  private String description;
  
  protected String id;
  
  private GoalCategory category;
  
  /* Mandatory articles to be shown in goals tools */
  private List<String> mandatoryArticles;
  
  private Set<PersonalityType> personalityType;
  
  private GoalStatus status;
  
  private boolean isMandatory;
  
  private List<String> accounts;
  
  private boolean isAdminGoal;
  
  private boolean allAccounts;
  
  @Translable
  private List<DevelopmentStrategy> developmentStrategyList;
  
  private Map<String, String> introVideo;
  
  private KeyOutcomes keyOutcomes;
  
  @Translable
  private List<DSActionCategory> devStrategyActionCategoryList;
  
  private SPRating rating;
  
  private Set<String> actionIds;
  
  private int durationDays;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * <code>getMandatoryArticles</code> will return the mandatory articles. It uses the Linked Hash
   * set for maintiaing ordering
   * 
   * @return the mandatoryArticles
   */
  public List<String> getMandatoryArticles() {
    if (mandatoryArticles == null) {
      mandatoryArticles = new ArrayList<String>();
    }
    return mandatoryArticles;
  }
  
  public void setMandatoryArticles(List<String> mandatoryArticles) {
    this.mandatoryArticles = mandatoryArticles;
  }
  
  public void setPersonalityType(Set<PersonalityType> personalityType) {
    this.personalityType = personalityType;
  }
  
  public Set<PersonalityType> getPersonalityType() {
    return personalityType;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    
    if ((obj == null) || !(obj instanceof SPGoal)) {
      return false;
    } else {
      SPGoal otherGoal = (SPGoal) obj;
      return (name.equalsIgnoreCase(otherGoal.getName()) && this.category == otherGoal
          .getCategory());
    }
  }
  
  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int code = 42;
    code += (this.name != null ? this.name.hashCode() : 0);
    return code;
  }
  
  @Override
  public String toString() {
    return "SPGoal [name=" + name + ", description=" + description + ", id=" + id + ", category="
        + category + ", mandatoryArticles=" + mandatoryArticles + ", personalityType="
        + personalityType + ", status=" + status + ", isMandatory=" + isMandatory + ", accountId="
        + accounts + "]";
  }
  
  public GoalCategory getCategory() {
    return category;
  }
  
  public void setCategory(GoalCategory category) {
    this.category = category;
  }
  
  public void setMandatory(boolean isMandatory) {
    this.isMandatory = isMandatory;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public boolean isMandatory() {
    return isMandatory;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public void setAccounts(List<String> accounts) {
    this.accounts = accounts;
  }
  
  /**
   * @return - the accounts.
   */
  public List<String> getAccounts() {
    if (accounts == null) {
      accounts = new ArrayList<String>();
    }
    return accounts;
  }
  
  public void setAllAccounts(boolean allAccounts) {
    this.allAccounts = allAccounts;
  }
  
  public boolean isAllAccounts() {
    return allAccounts;
  }
  
  public void setAdminGoal(boolean isAdminGoal) {
    this.isAdminGoal = isAdminGoal;
  }
  
  public boolean isAdminGoal() {
    return isAdminGoal;
  }
  
  /**
   * Get the development strategy list.
   * 
   * @return - the list of development strategies
   */
  public List<DevelopmentStrategy> getDevelopmentStrategyList() {
    if (developmentStrategyList == null) {
      developmentStrategyList = new ArrayList<>();
    }
    return developmentStrategyList;
  }
  
  public void setDevelopmentStrategyList(List<DevelopmentStrategy> listDevelopmentStrategy) {
    this.developmentStrategyList = listDevelopmentStrategy;
  }
  
  public Map<String, String> getIntroVideo() {
    return introVideo;
  }
  
  public void setIntroVideo(Map<String, String> introVideo) {
    this.introVideo = introVideo;
  }
  
  public KeyOutcomes getKeyOutcomes() {
    return keyOutcomes;
  }
  
  public void setKeyOutcomes(KeyOutcomes keyOutcomes) {
    this.keyOutcomes = keyOutcomes;
  }
  
  public List<DSActionCategory> getDevStrategyActionCategoryList() {
    return devStrategyActionCategoryList;
  }
  
  public void setDevStrategyActionCategoryList(List<DSActionCategory> devStrategyActionCategoryList) {
    this.devStrategyActionCategoryList = devStrategyActionCategoryList;
  }
  
  /**
   * Method to validate the UID.
   * 
   * @param uid
   *          - unique id
   * @return true if UID is present
   */
  public boolean validateUID(String uid) {
    if (devStrategyActionCategoryList != null) {
      return devStrategyActionCategoryList.stream().filter(ac -> ac.validateUID(uid)).findFirst()
          .isPresent();
    }
    return false;
  }
  
  /**
   * If the current goal is from action plan.
   * 
   * @return true
   */
  @Transient
  public boolean isActionPlan() {
    return (category != null && category == GoalCategory.ActionPlan);
  }
  
  /**
   * If the current goal is from action plan.
   * 
   * @return true
   */
  @Transient
  public boolean isBluePrint() {
    return (category != null && category == GoalCategory.Blueprint);
  }
  
  /**
   * If the current goal is from growth plan.
   * 
   * @return true
   */
  @Transient
  public boolean isIndividualLearningDevelopment() {
    return (category != null && category == GoalCategory.GrowthAreas);
  }
  
  /**
   * If the current goal is from growth plan.
   * 
   * @return
   */
  @Transient
  public boolean isCompetency() {
    return (category != null && category == GoalCategory.Competency);
 }
  
  public SPRating getRating() {
    return rating;
  }
  
  public void setRating(SPRating rating) {
    this.rating = rating;
  }
  
  /**
   * @return true if the goal status is active else false.
   */
  @Transient
  public boolean isActive() {
    return (status != null && status == GoalStatus.ACTIVE);
  }
  
  /**
   * The method to calculate the total number actions in the goal.
   * 
   * @return the count of actions.
   */
  public int calculateActionCount() {
    updateActionIds();
    return getActionCount();
  }
  
  /**
   * Finds all the action'ids for the given goal.
   * 
   * @return the list of action id's
   */
  public List<String> findActionIds() {
    final ArrayList<String> actionIdList = new ArrayList<String>();
    if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      devStrategyActionCategoryList.forEach(ac -> ac.addActionUids(actionIdList));
    }
    return actionIdList;
  }
  
  /**
   * Get the list of deactivated UID's.
   * 
   * @param uidList
   *          - uid list
   */
  public void updateDeactivatedUids(List<String> uidList) {
    if (status == GoalStatus.ACTIVE) {
      if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
        devStrategyActionCategoryList.forEach(ac -> ac.updateDeactivatedUids(uidList));
      }
    } else {
      updateActionUids(uidList);
    }
  }
  
  /**
   * Get all the action UID's for the given category.
   * 
   * @param uidList
   *          - uid list
   */
  private void updateActionUids(List<String> uidList) {
    if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      devStrategyActionCategoryList.stream().forEach(dsAc -> dsAc.updateActionUids(uidList));
    }
  }
  
  public Set<String> getActionIds() {
    return actionIds;
  }
  
  public void setActionIds(Set<String> actionIds) {
    this.actionIds = actionIds;
  }
  
  /**
   * Update the action ids for the given practice area.
   */
  public void updateActionIds() {
    if (status == GoalStatus.ACTIVE && !CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      List<String> actionIds = new ArrayList<String>();
      devStrategyActionCategoryList.forEach(dsac -> dsac.addActionUids(actionIds));
      this.setActionIds(new HashSet<String>(actionIds));
    } else {
      actionIds = new HashSet<String>();
    }
  }
  
  /**
   * Get the count of actions.
   * 
   * @return - action count
   */
  public int getActionCount() {
    return (actionIds == null) ? 0 : actionIds.size();
  }

  public int getDurationDays() {
    return durationDays;
  }

  public void setDurationDays(int durationDays) {
    this.durationDays = durationDays;
  }

  /**
   * Get the remaining actions from the given set of action ids.
   * 
   * @param completedActions
   *            - completed actions
   * @return
   *    the remaining actions
   */
  public Set<String> getRemainingActions(Set<String> completedActions) {
    HashSet<String> remainingActionIds = new HashSet<String>(actionIds);
    remainingActionIds.removeAll(completedActions);
    return remainingActionIds;
  }
  
  /**
   * Add the UID for all the actions and categories.
   * 
   * @param uidGenerator
   *            - uid generator
   */
  public void updateUids(Supplier<String> uidGenerator) {
    if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      devStrategyActionCategoryList.forEach(dsaCategory -> dsaCategory.addUID(uidGenerator));
    }
  }

  /**
   * Method to check if the passed actions consists of all the actions or there
   * are still some remaining actions.
   *  
   * @param completedActions
   *              - currently completed actions
   * @return
   *    true if remaining else false
   */
  public boolean hasRemainingActions(Set<String> completedActions) {
    if (!CollectionUtils.isEmpty(actionIds)) {
      if (!CollectionUtils.isEmpty(completedActions)) {
        return completedActions.size() < actionIds.size();
      }
      return true;
    }
    return false;
  }

  /**
   * Add the todo if present in remaining actions.
   * 
   * @param remainingActions
   *            - remaining actions
   * @param actions
   *            - actions to add to
   */
  public void addTodo(Set<String> remainingActions, List<ActionPlanStepActionTodoDTO> actions) {
    if (!CollectionUtils.isEmpty(devStrategyActionCategoryList)) {
      devStrategyActionCategoryList.forEach(dsActionCategory -> dsActionCategory.addTodo(
          remainingActions, actions));
    }
  }
}
