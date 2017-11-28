package com.sp.web.form.pulse;

import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.QuestionSetType;

import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The form bean for the create new pulse.
 */
public class CreatePulseForm {
  
  @NotBlank
  private String name;
  private QuestionSetType questionSetType;
  private PulseQuestionSetStatus status;
  private List<String> companyId;
  private boolean isForAll;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public QuestionSetType getQuestionSetType() {
    return questionSetType;
  }
  
  public void setQuestionSetType(QuestionSetType questionSetType) {
    this.questionSetType = questionSetType;
  }
  
  public PulseQuestionSetStatus getStatus() {
    return status;
  }
  
  public void setStatus(PulseQuestionSetStatus status) {
    this.status = status;
  }
  
  public List<String> getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(List<String> companyId) {
    this.companyId = companyId;
  }

  /**
   * Create a new pulse question set from the given data in the form.
   * 
   * @return
   *      the newly created pulse question set
   */
  public PulseQuestionSet generatePulseQuestionSet() {
    LocalDate createdOn = LocalDate.now();

    PulseQuestionSet pulseQuestionSet = new PulseQuestionSet();
    pulseQuestionSet.setName(name);
    pulseQuestionSet.setCreatedOn(createdOn);
    pulseQuestionSet.setUpdatedOn(createdOn);
    pulseQuestionSet.setQuestionSetType(getQuestionSetType());
    pulseQuestionSet.setStatus(getStatus());
    pulseQuestionSet.setCompanyId(getCompanyId());
    
    if (isForAll) {
      pulseQuestionSet.setForAll(true);
      pulseQuestionSet.setCompanyId(null);
    } else {
      pulseQuestionSet.setForAll(false);
    }
    
    return pulseQuestionSet;
  }

  public boolean isForAll() {
    return isForAll;
  }

  public void setForAll(boolean isForAll) {
    this.isForAll = isForAll;
  }
  
}
