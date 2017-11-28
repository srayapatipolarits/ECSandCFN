package com.sp.web.dto.competency;

import com.sp.web.model.User;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for capturing the competency details for the user.
 */
public class CompetencyEvaluationUserDTO extends BaseCompetencyEvaluationUserDTO implements
    Serializable {
  
  private static final long serialVersionUID = 2973430051501789755L;
  private UserCompetencyEvaluationScore manager;
  private BaseCompetencyEvaluationScore self;
  private PeerCompetencyEvaluationScore peers;
  
  public CompetencyEvaluationUserDTO(User user, UserCompetencyEvaluation userEvaluation) {
    super(user);
    BeanUtils.copyProperties(userEvaluation, this);
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
  
}
