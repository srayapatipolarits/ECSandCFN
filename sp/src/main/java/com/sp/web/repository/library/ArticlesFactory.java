package com.sp.web.repository.library;

import com.sp.web.Constants;
import com.sp.web.comments.CommentsFactory;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.dto.library.ArticleListingDTO;
import com.sp.web.dto.library.BaseArticleDto;
import com.sp.web.dto.library.TrainingSpotLightDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.translation.TranslationFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The Articles factory to retrieve and store articles.
 */
@Component
public class ArticlesFactory {
  
  TrainingLibraryArticleRepository articleRepository;
  
  @Autowired
  ArticlesFactoryCacheable factoryCache;
  
  @Autowired
  private TrackingRepository trackingRepository;
  
  @Autowired
  private CommentsFactory commentsFactory;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private TranslationFactory tranlsationFactory;
  
  private Map<String, String> allAuthors;
  private Map<String, String> articleSourceTypes;
  
  private Map<String, Map<String, String>> themesMap = new HashMap<String, Map<String,String>>();
  
  /**
   * Constructor.
   * 
   * @param articleRepository
   *          - the articles repository
   */
  @Inject
  public ArticlesFactory(TrainingLibraryArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
    load();
  }
  
  /**
   * Load the various fields.
   */
  public void load() {
    // all the authors
    Set<String> allAuthorsSet = articleRepository.getAllAuthors();
    allAuthors = allAuthorsSet.stream().collect(Collectors.toMap(String::new, String::new));
    
    // the themes
    List<SPGoal> spGoals = articleRepository.getAllThemes();
    /* creating a map for adding result in a placeholder required for json parsing in front end. */
    Map<String, String> themeMap = spGoals.stream()
        .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
        .collect(Collectors.toMap(SPGoal::getId, SPGoal::getName));
    themesMap.put(Constants.DEFAULT_LOCALE, themeMap);
    // the source types
    Set<String> articleSourceTypesSet = articleRepository.getAllArticleSources();
    articleSourceTypes = articleSourceTypesSet.stream().collect(
        Collectors.toMap(String::new, String::new));
  }
  
  /**
   * Get the list of articles for the given topic category and topic value.
   * 
   * @param topicCategroy
   *          - topic category
   * @param topicValue
   *          - topic value
   * @return - list of articles
   */
  @Cacheable("article")
  public List<ArticleDao> getArtilces(String topicCategroy, String topicValue, String locale) {
    if (StringUtils.isEmpty(topicValue)) {
      throw new InvalidRequestException("Article search text required.");
    }
    
    List<String> articleIdList = articleRepository.getArtilces(topicCategroy, topicValue);
    List<Article> articleList = articleIdList.stream().map(factoryCache::getArticle)
        .collect(Collectors.toList());
    return factoryCache.sortAndConvertToDao(articleList,locale);
  }
  
  /**
   * Update the comment for the article.
   * 
   * @param articleId
   *          - article id
   * @param user
   *          - user
   * @param comment
   *          - comment
   * @return the updated article
   */
  public Article addComment(String articleId, User user, String comment) {
    Assert.hasText(articleId, "Article id is required.");
    Assert.hasText(comment, "Blank comment or invalid comment text");
    Article article = factoryCache.getArticle(articleId);
    article.addComment(user, comment);
    articleRepository.updateGenericArticle(article);
    return article;
  }
  
  /**
   * Remove the comment from the article.
   * 
   * @param user
   *          - user
   * @param articleId
   *          - article id
   * @param index
   *          - index to remove
   */
  public void removeComment(User user, String articleId, int index) {
    Assert.notNull(user, "User is required.");
    Assert.hasText(articleId, "Article id is required.");
    Article article = factoryCache.getArticle(articleId);
    commentsFactory.removeComment(user, article.getComments(), index);
    articleRepository.updateGenericArticle(article);
  }
  
