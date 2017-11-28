package com.sp.web.model.goal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author pradeep
 * 
 *         The user goal entity.
 */
public class UserGoal {
  
  /**
   * Depricated :->UserArticle Progress contains the progress for the non mandatory articles for the user.
   * Below now contains the only the completed articles.
   */
  private List<UserArticleProgress> articleProgress;
  
  private List<UserGoalProgress> goalProgress;
  
  private Set<String> bookMarkedArticles;
  
  private String id;
  
  private String activeGoalId;
  
  /**
   * Default Constructor.
   */
  public UserGoal() {
  }
  
  /**
   * Constructor.
   * 
   * @param userId
   *          - user id
   * @param goalProgress
   *          - user goals progress
   */
  public UserGoal(List<UserGoalProgress> goalProgress) {
    this.goalProgress = goalProgress;
  }
  
  public List<UserGoalProgress> getGoalProgress() {
    return Optional.ofNullable(goalProgress).orElseGet(this::createGoalsProgress);
  }

  private List<UserGoalProgress> createGoalsProgress() {
    goalProgress = new ArrayList<UserGoalProgress>();
    return goalProgress;
  }
  
  public void setGoalProgress(List<UserGoalProgress> goalProgress) {
    this.goalProgress = goalProgress;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void addArticleProgress(UserArticleProgress articleProgress) {
    getArticleProgress().add(articleProgress);
  }
  
  public List<UserArticleProgress> getArticleProgress() {
    return Optional.ofNullable(articleProgress).orElseGet(this::createArticlesProgress);
  }

  private List<UserArticleProgress> createArticlesProgress() {
    articleProgress = new ArrayList<UserArticleProgress>();
    return articleProgress;
  }
  
  public void setArticleProgress(List<UserArticleProgress> articleProgress) {
    this.articleProgress = articleProgress;
  }

  public String getActiveGoalId() {
    return activeGoalId;
  }

  public void setActiveGoalId(String activeGoalId) {
    this.activeGoalId = activeGoalId;
  }
  
  public void setBookMarkedArticles(Set<String> bookMarkedArticles) {
    this.bookMarkedArticles = bookMarkedArticles;
  }
  
  /**
   * @return 
   *    - the bookMarkedArticles.
   */
  public Set<String> getBookMarkedArticles() {
    if (bookMarkedArticles == null) {
      bookMarkedArticles = new HashSet<>();
    }
    return bookMarkedArticles;
  }
  
}
