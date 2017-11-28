package com.sp.web.dto.hiring.group;

import com.sp.web.model.hiring.group.HiringGroup;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The base DTO for the people analytics groups.
 */
public class HiringGroupBaseDTO implements Serializable {
  
  private static final long serialVersionUID = -3845242913449459019L;
  private String id;
  private String name;
  
  /**
   * Constructor.
   * 
   * @param group
   *          - group
   */
  public HiringGroupBaseDTO(HiringGroup group) {
    BeanUtils.copyProperties(group, this);
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
}
