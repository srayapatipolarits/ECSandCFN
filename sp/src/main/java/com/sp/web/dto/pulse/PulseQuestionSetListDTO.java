package com.sp.web.dto.pulse;

import com.sp.web.model.pulse.PulseQuestionSet;

import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO for sending the listing for the pulse questions.
 */
public class PulseQuestionSetListDTO {
  
  private String id;
  private String name;
  private List<String> categoryKeys;
  
  /**
   * Constructor.
   * 
   * @param pulseQuestionSet
   *            - the pulse question set
   */
  public PulseQuestionSetListDTO(PulseQuestionSet pulseQuestionSet) {
    BeanUtils.copyProperties(pulseQuestionSet, this);
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
  
  /**
   * @param categoryKeys the categoryKeys to set
   */
  public void setCategoryKeys(List<String> categoryKeys) {
    this.categoryKeys = categoryKeys;
  }
  
  /**
   * @return the categoryKeys
   */
  public List<String> getCategoryKeys() {
    return categoryKeys;
  }
}
