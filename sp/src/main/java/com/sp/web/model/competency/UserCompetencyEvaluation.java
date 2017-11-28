package com.sp.web.model.competency;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;

import org.springframework.util.Assert;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity class to store the users competency evaluation.
 */
public class UserCompetencyEvaluation {
  
  private String competencyProfileId;
  private UserCompetencyEvaluationScore manager;
  private BaseCompetencyEvaluationScore self;
  private PeerCompetencyEvaluationScore peers;
  //private List<UserCompetencyEvaluationScore> peers;
  
  /**
   * Default constructor.
   */
  public UserCompetencyEvaluation() {
  }
  
  /**
   * Constructor from competency evaluation.
   * 
   * @param competencyProfileId
   *          - competency profile id
   */
  public UserCompetencyEvaluation(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  public String getCompetencyProfileId() {
    return competencyProfileId;
  }
  
  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
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
  
  /**
   * Add the given competency evaluation score to the peer request list.
   * 
   * @param userCompetencyEvaluationScore
   *          - user competency evaluation score
   * @return 
   *    true if added else false
   */
  public boolean addPeer(UserCompetencyEvaluationScore userCompetencyEvaluationScore) {
    if (peers == null) {
      peers = PeerCompetencyEvaluationScore.newInstance();
    }
    return peers.add(userCompetencyEvaluationScore);
  }
  
  /**
   * Update the evaluation score.
   * 
   * @param type
   *          - evaluation type
   * @param reviewer
   *          - reviewer
   * @param evaluationDetails
   *          - score
   * @return
   *    true if the score was updated 
   */
  public boolean updateScore(EvaluationType type, User reviewer, UserCompetencyEvaluationDetails evaluationDetails) {
    boolean isUpdated = false;
    switch (type) {
    case Manager:
      Assert.notNull(manager, "Evaluation not initiated.");
      manager.updateFrom(evaluationDetails);
      isUpdated = true;
      break;
    case Peer:
      Assert.notNull(peers, "Evaluation not initiated.");
      isUpdated = peers.updateScore(reviewer, evaluationDetails);
      break;
    case Self:
      self = new BaseCompetencyEvaluationScore(evaluationDetails);
      isUpdated = true;
      break;
    default:
      break;
    }
    return isUpdated;
  }

  /**
   * Get the reviewer for the given evaluation.
   * 
   * @param evaluationType
   *            - evaluation type
   * @param reviewerId
   *            - reviewer id
   * @return
   *    the reviewer
   */
  public BaseUserDTO getReviewer(EvaluationType evaluationType, String reviewerId) {
    BaseUserDTO reviewer = null;
    switch (evaluationType) {
    case Manager:
      Assert.notNull(manager, "Manager not initiated.");
      reviewer = manager.getReviewer();
      break;
    case Peer:
      Assert.notNull(peers, "Peer evaluation not initiated.");
      reviewer = peers.getPeer(reviewerId);
      break;
    
    default:
      break;
    }
    return reviewer;
  }
}