  /**
   * Update the comment for the article.
   * 
   * @param user
   *          - user
   * @param articleId
   *          - article id
   * @param index
   *          - index of the comment
   * @param commentStr
   *          - comment
   */
  public void updateComment(User user, String articleId, int index, String commentStr) {
    Assert.notNull(user, "User is required.");
    Assert.hasText(articleId, "Article id is required.");
    Assert.hasText(commentStr, "Comment is required.");
    Article article = factoryCache.getArticle(articleId);
    commentsFactory.updateComment(user, article.getComments(), index, commentStr);
    articleRepository.updateGenericArticle(article);
  }
  
  /**
   * Get all the authors.
   * 
   * @return - the set of authors
   */
  public Map<String, String> getAllAuthors() {
    return allAuthors;
  }
  
  /**
   * Get all the themes.
   * 
   * @return - the themes map
   */
  public Map<String, String> getAllThemes(String locale) {
    if (themesMap.get(locale) != null) {
      return themesMap.get(locale);
    } else {
      List<SPGoal> spGoals = articleRepository.getAllThemes();
      return spGoals
          .stream()
          .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
          .map(
              gl -> tranlsationFactory.getTranslation(gl.getId(), locale, SPGoal.class,
                  goalsRepository::findById))
          .collect(Collectors.toMap(SPGoal::getId, SPGoal::getName));
      
    }
    
  }
  
  /**
   * The article source types.
   * 
   * @return the article source types
   */
  public Map<String, String> getAllSourceTypes() {
    return articleSourceTypes;
  }
  
  /**
   * Update the recommendation count for the article.
   * 
   * @param articleId
   *          - article id
   * @param userEmail
   *          - user recommending the article
   * @param doReccomend
   *          - true to recommend article else remove previous recommend
   * @return the article recommendation count
   */
  public int reccomendArticle(String articleId, String userEmail, boolean doReccomend) {
    Assert.hasText(articleId, "Article is required.");
    Assert.hasText(userEmail, "User is required.");
    Article article = factoryCache.getArticle(articleId);
    Set<String> articleReccomendations = article.getArticleReccomendations();
    if (doReccomend) {
      if (articleReccomendations.contains(userEmail)) {
        throw new InvalidRequestException("User has already recommended the article.");
      } else {
        articleReccomendations.add(userEmail);
      }
    } else {
      if (articleReccomendations.contains(userEmail)) {
        articleReccomendations.remove(userEmail);
      } else {
        throw new InvalidRequestException("User has not recommended the article.");
      }
    }
    articleRepository.updateGenericArticle(article);
    return articleReccomendations.size();
  }
  
  /**
   * Remove all the cache.
   */
  @CacheEvict(value = { "goals", "userGoals", "article", "libraryHome" }, allEntries = true)
  public void resetAllCache() {
    load();
  }
  
  /**
   * Get the article.
   * 
   * @param articleId
   *          - article id
   * @return the article
   */
  public Article getArticle(String articleId) {
    return factoryCache.getArticle(articleId);
  }
  
  /**
   * Get a list of all the articles.
   * 
   * @return - the list of all the articles
   */
  public List<ArticleDao> getAllArticles(String locale) {
    return factoryCache.getAllArticles(locale);
  }
  
  /**
   * Get the list of trending articles.
   * 
   * @return the list of articles
   */
  @Cacheable(value = "article", key = "#root.methodName")
  public List<BaseArticleDto> getTrendingArticles() {
    List<String> trackingList = trackingRepository.findTrendingArticle();
    List<BaseArticleDto> collect = trackingList.stream().map(factoryCache::getArticle)
        .filter(a -> a != null).map(BaseArticleDto::new).collect(Collectors.toList());
    return collect;
  }
  
  /**
   * Get the list of top rated articles by the recommendation count.
   * 
   * @return the list of top rated articles
   */
  @Cacheable(value = "article", key = "#root.methodName")
  public List<BaseArticleDto> getTopRatedArticles() {
    List<String> trackingList = articleRepository.findTopRatedArticle();
    List<BaseArticleDto> collect = trackingList.stream().map(factoryCache::getArticle)
        .filter(a -> a != null).map(BaseArticleDto::new).collect(Collectors.toList());
    return collect;
  }
  
