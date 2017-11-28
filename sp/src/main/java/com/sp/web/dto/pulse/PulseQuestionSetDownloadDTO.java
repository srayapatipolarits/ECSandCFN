package com.sp.web.dto.pulse;

import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The pulse question set DTO.
 */
public class PulseQuestionSetDownloadDTO {
  
  private List<String> categoryKeys;
  private Map<String, List<PulseQuestionBean>> questions;
  
  /**
   * Constructor.
   * 
   * @param pulseQuestionSet
   *            - the pulse question set
   */
  public PulseQuestionSetDownloadDTO(PulseQuestionSet pulseQuestionSet) {
    BeanUtils.copyProperties(pulseQuestionSet, this);
  }

  public List<String> getCategoryKeys() {
    return categoryKeys;
  }

  public void setCategoryKeys(List<String> categoryKeys) {
    this.categoryKeys = categoryKeys;
  }

  public Map<String, List<PulseQuestionBean>> getQuestions() {
    return questions;
  }

  public void setQuestions(Map<String, List<PulseQuestionBean>> questions) {
    this.questions = questions;
  }
}
