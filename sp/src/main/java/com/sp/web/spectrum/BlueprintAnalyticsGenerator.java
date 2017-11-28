package com.sp.web.spectrum;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dto.blueprint.BlueprintAnalytics;
import com.sp.web.dto.blueprint.BlueprintUserAnalytics;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.spectrum.SpectrumFilter;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BlueprintAnalyticsGenerator class will generate the blueprint analytics for the spectrum.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class BlueprintAnalyticsGenerator {
  
  private static final Logger LOG = Logger.getLogger(BlueprintAnalyticsGenerator.class);
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  /**
   * getBlueprintAnalytics method will calculate the blueprint anayltics for a company.
   * 
   * @param users
   *          list of the users
   * @return the Proifle Balance.
   */
  @Cacheable(value = "blueprintAnalytics", key = "#companyId")
  public BlueprintAnalytics getBlueprintAnalytics(String companyId, List<User> users) {
    
    /* filter the users who have completed there assessment */
    
    if (CollectionUtils.isEmpty(users)) {
      throw new SPException("No users present to show Profile Balance");
    }
    
    /* Create a map of users against the personality types */
    Set<String> allGroups = new HashSet<>();
    Set<String> allTags = new HashSet<>();
    /* filter out the users who have not completed there assesemnts */
    List<User> validUsers = users.stream().filter(usr -> usr.getUserStatus() == UserStatus.VALID)
        .collect(Collectors.toList());
    
    BlueprintAnalytics blueprintAnalytics = new BlueprintAnalytics();
    List<BlueprintUserAnalytics> userAnalytics = validUsers
      .stream()
      .map(
        usr -> {
          PersonalityType userPersonality = getUserPersonality(usr);
          if (userPersonality == null || userPersonality == PersonalityType.Tight
              || userPersonality == PersonalityType.Undershift
              || userPersonality == PersonalityType.Overshift
              || userPersonality == PersonalityType.Invalid) {
            LOG.warn("User " + usr
                + "is having invalid persanlity type. Sectrum results will not be correct ");
            return null;
          }
          
          Blueprint blueprint = goalsFactory.getBlueprint(usr);
          
          if (blueprint != null) {
            blueprintAnalytics.addTotalBlueprints();
            if (blueprint.getStatus() == GoalStatus.PUBLISHED) {
              BlueprintUserAnalytics userDto = new BlueprintUserAnalytics(usr);
              if (blueprint.getCompletedActions() != null) {
                userDto.setCompletedKpi(blueprint.getCompletedActions().size());
                blueprintAnalytics.addCompletedKpi(blueprint.getCompletedActions().size());
                
                if (blueprint.getCompletedActions().size() == blueprint.getTotalActionCount()) {
                  blueprintAnalytics.addCompletdBlueprint();
                }
              }
              userDto.setTotalKpi(blueprint.getTotalActionCount());
              blueprintAnalytics.addPublishedBlueprint();
              blueprintAnalytics.addTotalKpi(blueprint.getTotalActionCount());
              List<String> groups = usr.getGroupAssociationList().stream().map(grp -> {
                  String name = grp.getName();
                  allGroups.add(name);
                  return name;
                }).collect(Collectors.toList());
              userDto.setGroups(groups);
              if (usr.getTagList() != null) {
                userDto.setTags(usr.getTagList());
                allTags.addAll(usr.getTagList());
              }
              return userDto;
            }
          }
          return null;
          
        }).filter(sUsr -> sUsr != null).collect(Collectors.toList());
    /* Iterate through the users */
    /* check for users having invalid profile in the system */
    
    SpectrumFilter spectrumFilter = new SpectrumFilter();
    spectrumFilter.getFilters().put(Constants.PARAM_GROUPS, allGroups);
    spectrumFilter.getFilters().put(Constants.PARAM_TAGS, allTags);
    
    blueprintAnalytics.setSpectrumFilter(spectrumFilter);
    blueprintAnalytics.setUserAnalytics(userAnalytics);
    ;
    blueprintAnalytics.setCompanyId(companyId);
    return blueprintAnalytics;
    
  }
  
  /**
   * getUserPersonality method will return the personality of the user
   * 
   * @param user
   *          for which perosnality is to be retireved.
   * @return the Personality.
   */
  private PersonalityType getUserPersonality(User user) {
    if (user.getAnalysis() == null) {
      LOG.warn("User :" + user.getEmail() + ", status is Valid, but analysis is not present.");
      return null;
    }
    HashMap<RangeType, PersonalityBeanResponse> personality = user.getAnalysis().getPersonality();
    PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
    return personalityBeanResponse.getPersonalityType();
  }
  
  @CacheEvict(value = "blueprintAnalytics", allEntries = true)
  public void reset() {
    
  }
  
}