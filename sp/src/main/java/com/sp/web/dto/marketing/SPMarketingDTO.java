package com.sp.web.dto.marketing;

import com.sp.web.model.marketing.SPMarketing;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

public class SPMarketingDTO implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = 5035677525063792766L;

  private String id;
  
  private String version;
  
  /**
   * Default constructor.
   * 
   * @param spMarketing
   *          - the SPMarketing object
   */
  public SPMarketingDTO(SPMarketing spMarketing) {
    BeanUtils.copyProperties(spMarketing, this);
  }

  public String getId() {
    return id;
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
