package com.sp.web.dto.competency;

import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.goal.SPGoal;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the competency profile summary.
 */
public class CompetencyProfileSummaryDTO implements Serializable {

  private static final long serialVersionUID = 1155554638357651104L;
  private String id;
  private String name;
  private List<CompetencySummaryDTO> competencyList;
  private RatingConfiguration ratingConfiguration;

  /**
   * Default Constructor. 
   */
  public CompetencyProfileSummaryDTO() { }
  
  /**
   * Constructor from competency profile.
   * 
   * @param competencyProfile
   *            - competency profile
   */
  public CompetencyProfileSummaryDTO(CompetencyProfileDao competencyProfile) {
    this.id = competencyProfile.getId();
    this.name = competencyProfile.getName();
    this.ratingConfiguration = competencyProfile.getRatingConfiguration();
    final List<CompetencyDao> srcCompetencyList = competencyProfile.getCompetencyList();
    if (!CollectionUtils.isEmpty(srcCompetencyList)) {
      this.competencyList = srcCompetencyList.stream().filter(SPGoal::isActive)
          .collect(Collectors.mapping(CompetencySummaryDTO::new, Collectors.toList()));
    }
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
  
  public List<CompetencySummaryDTO> getCompetencyList() {
    return competencyList;
  }
  
  public void setCompetencyList(List<CompetencySummaryDTO> competencyList) {
    this.competencyList = competencyList;
  }

  public RatingConfiguration getRatingConfiguration() {
    return ratingConfiguration;
  }

  public void setRatingConfiguration(RatingConfiguration ratingConfiguration) {
    this.ratingConfiguration = ratingConfiguration;
  }
}
