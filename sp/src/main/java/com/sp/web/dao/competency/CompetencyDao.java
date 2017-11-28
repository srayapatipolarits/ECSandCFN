package com.sp.web.dao.competency;

import com.sp.web.Constants;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DAO class to hold the goals as well as the articles.
 */
@Document(collection = "sPGoal")
public class CompetencyDao extends SPGoal {
  
  private static final long serialVersionUID = -8595467587183661846L;
  @Transient
  private List<ArticleDao> articlesList;
  
  /**
   * Default constructor. 
   */
  public CompetencyDao() {
    super();
  }

  /**
   * Constructor from goal and articles factory.
   * 
   * @param competency
   *          - competency
   * @param articlesFactory
   *          - articles factory
   * @param goalsFactory
   *          - goals factory         
   */
  public CompetencyDao(SPGoal competency, ArticlesFactory articlesFactory,
      SPGoalFactory goalsFactory) {
    BeanUtils.copyProperties(competency, this);
    articlesList = getMandatoryArticles().stream().map(articlesFactory::getArticle).collect(
            Collectors.mapping(a -> new ArticleDao(a, goalsFactory, Constants.DEFAULT_LOCALE
                .toString()), Collectors.toList()));
  }

  public List<ArticleDao> getArticlesList() {
    return articlesList;
  }

  public void setArticlesList(List<ArticleDao> articlesList) {
    this.articlesList = articlesList;
  }
  
}
