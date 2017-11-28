package com.sp.web.form.marketing;

import java.io.Serializable;

public class SPMarketingForm implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 229585370109232530L;
  
  private String id;
  
  private String userId;
  
  private String version;

  public String getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
  
}
