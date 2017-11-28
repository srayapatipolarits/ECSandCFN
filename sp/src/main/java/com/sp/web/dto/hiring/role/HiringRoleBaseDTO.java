package com.sp.web.dto.hiring.role;

import com.sp.web.model.hiring.role.HiringRole;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class for the base hiring role information.
 */
public class HiringRoleBaseDTO implements Serializable {
  
  private static final long serialVersionUID = -5886013750776548604L;
  private String id;
  private String name;
  
  /**
   * Constructor.
   * 
   * @param role
   *          - role
   */
  public HiringRoleBaseDTO(HiringRole role) {
    BeanUtils.copyProperties(role, this);
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
