package com.sp.web.dto.library;

import com.sp.web.dto.BaseGoalDto;
import com.sp.web.model.article.Article;
import com.sp.web.model.library.TrainingLibraryHomeArticle;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO to list the articles.
 */
public class ArticleListingDTO extends BaseArticleDto {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 7635915121767831417L;
  private String imageUrl;
  private String shortDescription;
  private BaseGoalDto goal;
  
  /**
   * Constructor.
   * 
   * @param article
   *          - article
   * @param tlHomeArticle 
   *          - training library home article
   * @param goal
   *          - Goal of the article
   */
  public ArticleListingDTO(Article article, TrainingLibraryHomeArticle tlHomeArticle, BaseGoalDto goal) {
    super(article);
    this.shortDescription = tlHomeArticle.getShortDescription();
    if (shortDescription == null) {
      List<String> content = article.getContent();
      if (content != null && !content.isEmpty()) {
        shortDescription = article.getContent().get(0);
      }
    }
    this.goal = goal;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public BaseGoalDto getGoal() {
    return goal;
  }

  public void setGoal(BaseGoalDto goal) {
    this.goal = goal;
  }
}
