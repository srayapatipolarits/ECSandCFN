package com.sp.web.model.spectrum.competency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.model.User;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity to store the competency evaluation summary for a competency profile.
 */
public class SpectrumCompetencyEvaluationSummary implements Serializable {
  
  private static final long serialVersionUID = -8782364409856098342L;
  private LocalDateTime startDate;
  private LocalDateTime endedOn;
  private int memberCount;
  private List<EvaluationType> requiredEvaluationList;
  private Map<EvaluationType, SpectrumCompetencyEvaluationScore> averageScores;
  private SpectrumCompetencyProfileSummaryDTO competencyProfile;
  private String competencyEvaluationId;
  @JsonIgnore
  private Set<String> userIds;
  
  /**
   * Default Constructor.
   */
  public SpectrumCompetencyEvaluationSummary() {
  }
  
  /**
   * Constructor.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   * @param competencyProfile
   *          - competency profile
   */
  public SpectrumCompetencyEvaluationSummary(CompetencyEvaluation competencyEvaluation,
      CompetencyProfileSummaryDTO competencyProfile) {
    BeanUtils.copyProperties(competencyEvaluation, this);
    averageScores = new HashMap<EvaluationType, SpectrumCompetencyEvaluationScore>();
    this.setCompetencyProfile(new SpectrumCompetencyProfileSummaryDTO(competencyProfile));
    this.competencyEvaluationId = competencyEvaluation.getId();
    this.userIds = new HashSet<String>();
  }
  
  public List<EvaluationType> getRequiredEvaluationList() {
    return requiredEvaluationList;
  }
  
  public void setRequiredEvaluationList(List<EvaluationType> requiredEvaluationList) {
    this.requiredEvaluationList = requiredEvaluationList;
  }
  
  public Map<EvaluationType, SpectrumCompetencyEvaluationScore> getAverageScores() {
    return averageScores;
  }
  
  public void setAverageScores(Map<EvaluationType, SpectrumCompetencyEvaluationScore> averageScores) {
    this.averageScores = averageScores;
  }
  
  public String getCompetencyEvaluationId() {
    return competencyEvaluationId;
  }
  
  public void setCompetencyEvaluationId(String competencyEvaluationId) {
    this.competencyEvaluationId = competencyEvaluationId;
  }
  
  public SpectrumCompetencyProfileSummaryDTO getCompetencyProfile() {
    return competencyProfile;
  }
  
  public void setCompetencyProfile(SpectrumCompetencyProfileSummaryDTO competencyProfile) {
    this.competencyProfile = competencyProfile;
  }
  
  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }
  
  public LocalDateTime getEndedOn() {
    return endedOn;
  }
  
  public void setEndedOn(LocalDateTime endedOn) {
    this.endedOn = endedOn;
  }
  
  /**
   * Method to update the user evaluation data to the average.
   * 
   * @param evaluation
   *          - evaluation to add
   * @param competencyFactory
   *          - reference to the competency factory
   * @param user
   *          - user whose evaluation is being added 
   */
  public void addEvaluation(UserCompetencyEvaluation evaluation,
      CompetencyFactory competencyFactory, User user) {
    add(user);
    for (EvaluationType type : requiredEvaluationList) {
      SpectrumCompetencyEvaluationScore score = getScoreToUpdate(type);
      switch (type) {
      case Manager:
        score.add(getEvaluationDetails(evaluation.getManager(), competencyFactory));
        break;
      case Self:
        score.add(getEvaluationDetails(evaluation.getSelf(), competencyFactory));
        break;
      case Peer:
        Optional.ofNullable(evaluation.getPeers()).ifPresent(
            peers -> peers.getPeers().forEach(
                peer -> score.add(getEvaluationDetails(peer, competencyFactory))));
        break;
      default:
        break;
      }
    }
  }
  
  private void add(User user) {
    userIds.add(user.getId());
    memberCount++;
  }

  /**
   * Perform the averaging of the scores.
   */
  public void doAverage() {
    requiredEvaluationList.stream().map(averageScores::get).filter(Objects::nonNull)
        .forEach(SpectrumCompetencyEvaluationScore::doAverage);
  }
  
  private UserCompetencyEvaluationDetails getEvaluationDetails(BaseCompetencyEvaluationScore score,
      CompetencyFactory competencyFactory) {
    UserCompetencyEvaluationDetails competencyEvaluationDetails = null;
    if (score != null) {
      competencyEvaluationDetails = competencyFactory.getCompetencyEvaluationDetails(score
          .getCompetencyEvaluationScoreDetailsId());
    }
    return competencyEvaluationDetails;
  }
  
  private SpectrumCompetencyEvaluationScore getScoreToUpdate(EvaluationType type) {
    return averageScores.computeIfAbsent(type, k -> new SpectrumCompetencyEvaluationScore());
  }
  
  public Set<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<String> userIds) {
    this.userIds = userIds;
  }

  public int getMemberCount() {
    return memberCount;
  }

  public void setMemberCount(int memberCount) {
    this.memberCount = memberCount;
  }
}
