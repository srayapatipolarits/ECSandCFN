package com.sp.web.model;

import java.io.Serializable;


/**
 * Model to store id and its type(company / user). 
 * This is send to JMS Topic to expire session related to id
 * 
 * @author vikram.
 */
public class InvalidateSessionRequest implements Serializable {

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 4540256385817490088L;
  
  private String id ;
  private String type  ;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

}
