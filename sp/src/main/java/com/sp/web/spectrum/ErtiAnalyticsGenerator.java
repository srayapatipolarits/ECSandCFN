package com.sp.web.spectrum;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.exception.SPException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.spectrum.AgeCategory;
import com.sp.web.model.spectrum.ErtiAnalytics;
import com.sp.web.model.spectrum.ErtiUserInsights;
import com.sp.web.model.spectrum.SpectrumErtiAnalyticsFilter;
import com.sp.web.model.spectrum.SpectrumFilter;
import com.sp.web.service.hiring.group.HiringGroupFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ErtiAnalyticsGenerator class will generate the erti analytics data.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class ErtiAnalyticsGenerator {
  
  private static final Logger LOG = Logger.getLogger(ErtiAnalyticsGenerator.class);
  
  @Autowired
  private HiringGroupFactory groupFactory;
  /**
   * getErtiAnalytics method will give the ertiAnalytics for the company.
   * 
   * @param users
   *          list of the users
   * @return the Proifle Balance.
   */
  @Cacheable(value = "ertiAnalytics", key = "#companyId")
  public ErtiAnalytics getErtiAnalytics(String companyId, List<User> users, boolean paModule) {
    
    return getErtiAnalytic(companyId, users, paModule);
    
  }
  
  private ErtiAnalytics getErtiAnalytic(String companyId, List<? extends User> users,
      boolean paModule) {
    /* filter the users who have completed there assessment */
    
    if (CollectionUtils.isEmpty(users)) {
      throw new SPException("No users present to to give the erti insights.");
    }
    
    /* Create a map of users against the personality types */
    Set<String> allGroups = new HashSet<>();
    Set<String> allTags = new HashSet<>();
    /* filter out the users who have not completed there assesemnts */
    List<User> validUsers = users.stream().filter(usr -> usr.getUserStatus() == UserStatus.VALID)
        .collect(Collectors.toList());
    List<ErtiUserInsights> specUsers = validUsers
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
              ErtiUserInsights userDto = new ErtiUserInsights(usr);
              
              userDto.setGender(usr.getGender());
              
              List<String> groups = null;
              if (paModule) {
                List<HiringGroupBaseDTO> groupsForUser = groupFactory.getGroupsForUser(usr,
                    usr.getId());
                if (!CollectionUtils.isEmpty(groupsForUser)) {
                  groups = groupsForUser.stream().map(grp -> {
                    String name = grp.getName();
                    allGroups.add(name);
                    return name;
                  }).collect(Collectors.toList());
                  
                }
              } else {
                groups = usr.getGroupAssociationList().stream().map(grp -> {
                  String name = grp.getName();
                  allGroups.add(name);
                  return name;
                }).collect(Collectors.toList());
              }
              userDto.setGroups(groups);
              if (usr.getTagList() != null) {
                userDto.setTags(usr.getTagList());
                allTags.addAll(usr.getTagList());
              }
              LocalDate dob = usr.getDob();
              LocalDate currentDate = LocalDate.now();
              if (dob != null) {
                int age = (int) dob.until(currentDate, ChronoUnit.YEARS);
                userDto.setAge(age);
              }
              
              userDto.getErtiData().putAll(usr.getAnalysis().getConflictManagement());
              userDto.getErtiData().putAll(usr.getAnalysis().getDecisionMaking());
              userDto.getErtiData().putAll(usr.getAnalysis().getFundamentalNeeds());
              userDto.getErtiData().putAll(usr.getAnalysis().getLearningStyle());
              userDto.getErtiData().putAll(usr.getAnalysis().getMotivationHow());
              userDto.getErtiData().putAll(usr.getAnalysis().getMotivationWhat());
              userDto.getErtiData().putAll(usr.getAnalysis().getMotivationWhy());
              userDto.getErtiData().putAll(usr.getAnalysis().getProcessing());
              HashMap<RangeType, PersonalityBeanResponse> personality = usr.getAnalysis()
                  .getPersonality();
              PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
              populatePersonalityData(userDto.getErtiData(), personalityBeanResponse);
              userDto.setPrimary(personalityBeanResponse.getPersonalityType());
              
              PersonalityBeanResponse underPressure = personality.get(RangeType.UnderPressure);
              populatePersonalityData(userDto.getErtiUnderPressureData(), underPressure);
              userDto.setUnderPresssure(underPressure.getPersonalityType());
              
              return userDto;
            }).filter(sUsr -> sUsr != null).collect(Collectors.toList());
    /* Iterate through the users */
    /* check for users having invalid profile in the system */
    
    SpectrumFilter spectrumFilter = new SpectrumFilter();
    spectrumFilter.getFilters().put(Constants.PARAM_GROUPS, allGroups);
    spectrumFilter.getFilters().put(Constants.PARAM_TAGS, allTags);
    spectrumFilter.getFilters().put(Constants.PARAM_AGE, AgeCategory.values());
    List<SpectrumErtiAnalyticsFilter> analyticsFilters = new ArrayList<SpectrumErtiAnalyticsFilter>();
    for (CategoryType categoryType : CategoryType.values()) {
      if (categoryType == CategoryType.UnderPresssure) {
        continue;
      }
      SpectrumErtiAnalyticsFilter analyticsFilter = new SpectrumErtiAnalyticsFilter();
      analyticsFilter.setCategoryType(categoryType);
      ;
      analyticsFilter.initializeTraitsTYpe();
      analyticsFilters.add(analyticsFilter);
    }
    spectrumFilter.getFilters().put(Constants.PARAM_CATEGORY_TYPE, analyticsFilters);
    
    ErtiAnalytics ertiAnalytics = new ErtiAnalytics();
    ertiAnalytics.setSpectrumFilter(spectrumFilter);
    ertiAnalytics.setUserAnalytics(specUsers);
    ertiAnalytics.setCompanyId(companyId);
    return ertiAnalytics;
  }
  
  /**
   * @param userDto
   * @param personalityBeanResponse
   */
  private void populatePersonalityData(Map<TraitType, BigDecimal> ertiData,
      PersonalityBeanResponse personalityBeanResponse) {
    BigDecimal hundred = new BigDecimal(100);
    BigDecimal powerFull = personalityBeanResponse.getSegmentGraph()[0].multiply(hundred);
    BigDecimal versatile = personalityBeanResponse.getSegmentGraph()[1].multiply(hundred);
    BigDecimal adaptable = personalityBeanResponse.getSegmentGraph()[2].multiply(hundred);
    BigDecimal precise = personalityBeanResponse.getSegmentGraph()[3].multiply(hundred);
    ertiData.put(TraitType.Powerfull, powerFull);
    ertiData.put(TraitType.Versatile, versatile);
    ertiData.put(TraitType.Adaptable, adaptable);
    ertiData.put(TraitType.Precise, precise);
    ertiData.put(TraitType.BigPicture, (getAverage(powerFull, versatile)));
    ertiData.put(TraitType.RelationOriented, (getAverage(versatile, adaptable)));
    ertiData.put(TraitType.MissionOriented, (getAverage(powerFull, precise)));
    ertiData.put(TraitType.DetailOriented, (getAverage(adaptable, precise)));
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
  
  @CacheEvict(value = "ertiAnalytics", allEntries = true)
  public void reset() {
    
  }
  
  private BigDecimal getAverage(BigDecimal firstNumber, BigDecimal lastNumber) {
    BigDecimal total = firstNumber.add(lastNumber);
    return total.divide(new BigDecimal(2), 4);
  }
  
  public ErtiAnalytics getHiringErtiAnalytics(String companyId, List<HiringUser> all, boolean paModule) {
    return getErtiAnalytic(companyId, all, true);
  }
  
}