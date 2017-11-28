package com.sp.web.dto.library;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dto.BaseGoalDto;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO to list the articles.
 */
public class ArticleSelectListingDTO extends BaseArticleDto {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 7635915121767831417L;
  private List<BaseGoalDto> goalList;
  private String id;
  
  /**
   * Constructor.
   * 
   * @param article
   *          - article
   */
  public ArticleSelectListingDTO(ArticleDao article) {
    super(article);
    this.goalList = article.getGoalList();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<BaseGoalDto> getGoalList() {
    return goalList;
  }

  public void setGoalList(List<BaseGoalDto> goalList) {
    this.goalList = goalList;
  }
}
