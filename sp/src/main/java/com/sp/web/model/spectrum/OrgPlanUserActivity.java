package com.sp.web.model.spectrum;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * OrgPlanUserActivity hold the user activity for the action plan, how much activity he has
 * completed.
 * 
 * @author pradeepruhil
 *
 */
public class OrgPlanUserActivity extends BaseUserDTO {
  
  private static final long serialVersionUID = 681795129515541093L;
  
  private int completedLKpi;
  
  private Set<String> completedUserSteps;
  
  private List<String> groups;
  
  /**
   * Default constructor initiazling the base user dto with the user.
   */
  public OrgPlanUserActivity(User user) {
    super(user);
  }
  
  public void setCompletedLKpi(int completedLKpi) {
    this.completedLKpi = completedLKpi;
  }
  
  public int getCompletedLKpi() {
    return completedLKpi;
  }
  
  public void setCompletedUserSteps(Set<String> completedUserSteps) {
    this.completedUserSteps = completedUserSteps;
  }
  
  public Set<String> getCompletedUserSteps() {
    return completedUserSteps;
  }
  
  public void setGroups(List<String> groups) {
    this.groups = groups;
  }
  
  /**
   * Groups for the user.
   * 
   * @return the user groups.
   */
  public List<String> getGroups() {
    if (groups == null) {
      groups = new ArrayList<>();
    }
    return groups;
  }
  
}
