package com.sp.web.dto.hiring.dashboard;

import java.io.Serializable;

/**
 * EmployeeSummarDTO captures the employee summary count for the people analytics user.
 * 
 * @author pradeepruhil
 *
 */
public class EmployeeSummaryDTO implements Serializable {
  
  private static final long serialVersionUID = -4617568953939950247L;
  
  private long totalUsers;
  
  private int prismCompleted;
  
  private int prismInProgress;
  
  private int prismPending;
  
  private int profileIncomplete;
  
  public long getTotalUsers() {
    return totalUsers;
  }
  
  public void setTotalUsers(long totalUsers) {
    this.totalUsers = totalUsers;
  }
  
  public int getPrismCompleted() {
    return prismCompleted;
  }
  
  public void setPrismCompleted(int prismCompleted) {
    this.prismCompleted = prismCompleted;
  }
  
  public int getPrismInProgress() {
    return prismInProgress;
  }
  
  public void setPrismInProgress(int prismInProgress) {
    this.prismInProgress = prismInProgress;
  }
  
  public int getPrismPending() {
    return prismPending;
  }
  
  public void setPrismPending(int prismPending) {
    this.prismPending = prismPending;
  }
  
  public int getProfileIncomplete() {
    return profileIncomplete;
  }
  
  public void setProfileIncomplete(int profileIncomplete) {
    this.profileIncomplete = profileIncomplete;
  }
  
  public void increaseTotalUsers() {
    totalUsers += 1;
  }
  
  public void increasePrismCompleted() {
    prismCompleted += 1;
  }
  
  public void increasePrismInProgress() {
    prismInProgress += 1;
  }
  
  public void increasePrismPending() {
    prismPending += 1;
  }
  
  public void increaseProfileIncomplete() {
    profileIncomplete += 1;
  }
  
}
