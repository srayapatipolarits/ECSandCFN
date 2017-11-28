package com.sp.web.dao.competency;

import com.sp.web.form.competency.CompetencyProfileForm;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DAO for the competency profile entity.
 */
@Document(collection = "competencyProfile")
public class CompetencyProfileDao extends CompetencyProfile {
  
  private static final long serialVersionUID = -4416880491192894017L;
  
  @Transient
  private List<CompetencyDao> competencyList;
  
  /**
   * Default constructor.
   */
  public CompetencyProfileDao() { }

  /**
   * Constructor form competency profile.
   * 
   * @param competencyProfile
   *            - competency profile
   * @param goalsFactory
   *            - goals factory           
   */
  public CompetencyProfileDao(CompetencyProfile competencyProfile, ArticlesFactory articlesFactory,
      SPGoalFactory goalsFactory) {
    BeanUtils.copyProperties(competencyProfile, this);
    final List<String> competencyIdList = getCompetencyIdList();
    if (!CollectionUtils.isEmpty(competencyIdList)) {
      competencyList = competencyIdList
          .stream()
          .map(goalsFactory::getGoalFromDB)
          .collect(
              Collectors.mapping(g -> new CompetencyDao(g, articlesFactory, goalsFactory),
                  Collectors.toList()));
    }
  }

  /**
   * Constructor from competency form.
   * 
   * @param form
   *          - competency form
   */
  public CompetencyProfileDao(CompetencyProfileForm form) {
    setName(form.getName());
    setCompanyId(form.getCompanyId());
  }

  public List<CompetencyDao> getCompetencyList() {
    return competencyList;
  }

  public void setCompetencyList(List<CompetencyDao> competencyList) {
    this.competencyList = competencyList;
  }

}
