package com.sp.web.dto.competency;

import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.User;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.media.SPMediaPlaceholder;
import com.sp.web.repository.library.ArticlesFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for competency profile.
 */
public class CompetencyProfileDTO extends BaseCompetencyProfileDTO {
  
  private BaseCompanyDTO company;
  private String description;
  private List<SPMediaPlaceholder> mediaList;
  private boolean evaluationInProgress;
  private boolean active;
  private List<CompetencyDetailsDTO> competencyList;
  private RatingConfiguration ratingConfiguration;
  
  /**
   * Constructor from competency profile.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param company
   *          - company object
   */
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile, CompanyDao company) {
    this(competencyProfile, company, false);
  }
  
  /**
   * Flag to check if this is an administrator request.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param company
   *          - company
   * @param isAdmin
   *          - flag if it is company request
   */
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile, CompanyDao company,
      boolean isAdmin) {
    this(competencyProfile, new HashMap<String, UserArticleProgressDao>(), isAdmin);
    this.company = new BaseCompanyDTO(company);
    evaluationInProgress = company.isEvaluationInProgress();
  }
  
  /**
   * Constructor from competency profile and article progress.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param articleProgressMap
   *          - article progress map
   */
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile,
      HashMap<String, UserArticleProgressDao> articleProgressMap) {
    this(competencyProfile, articleProgressMap, false);
  }
  
  /**
   * Constructor for competency profile with flag if it is admin request or user.
   * 
   * @param competencyProfile
   *          - competency profile
   * @param articleProgressMap
   *          - articles map
   * @param isAdmin
   *          - flag for if is admin request
   */
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile,
      HashMap<String, UserArticleProgressDao> articleProgressMap, boolean isAdmin) {
    super(competencyProfile);
    if (isAdmin) {
      this.competencyList = competencyProfile
          .getCompetencyList()
          .stream()
          .collect(
              Collectors.mapping(c -> new CompetencyDetailsDTO(c, articleProgressMap),
                  Collectors.toList()));
    } else {
      this.competencyList = competencyProfile
          .getCompetencyList()
          .stream()
          .filter(SPGoal::isActive)
          .collect(
              Collectors.mapping(c -> new CompetencyDetailsDTO(c, articleProgressMap),
                  Collectors.toList()));
    }
  }
  
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile, User user,
      UserGoalDao userGoalDao,  ArticlesFactory articlesFactory) {
    this(competencyProfile, user, userGoalDao, false, articlesFactory);
  }
  
  /**
   * Constructor.
   * 
   * @param competencyProfile
   *            - competency profile
   * @param user
   *            - user
   * @param userGoalDao
   *            - user goal dao
   * @param isAdmin
   *            - is admin flag
   * @param articlesFactory
   *            - articles
   */
  public CompetencyProfileDTO(CompetencyProfileDao competencyProfile, User user,
      UserGoalDao userGoalDao, boolean isAdmin,  ArticlesFactory articlesFactory) {
    super(competencyProfile);
    if (isAdmin) {
      this.competencyList = competencyProfile
          .getCompetencyList()
          .stream()
          .collect(
              Collectors.mapping(c -> new CompetencyDetailsDTO(c, user,userGoalDao, articlesFactory),
                  Collectors.toList()));
    } else {
      this.competencyList = competencyProfile
          .getCompetencyList()
          .stream()
          .filter(SPGoal::isActive)
          .collect(
              Collectors.mapping(c -> new CompetencyDetailsDTO(c,user, userGoalDao, articlesFactory),
                  Collectors.toList()));
    }
  }
  
  public BaseCompanyDTO getCompany() {
    return company;
  }
  
  public void setCompany(BaseCompanyDTO company) {
    this.company = company;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public List<SPMediaPlaceholder> getMediaList() {
    return mediaList;
  }
  
  public void setMediaList(List<SPMediaPlaceholder> mediaList) {
    this.mediaList = mediaList;
  }
  
  public boolean isEvaluationInProgress() {
    return evaluationInProgress;
  }
  
  public void setEvaluationInProgress(boolean evaluationInProgress) {
    this.evaluationInProgress = evaluationInProgress;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public List<CompetencyDetailsDTO> getCompetencyList() {
    return competencyList;
  }
  
  public void setCompetencyList(List<CompetencyDetailsDTO> competencyList) {
    this.competencyList = competencyList;
  }

  public RatingConfiguration getRatingConfiguration() {
    return ratingConfiguration;
  }

  public void setRatingConfiguration(RatingConfiguration ratingConfiguration) {
    this.ratingConfiguration = ratingConfiguration;
  }
}
