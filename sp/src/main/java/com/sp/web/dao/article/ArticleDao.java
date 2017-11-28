package com.sp.web.dao.article;

import com.sp.web.dto.BaseGoalDto;
import com.sp.web.model.article.Article;
import com.sp.web.service.goals.SPGoalFactory;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The article dao.
 */
public class ArticleDao implements Serializable {
  
  private static final long serialVersionUID = 7905981409580824918L;
  private Article article;
  private List<BaseGoalDto> goalList;
  
  /**
   * Constructor.
   * 
   * @param article
   *          - the article
   * @param goalsFactory
   *          - the goals factory
   * @param locale
   *          of the user.
   */
  public ArticleDao(Article article, SPGoalFactory goalsFactory, String locale) {
    this.article = article;
    goalList = article.getGoals().stream().map(gl -> goalsFactory.getGoal(gl, locale))
        .filter(g -> g != null).map(BaseGoalDto::new).collect(Collectors.toList());
  }
  
  public Article getArticle() {
    return article;
  }
  
  public void setArticle(Article article) {
    this.article = article;
  }
  
  public List<BaseGoalDto> getGoalList() {
    return goalList;
  }
  
  public void setGoalList(List<BaseGoalDto> goalList) {
    this.goalList = goalList;
  }

  /**
   * Get the article id.
   * 
   * @return
   *      - article id
   */
  public String getId() {
    return (article != null) ? article.getId() : null;
  }
}
