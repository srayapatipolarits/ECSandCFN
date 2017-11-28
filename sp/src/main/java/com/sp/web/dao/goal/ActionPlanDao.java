package com.sp.web.dao.goal;

import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The DAO class for storing the action plan along with the practice area associations.
 */
@Document(collection = "actionPlan")
public class ActionPlanDao extends ActionPlan {
  
  private static final long serialVersionUID = 7128168633381033449L;
  @Transient
  private List<SPGoal> practiceAreaList;
  @Transient
  private boolean readOnly;

  /**
   * Default Constructor.
   */
  public ActionPlanDao() { }
  
  /**
   * Constructor.
   * 
   * @param actionPlan
   *          - action plan
   */
  public ActionPlanDao(ActionPlan actionPlan) {
    BeanUtils.copyProperties(actionPlan, this);
  }

  public List<SPGoal> getPracticeAreaList() {
    return practiceAreaList;
  }
  
  public void setPracticeAreaList(List<SPGoal> practiceAreaList) {
    this.practiceAreaList = practiceAreaList;
  }

  /**
   * Validate if the UID is present in the given practice area.
   * 
   * @param stepId
   *          - step id
   * @param uid
   *          - action id
   * @return
   *    true if present else false
   */
  public boolean validateUID(String stepId, String uid) {
    Optional<SPGoal> findFirst = practiceAreaList.stream().filter(g -> g.getId().equals(stepId)).findFirst();
    if (findFirst.isPresent()) {
      return findFirst.get().validateUID(uid);
    }
    return false;
  }

  /**
   * Method to update the action count.
   */
  public void updateActionCount() {
    if (!CollectionUtils.isEmpty(practiceAreaList)) {
      setActionCount(practiceAreaList.stream().mapToInt(SPGoal::calculateActionCount).sum());
    }
  }

  /**
   * Getting all the deactivated UID's.
   * 
   * @return
   *      the list of deactivated uid's
   */
  public List<String> getAllDeactivatedUids() {
    final List<String> uidList = new ArrayList<String>();
    if (!CollectionUtils.isEmpty(practiceAreaList)) {
      practiceAreaList.forEach(pal -> pal.updateDeactivatedUids(uidList));
    }
    return uidList;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
}
