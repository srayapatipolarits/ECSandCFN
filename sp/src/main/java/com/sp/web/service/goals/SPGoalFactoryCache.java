package com.sp.web.service.goals;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.translation.TranslationFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The cache helper for SP Goal factory.
 */
@Component
public class SPGoalFactoryCache {
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  private TranslationFactory tranlsationFactory;
  
  /**
   * <code>getGoals</code> method will fetch the SPGoals associated with a personality type in the
   * system.
   * 
   * @param personalityType
   *          User personality type for which goals are to be retrieved.
   * @param goalSource
   *          goals source
   * @return the List of spGoals.
   */
  @Cacheable(value = "goals", key = "#personalityType")
  public List<SPGoal> getGoals(PersonalityType personalityType) {
    return goalsRepository.getGoals(personalityType);
  }
  
  @Cacheable(value = "goals", key = "#root.methodName")
  public List<SPGoal> getAllGoals() {
    return goalsRepository.findAllGoals();
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id else invalid request exception
   */
  @Cacheable(value = "goals", key = "#goalId+#locale")
  public SPGoal getGoal(String goalId, String locale) {
    if (locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      return goalsRepository.findById(goalId);
    } else {
      /* get the tranlsation data for the goal and populate the data */
      return tranlsationFactory.getTranslation(goalId, locale, SPGoal.class,
          goalsRepository::findById);
    }
    
  }
  
  /**
   * Get the goal for the given goal id.
   * 
   * @param goalId
   *          - goal id
   * @return the goal for the given goal id else invalid request exception
   */
  @CacheEvict(value = { "goals", "userGoals" }, allEntries = true)
  public SPGoal updateGoal(SPGoal spGoal) {
    return goalsRepository.updateGoal(spGoal);
  }
  
  @CacheEvict(value = "goals", allEntries = true)
  public void removeGoal(SPGoal spGoal) {
    goalsRepository.removeGoal(spGoal);
  }
  
  @Cacheable(value = "goals", key = "#root.methodName+#category")
  public List<SPGoal> getGoalsForCategory(GoalCategory category) {
    return goalsRepository.findAllGoalsByCategory(category);
  }
  
  /**
   * Gets the goals for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the goals for the given company
   */
  @Cacheable(value = "goals", key = "#root.methodName+#companyId+#locale")
  public Map<String, String> getGoalsForCompany(String companyId, String locale) {
    List<SPGoal> findAllGoalsByCategory = goalsRepository
        .findAllGoalsByCategory(GoalCategory.Business);
    Map<String, String> allThemes = articlesFactory.getAllThemes(locale);
    Map<String, String> collect = findAllGoalsByCategory
        .stream()
        .filter(g -> filterGoalByCompany(companyId, g))
        .map(
            gl -> tranlsationFactory.getTranslation(gl.getId(), locale, SPGoal.class,
                goalsRepository::findById))
        .collect(Collectors.toMap(SPGoal::getId, SPGoal::getName));
    collect.putAll(allThemes);
    return collect;
  }
  
  /**
   * Check if the goal is active and valid for the given company.
   * 
   * @param companyId
   *          - company id
   * @param goal
   *          - goal to check
   * @return true if valid else false
   */
  private boolean filterGoalByCompany(String companyId, SPGoal goal) {
    if (goal.getStatus() == GoalStatus.ACTIVE) {
      if (goal.isAllAccounts()) {
        return true;
      } else {
        return goal.getAccounts().contains(companyId);
      }
    }
    return false;
  }
  
  /**
   * Get the goals for the individuals.
   * 
   * @return - the goals for individuals
   */
  @Cacheable(value = "goals", key = "#root.methodName+#locale")
  public Map<String, String> getGoalsForIndividual(String locale) {
    List<SPGoal> findAllGoalsByCategory = goalsRepository
        .findAllGoalsByCategory(GoalCategory.Individual);
    Map<String, String> allThemes = articlesFactory.getAllThemes(locale);
    Map<String, String> collect = findAllGoalsByCategory.stream()
        .filter(g -> g.getStatus() == GoalStatus.ACTIVE)
        .collect(Collectors.toMap(SPGoal::getId, SPGoal::getName));
    collect.putAll(allThemes);
    return collect;
  }
  
  /**
   * <code>personalityPracticeArea</code> method returns the practice area for the personality area
   * in the order having more weight.
   * 
   * @param personalityType
   *          for which the practice area are to be returned.
   * @return the practice areas.
   */
  @Cacheable(value = "personalityPracticeArea", key = "#personalityType+#locale", unless = "#result == null")
  public PersonalityPracticeArea getPersonalityPracticeArea(PersonalityType personalityType,
      String locale) {
    
    if (Constants.DEFAULT_LOCALE.equalsIgnoreCase(locale)) {
      return goalsRepository.findPersonalityPracticeArea(personalityType.toString());
    } else {
      return tranlsationFactory.getTranslation(personalityType.toString(), locale,
          PersonalityPracticeArea.class, goalsRepository::findPersonalityPracticeArea);
    }
  }
  
  /**
   * Cache method to update the personality practice area from the cache.
   * 
   * @param personalityPracticeArea
   *          - personality practice area to update
   */
  @CacheEvict(value = "personalityPracticeArea", key = "#personalityPracticeArea.personalityType")
  public void updatePersonalityPracticeArea(PersonalityPracticeArea personalityPracticeArea) {
    goalsRepository.updatePersonalityPracticeArea(personalityPracticeArea);
  }
  
  /**
   * The catchable method to get the user's blueprint.
   * 
   * @param blueprintId
   *          - blueprint id
   * @return the blueprint
   */
  @Cacheable(value = "blueprint")
  public Blueprint getBlueprint(String blueprintId) {
    return goalsRepository.getBlueprint(blueprintId);
  }
  
  /**
   * Update the given blueprint in cache and db.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  @CacheEvict(value = "blueprint", key = "#blueprint.id")
  public void updateBlueprint(Blueprint blueprint) {
    goalsRepository.updateBlueprint(blueprint);
  }
  
  /**
   * Remove the given blueprint.
   * 
   * @param blueprint
   *          - blueprint to remove
   */
  @CacheEvict(value = "blueprint", key = "#blueprint.id")
  public void removeBlueprint(Blueprint blueprint) {
    goalsRepository.removeGoal(blueprint);
  }
}
