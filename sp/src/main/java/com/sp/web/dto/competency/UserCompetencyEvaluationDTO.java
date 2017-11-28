package com.sp.web.dto.competency;

import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class to store the users competency evaluation details.
 */
public class UserCompetencyEvaluationDTO implements Serializable {
  
  private static final long serialVersionUID = 7706469591982142980L;
  private LocalDateTime startDate;
  private CompetencyProfileSummaryDTO competencyProfile;
  private List<EvaluationType> requiredEvaluationList;
  private UserCompetencyEvaluationScore manager;
  private BaseCompetencyEvaluationScore self;
  private PeerCompetencyEvaluationScore peers;
  
  /**
   * Constructor.
   * 
   * @param competencyEvaluation
   *              - competency evaluation
   * @param competencyProfile
   *              - competency profile
   * @param userCompetencyEvaluation
   *              - user competency evaluation
   */
  public UserCompetencyEvaluationDTO(CompetencyEvaluation competencyEvaluation,
      CompetencyProfileSummaryDTO competencyProfile,
      UserCompetencyEvaluation userCompetencyEvaluation) {
    this.startDate = competencyEvaluation.getStartDate();
    this.requiredEvaluationList = competencyEvaluation.getRequiredEvaluationList();
    this.manager = userCompetencyEvaluation.getManager();
    this.self = userCompetencyEvaluation.getSelf();
    this.peers = userCompetencyEvaluation.getPeers();
    if (!(manager == null && self == null && peers == null)) {
      this.competencyProfile = competencyProfile;
    }
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }
  
  public CompetencyProfileSummaryDTO getCompetencyProfile() {
    return competencyProfile;
  }
  
  public void setCompetencyProfile(CompetencyProfileSummaryDTO competencyProfile) {
    this.competencyProfile = competencyProfile;
  }
  
  public UserCompetencyEvaluationScore getManager() {
    return manager;
  }
  
  public void setManager(UserCompetencyEvaluationScore manager) {
    this.manager = manager;
  }
  
  public BaseCompetencyEvaluationScore getSelf() {
    return self;
  }
  
  public void setSelf(BaseCompetencyEvaluationScore self) {
    this.self = self;
  }
  
  public PeerCompetencyEvaluationScore getPeers() {
    return peers;
  }
  
  public void setPeers(PeerCompetencyEvaluationScore peers) {
    this.peers = peers;
  }

  public List<EvaluationType> getRequiredEvaluationList() {
    return requiredEvaluationList;
  }

  public void setRequiredEvaluationList(List<EvaluationType> requiredEvaluationList) {
    this.requiredEvaluationList = requiredEvaluationList;
  }
  
}
