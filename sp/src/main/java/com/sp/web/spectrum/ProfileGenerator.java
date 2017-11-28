package com.sp.web.spectrum;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.dto.spectrum.SpectrumUserDTO;
import com.sp.web.exception.SPException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.spectrum.AgeCategory;
import com.sp.web.model.spectrum.ProfileBalance;
import com.sp.web.model.spectrum.SpectrumFilter;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ProfileGenerator class will generate the profile balance data for the spectrum.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class ProfileGenerator {
  
  private static final Logger LOG = Logger.getLogger(ProfileGenerator.class);
  
  @Autowired
  private HiringGroupFactory groupFactory;
  
  /**
   * getProfileBalanceForUsers method will calculate the profile balance and will return the result.
   * 
   * @param users
   *          list of the users
   * @return the Proifle Balance.
   */
  public ProfileBalance generateHiringProfileBalances(String companyId, List<HiringUser> users,
      boolean paModule) {
    return getProfileBalance(companyId, users, paModule);
  }
  
  /**
   * getProfileBalanceForUsers method will calculate the profile balance and will return the result.
   * 
   * @param users
   *          list of the users
   * @return the Proifle Balance.
   */
  @Cacheable(value = "profileBalance", key = "#companyId")
  public ProfileBalance generateProfileBalances(String companyId, List<User> users, boolean paModule) {
    return getProfileBalance(companyId, users, paModule);
    
  }
  
  private ProfileBalance getProfileBalance(String companyId, List<? extends User> users,
      boolean paModule) {
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
    
    List<SpectrumUserDTO> specUsers = validUsers
        .stream()
        .map(
            usr -> {
              PersonalityType userPersonality = getUserPersonality(usr, RangeType.Primary);
              PersonalityType uderPressure = getUserPersonality(usr, RangeType.UnderPressure);
              if ((userPersonality == null && uderPressure == null)
                  || (userPersonality == PersonalityType.Tight && uderPressure == PersonalityType.Tight)
                  || (userPersonality == PersonalityType.Undershift && uderPressure == PersonalityType.Undershift)
                  || (userPersonality == PersonalityType.Overshift || uderPressure == PersonalityType.Overshift)
                  || (userPersonality == PersonalityType.Invalid || uderPressure == PersonalityType.Invalid)) {
                LOG.warn("User " + usr
                    + "is having invalid persanlity type. Sectrum results will not be correct ");
                return null;
              }
              SpectrumUserDTO userDto = new SpectrumUserDTO(usr);
              
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
              userDto.setPrimaryPersonality(userPersonality);
              userDto.setUnderPressurePersonality(uderPressure);
              LocalDate dob = usr.getDob();
              LocalDate currentDate = LocalDate.now();
              if (dob != null) {
                int age = (int) dob.until(currentDate, ChronoUnit.YEARS);
                userDto.setAge(age);
              }
              
              return userDto;
            }).filter(sUsr -> sUsr != null).collect(Collectors.toList());
    /* Iterate through the users */
    /* check for users having invalid profile in the system */
    
    SpectrumFilter spectrumFilter = new SpectrumFilter();
    spectrumFilter.getFilters().put(Constants.PARAM_GROUPS, allGroups);
    spectrumFilter.getFilters().put(Constants.PARAM_TAGS, allTags);
    spectrumFilter.getFilters().put(Constants.PARAM_AGE, AgeCategory.values());
    
    /* Add blank date for missing personalityTypes */
    Map<PersonalityType, Map<String, String>> personalityDescrptionData = new HashMap<>();
    for (PersonalityType personalityType : PersonalityType.values()) {
      if (personalityType == null || personalityType == PersonalityType.Tight
          || personalityType == PersonalityType.Undershift
          || personalityType == PersonalityType.Overshift
          || personalityType == PersonalityType.Invalid) {
        continue;
      }
      Map<String, String> personalityMap = personalityDescrptionData.get(personalityType);
      if (personalityMap == null) {
        personalityMap = new HashMap<>();
        personalityDescrptionData.put(personalityType, personalityMap);
      }
      personalityMap.put("description",
          MessagesHelper.getMessage("profile.spectrum." + personalityType + ".description"));
      
      String personalityDimesion = MessagesHelper.getMessage("profile.personality.dimension.text")
          .concat(
              " " + MessagesHelper.getMessage("profile.personality." + personalityType + ".type"));
      personalityMap.put("dimesionType", personalityDimesion);
      personalityMap.put("videoUrl", MessagesHelper.getMessage("profile.video." + personalityType));
    }
    
    ProfileBalance profileBalances = new ProfileBalance();
    profileBalances.setSpectrumFilter(spectrumFilter);
    profileBalances.setSpectrumUserDTOs(specUsers);
    profileBalances.setPersonalityData(personalityDescrptionData);
    profileBalances.setCompanyId(companyId);
    return profileBalances;
  }
  
  /**
   * getUserPersonality method will return the personality of the user
   * 
   * @param user
   *          for which perosnality is to be retireved.
   * @return the Personality.
   */
  private PersonalityType getUserPersonality(User user, RangeType rangeType) {
    if (user.getAnalysis() == null) {
      LOG.warn("User :" + user.getEmail() + ", status is Valid, but analysis is not present.");
      return null;
    }
    HashMap<RangeType, PersonalityBeanResponse> personality = user.getAnalysis().getPersonality();
    PersonalityBeanResponse personalityBeanResponse = personality.get(rangeType);
    return personalityBeanResponse.getPersonalityType();
  }
  
  @CacheEvict(value = "profileBalance", allEntries = true)
  public void reset() {
    
  }
  
}