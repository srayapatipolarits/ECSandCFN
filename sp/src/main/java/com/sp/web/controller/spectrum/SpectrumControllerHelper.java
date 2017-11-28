package com.sp.web.controller.spectrum;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dto.HiringRoleUserCountDTO;
import com.sp.web.dto.PracticeAreaDetailsDTO;
import com.sp.web.dto.blueprint.BlueprintAnalytics;
import com.sp.web.dto.spectrum.TopPracticeAreaDTO;
import com.sp.web.dto.spectrum.competency.SpectrumCompetencyProfileEvaluationResultsListingDTO;
import com.sp.web.dto.usertracking.UserActivityTrackingDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.HiringUser;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.spectrum.AgeCategory;
import com.sp.web.model.spectrum.ErtiAnalytics;
import com.sp.web.model.spectrum.ErtiInsights;
import com.sp.web.model.spectrum.HiringFilterInsights;
import com.sp.web.model.spectrum.HiringFilterProfileBalance;
import com.sp.web.model.spectrum.LearnerStatus;
import com.sp.web.model.spectrum.OrganizationPlanActivities;
import com.sp.web.model.spectrum.ProfileBalance;
import com.sp.web.model.spectrum.SpectrumFilter;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;
import com.sp.web.model.usertracking.TopPracticeTracking;
import com.sp.web.model.usertracking.UserActivityTracking;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.tracking.UserActivityTrackingRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.spectrum.SpectrumFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * SpectrumConrollerHelper is a helper class for the spectrum controller.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SpectrumControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(SpectrumControllerHelper.class);
  
  @Autowired
  private SpectrumFactory spectrumFactory;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserActivityTrackingRepository trackingRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  private static final Comparator<SpectrumCompetencyProfileEvaluationResults> spectrumComparator = (
      SpectrumCompetencyProfileEvaluationResults o1, SpectrumCompetencyProfileEvaluationResults o2) -> o1
      .getName().compareTo(o2.getName());
  
  /**
   * <code>getProfileBalance</code> method will return the profile balance for the spectrum.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getProfileBalance(User user, Object[] params) {
    
    String userCompany = user.getCompanyId();
    
    Company company = companyFactory.getCompany(userCompany);
    SPResponse profileResponse = new SPResponse();
    
    if (company != null && company.getFeatureList().contains(SPFeature.Prism)) {
      ProfileBalance profileBalance = spectrumFactory.getProfileBalance(userCompany, false);
      
      LOG.debug("PRofile Balance returened is " + profileBalance);
      
      profileResponse.add("profileBalance", profileBalance);
      profileResponse.isSuccess();
    } else {
      LOG.debug("User " + user.getId() + " does not have access to Prism");
      profileResponse.addError(new SPException("User does not have access to Prism"));
      
    }
    
    return profileResponse;
  }
  
  /**
   * getHringFilterInsights method will return the hiring filter insigts for the comapny
   * 
   * @param user
   *          logged in user
   * @param params
   *          contains the paramters.
   * @return the SPResposne.
   */
  public SPResponse getHirngFilterInsights(User user, Object[] params) {
    
    String userCompanyId = user.getCompanyId();
    
    Company company = companyFactory.getCompany(user.getCompanyId());
    
    Account account = null;
    if (company != null) {
      account = accountRepository.findAccountByCompanyId(company.getId());
    } else {
      throw new InvalidRequestException("Company cannot be null");
    }
    
    // hiring role
    String role = (String) params[0];
    
    HiringFilterInsights hiringFilterInsights = null;
    SPResponse profileResponse = new SPResponse();
    
    // access to hiring insights is available only for Plan type IntelligentHiring
    if (account != null && account.getSpPlanMap().containsKey(SPPlanType.IntelligentHiring)) {
      hiringFilterInsights = spectrumFactory.getHiringFilterInsights(userCompanyId, role);
      LOG.debug("Hiring Filter insgits returened is " + hiringFilterInsights);
      profileResponse.add("hringFiterInsights", hiringFilterInsights);
      profileResponse.isSuccess();
      
    } else {
      
      LOG.debug("User " + user.getId() + " does not have access to Hiring Filter Insights");
      profileResponse.addError(new SPException(
          "User does not have access to Hiring Filter Insights"));
      
    }
    return profileResponse;
    
  }
  
  /**
   * getHringFilterProfileBalance method will return the hiring filter profilebalance for the
   * comapny
   * 
   * @param user
   *          logged in user
   * @param params
   *          contains the paramters.
   * @return the SPResposne.
   */
  public SPResponse getHirngFilterProfileBalance(User user, Object[] params) {
    
    String userCompanyId = user.getCompanyId();
    Company company = companyFactory.getCompany(user.getCompanyId());
    
    Account account = null;
    if (company != null) {
      account = accountRepository.findAccountByCompanyId(company.getId());
    } else {
      throw new InvalidRequestException("Company cannot be null");
    }
    
    String role = (String) params[0];
    
    SPResponse profileResponse = new SPResponse();
    HiringFilterProfileBalance hiringFilterProfileBalance = null;
    
    // access to hiring profile balance is available only for Plan type IntelligentHiring
    if (account != null && account.getSpPlanMap().containsKey(SPPlanType.IntelligentHiring)) {
      
      hiringFilterProfileBalance = spectrumFactory.getHiringProfileBalance(role, userCompanyId);
      
      LOG.debug("Hiring Filter profile balance returened is " + hiringFilterProfileBalance);
      
      profileResponse.add("hringFiterProfileBalance", hiringFilterProfileBalance);
      profileResponse.isSuccess();
    } else {
      LOG.debug("User " + user.getId() + " does not have access to Hiring Filter Profile Balance");
      profileResponse.addError(new SPException(
          "User does not have access to Hiring Filter Profile Balance"));
      
    }
    return profileResponse;
  }
  
  /**
   * getLearnerStatus method will return the learnign statistics of the user.
   * 
   * @param user
   *          logged in user
   * @param params
   *          contains the paramters.
   * @return the SPResposne.
   */
  public SPResponse getLearnerStatus(User user, Object[] params) {
    
    String groupName = (String) params[0];
    
    LearnerStatus learnerStatus = spectrumFactory.getLearnerStatusBalance(groupName,
        user.getCompanyId());
    
    LOG.debug("Learner Status balance returened is " + learnerStatus);
    Company company = companyFactory.getCompany(user.getCompanyId());
    if (company != null && company.getFeatureList().contains(SPFeature.PrismLens)) {
      learnerStatus.setPrismLensActive(true);
    }
    
    SPResponse learnerStatusResponse = new SPResponse();
    learnerStatusResponse.add("learnerStatus", learnerStatus);
    learnerStatusResponse.isSuccess();
    return learnerStatusResponse;
  }
  
  /**
   * getHiringRoles method will return all the hiring roles present in the system.
   * 
   * @param user
   *          logged in user
   * @param params
   *          contains the paramters.
   * @return the SPResposne.
   */
  public SPResponse getHiringRoles(User user, Object[] params) {
    
    SPResponse hringRoles = new SPResponse();
    
    List<User> allUsers = new ArrayList<User>();
    Collection<? extends HiringUser> allArchivedUsers = hiringRepository.getAllArchivedUsers(user
        .getCompanyId());
    allUsers.addAll(allArchivedUsers);
    
    List<HiringUser> allCandidatesUsers = hiringRepository.getAllUsers(user.getCompanyId());
    allUsers.addAll(allCandidatesUsers);
    
    SortedMap<String, HiringRoleUserCountDTO> hiringUsersDTOMap = new TreeMap<String, HiringRoleUserCountDTO>();
    SortedMap<String, HiringRoleUserCountDTO> hiringProfileBalanceRolesMap = new TreeMap<String, HiringRoleUserCountDTO>();
    HiringRoleUserCountDTO hiringUserDTO = new HiringRoleUserCountDTO();
    HiringRoleUserCountDTO hiringUserProfielBalacneDTO = new HiringRoleUserCountDTO();
    
    allUsers.stream().forEach(ru -> {
      Set<String> roles;
      if (ru instanceof HiringUser) {
        roles = ((HiringUser) ru).getHiringRoleIds();
      } else {
        roles = ((HiringUserArchive) ru).getHiringRoleIds();
      }
      roles.stream().forEach(rl -> {
        HiringRoleUserCountDTO hiringRoleUserCountDTO = hiringUsersDTOMap.get(rl);
        if (hiringRoleUserCountDTO == null) {
          hiringRoleUserCountDTO = new HiringRoleUserCountDTO();
          hiringUsersDTOMap.put(rl, hiringRoleUserCountDTO);
        }
        hiringRoleUserCountDTO.setName(rl);
        hiringRoleUserCountDTO.setTotalMembers(hiringRoleUserCountDTO.getTotalMembers() + 1);
        if (ru.getUserStatus() == UserStatus.VALID) {
          hiringRoleUserCountDTO.setValidMembers(hiringRoleUserCountDTO.getValidMembers() + 1);
        }
      });
      
      hiringUserDTO.setTotalMembers(hiringUserDTO.getTotalMembers() + 1);
      if (ru.getUserStatus() == UserStatus.VALID) {
        hiringUserDTO.setValidMembers(hiringUserDTO.getValidMembers() + 1);
      }
    });
    
    allCandidatesUsers
        .stream()
        .forEach(
            ru -> {
              Set<String> roles = ((HiringUser) ru).getHiringRoleIds();
              roles
                  .stream()
                  .forEach(
                      rl -> {
                        HiringRoleUserCountDTO hiringRoleUserCountDTO = hiringProfileBalanceRolesMap
                            .get(rl);
                        if (hiringRoleUserCountDTO == null) {
                          hiringRoleUserCountDTO = new HiringRoleUserCountDTO();
                          hiringProfileBalanceRolesMap.put(rl, hiringRoleUserCountDTO);
                        }
                        hiringRoleUserCountDTO.setName(rl);
                        hiringRoleUserCountDTO.setTotalMembers(hiringRoleUserCountDTO
                            .getTotalMembers() + 1);
                        if (ru.getUserStatus() == UserStatus.VALID) {
                          hiringRoleUserCountDTO.setValidMembers(hiringRoleUserCountDTO
                              .getValidMembers() + 1);
                        }
                      });
              
              hiringUserProfielBalacneDTO.setTotalMembers(hiringUserProfielBalacneDTO
                  .getTotalMembers() + 1);
              if (ru.getUserStatus() == UserStatus.VALID) {
                hiringUserProfielBalacneDTO.setValidMembers(hiringUserProfielBalacneDTO
                    .getValidMembers() + 1);
              }
            });
    
    hringRoles.add("filterInsightsRoles", hiringUsersDTOMap);
    hringRoles.add("filterInsightsRolesAllRoles", hiringUserDTO);
    
    hringRoles.add("hrProfileBalanceRoles", hiringProfileBalanceRolesMap);
    hringRoles.add("hrProfileBalanceRolesAllRoles", hiringUserProfielBalacneDTO);
    
    return hringRoles;
  }
  
  /**
   * <code>getBlueprintAnalytics</code> method will return the blueprint analytics of the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getBlueprintAnalytics(User user, Object[] params) {
    
    String userCompany = user.getCompanyId();
    
    Company company = companyFactory.getCompany(userCompany);
    SPResponse blueprintAnalyticsResponse = new SPResponse();
    
    if (company != null && company.getFeatureList().contains(SPFeature.Blueprint)) {
      BlueprintAnalytics blueprintAnalytics = spectrumFactory.getBlueprintAnalytics(userCompany);
      
      LOG.debug("Blueprint  returened is " + blueprintAnalytics);
      
      blueprintAnalyticsResponse.add("bluePrintAnalytics", blueprintAnalytics);
      blueprintAnalyticsResponse.isSuccess();
    } else {
      LOG.debug("User " + user.getId() + " does not have access to Blueprint");
      blueprintAnalyticsResponse
          .addError(new SPException("User does not have access to Blueprint"));
    }
    
    return blueprintAnalyticsResponse;
  }
  
  /**
   * <code>getErtiInsights</code> method will return the ERTI-Insights for the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getErtiInsights(User user, Object[] params) {
    
    String userCompany = user.getCompanyId();
    
    SPResponse ertiInsightsResponse = new SPResponse();
    
    ErtiInsights ertiInsights = spectrumFactory.getErtiInsights(userCompany);
    
    LOG.debug("ertiInsights  returened is " + ertiInsights);
    
    ertiInsightsResponse.add("ertiInsights", ertiInsights);
    ertiInsightsResponse.isSuccess();
    return ertiInsightsResponse;
  }
  
  /**
   * <code>getOrganizationPlanActivities</code> method will return organization plan activity for
   * the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getOrganizationPlanActivities(User user, Object[] params) {
    
    String userCompany = user.getCompanyId();
    
    SPResponse ertiInsightsResponse = new SPResponse();
    
    OrganizationPlanActivities ertiInsights = spectrumFactory
        .getOrganizationPlanActivities(userCompany);
    
    LOG.debug("getOrganizationPlanActivities  returened is " + ertiInsights);
    
    ertiInsightsResponse.add("organizationPlanActivities", ertiInsights);
    ertiInsightsResponse.isSuccess();
    return ertiInsightsResponse;
  }
  
  /**
   * <code>getErtiAnalytics</code> method will return the ERTI-Analytics for the company
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getErtiAnalytics(User user, Object[] params) {
    
    String userCompany = user.getCompanyId();
    
    SPResponse ertiInsightsResponse = new SPResponse();
    
    ErtiAnalytics ertiAnalytics = spectrumFactory.getErtiAnalytics(userCompany);
    
    LOG.debug("erti analytics  returened is " + ertiAnalytics);
    
    ertiInsightsResponse.add("ertiAnalytics", ertiAnalytics);
    ertiInsightsResponse.isSuccess();
    return ertiInsightsResponse;
  }
  
  /**
   * <code>getCompetencyInsights</code> method will return the competency analytics for the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse getCompetencyInsights(User user, Object[] params) {
    
    final SPResponse resp = new SPResponse();
    
    String competencyProfileId = (String) params[0];
    String companyId = user.getCompanyId();
    
    SpectrumCompetencyProfileEvaluationResults evaluationResult = null;
    if (StringUtils.isBlank(competencyProfileId)) {
      // getting all the spectrum results by competency profile for the given company
      List<SpectrumCompetencyProfileEvaluationResults> evaluationResults = competencyFactory
          .getAllSpectrumCompetencyProfileEvaluationResults(companyId);
      // sorting by competency profile name 
      evaluationResults.sort(spectrumComparator);
      resp.add(Constants.PARAM_SPECTRUM_COMPETENCY_PROFILE_LIST,
          evaluationResults.stream().map(SpectrumCompetencyProfileEvaluationResultsListingDTO::new)
              .collect(Collectors.toList()));
      if (!evaluationResults.isEmpty()) {
        evaluationResult = evaluationResults.get(0);
      }
    } else {
      evaluationResult = competencyFactory
          .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
    }
    
    resp.add(Constants.PARAM_SPECTRUM_COMPETENCY_PROFILE, evaluationResult);
    return resp;
  }
  
  /**
   * <code>getEngagementMatrix</code> method will return the engagement matrix of the user.
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the start date and end date.
   * @return the engagment matrix of a company. Default is 3 months.
   */
  public SPResponse getEngagementMatrix(User user, Object[] param) {
    
    final SPResponse response = new SPResponse();
    
    String startDateStr = (String) param[0];
    String endDateStr = (String) param[1];
    
    Set<String> allGroups = new HashSet<>();
    Set<String> allTags = new HashSet<>();
    LocalDate startDate = !StringUtils.isBlank(startDateStr) ? DateTimeUtil
        .getLocalDate(startDateStr) : LocalDate.now().minusMonths(3);
    LocalDate endDate = !StringUtils.isBlank(endDateStr) ? DateTimeUtil.getLocalDate(endDateStr)
        : LocalDate.now();
    List<UserActivityTracking> userActivity = trackingRepository.findUserActivityTracking(
        user.getCompanyId(), startDate, endDate);
    
    List<UserActivityTrackingDTO> collect = userActivity.stream().map(ua -> {
      User activityUser = userFactory.getUser(ua.getUserId());
      if (activityUser != null && !activityUser.isDeactivated()) {
        
        UserActivityTrackingDTO trackingDTO = new UserActivityTrackingDTO(activityUser, ua);
        trackingDTO.setGender(activityUser.getGender());
        List<String> groups = activityUser.getGroupAssociationList().stream().map(grp -> {
          String name = grp.getName();
          allGroups.add(name);
          return name;
        }).collect(Collectors.toList());
        trackingDTO.setGroups(groups);
        if (activityUser.getTagList() != null) {
          trackingDTO.setTags(activityUser.getTagList());
          allTags.addAll(activityUser.getTagList());
        }
        LocalDate dob = activityUser.getDob();
        LocalDate currentDate = LocalDate.now();
        if (dob != null) {
          int age = (int) dob.until(currentDate, ChronoUnit.YEARS);
          trackingDTO.setAge(age);
        }
        return trackingDTO;
      } else {
        return null;
      }
      
    }).filter(Objects::nonNull).collect(Collectors.toList());
    
    LocalDate localDate = LocalDate.now();
    
    List<TopPracticeTracking> findTopPracticeArea = trackingRepository.findTopPracticeAreaFromDate(
        user.getCompanyId(), localDate.minusMonths(12));
    
    List<TopPracticeAreaDTO> topDto = findTopPracticeArea.stream().map(top -> {
      
      TopPracticeAreaDTO areaDTO = new TopPracticeAreaDTO(top);
      top.getPracticeAreas().stream().forEach(prId -> {
        SPGoal goal = goalFactory.getGoal(prId, user.getUserLocale());
        PracticeAreaDetailsDTO baseGoalDto = new PracticeAreaDetailsDTO(goal);
        areaDTO.getPracticeAreasGoals().add(baseGoalDto);
      });
      return areaDTO;
    }).collect(Collectors.toList());
    
    SpectrumFilter spectrumFilter = new SpectrumFilter();
    spectrumFilter.getFilters().put(Constants.PARAM_GROUPS, allGroups);
    spectrumFilter.getFilters().put(Constants.PARAM_TAGS, allTags);
    spectrumFilter.getFilters().put(Constants.PARAM_AGE, AgeCategory.values());
    
    response.add("engagmentMatrixData", collect);
    response.add("startDate", startDate);
    response.add("endDate", endDate);
    response.add("filter", spectrumFilter);
    response.add("topPraticeArea", topDto);
    
    return response;
  }
}
