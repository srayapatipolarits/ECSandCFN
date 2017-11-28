package com.sp.web.model.spectrum.competency;

import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.model.competency.RatingConfiguration;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the competency profile summary.
 */
public class SpectrumCompetencyProfileSummaryDTO implements Serializable {
  
  private static final long serialVersionUID = 1155554638357651104L;
  private String id;
  private String name;
  private List<SpectrumCompetencySummaryDTO> competencyList;
  private RatingConfiguration ratingConfiguration;
  
  /**
   * Default Constructor.
   */
  public SpectrumCompetencyProfileSummaryDTO() {
  }
  
  /**
   * Constructor.
   * 
   * @param competencyProfile
   *          - competency profile
   */
  public SpectrumCompetencyProfileSummaryDTO(CompetencyProfileSummaryDTO competencyProfile) {
    this.id = competencyProfile.getId();
    this.name = competencyProfile.getName();
    this.ratingConfiguration = competencyProfile.getRatingConfiguration();
    competencyList = competencyProfile.getCompetencyList().stream()
        .map(SpectrumCompetencySummaryDTO::new).collect(Collectors.toList());
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
  
  public List<SpectrumCompetencySummaryDTO> getCompetencyList() {
    return competencyList;
  }
  
  public void setCompetencyList(List<SpectrumCompetencySummaryDTO> competencyList) {
    this.competencyList = competencyList;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SpectrumCompetencyProfileSummaryDTO other = (SpectrumCompetencyProfileSummaryDTO) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }
  
  public RatingConfiguration getRatingConfiguration() {
    return ratingConfiguration;
  }
  
  public void setRatingConfiguration(RatingConfiguration ratingConfiguration) {
    this.ratingConfiguration = ratingConfiguration;
  }
}
