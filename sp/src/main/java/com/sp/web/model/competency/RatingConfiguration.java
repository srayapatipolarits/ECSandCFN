package com.sp.web.model.competency;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The model to store the rating configuration for the competency profile.
 */
public class RatingConfiguration implements Serializable {
  
  private static final long serialVersionUID = -7944661259879983045L;
  private RatingConfigurationType type;
  private int size;
  private Map<String, Object> configParams;
  
  
  /**
   * Default Constructor. 
   */
  public RatingConfiguration() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param configurationType
   *              - configuration type
   * @param size
   *              - size
   */
  public RatingConfiguration(RatingConfigurationType configurationType, int size) {
    this.type = configurationType;
    this.size = size;
  }

  public RatingConfigurationType getType() {
    return type;
  }
  
  public void setType(RatingConfigurationType type) {
    this.type = type;
  }
  
  public Map<String, Object> getConfigParams() {
    return configParams;
  }
  
  public void setConfigParams(Map<String, Object> configParams) {
    this.configParams = configParams;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
