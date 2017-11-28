package com.sp.web.spectrum;

import com.sp.web.dto.blueprint.BlueprintAnalytics;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.spectrum.ErtiAnalytics;
import com.sp.web.model.spectrum.ErtiInsights;
import com.sp.web.model.spectrum.HiringFilterInsights;
import com.sp.web.model.spectrum.HiringFilterProfileBalance;
import com.sp.web.model.spectrum.LearnerStatus;
import com.sp.web.model.spectrum.OrganizationPlanActivities;
import com.sp.web.model.spectrum.ProfileBalance;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.spectrum.SpectrumRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpectrumFactory will act a factory for giving the spectrum data for different modules.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SpectrumFactory {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(SpectrumFactory.class);
  
  @Autowired
  private SpectrumRepository spectrumRepository;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private ProfileGenerator profileGenerator;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private HiringInsightsGenerator hiringInsightsGenerator;
  
  @Autowired
  private LearnerStatusGenerator learnerStatusGenerator;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private BlueprintAnalyticsGenerator blueprintAnalyticsGenerator;
  
  @Autowired
  private ErtiInsightsGenerator ertiInsightsGenerator;
  
  @Autowired
  private OrganizationPlanActivitiesGenerator organizationActiviesGenerator;
  
  @Autowired
  private ErtiAnalyticsGenerator analyticsGenerator;
  
  /**
   * <code>getProfileBalance</code> method will return the profileBalance to be shown to the user.
   * 
   * @param paModule
   *          - groupid for which the profile stats to be returned.
   * @return the profile balance.
   */
  public ProfileBalance getProfileBalance(String companyId, boolean paModule) {
    
    /*
     * return all the users profile balance, so we directly fetching from the mogno
     */
    
    List<User> validUsers = getValidUsers(companyId);
    return profileGenerator.generateProfileBalances(companyId, validUsers, paModule);
  }
  
  /**
   * <code>getHiringFilterInsigts</code> method will return the hiringFilter insights for a company
   * 
   * @param companyId
   *          for which hiring user to be retreived.
   * @param role
   *          for which hiring users stats to be calulcated.
   * @return the HiringFilter insights.
   */
  public HiringFilterInsights getHiringFilterInsights(String companyId, String role) {
    
    List<User> hiringUsers = new ArrayList<User>();
    /* check if any role is passed to the user or not */
    if (StringUtils.isEmpty(role)) {
      /* return the hiring filter insits for all the roles of the company */
      
      HiringFilterInsights hiringFilterInsights = getCompanyHiringFilterInsights(companyId);
      if (hiringFilterInsights == null) {
        LOG.debug("Hiring Filter insightsis not loaded in the DB for the companyID" + companyId
            + ", Loading ti now ");
        
        hiringFilterInsights = hiringInsightsGenerator.populateLoadHiringFilterInsights(companyId);
        return hiringFilterInsights;
      }
      LOG.debug("Returning hiring filter insits from the repository ");
      return hiringFilterInsights;
    }
    
    /* Get the user which belong to this from archive and hiring users */
    
    hiringUsers.addAll(hiringRepository.findByRole(companyId, role));
    hiringUsers.addAll(hiringRepository.getAllArchivedUsersForRole(companyId, role));
    return hiringInsightsGenerator.getHiringFilterInsights(hiringUsers);
  }
  
  /**
   * <code>getProfileBalance</code> method will return the profileBalance to be shown to the user.
   * 
   * @param groupName
   *          groupid for which the profile stats to be returned.
   * @return the profile balance.
   */
  public ProfileBalance getHiringProfileBalance(String companyId, List<HiringUser> users,
      boolean paModule) {
    return profileGenerator.generateHiringProfileBalances(companyId, users, paModule);
  }
  
  /**
   * <code>getProfileBalance</code> method will return the profileBalance to be shown to the user.
   * 
   * @param roleName
   *          roleName of the user.
   * @param companyId
   *          of the usre to which he belongs.
   * @return the profile balance.
   */
  public HiringFilterProfileBalance getHiringProfileBalance(String roleName, String companyId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("role " + roleName + ", companyId" + companyId);
    }
    
    /*
     * check if role is null, then we need to return the all the hring users profile balance, so we
     * directly fetching from the mogno
     */
    HiringFilterProfileBalance profileBalance = null;
    if (StringUtils.isEmpty(roleName)) {
      profileBalance = getAllCompanyHiringUsersProfileBalance(companyId);
      if (profileBalance == null) {
        LOG.debug("Hring Profile Balance is not loaded in the DB for the companyID" + companyId
            + ", Loading ti now ");
        
        profileBalance = hiringInsightsGenerator.populateLoadHFProfileBalances(companyId);
      }
      
    } else {
      // profileBalance = getRoleProfileBalance(roleName, companyId);
    }
    return profileBalance;
  }
  
  private HiringFilterInsights getCompanyHiringFilterInsights(String companyId) {
    return spectrumRepository.getHiringFilterInsights(companyId);
  }
  
  private HiringFilterProfileBalance getAllCompanyHiringUsersProfileBalance(String companyId) {
    return spectrumRepository.getHiringFilterProfileBalance(companyId);
  }
  
  // /**
  // * getGroupProfileBalance method will generate the profile balance for the groups
  // *
  // * @param role
  // * for which profile is to be retreived.
  // * @return the PRofileBalancne for the requested group.
  // */
  // private HiringFilterProfileBalance getRoleProfileBalance(String role, String companyId) {
  //
  // /* Get all the users for a group for the user */
  //
  // /* fetch the list fo users belong to this company */
  //
  // List<HiringUser> allUsers = hiringRepository.getAllUsersForRole(companyId, role);
  //
  // if (CollectionUtils.isEmpty(allUsers)) {
  // LOG.debug("No members present for the role ");
  //
  // HiringFilterProfileBalance profileBalance = new HiringFilterProfileBalance();
  // profileBalance.setPersonalityBalanceAllZero(true);
  // for (PersonalityType personalityType : PersonalityType.values()) {
  // PersonalityBalance personalityBalance = new PersonalityBalance();
  // personalityBalance.setPersonalityType(personalityType);
  // personalityBalance.setPersonalityType(personalityType);
  // personalityBalance.setPersonalityDescription(MessagesHelper.getMessage("profile.spectrum."
  // + personalityType + ".description"));
  // String personalityDimesion = MessagesHelper
  // .getMessage("profile.personality.dimension.text")
  // .concat(
  // " " + MessagesHelper.getMessage("profile.personality." + personalityType + ".type"));
  // personalityBalance.setDimensionType(personalityDimesion);
  // personalityBalance.setVideoUrl(MessagesHelper
  // .getMessage("profile.video." + personalityType));
  // profileBalance.getPersonalityBalances().add(personalityBalance);
  // }
  //
  // return profileBalance;
  // }
  // return hiringInsightsGenerator.getHiringFilterProfileBalance(allUsers);
  //
  // }
  
  /**
   * <code>getLearnerStatusBalance</code> method will return the learnerStatus to be shown to the
   * user.
   * 
   * @param groupName
   *          groupid for which the profile stats to be returned.
   * @param companyId
   *          of the usre to which he belongs.
   * @return the profile balance.
   */
  public LearnerStatus getLearnerStatusBalance(String groupName, String companyId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("groupName " + groupName + ", companyId" + companyId);
    }
    
    /*
     * check if groupId is null, then we need to return the all learning library status of all the
     * users of a company directly fetching from the mogno
     */
    LearnerStatus learnerStatus;
    if (StringUtils.isEmpty(groupName)) {
      learnerStatus = getAllCompanyLearnerStatusBalance(companyId);
      if (learnerStatus == null) {
        LOG.debug("Learner Status Balance is not loaded in the DB for the companyID" + companyId
            + ", Loading ti now ");
        
        learnerStatus = learnerStatusGenerator.populateLearnerStatus(companyId);
      }
      
    } else {
      learnerStatus = getGroupLearnerBalance(groupName, companyId);
    }
    return learnerStatus;
  }
  
  private LearnerStatus getAllCompanyLearnerStatusBalance(String companyId) {
    return spectrumRepository.getLearnerStatusBalance(companyId);
  }
  
  /**
   * getGroupProfileBalance method will generate the profile balance for the groups
   * 
   * @param groupName
   *          for which profile is to be retreived.
   * @return the PRofileBalancne for the requested group.
   */
  private LearnerStatus getGroupLearnerBalance(String groupName, String companyId) {
    
    /* Get all the users for a group for the user */
    
    List<User> members = groupRepository.getMembers(companyId, groupName);
    if (CollectionUtils.isEmpty(members)) {
      LOG.debug("No members present for the group, Returnign blank data. ");
      return new LearnerStatus();
      
    }
    List<User> validUsers = members.stream().filter(usr -> usr.getUserStatus() == UserStatus.VALID)
        .collect(Collectors.toList());
    return learnerStatusGenerator.generateLearnerStatus(validUsers, companyId);
    
  }
  
  /**
   * @param companyId
   *          for which blueprint analytics is to be returned.
   * @return blue print analytics.
   */
  public BlueprintAnalytics getBlueprintAnalytics(String companyId) {
    List<User> validUsers = getValidUsers(companyId);
    return blueprintAnalyticsGenerator.getBlueprintAnalytics(companyId, validUsers);
  }
  
  /**
   * getValidusers method return the valid users of the company.
   * 
   * @param companyId
   *          for which users are to be found.
   * @return the valid users of the company.
   */
  private List<User> getValidUsers(String companyId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("companyId" + companyId);
    }
    
    /*
     * return all the blueprint analytics for all the users.
     */
    
    List<User> validUsers = userRepository.findValidUsers(companyId);
    return validUsers;
  }
  
  /**
   * getErtiInsights will return the erti insights of the company.
   * 
   * @param companyId
   *          of the company.
   * @return the erti insghts.
   */
  public ErtiInsights getErtiInsights(String companyId) {
    List<User> validUsers = getValidUsers(companyId);
    return ertiInsightsGenerator.getErtiInsights(companyId, validUsers, false);
  }
  
  /**
   * getErtiInsights will return the erti insights of the company.
   * 
   * @param companyId
   *          of the company.
   * @return the erti insghts.
   */
  public ErtiInsights getHiringErtiInsights(String companyId, List<HiringUser> validUsers) {
    return ertiInsightsGenerator.getHiringErtiInsights(companyId, validUsers, true);
  }
  
  /**
   * getErtiInsights will return the erti insights of the company.
   * 
   * @param companyId
   *          of the company.
   * @return the erti insghts.
   */
  public OrganizationPlanActivities getOrganizationPlanActivities(String companyId) {
    List<User> validUsers = getValidUsers(companyId);
    return organizationActiviesGenerator.getOrganizationPlanActivities(companyId, validUsers);
  }
  
  /**
   * getErtiAnalytics will return the erti analytics of the company.
   * 
   * @param companyId
   *          of the company.
   * @return the erti insghts.
   */
  public ErtiAnalytics getErtiAnalytics(String companyId) {
    List<User> validUsers = getValidUsers(companyId);
    return analyticsGenerator.getErtiAnalytics(companyId, validUsers, false);
  }
  
  public ErtiAnalytics getHiringErtiAnalytics(String companyId, List<HiringUser> all) {
    return analyticsGenerator.getHiringErtiAnalytics(companyId, all, true);
  }
  
}