  /**
   * This method gets the map of all the training spotlight articles.
   * 
   * @return - map of training spotlight key is the goal id
   */
  @Cacheable(value = "article", key = "#root.methodName+#locale")
  public Map<String, List<TrainingSpotLightDTO>> getTrainingSpotLightArticles(String locale) {
    Map<String, List<TrainingSpotLightDTO>> spotlightMap = factoryCache
        .getAllArticles(locale)
        .stream()
        .filter(
            a -> {
              Article article = a.getArticle();
              String articleSource = article.getArticleSource();
              if (article.getArticleType() == ArticleType.VIDEO && !article.getGoals().isEmpty()
                  && articleSource != null && articleSource.equals("SurePeople")) {
                return true;
              }
              return false;
            }).map(TrainingSpotLightDTO::new)
        .collect(Collectors.groupingBy(TrainingSpotLightDTO::getGoalId));
    return spotlightMap;
  }
  
  /**
   * Add the given home page article.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description
   * @param position
   *          - position
   * @param companyId
   *          - Company id
   * @param articleLocaltion
   *          - article location
   */
  @CacheEvict(value = "libraryHome", allEntries = true)
  public void addHomepageArticle(String articleId, String shortDescription, int position,
      String companyId, ArticleLocation articleLocaltion) {
    // validate if the article is present
    Article article = getArticle(articleId);
    // create a new home page article node
    TrainingLibraryHomeArticle homepageArticle = new TrainingLibraryHomeArticle(article,
        shortDescription, position, companyId, articleLocaltion, article.getArticleLinkLabel());
    articleRepository.addHomepageArticle(homepageArticle);
  }
  
  /**
   * Get the home page articles.
   * 
   * @param articleLocation
   *          - articles location
   * @param companyId
   *          - company id
   * @return the list of articles
   */
  @Cacheable(value = "libraryHome", key = "#companyId+#locale")
  public List<ArticleListingDTO> getAllHomepageArticles(ArticleLocation articleLocation,
      String companyId, String locale) {
    List<TrainingLibraryHomeArticle> articleList = articleRepository
        .findHomepageArticlesByLocation(articleLocation, companyId);
    
    // check if the articles were returned for the training library home
    if (CollectionUtils.isEmpty(articleList)) {
      return null;
    }
    return articleList.stream().map(tlArticle -> {
      Article article = getArticle(tlArticle.getArticleId());
      BaseGoalDto goal = null;
      if (!article.getGoals().isEmpty()) {
        goal = new BaseGoalDto(goalsFactory.getGoal(article.getGoals().iterator().next(),locale));
      }
      return new ArticleListingDTO(article, tlArticle, goal);
    }).collect(Collectors.toList());
  }
  
  /**
   * Method to delete the home page articles for the given company and also to clear the cache.
   * 
   * @param companyId
   *          - company id
   */
  @CacheEvict(value = "libraryHome", allEntries = true)
  public void deleteHomePageArticles(String companyId) {
    articleRepository.deleteHomePageArticles(companyId);
  }
  
  /**
   * Update the recommendation count for the article.
   * 
   * @param articleId
   *          - article id
   * @return the article recommendation count
   */
  public Map<String, Integer> addUseFullnessCount(String articleId, boolean isArticleUsefull) {
    Assert.hasText(articleId, "Article is required.");
    Article article = factoryCache.getArticle(articleId);
    if (isArticleUsefull) {
      article.incrementScoreInfluence();
    } else {
      article.incrementArticleNotUsefull();
    }
    
    articleRepository.updateGenericArticle(article);
    Map<String, Integer> useFullNess = new HashMap<String, Integer>();
    useFullNess.put("usefull", article.getScoreInfluence());
    useFullNess.put("notUsefull", article.getArticleNotUsefull());
    return useFullNess;
  }
  
}
