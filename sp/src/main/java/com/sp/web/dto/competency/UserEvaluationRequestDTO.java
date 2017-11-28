package com.sp.web.dto.competency;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetencyEvaluation;

/**
 * @author Dax Abraham
 * 
 *         The evaluation request DTO.
 */
public class UserEvaluationRequestDTO {
  
  private BaseUserDTO user;
  private EvaluationType type;
  private String competencyProfileId;
  private PeerCompetencyEvaluationScore peers;
  
  /**
   * Constructor from evaluation result. This is assumed for evaluation type manager.
   *
   * @param user
   *          - user
   * @param evaluation
   *          - evaluation
   *  
   */
  public UserEvaluationRequestDTO(User user, UserCompetencyEvaluation evaluation) {
    this.user = new BaseUserDTO(user);
    this.type = EvaluationType.Manager;
    if (evaluation.getPeers() != null) {
      this.setPeers(evaluation.getPeers());
    }
  }

  /**
   * Constructor from user.
   * 
   * @param user
   *          - user
   */
  public UserEvaluationRequestDTO(User user) {
    this.user = new BaseUserDTO(user);
    this.type = EvaluationType.Peer;
  }

  public BaseUserDTO getUser() {
    return user;
  }
  
  public void setUser(BaseUserDTO user) {
    this.user = user;
  }
  
  public EvaluationType getType() {
    return type;
  }
  
  public void setType(EvaluationType type) {
    this.type = type;
  }
  
  public PeerCompetencyEvaluationScore getPeers() {
    return peers;
  }

  public void setPeers(PeerCompetencyEvaluationScore peers) {
    this.peers = peers;
  }

  public String getCompetencyProfileId() {
    return competencyProfileId;
  }

  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
}
