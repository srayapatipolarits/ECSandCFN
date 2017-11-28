package com.sp.web.dto.hiring.match;

import com.sp.web.model.hiring.match.HiringPortrait;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for hiring portraits.
 */
public class HiringPortraitBaseDTO implements Serializable {
  
  private static final long serialVersionUID = -1797570701606815156L;
  private String id;
  private String name;
  
  /**
   * Constructor.
   * 
   * @param portriat
   *          - portrait
   */
  public HiringPortraitBaseDTO(HiringPortrait portrait) {
    BeanUtils.copyProperties(portrait, this);
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
