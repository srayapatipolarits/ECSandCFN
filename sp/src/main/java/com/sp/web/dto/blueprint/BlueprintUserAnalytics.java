package com.sp.web.dto.blueprint;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;

import java.util.List;

/**
 * BlueprintUserAnalytics class with hold the analytics for the blueprint of a user.
 * 
 * @author pradeepruhil
 *
 */
public class BlueprintUserAnalytics extends BaseUserDTO {
  
  private static final long serialVersionUID = 8774144175079372143L;
  
  private long totalKpi;
  
  private long completedKpi;
  
  private List<String> groups;
  
  private List<String> tags;

  /**
   * @param usr
   */
  public BlueprintUserAnalytics(User usr) {
   super(usr);
  }

  public long getTotalKpi() {
    return totalKpi;
  }
  
  public void setTotalKpi(long totalKpi) {
    this.totalKpi = totalKpi;
  }
  
  public long getCompletedKpi() {
    return completedKpi;
  }
  
  public void setCompletedKpi(long completedKpi) {
    this.completedKpi = completedKpi;
  }

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  
}
