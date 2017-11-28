package com.sp.web.dto.hiring.group;

import com.sp.web.model.hiring.group.HiringGroup;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the people analytics listing DTO.
 */
public class HiringGroupListingDTO extends HiringGroupBaseDTO {
  
  private static final long serialVersionUID = -7268813441530990480L;
  private int count;
  
  /**
   * Constructor.
   * 
   * @param group
   *          - group
   */
  public HiringGroupListingDTO(HiringGroup group) {
    super(group);
    this.count = group.getUserIds().size();
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
}
