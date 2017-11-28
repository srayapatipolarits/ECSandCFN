package com.sp.web.dto.competency;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.PracticeAreaActionDetailsDTO;
import com.sp.web.dto.UserArticleProgressDetailDTO;
import com.sp.web.dto.library.ArticleDto;
import com.sp.web.model.SPRating;
import com.sp.web.model.User;
import com.sp.web.repository.library.ArticlesFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for competency profile details.
 */
public class CompetencyDetailsDTO extends PracticeAreaActionDetailsDTO {
  
  private static final long serialVersionUID = 4518646772768451550L;
  private List<String> mandatoryArticles;
  private List<ArticleDto> articlesDTOList;
  private UserArticleProgressDetailDTO articleProgressDetailDTO;
  private SPRating rating;
  
  /**
   * Constructor from Competency.
   * 
   * @param competency
   *          - competency
   */
  public CompetencyDetailsDTO(CompetencyDao competency) {
    super(competency);
  }
  
  /**
   * Constructor from competency and user article progress map.
   * 
   * @param competency
   *          - competency
   * @param articleProgressMap
   *          - articles progress map
   */
  public CompetencyDetailsDTO(CompetencyDao competency,
      HashMap<String, UserArticleProgressDao> articleProgressMap) {
    this(competency);
    List<ArticleDao> articlesList = competency.getArticlesList();
    if (articlesList != null) {
      articlesDTOList = articlesList.stream().collect(
          Collectors.mapping(a -> new ArticleDto(a, articleProgressMap), Collectors.toList()));
    }
  }
  
  /**
   * Constructor from competency and user article progress map.
   * 
   * @param competency
   *          - competency
   */
  public CompetencyDetailsDTO(CompetencyDao competency, User user, UserGoalDao userGoalDao,
      ArticlesFactory articlesFactory) {
    this(competency);
    List<ArticleDao> articlesList = competency.getArticlesList();
    if (articlesList != null) {
      articleProgressDetailDTO = new UserArticleProgressDetailDTO();
      articleProgressDetailDTO.updateArticles(userGoalDao.getArticleProgressMap(), articlesList,
          userGoalDao.getBookMarkedArticles(), user, mandatoryArticles, articlesFactory);
    }
  }
  
  public List<String> getMandatoryArticles() {
    return mandatoryArticles;
  }
  
  public void setMandatoryArticles(List<String> mandatoryArticles) {
    this.mandatoryArticles = mandatoryArticles;
  }
  
  public List<ArticleDto> getArticlesDTOList() {
    return articlesDTOList;
  }
  
  public void setArticlesDTOList(List<ArticleDto> articlesDTOList) {
    this.articlesDTOList = articlesDTOList;
  }
  
  public SPRating getRating() {
    return rating;
  }
  
  public void setRating(SPRating rating) {
    this.rating = rating;
  }
  
  public void setArticleProgressDetailDTO(UserArticleProgressDetailDTO articleProgressDetailDTO) {
    this.articleProgressDetailDTO = articleProgressDetailDTO;
  }
  
  public UserArticleProgressDetailDTO getArticleProgressDetailDTO() {
    return articleProgressDetailDTO;
  }
}
