package com.sp.web.model.competency;

import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The model to store all the details for a competency evaluation.
 */
public class CompetencyEvaluation implements Serializable {
  
  private static final long serialVersionUID = 3702027709431257861L;
  private String id;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime endedOn;
  private String companyId;
  private List<EvaluationType> requiredEvaluationList;
  @Deprecated
  private Map<String, CompetencyEvaluationByProfile> evaluationMap;
  private Map<String, CompetencyProfileSummaryDTO> competencyProfileMap;
  private boolean completed;
  private Set<String> userIds;
  
  /**
   * Creates a new instance of the competency evaluation along with initialization of the various
   * fields.
   * 
   * @param companyId
   *          -company id
   * @param requiredEvaluationList
   *          - the required evaluation list         
   * @return the new competency evaluation
   */
  public static final CompetencyEvaluation newInstance(String companyId, List<EvaluationType> requiredEvaluationList) {
    CompetencyEvaluation competencyEvaluation = new CompetencyEvaluation();
    competencyEvaluation.setCompanyId(companyId);
    competencyEvaluation.setStartDate(LocalDateTime.now());
    competencyEvaluation.setRequiredEvaluationList(requiredEvaluationList);
    competencyEvaluation.setUserIds(new HashSet<String>());
    competencyEvaluation
        .setCompetencyProfileMap(new HashMap<String, CompetencyProfileSummaryDTO>());
    return competencyEvaluation;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }
  
  public LocalDateTime getEndDate() {
    return endDate;
  }
  
  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public List<EvaluationType> getRequiredEvaluationList() {
    return requiredEvaluationList;
  }
  
  public void setRequiredEvaluationList(List<EvaluationType> requiredEvaluationList) {
    this.requiredEvaluationList = requiredEvaluationList;
  }
  
  /**
   * @return - get the evaluation map create a blank one if none is present.
   * 
   * @deprecated user getCompetencyProfileMap to get the competency profiles and use the 
   *             userCompetency to retrieve the competency request details
   */
  @Deprecated
  public Map<String, CompetencyEvaluationByProfile> getEvaluationMap() {
    if (evaluationMap == null) {
      evaluationMap = new HashMap<String, CompetencyEvaluationByProfile>();
    }
    return evaluationMap;
  }
  
  public void setEvaluationMap(Map<String, CompetencyEvaluationByProfile> evaluationMap) {
    this.evaluationMap = evaluationMap;
  }
  
  public boolean isCompleted() {
    return completed;
  }
  
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
  
  public LocalDateTime getEndedOn() {
    return endedOn;
  }
  
  public void setEndedOn(LocalDateTime endedOn) {
    this.endedOn = endedOn;
  }
  
  /**
   * Checks if the evaluation type is supported for the evaluation.
   * 
   * @param evaluationType
   *          - evaluation type
   * @return true if supported else false
   */
  public boolean isSupported(EvaluationType evaluationType) {
    return requiredEvaluationList != null && requiredEvaluationList.contains(evaluationType);
  }
  
  /**
   * Get the competency evaluation by profile for the given competency profile id.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @return the competency profile by id
   */
//  public CompetencyEvaluationByProfile findCompetencyEvaluationByProfile(String competencyProfileId) {
//    final CompetencyEvaluationByProfile competencyEvaluationByProfile = getEvaluationMap().get(
//        competencyProfileId);
//    Assert.notNull(competencyEvaluationByProfile, "Competency evaluation profile not found.");
//    return competencyEvaluationByProfile;
//  }
  
  /**
   * Get the competency evaluation by profile if not found create one.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @param competencyFactory
   *          - competency factory
   * @return the competency evaluation profile
   */
//  public CompetencyEvaluationByProfile findOrCreateCompetencyEvaluationByProfile(
//      String competencyProfileId, CompetencyFactory competencyFactory) {
//    CompetencyEvaluationByProfile competencyEvaluationByProfile = getEvaluationMap().get(
//        competencyProfileId);
//    if (competencyEvaluationByProfile == null) {
//      competencyEvaluationByProfile = new CompetencyEvaluationByProfile(competencyProfileId,
//          competencyFactory);
//      evaluationMap.put(competencyProfileId, competencyEvaluationByProfile);
//      competencyFactory.update(this);
//    }
//    return competencyEvaluationByProfile;
//  }
  
  /**
   * Create a new user competency result for the given user.
   * 
   * @param user
   *          - user
   */
  public void addUser(User user) {
    userIds.add(user.getId());
  }
  
  /**
   * Add the competency evaluation profile for the given competency profile.
   * 
   * @param competencyProfile
   *          - competency profile
   */
  public void addCompetencyProfile(CompetencyProfileDao competencyProfile) {
    competencyProfileMap.computeIfAbsent(competencyProfile.getId(),
        k -> new CompetencyProfileSummaryDTO(competencyProfile));
  }
  
  /**
   * Get the user evaluation result for the given user evaluation request.
   * 
   * @param evaluationRequest
   *          - evaluation request
   * @return the user evaluation result
   */
//  public UserEvaluationResult findUserEvaluationResult(UserEvaluationRequest evaluationRequest) {
//    return findCompetencyEvaluationByProfile(evaluationRequest.getCompetencyProfileId())
//        .findUserEvaluationResult(evaluationRequest.getUserId());
//  }
  
  public Map<String, CompetencyProfileSummaryDTO> getCompetencyProfileMap() {
    return competencyProfileMap;
  }
  
  public void setCompetencyProfileMap(Map<String, CompetencyProfileSummaryDTO> competencyProfileMap) {
    this.competencyProfileMap = competencyProfileMap;
  }
  
  public Set<String> getUserIds() {
    return userIds;
  }
  
  public void setUserIds(Set<String> userIds) {
    this.userIds = userIds;
  }

  public boolean hasUser(String uid) {
    return userIds.contains(uid);
  }

  public boolean removeUser(String uid) {
    return userIds.remove(uid);
  }

  public CompetencyProfileSummaryDTO getCompetencyProfile(String competencyProfileId) {
    return competencyProfileMap.get(competencyProfileId);
  }

  public boolean isUserPartOfEvaluation(String userId) {
    return userIds.contains(userId);
  }
}
