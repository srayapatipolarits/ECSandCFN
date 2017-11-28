package com.sp.web.model;

import java.io.Serializable;

/**
 * Master Password for all users.
 * 
 * @author pradeepruhil
 *
 */
public class MasterPassword implements Serializable {
  
  private static final long serialVersionUID = 7794205369749171574L;
  
  private String id;

  private String password;
  
  private String oldPassword;
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getOldPassword() {
    return oldPassword;
  }
  
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
