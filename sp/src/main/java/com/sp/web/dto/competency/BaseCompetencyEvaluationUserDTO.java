package com.sp.web.dto.competency;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for capturing the competency details for the user.
 */
public class BaseCompetencyEvaluationUserDTO extends UserMarkerDTO implements Serializable {
  
  private static final long serialVersionUID = -2967850501446033758L;
  private String competencyProfileId;
  private List<String> groupIds;
  
  public BaseCompetencyEvaluationUserDTO(User user) {
    super(user);
  }

  public String getCompetencyProfileId() {
    return competencyProfileId;
  }
  
  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  public List<String> getGroupIds() {
    return groupIds;
  }
  
  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }

  /**
   * Add the group to the user.
   * 
   * @param ga
   *          - group association
   */
  public void addGroup(GroupAssociation ga) {
    if (groupIds == null) {
      groupIds = new ArrayList<String>();
    }
    groupIds.add(ga.getGroupId());
  }
  
}
