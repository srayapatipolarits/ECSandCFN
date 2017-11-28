package com.sp.web.dto.competency;

import com.sp.web.dto.UserGroupDTO;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.competency.CompetencyEvaluation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for returning the competency evaluation data.
 */
public class CompetencyEvaluationDTO extends BaseCompetencyEvaluationDTO implements Serializable {
  
  private static final long serialVersionUID = -5546239361828518763L;
  
  private Map<String, CompetencyProfileSummaryDTO> competencyProfileMap;
  private Map<String, UserGroupDTO> userGroupMap;
  private boolean completed;
  private List<CompetencyEvaluationUserDTO> users;
  
  /**
   * Constructor for the competency evaluation DTO.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   */
  public CompetencyEvaluationDTO(CompetencyEvaluation competencyEvaluation) {
    super(competencyEvaluation);
    userGroupMap = new HashMap<String, UserGroupDTO>();
  }
  
  public Map<String, CompetencyProfileSummaryDTO> getCompetencyProfileMap() {
    return competencyProfileMap;
  }
  
  public void setCompetencyProfileMap(Map<String, CompetencyProfileSummaryDTO> competencyProfileMap) {
    this.competencyProfileMap = competencyProfileMap;
  }
  
  public Map<String, UserGroupDTO> getUserGroupMap() {
    return userGroupMap;
  }
  
  public void setUserGroupMap(Map<String, UserGroupDTO> userGroupMap) {
    this.userGroupMap = userGroupMap;
  }
  
  public boolean isCompleted() {
    return completed;
  }
  
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
  
  public List<CompetencyEvaluationUserDTO> getUsers() {
    return users;
  }
  
  public void setUsers(List<CompetencyEvaluationUserDTO> users) {
    this.users = users;
  }
  
  public void addGroup(GroupAssociation ga) {
    userGroupMap.computeIfAbsent(ga.getGroupId(), g -> new UserGroupDTO(ga));
  }
}
