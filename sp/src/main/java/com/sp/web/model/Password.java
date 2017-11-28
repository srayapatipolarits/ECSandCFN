package com.sp.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Password class holds the information for the user password, previous password, attempts, expiry
 * time.
 * 
 * @author pradeepruhil
 *
 */
public class Password implements Serializable {
  
  private static final long serialVersionUID = -6015704276126262606L;

  private List<String> previousPassword;
  
  private String password;
  
  private LocalDateTime lastUpdated;
  
  private int failedAttempts;
  
  private boolean accountLocked;
  
  private LocalDateTime lastChanged;
  
  /**
   * Gets the list of previous passwords or else an empty array list.
   * 
   * @return
   *    previous passwords list
   */
  public List<String> getPreviousPassword() {
    if (previousPassword == null) {
      previousPassword = new ArrayList<String>();
    }
    return previousPassword;
  }
  
  public void setPreviousPassword(List<String> previousPassword) {
    this.previousPassword = previousPassword;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }
  
  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
  
  public int getFailedAttempts() {
    return failedAttempts;
  }
  
  public void setFailedAttempts(int failedAttempts) {
    this.failedAttempts = failedAttempts;
  }
  
  /**
   * Sets the new password.
   * 
   * @param oldPassword
   *          - old password
   * @param newPassword
   *          - new password
   * @return
   *    the password entity
   */
  public static Password newPassword(String oldPassword, String newPassword) {
    Password password = new Password();
    password.getPreviousPassword().add(newPassword);
    password.setPassword(newPassword);
    password.setLastUpdated(LocalDateTime.now());
    password.setLastChanged(LocalDateTime.now());
    return password;
  }
  
  /**
   * Update the password.
   * 
   * @param newPassword
   *            - password to update
   */
  public void updatePassword(String newPassword) {
    this.getPreviousPassword().add(newPassword);
    if (this.getPreviousPassword().size() >= 5) {
      this.getPreviousPassword().remove(0);
    }
    
    this.setPassword(newPassword);
    this.setLastUpdated(LocalDateTime.now());
    this.setLastChanged(LocalDateTime.now());
    this.failedAttempts = 0;
    setAccountLocked(false);
  }
  
  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }
  
  public boolean isAccountLocked() {
    return accountLocked;
  }
  
  /**
   * Reset all the settings.
   * @return 
   *    true if reset was required else false
   */
  public boolean resetSettings() {
    if ( failedAttempts > 0 || accountLocked) {
      setFailedAttempts(0);
      setAccountLocked(false);
      setLastUpdated(LocalDateTime.now());
      return true;
    }
    return false;
  }
  
  public void setLastChanged(LocalDateTime lastChanged) {
    this.lastChanged = lastChanged;
  }
  
  /**
   * Gets the last changed date time.
   * 
   * @return
   *      - gets the last changed date and time
   */
  public LocalDateTime getLastChanged() {
    if (lastChanged == null) {
      lastChanged = LocalDateTime.now();
    }
    return lastChanged;
  }

  public void incrementFailedAttempts() {
    failedAttempts++;
  }
}
