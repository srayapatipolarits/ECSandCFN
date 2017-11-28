package com.sp.web.model.article;

import com.sp.web.model.Comments;
import com.sp.web.model.User;
import com.sp.web.model.library.ArticleSourceType;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <code>Articles</code> holds the articles which could be video, audio or text
 * article.
 * 
 * @author pradeep
 */
@Document(collection = "articles")
public class Article implements Serializable {

  private static final long serialVersionUID = -6392419021721965368L;

  /** Article Link label. */
  private String articleLinkLabel;

  /** author of the article. */
  private List<String> author;

  private String articleLinkUrl;

  /** article recommendations . */
  private Set<String> articleReccomendations;

  /** id of the article. */
  private String id;

  /** Articles associated to user goals. */
  Set<String> goals;

  /** Article type (video/audio/text). */
  private ArticleType articleType;

  private ArticleSourceType articleSourceTypes;

  /**
   * The source for the article.
   */
  private String articleSource;

  /** Article created on. */
  private Date createdOn;

  /** Comments given in the article. */
  private List<Comments> comments;

  /** article rank. */
  private int score;

  private int scoreInfluence;

  private String title;

  private List<String> content;
  
  private String imageUrl;
  
  private ArticleReadability articleReadability;
  
  /** Unique id for the article */
  private String originalGuid;
  
  private int articleNotUsefull;

  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }

  public void setAuthor(List<String> author) {
    this.author = author;
  }

  /**
   * Get the list of authors for the article.
   * 
   * @return list of articles
   */
  public List<String> getAuthor() {
    if (author == null) {
      author = new ArrayList<String>();
    }
    return author;
  }

  public int getRecommendationCount() {
    return (articleReccomendations != null) ? articleReccomendations.size() : 0;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get the goals the article belongs to.
   * 
   * @return the set of goals
   */
  public Set<String> getGoals() {
    if (goals == null) {
      goals = new HashSet<String>();
    }
    return goals;
  }

  public void setGoals(Set<String> goals) {
    this.goals = goals;
  }

  public ArticleType getArticleType() {
    return articleType;
  }

  public void setArticleType(ArticleType articleType) {
    this.articleType = articleType;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public List<Comments> getComments() {
    return Optional.ofNullable(comments).orElseGet(() -> {
      comments = new LinkedList<Comments>();
      return comments;
    });
  }

  public void setComments(List<Comments> comments) {
    this.comments = comments;
  }

  public void setArticleLinkLabel(String articleLinkLabel) {
    this.articleLinkLabel = articleLinkLabel;
  }

  public String getArticleLinkLabel() {
    return articleLinkLabel;
  }

  public void setArticleLinkUrl(String articleLinkUrl) {
    this.articleLinkUrl = articleLinkUrl;
  }

  public String getArticleLinkUrl() {
    return articleLinkUrl;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public void setArticleSourceTypes(ArticleSourceType articleSourceTypes) {
    this.articleSourceTypes = articleSourceTypes;
  }

  public ArticleSourceType getArticleSourceTypes() {
    return articleSourceTypes;
  }

  public void setScoreInfluence(int scoreInfluence) {
    this.scoreInfluence = scoreInfluence;
  }

  public int getScoreInfluence() {
    return scoreInfluence;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void addComment(User user, String comment) {
    getComments().add(0, new Comments(user.getId(), comment));
  }

  public String getArticleSource() {
    return articleSource;
  }

  public void setArticleSource(String articleSource) {
    this.articleSource = articleSource;
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

  /**
   * @param originalGuid the originalGuid to set
   */
  public void setOriginalGuid(String originalGuid) {
    this.originalGuid = originalGuid;
  }
  
  /**
   * @return 
   *    - the originalGuid.
   */
  public String getOriginalGuid() {
    return originalGuid;
  }

  /**
   * Get the article recommendations.
   * @return
   *      - Set of user email who have recommended the article.
   */
  public Set<String> getArticleReccomendations() {
    if (articleReccomendations == null) {
      articleReccomendations = new HashSet<String>();
    }
    return articleReccomendations;
  }

  public void setArticleReccomendations(Set<String> articleReccomendations) {
    this.articleReccomendations = articleReccomendations;
  }
  
  public void incrementScoreInfluence() {
    ++scoreInfluence;
  }
  
  public void setArticleNotUsefull(int articleNotUsefull) {
    this.articleNotUsefull = articleNotUsefull;
  }
  
  public int getArticleNotUsefull() {
    return articleNotUsefull;
  }
  
  public void incrementArticleNotUsefull() {
    ++articleNotUsefull;
  }

  /**
   * getShortDescription will return the short description of the article.
   * @param length for which short description is to be returned.
   * @return the short description.
   */
  @Transient
  public String getShortDescription(int length) {
    
    StringBuffer sb = new StringBuffer();
    List<String> content = getContent();
    if (content != null) {
      for (String contentPara : content) {
        // break if the content has an img tag
        if (contentPara.contains("img")) {
          break;
        }
        sb.append(contentPara);
        if (sb.length() > length) {
          break;
        }
      }
      // sb.append("...");
    }
    return sb.toString();
  }
}
