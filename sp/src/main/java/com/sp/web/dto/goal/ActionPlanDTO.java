package com.sp.web.dto.goal;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.PracticeAreaActionDetailsDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The DTO class for Action Plan.
 */
public class ActionPlanDTO extends BaseActionPlanDTO {
  
  private List<PracticeAreaActionDetailsDTO> practiceAreaList;
  private boolean editAllowed;
  
  /**
   * Constructor from action plan dao.
   * 
   * @param actionPlan
   *          - action plan dao
   */
  public ActionPlanDTO(ActionPlanDao actionPlan) {
    super(actionPlan);
    practiceAreaList = actionPlan.getPracticeAreaList().stream()
        .collect(Collectors.mapping(PracticeAreaActionDetailsDTO::new, Collectors.toList()));
  }
  
  public List<PracticeAreaActionDetailsDTO> getPracticeAreaList() {
    return practiceAreaList;
  }
  
  public void setPracticeAreaList(List<PracticeAreaActionDetailsDTO> practiceAreaList) {
    this.practiceAreaList = practiceAreaList;
  }
  
  public boolean isEditAllowed() {
    return editAllowed;
  }

  public void setEditAllowed(boolean editAllowed) {
    this.editAllowed = editAllowed;
  }
}
