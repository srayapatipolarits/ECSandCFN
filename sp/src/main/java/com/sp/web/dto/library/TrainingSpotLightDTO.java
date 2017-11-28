package com.sp.web.dto.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.article.Article;
import com.sp.web.model.library.NewsCredVideoArticle;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         This the training spot light article DTO.
 */
public class TrainingSpotLightDTO implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -4030238450523534425L;
  private String imageUrl;
  private String articleLinkLabel;
  private String articleLinkUrl;
  private List<String> author;
  private BaseGoalDto goal;
  private String content;
  private String videoUrl;
  
  /**
   * Constructor.
   * 
   * @param articleDao
   *          - the spotlight article
   */
  public TrainingSpotLightDTO(ArticleDao articleDao) {
    Article article = articleDao.getArticle();
    if (article instanceof NewsCredVideoArticle) {
      NewsCredVideoArticle videoArticle = (NewsCredVideoArticle) article;
      this.articleLinkLabel = videoArticle.getArticleLinkLabel();
      this.articleLinkUrl = videoArticle.getArticleLinkUrl();
      this.imageUrl = videoArticle.getImageUrl();
      this.videoUrl = videoArticle.getVideoUrl();
      
      this.author = videoArticle.getAuthor();
      this.content = (videoArticle.getContent() != null && videoArticle.getContent().size() > 0) ? videoArticle
          .getContent().get(0) : null;
      List<BaseGoalDto> goalList = articleDao.getGoalList();
      if (goalList != null && !goalList.isEmpty()) {
        this.goal = goalList.get(0);
      } else {
        throw new InvalidRequestException("No goals found for the article. " + article.getId());
      }
    } else {
      throw new InvalidRequestException("Must be a video type of article." + article.getId());
    }
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public List<String> getAuthor() {
    return author;
  }
  
  public void setAuthor(List<String> author) {
    this.author = author;
  }
    
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }

  public String getArticleLinkUrl() {
    return articleLinkUrl;
  }

  public void setArticleLinkUrl(String articleLinkUrl) {
    this.articleLinkUrl = articleLinkUrl;
  }

  @JsonIgnore
  public BaseGoalDto getGoal() {
    return goal;
  }

  public void setGoal(BaseGoalDto goal) {
    this.goal = goal;
  }
  
  @JsonIgnore
  public String getGoalId() {
    return goal.getId();
  }

  public String getArticleLinkLabel() {
    return articleLinkLabel;
  }

  public void setArticleLinkLabel(String articleLinkLabel) {
    this.articleLinkLabel = articleLinkLabel;
  }
  
  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }
  
  public String getVideoUrl() {
    return videoUrl;
  }
  
}
