package com.sp.web.dto.library;

import com.sp.web.comments.CommentsFactory;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dto.CommentsDTO;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleReadability;

import java.util.List;
import java.util.Map;

/**
 * @author Pradeep Ruhil
 *
 *         The Article DTO.
 */
public class ArticleDetailsDto extends ArticleDto {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<CommentsDTO> comments;

  private List<String> content;
  
  private String nextArticleUrl;
  
  private String imageUrl;
  
  /** article readability */
  private ArticleReadability articleReadability;

  /**
   * Constructor to create the DTO from the article.
   */
  public ArticleDetailsDto(ArticleDao articleDao) {
    this(articleDao.getArticle(), null, null);
    setGoals(articleDao.getGoalList());
  }

  /**
   * Constructor to create the DTO from the article.
   */
  public ArticleDetailsDto(Article article) {
    this(article, null, null);
  }

  /**
   * Constructor that creates the article progress along with the user status.
   * 
   * @param article
   *          - article
   * @param userArticlesMap
   *          - map of user articles
   */
  public ArticleDetailsDto(Article article, Map<String, UserArticleProgressDao> userArticlesMap,
      CommentsFactory commentsFactory) {
    super(article, userArticlesMap);
    if (commentsFactory != null) {
      comments = commentsFactory.getCommentsDTO(article.getComments());
    }
  }

//  /**
//   * Get the formatted date.
//   * 
//   * @param date
//   *          - date
//   * @return formatted date
//   */
//  private String getFormattedDate(Date date) {
//    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
//    return dateFormat.format(date);
//  }

  public List<CommentsDTO> getComments() {
    return comments;
  }

  public void setComments(List<CommentsDTO> comments) {
    this.comments = comments;
  }

  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }

  public String getNextArticleUrl() {
    return nextArticleUrl;
  }

  public void setNextArticleUrl(String nextArticleUrl) {
    this.nextArticleUrl = nextArticleUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public ArticleReadability getArticleReadability() {
    return articleReadability;
  }

  public void setArticleReadability(ArticleReadability articleReadability) {
    this.articleReadability = articleReadability;
  }
}
