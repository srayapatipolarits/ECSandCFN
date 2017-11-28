package com.sp.web.spectrum;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.exception.SPException;
import com.sp.web.model.Company;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.spectrum.HiringFilterInsights;
import com.sp.web.model.spectrum.HiringFilterProfileBalance;
import com.sp.web.model.spectrum.HiringInsights;
import com.sp.web.model.spectrum.PersonalityBalance;
import com.sp.web.model.spectrum.TimeFilter;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.spectrum.SpectrumRepository;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HiringInsightsGenerator class will generate the hiring insights data.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class HiringInsightsGenerator {
  
  /** Initializng the logger. */
  private static final Logger LOG = Logger.getLogger(HiringInsightsGenerator.class);
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private SpectrumRepository spectrumRepository;
  
  @Autowired
  private Environment environment;
  
  /**
   * getHiringFilterInsights method will return the hiring filter insights for the users passed.
   * 
   * @param hiringUsers
   *          hiring users.
   * @return the HiringFilter isnightes.
   */
  public HiringFilterInsights getHiringFilterInsights(List<User> hiringUsers) {
    
    Map<TimeFilter, HiringInsights> hiringInsigthsMap = new LinkedHashMap<TimeFilter, HiringInsights>();
    populateHiringInsihts(hiringInsigthsMap);
    
    for (User user : hiringUsers) {
      
      /* fetch the date of the user */
      /*
       * in case of arhvie user, user the archive on date as the activity of time is captured in
       * that fiedl
       */
      LocalDate userCreatedOnDate;
      if (user.getClass().isAssignableFrom(HiringUserArchive.class)
          && user instanceof HiringUserArchive) {
        userCreatedOnDate = ((HiringUserArchive) user).getArchivedOn();
      } else {
        userCreatedOnDate = DateTimeUtil.getLocalDateFromMongoId(user.getId());
      }
      
      TimeFilter filterForUser = DateTimeUtil.getTimeFilterDate(userCreatedOnDate);
      
      List<HiringInsights> populateHiringInsihts = getHiringIinsightsListToUpdate(hiringInsigthsMap, filterForUser);
      
      populateHiringInsihts.forEach((v) -> {
          v.setTotalCandidates(v.getTotalCandidates() + 1);
          
          if (user.getClass().isAssignableFrom(HiringUser.class)) {
            
            /* check user assessment is pending or not */
            if (user.getUserStatus() != UserStatus.VALID) {
              v.setAssesmentIncomplete(v.getAssesmentIncomplete() + 1);
            } else {
              v.setAssessmentCompleted(v.getAssessmentCompleted() + 1);
            }
          } else if (user.getClass().isAssignableFrom(HiringUserArchive.class)) {
            if (user.getUserStatus() == UserStatus.HIRED) {
              v.setHired(v.getHired() + 1);
          } else {
            v.setAchived(v.getAchived() + 1);
          }
          }
        });
        
    }
    
    HiringFilterInsights hiringFilterInsights = new HiringFilterInsights();
    hiringFilterInsights.setHiringInsightsMap(hiringInsigthsMap);
    return hiringFilterInsights;
    
  }
  
  private List<HiringInsights> getHiringIinsightsListToUpdate(
      Map<TimeFilter, HiringInsights> hiringInsightsMap, TimeFilter timeFilter) {
    List<HiringInsights> hiringInsights = new ArrayList<HiringInsights>();
    switch (timeFilter) {
    case DAY:
      hiringInsights.add(hiringInsightsMap.get(TimeFilter.DAY));
    case WEEK:
      hiringInsights.add(hiringInsightsMap.get(TimeFilter.WEEK));
    case MONTH:
      hiringInsights.add(hiringInsightsMap.get(TimeFilter.MONTH));
    case YEAR:
      hiringInsights.add(hiringInsightsMap.get(TimeFilter.YEAR));
    default:
      break;
    }
    return hiringInsights;
    
  }
  
  /**
   * create the hiring insights objects for the filters.
   * @param hiringInsigthsMap map.
   */
  private void populateHiringInsihts(Map<TimeFilter, HiringInsights> hiringInsigthsMap){
    createHiringInsights(hiringInsigthsMap, TimeFilter.DAY);
    createHiringInsights(hiringInsigthsMap, TimeFilter.WEEK);
    createHiringInsights(hiringInsigthsMap, TimeFilter.MONTH);
    createHiringInsights(hiringInsigthsMap, TimeFilter.YEAR);
  }
  
  private void createHiringInsights(Map<TimeFilter, HiringInsights> hiringInsigthsMap,
      TimeFilter timeFilter) {
    HiringInsights hiringInsights = hiringInsigthsMap.get(timeFilter);
    if (hiringInsights == null) {
      hiringInsights = new HiringInsights();
      hiringInsigthsMap.put(timeFilter, hiringInsights);
    }
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  public void loadHiringFilterInsights() {
    
    /* find all the company present in the SUrepeople plateform. */
    List<Company> findAllCompanies = accountRepository.findAllCompanies();
    findAllCompanies.stream().forEach(comp -> {
        try {
          populateLoadHiringFilterInsights(comp.getId());
        } catch (Exception ex) {
          LOG.info("No users present for the company, Skiiping it " + comp.getName());
        }
        
      });
  }
  
  /**
   * <code>populateLoadProfileBalance</code> method will populate the profile balaance for the
   * companyID.
   * 
   * @param companyId
   *          for which profile balance is to be retreived.
   * @return the profile balance.
   */
  public HiringFilterInsights populateLoadHiringFilterInsights(String companyId) {
    
    /* fetch the list fo users belong to this company */
    List<User> hiringUsers = new ArrayList<User>();
    
    List<? extends User> allUsers = hiringRepository.getAllUsers(companyId);
    hiringUsers.addAll(allUsers);
    Collection<? extends HiringUser> allArchivedUsers = hiringRepository
        .getAllArchivedUsers(companyId);
    hiringUsers.addAll(allArchivedUsers);
    
    HiringFilterInsights hiringFilterInsights = spectrumRepository
        .getHiringFilterInsights(companyId);
    HiringFilterInsights generateHiringFilterInsights = getHiringFilterInsights(hiringUsers);
    if (hiringFilterInsights == null) {
      generateHiringFilterInsights.setCompanyId(companyId);
      hiringFilterInsights = generateHiringFilterInsights;
    } else {
      hiringFilterInsights.setCompanyId(companyId);
      hiringFilterInsights
          .setHiringInsightsMap(generateHiringFilterInsights.getHiringInsightsMap());
    }
    spectrumRepository.saveHiringFilterInsights(hiringFilterInsights);
    return hiringFilterInsights;
  }
  
  /**
   * <code>getHiringFilterProfileBalance</code> method will return the hiirng filter proiflebalance.
   * 
   * @param hiringUsers
   *          list of hiring user.
   * @return the hiring filter profile balance.
   */
  public HiringFilterProfileBalance getHiringFilterProfileBalance(List<HiringUser> hiringUsers) {
    /* filter the users who have completed there assessment */
    
    if (CollectionUtils.isEmpty(hiringUsers)) {
      throw new SPException("No hiring users present to show Profile Balance");
    }
    
    /* filter out the users who have not completed there assesemnts */
    List<User> validUsers = hiringUsers.stream().filter(usr -> usr.getUserStatus() == UserStatus.VALID)
        .collect(Collectors.toList());
    
    int totalPersonality = validUsers.size();
    
    /* Create a map of users against the personality types */
    
    Map<PersonalityType, List<User>> personalityUsersMap = new HashMap<PersonalityType, List<User>>();
    
    /* Iterate through the users */
    for (User usr : validUsers) {
      
      /* find profile balances */
      PersonalityType userPersonality = getUserPersonality(usr);
      List<User> persUser = personalityUsersMap.get(userPersonality);
      if (persUser == null) {
        persUser = new ArrayList<User>();
        personalityUsersMap.put(userPersonality, persUser);
      }
      persUser.add(usr);
    }
    
    HiringFilterProfileBalance profileBalances = getHiringFilterProfileBalances(
        personalityUsersMap, totalPersonality);
    
    /* Add blank date for missing personalityTypes */
    List<PersonalityBalance> personalityBalances = profileBalances.getPersonalityBalances();
    if (CollectionUtils.isEmpty(personalityBalances)) {
      profileBalances.setPersonalityBalanceAllZero(true);
    }
    for (PersonalityType personalityType : PersonalityType.values()) {
      boolean profielBalancePresent = false;
      if (personalityType == PersonalityType.Tight || personalityType == PersonalityType.Undershift
          || personalityType == PersonalityType.Overshift
          || personalityType == PersonalityType.Invalid){
        continue;
      }
      for (PersonalityBalance personalityBalance : personalityBalances) {
        if (personalityBalance.getPersonalityType() == personalityType) {
          profielBalancePresent = true;
          break;
        }
      }
      if (!profielBalancePresent) {
        PersonalityBalance personalityBalance = new PersonalityBalance();
        personalityBalance.setPersonalityType(personalityType);
        personalityBalance.setPersonalityDescription(MessagesHelper.getMessage("profile.spectrum."
            + personalityType + ".description"));
        String personalityDimesion = MessagesHelper.getMessage("profile.personality.dimension.text")
            .concat(" " + MessagesHelper.getMessage("profile.personality." + personalityType + ".type"));
        personalityBalance.setDimensionType(personalityDimesion);
        personalityBalance.setVideoUrl(MessagesHelper
            .getMessage("profile.video." + personalityType));
        personalityBalances.add(personalityBalance);
      }
      
    }
    return profileBalances;
  }
  
  /**
   * getUserPersonality method will return the personality of the user
   * 
   * @param user
   *          for which perosnality is to be retireved.
   * @return the Personality.
   */
  private PersonalityType getUserPersonality(User user) {
    HashMap<RangeType, PersonalityBeanResponse> personality = user.getAnalysis().getPersonality();
    PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
    return personalityBeanResponse.getPersonalityType();
  }
  
  /**
   * getHiringFilterProfileBalances method will calculate the profileBalance and will return the
   * result
   * 
   * @param persMap
   *          persMap will return the personaltiymap
   * @param totalUsers
   *          total user.
   * @return the profile balance.
   */
  private HiringFilterProfileBalance getHiringFilterProfileBalances(
      Map<PersonalityType, List<User>> persMap, int totalUsers) {
    
    HiringFilterProfileBalance profileBalance = new HiringFilterProfileBalance();
    
    /* set the perosnality balances */
    List<PersonalityBalance> personalityBalances = new ArrayList<PersonalityBalance>();
    profileBalance.setPersonalityBalances(personalityBalances);
    persMap.forEach((key, v) -> {
        PersonalityBalance personalityBalance = new PersonalityBalance();
        personalityBalance.setPersonalityType(key);
        personalityBalance.setNumber(v.size());
        personalityBalance.setUsersId(v.stream().map(urs -> {
            BaseUserDTO baseUserDTo = new BaseUserDTO(urs);
            baseUserDTo.setTitle((StringUtils.join(((HiringUser)urs).getHiringRoleIds(), ",")));
            return baseUserDTo;
          })
            .collect(Collectors.toList()));
        personalityBalance.setPercent(calculatePercentage(totalUsers, v.size()));
      personalityBalance.setPersonalityDescription(MessagesHelper.getMessage("profile.spectrum."
          + key + ".description"));
        String personalityDimesion = MessagesHelper.getMessage("profile.personality.dimension.text")
            .concat(" " + MessagesHelper.getMessage("profile.personality." + key + ".type"));
        personalityBalance.setDimensionType(personalityDimesion);
        personalityBalance.setVideoUrl(MessagesHelper.getMessage("profile.video." +key));
        personalityBalances.add(personalityBalance);
      });
    LOG.debug("HiringFilterProfileBalance  Balance returned is " + profileBalance);
    Comparator<PersonalityBalance> personalityBalance = (obj1, obj2) -> ((Integer) obj2.getNumber())
        .compareTo(obj1.getNumber());
    personalityBalances.sort(personalityBalance);
    return profileBalance;
    
  }
  
  private BigDecimal calculatePercentage(int baseValue, int perc) {
    return new BigDecimal(((perc * 100) / baseValue)).setScale(Constants.PULSE_PRECISION,
        Constants.ROUNDING_MODE);
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  public void loadHiringFilterProfileBalances() {
    
    /* find all the company present in the SUrepeople plateform. */
    List<Company> findAllCompanies = accountRepository.findAllCompanies();
    findAllCompanies.stream().forEach(comp -> {
        try {
          populateLoadHFProfileBalances(comp.getId());
        } catch (Exception ex) {
          LOG.info("No users present for the company, Skiiping it " + comp.getName());
        }
        
      });
  }
  
  /**
   * <code>populateLoadHFProfileBalances</code> method will populate the profile balaance for the
   * companyID.
   * 
   * @param companyId
   *          for which profile balance is to be retreived.
   * @return the profile balance.
   */
  public HiringFilterProfileBalance populateLoadHFProfileBalances(String companyId) {
    
    /* fetch the list fo users belong to this company */
    
    List<HiringUser> allUsers = hiringRepository.getAllUsers(companyId);


    HiringFilterProfileBalance profileBalance = spectrumRepository
        .getHiringFilterProfileBalance(companyId);
    if (!allUsers.isEmpty()) {
      HiringFilterProfileBalance generateProfileBalances = getHiringFilterProfileBalance(allUsers);
      if (profileBalance == null) {
        generateProfileBalances.setCompanyId(companyId);
        profileBalance = generateProfileBalances;
      } else {
        profileBalance.setPersonalityBalances(generateProfileBalances.getPersonalityBalances());
        profileBalance.setPersonalityBalanceAllZero(generateProfileBalances
            .isPersonalityBalanceAllZero());
      }
    } else {
      if(profileBalance != null){
        profileBalance.setPersonalityBalances(null);
        profileBalance.setPersonalityBalanceAllZero(true);  
      }
    }
    
    if(profileBalance != null){
      spectrumRepository.saveHiringFilterProfileBalances(profileBalance);  
    }
    return profileBalance;
  }
}