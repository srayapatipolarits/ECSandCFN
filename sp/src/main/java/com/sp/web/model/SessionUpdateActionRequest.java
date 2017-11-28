package com.sp.web.model;

import com.sp.web.service.session.UserUpdateAction;

import java.io.Serializable;
import java.util.Map;


/**
 * Model to store id and its type(company / user). 
 * This is send to JMS Topic to expire session related to id
 * 
 * @author vikram.
 */
public class SessionUpdateActionRequest implements Serializable {

  private static final long serialVersionUID = 4786872151828030924L;
  
  private String userId ;
  private UserUpdateAction action  ;
  private Map<String,Object> params;
  private String companyId ;
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public UserUpdateAction getAction() {
    return action;
  }
  
  public void setAction(UserUpdateAction action) {
    this.action = action;
  }
  
  public Map<String,Object> getParams() {
    return params;
  }
  
  public void setParams(Map<String,Object> params) {
    this.params = params;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  
  
}
