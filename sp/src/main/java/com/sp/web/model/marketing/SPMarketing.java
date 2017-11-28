package com.sp.web.model.marketing;

import java.io.Serializable;

public class SPMarketing implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 128208740086166518L;
  
  private String id;
  
  private String userId;
  
  private String version;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
  
}
