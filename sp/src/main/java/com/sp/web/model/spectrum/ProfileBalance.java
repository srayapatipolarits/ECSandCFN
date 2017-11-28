package com.sp.web.model.spectrum;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.dto.spectrum.SpectrumUserDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProfileBalance model for holding the profile balance.
 * 
 * @author pradeepruhil
 *
 */
public class ProfileBalance implements Serializable {
  
  /**
   * defualt serial version id.
   */
  private static final long serialVersionUID = 3559847901095717128L;
  
  private List<SpectrumUserDTO> spectrumUserDTOs;
  
  private SpectrumFilter spectrumFilter;
  
  private String companyId;
  
  private Map<PersonalityType, Map<String, String>> personalityData;
  
  /** Spectrum User DTO list */
  public List<SpectrumUserDTO> getSpectrumUserDTOs() {
    if (spectrumUserDTOs == null) {
      spectrumUserDTOs = new ArrayList<>();
    }
    return spectrumUserDTOs;
  }
  
  public void setSpectrumUserDTOs(List<SpectrumUserDTO> spectrumUserDTOs) {
    this.spectrumUserDTOs = spectrumUserDTOs;
  }
  
  public SpectrumFilter getSpectrumFilter() {
    return spectrumFilter;
  }
  
  public void setSpectrumFilter(SpectrumFilter spectrumFilter) {
    this.spectrumFilter = spectrumFilter;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setPersonalityData(Map<PersonalityType, Map<String, String>> personalityData) {
    this.personalityData = personalityData;
  }
  
  public Map<PersonalityType, Map<String, String>> getPersonalityData() {
    return personalityData;
  }
}
