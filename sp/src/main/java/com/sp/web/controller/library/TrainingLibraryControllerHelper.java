package com.sp.web.controller.library;

import com.sp.web.Constants;
import com.sp.web.comments.CommentsFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.dto.library.ArticleDetailsDto;
import com.sp.web.dto.library.ArticleDto;
import com.sp.web.dto.library.ArticleListingDTO;
import com.sp.web.dto.library.BaseArticleDto;
import com.sp.web.dto.library.TrainingSpotLightDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.TrackingBean;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.ArticleUsefullArticleTracking;
import com.sp.web.model.tracking.ArticlesCompletedArticleTracking;
import com.sp.web.model.tracking.RecommendedArticleTracking;
import com.sp.web.model.tracking.TrainingLibraryVisitTracking;
import com.sp.web.model.usertracking.UserTrackingType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.message.MessageHandlerType;
import com.sp.web.service.message.SPMessageEnvelop;
import com.sp.web.service.translation.TranslationFactory;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

/**
 * <code>TrainingLIbraryControllerHelper</code> is the helper class for training library. It provide
 * implemenation for the training library module.
 * 
 * @author pradeep
 *
 */
@Component
public class TrainingLibraryControllerHelper {
  
  private static final String ARTICLE_ORANGE_BAND_TEXT = "articleOrangeBandText";
  
  /**
   * 
   */
  private static final String ARTICLE_LINK_TEXT = "articleLinkText";
  
  /** INitializing the logger. */
  private static final Logger LOG = Logger.getLogger(TrainingLibraryController.class);
  
  /** training library article repository. */
  @Autowired
  private TrainingLibraryArticleRepository libraryArticleRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private CommentsFactory commentsFactory;
  
  @Autowired
  private ThemeCacheableFactory themeCacheableFactory;
  
  @Autowired
  private TrackingRepository trackingRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  @Qualifier("jmsTemplatePub")
  private JmsTemplate jmsTemplatePub;
  
  @Autowired
  private TranslationFactory translationFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;
  
  private static final Random random = new Random();
  
  /**
   * Topics constant.
   */
  private static final String TOPICS = "topics";
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  /**
   * <code>getAllTopics</code> method will return the requested goals/authors or all the source
   * present in the articles
   * 
   * @param param
   *          contains the request param which is requested.
   * @return the sp response.
   */
  public SPResponse getAllTopics(User user, Object[] param) {
    
    /* get the topic param which is requrested */
    
    String topicParam = (String) param[0];
    Assert.hasText(topicParam, "Invalid Parameter");
    
    switch (topicParam) {
    case "Themes":
      return getAllThemes(user);
    case "Author":
      return getAllAuthors();
    case "Source":
      return getAllSources();
    default:
      LOG.warn("invalid paramter is sent from request, sending the defaultl themes as fallback");
      return getAllThemes(user);
    }
  }
  
  /**
   * <code>getAllArticlces</code> method will return all the articles present in the training
   * library.
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains topicCateggory and topic value if present.
   * @return all the articles.
   */
  public SPResponse getAllArticles(User user, Object[] param) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Enter getAllArticles");
    }
    final SPResponse response = new SPResponse();
    
    List<ArticleDao> articleList = null;
    /* check if length of the params array is of 2 */
    String topicCategroy = (String) param[0];
    String topicValue = (String) param[1];
    
    /* check if topic Category are null or not */
    if (StringUtils.isNotBlank(topicValue)) {
      articleList = articlesFactory.getArtilces(topicCategroy, topicValue, user.getUserLocale());
    }
    
    if (articleList == null) {
      articleList = articlesFactory.getAllArticles(user.getUserLocale());
    }
    
    // creating a map from the user articles
    UserGoalDao goalDao = getUserArticleProgressMap(user);
    
    Map<String, UserArticleProgressDao> articlesProgressMap = goalDao != null ? goalDao
        .getArticleProgressMap() : new HashMap<>();
    List<ArticleDto> articleDTOList = articleList
        .stream()
        .map(a -> {
          ArticleDto articleDto = new ArticleDto(a, articlesProgressMap);
          // check if the article goals are present in the user goals
            if (goalDao != null) {
              final Map<String, UserGoalProgressDao> goalsProgressMap = goalDao
                  .getGoalsProgressMap();
              for (String goalId : articleDto.getGoalIds()) {
                UserGoalProgressDao userGoalProgressDao = goalsProgressMap.get(goalId);
                if (userGoalProgressDao != null && userGoalProgressDao.isSelected()) {
                  if (articleDto.getUserArticleStatus() == null) {
                    articleDto.setUserArticleStatus(ArticleStatus.NOT_STARTED);
                  }
                  break;
                }
              }
            }
            
            return articleDto;
          }).collect(Collectors.toList());
    
    response.add("totalArticlesCount", articleDTOList.size());
    response.add("articles", articleDTOList);
    LOG.debug("Artilcels respones " + response);
    return response;
  }
  
  /**
   * Gets the user article progress map if present or an empty map.
   * 
   * @param user
   *          - user
   * @return the user article progress map
   */
  public UserGoalDao getUserArticleProgressMap(User user) {
    if (user.getUserGoalId() != null) {
      return goalsFactory.getUserGoal(user.getUserGoalId(), user.getUserLocale());
    }
    return null;
  }
  
  /**
   * <code>getArticleDetail</code> method will return the article detail.
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the article id
   * @return SPResonse
   */
  public SPResponse getArticleDetail(User user, Object[] param) {
    
    LOG.info("Enter getArtifleDetail method");
    String articleId = (String) param[0];
    
    /* check if article id is present or not */
    Assert.hasText(articleId, "Invalid article id");
    
    Article newsCredArticle = articlesFactory.getArticle(articleId);
    
    UserGoalDao goalDao = getUserArticleProgressMap(user);
    /* create the dto */
    final Map<String, UserArticleProgressDao> userArticleProgressMap = goalDao != null ? goalDao
        .getArticleProgressMap() : new HashMap<>();
    ArticleDetailsDto articleDTO = new ArticleDetailsDto(newsCredArticle, userArticleProgressMap,
        commentsFactory);
    
    // check if the article goals are present in the user goals
    if (!articleDTO.isArticleForUser()) {
      if (user.getUserGoalId() != null) {
        final Map<String, UserGoalProgressDao> goalsProgressMap = goalsFactory.getUserGoal(
            user.getUserGoalId(), user.getUserLocale()).getGoalsProgressMap();
        for (String goalId : articleDTO.getGoalIds()) {
          UserGoalProgressDao userGoalProgressDao = goalsProgressMap.get(goalId);
          if (userGoalProgressDao != null && userGoalProgressDao.isSelected()) {
            articleDTO.setCanAddToUserGoals(true);
            break;
          }
        }
      }
    }
    
    // check if the article is bookmarked for the user
    if (goalDao != null && !goalDao.getBookMarkedArticles().isEmpty()) {
      if (goalDao.getBookMarkedArticles().contains(articleId)) {
        articleDTO.setArticleBookmarked(true);
      }
    }
    
    // check if the article is recommended by the user
    if (newsCredArticle.getArticleReccomendations().contains(user.getEmail())) {
      articleDTO.setArticleRecommendedByUser(true);
    }
    
    final SPResponse articleDetailResponse = new SPResponse();
    
    // getting related articles
    if (!newsCredArticle.getGoals().isEmpty()) {
      String goalId = newsCredArticle.getGoals().stream().findFirst().get();
      // adding goal to the article DTO
      SPGoal goal = goalsFactory.getGoal(goalId);
      goal = translationFactory.getTranslationWithSource(goal.getId(), user.getUserLocale(),
          SPGoal.class, goal);
      
      articleDTO.getGoals().add(new BaseGoalDto(goal));
      List<BaseArticleDto> relatedArticles = articlesFactory
          .getArtilces(Constants.THEME, goalId, user.getUserLocale()).stream()
          .filter(a -> !a.getArticle().getId().equals(articleId)).map(BaseArticleDto::new)
          .limit(Constants.DEFAULT_RELATED_ARTICLE_COUNT).collect(Collectors.toList());
      
      articleDetailResponse.add(Constants.PARAM_RELATED_ARTICLES, relatedArticles);
    }
    
    // add next articles if exist
    /* get all the article for the current article theme */
    if (articleDTO.getGoals().size() > 0) {
      List<ArticleDao> artilces = articlesFactory.getArtilces(Constants.THEME, articleDTO
          .getGoals().get(0).getId(), user.getUserLocale());
      /*
       * check the article is not current article and not present in the completed articles list.
       * Return the article as next article link url
       */
      
      int currentArticleIndex = artilces.size();
      int firstArticleIndex = 0;
      boolean isFirstIndexArticle = false;
      ArticleDao nextArticle = null;
      for (int i = 0; i < artilces.size(); i++) {
        ArticleDao ar = artilces.get(i);
        if (ar.getArticle().getId().equalsIgnoreCase(articleId)) {
          currentArticleIndex = i;
        }
        if (!ar.getArticle().getId().equalsIgnoreCase(articleId)
            && !userArticleProgressMap.containsKey(ar.getArticle().getId())) {
          /* in case list reaches at the end, then show the first article */
          if (!isFirstIndexArticle) {
            firstArticleIndex = i;
            isFirstIndexArticle = true;
          }
          
          /*
           * at any point of time, whenever i is greater, give the next article and break, when it
           * is at the last, show the first article in the list
           */
          if (i > currentArticleIndex) {
            nextArticle = ar;
            break;
          }
          nextArticle = artilces.get(firstArticleIndex);
        }
        
      }
      
      if (nextArticle != null) {
        articleDTO.setNextArticleUrl(nextArticle.getArticle().getArticleLinkUrl());
      }
    }
    
    // Optional<UserArticleProgressDao> nextArticle = userArticleProgressMap
    // .values()
    // .stream()
    // .filter(
    // ap -> (!ap.getArticle().getId().equals(articleId))
    // && (ap.getArticleStatus() != ArticleStatus.COMPLETED)
    // && newsCredArticle.getGoals().containsAll(ap.getArticle().getGoals())).findFirst();
    
    /*
     * check if article is present in user goal, then send the status of the article to show
     * respective call to action
     */
    articleDetailResponse.add("article", articleDTO);
    
    /* Track the article viewed */
    
    SPMessageEnvelop messageEnvelop = new SPMessageEnvelop();
    messageEnvelop.setMessageHandler(MessageHandlerType.EngagementMatrix);
    messageEnvelop.addData("userId", user.getId());
    messageEnvelop.addData("activityType", UserTrackingType.ArticleViewed);
    jmsTemplatePub.convertAndSend(messageEnvelop);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Article Detail response retunred " + articleDetailResponse);
    }
    
    return articleDetailResponse;
  }
  
  /**
   * Recommend the article.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the recommend article
   */
  public SPResponse recommendArticle(User user, Object[] param) {
    LOG.info("Enter recommendArticle method");
    String articleId = (String) param[0];
    boolean doReccomend = (boolean) param[1];
    SPResponse add = new SPResponse().add("recommendationCount",
        articlesFactory.reccomendArticle(articleId, user.getEmail(), doReccomend));
    
    /* Track the article */
    if (doReccomend) {
      RecommendedArticleTracking articleTracking = new RecommendedArticleTracking(user, false);
      trackArticles(articleTracking);
    } else {
      RecommendedArticleTracking reccomendationArticleTrackingBean = trackingRepository
          .findReccomendationArticleTrackingBean(user.getId(), articleId);
      if (reccomendationArticleTrackingBean != null) {
        trackingRepository.remove(reccomendationArticleTrackingBean);
      }
    }
    return add;
  }
  
  /**
   * <code>updateArticleToProfile</code> method will update the article profile to user
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the article id which will get updated in user profile.
   * @return the true or false
   */
  public SPResponse updateArticleToProfile(User user, Object[] param) {
    String articleId = (String) param[0];
    Assert.hasText(articleId, "Invalid Request Paramter");
    goalsFactory.updateArticle(user, articleId);
    
    ArticlesCompletedArticleTracking articleTracking = new ArticlesCompletedArticleTracking(false,
        user);
    trackArticles(articleTracking);
    goalsFactory.addArticleToUserGoals(user, articleId);
    
    Article article = articlesFactory.getArticle(articleId);
    if (article != null) {
      LogRequest logRequest = new LogRequest(LogActionType.ArticleCompleted, user);
      logRequest.addActivityMessage(MessagesHelper.getMessage(
          LogActionType.ArticleCompleted.getActivityKey(), article.getArticleLinkLabel(),
          article.getArticleLinkUrl()));
      logGateway.logActivity(logRequest);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to add a comment for the user.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the add comment
   */
  public SPResponse addComment(User user, Object[] param) {
    String comment = (String) param[0];
    String articleId = (String) param[1];
    
    articlesFactory.addComment(articleId, user, comment);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to remove the comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeComment(User user, Object[] params) {
    // get the comments user email
    String articleId = (String) params[1];
    // get the roles
    int index = (int) params[2];
    
    articlesFactory.removeComment(user, articleId, index);
    return new SPResponse().isSuccess();
  }
  
  /**
   * The helper method to update the user comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the update request
   */
  public SPResponse updateComment(User user, Object[] params) {
    // get the comment user email
    String articleId = (String) params[1];
    // get the roles
    int index = (int) params[2];
    // get the updated comment
    String commentStr = (String) params[3];
    
    articlesFactory.updateComment(user, articleId, index, commentStr);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>getAllSources</code> method will return all the sources present for the articles
   * 
   * @return the sp response.
   */
  private SPResponse getAllSources() {
    return new SPResponse().add(TOPICS, articlesFactory.getAllSourceTypes());
  }
  
  /**
   * <code>GetAllAuthors</code> method will return all the authors for the articles.
   * 
   * @return all the authors
   */
  private SPResponse getAllAuthors() {
    return new SPResponse().add(TOPICS, articlesFactory.getAllAuthors());
  }
  
  /**
   * <code>getAllThemes</code> method will return all the themes present in the system.
   * 
   * @param user
   *          - user
   * @return all the themes.
   */
  private SPResponse getAllThemes(User user) {
    Map<String, String> allThemes = null;
    if (user.getCompanyId() != null) {
      allThemes = goalsFactory.getAllThemesForCompany(user.getCompanyId(), user.getUserLocale());
    } else {
      allThemes = goalsFactory.getAllThemesForIndividual(user.getUserLocale());
    }
    return new SPResponse().add(TOPICS, allThemes);
  }
  
  /**
   * Helper method to get the trending and top rated articles.
   * 
   * @param user
   *          logged in user.
   * @return the list trending and top rated articles.
   */
  public SPResponse trendingAndTopRated(User user) {
    final SPResponse spResponse = new SPResponse();
    
    // add the trending articles
    List<BaseArticleDto> trendingArticleList = articlesFactory.getTrendingArticles();
    spResponse.add("trendingArticles", trendingArticleList);
    // add the top rated articles
    List<BaseArticleDto> topRatedarticleList = articlesFactory.getTopRatedArticles();
    spResponse.add("topArticles", topRatedarticleList);
    return spResponse;
  }
  
  /**
   * Helper method to get the training spotlight video.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the training spotlight request
   */
  public SPResponse trainingSpotlight(User user, Object[] params) {
    final SPResponse spResponse = new SPResponse();
    
    String goalId = (String) params[0];
    
    // if the goal id is present then get the articles for that goal
    Map<String, List<TrainingSpotLightDTO>> trainingSpotLightArticles = articlesFactory
        .getTrainingSpotLightArticles(user.getUserLocale());
    List<TrainingSpotLightDTO> articleList = null;
    if (!StringUtils.isEmpty(goalId)) {
      articleList = trainingSpotLightArticles.get(goalId);
    }
    
    // if no goal found or no articles found for the given goal
    if (articleList == null || articleList.isEmpty()) {
      Optional<String> findAny = getRandomKey(trainingSpotLightArticles);
      if (findAny.isPresent()) {
        articleList = trainingSpotLightArticles.get(findAny.get());
      }
    }
    
    // get the training spotlight video
    if (articleList != null) {
      spResponse.add(Constants.PARAM_TRAINING_SPOT_LIGHT,
          articleList.get(random.nextInt(articleList.size())));
    } else {
      spResponse.isSuccess();
    }
    return spResponse;
  }
  
  /**
   * Helper method to add the given article at the given position.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add request
   */
  public SPResponse addHomePageArticles(User user, Object[] params) {
    final SPResponse spResponse = new SPResponse();
    String articleId = (String) params[0];
    String shortDescription = (String) params[1];
    int position = (int) params[2];
    String companyId = (String) params[3];
    ArticleLocation articleLocation = (ArticleLocation) params[4];
    
    // validate if the article is present
    articlesFactory.addHomepageArticle(articleId, shortDescription, position, companyId,
        articleLocation);
    return spResponse.isSuccess();
  }
  
  /**
   * Helper method to get all the articles for the training library home page.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get request
   */
  public SPResponse getHomePageArticles(User user) {
    final SPResponse spResponse = new SPResponse();
    ArticleLocation content = ArticleLocation.Content;
    // get the company id
    String companyId = user.getCompanyId();
    if (companyId == null) {
      // user is an individual
      companyId = Constants.TRAINING_LIBRARY_HOME_INDIVIDUAL;
    }
    
    // get the list of articles for the given company
    List<ArticleListingDTO> allHomepageArticles = articlesFactory.getAllHomepageArticles(content,
        companyId, user.getUserLocale());
    
    // fall back on the business default
    if (allHomepageArticles == null) {
      allHomepageArticles = articlesFactory.getAllHomepageArticles(content,
          Constants.TRAINING_LIBRARY_HOME_BUSINESS, user.getUserLocale());
    }
    spResponse.add(content + "", allHomepageArticles);
    return spResponse;
  }
  
  /**
   * Gets a random key from the training spot light articles map.
   * 
   * @param trainingSpotLightArticles
   *          - the map of training spotlight articles
   * @return the optional key if found
   */
  public Optional<String> getRandomKey(
      Map<String, List<TrainingSpotLightDTO>> trainingSpotLightArticles) {
    return trainingSpotLightArticles.keySet().stream()
        .skip(random.nextInt(trainingSpotLightArticles.size())).findFirst();
  }
  
  /**
   * TrackTrainingLIbraryVisits.
   * 
   * @param user
   *          logged inuser
   * @param param
   *          contains the session
   * @return the traiing librray.
   */
  public SPResponse trackTrainingLibraryVisits(User user, Object[] param) {
    HttpSession httpSession = (HttpSession) param[0];
    /* check if user has already visited the articles */
    Boolean visitedTL = (Boolean) httpSession.getAttribute("visitedTrainingLibrary");
    if (visitedTL == null || !visitedTL.booleanValue()) {
      TrainingLibraryVisitTracking libraryVisitTracking = new TrainingLibraryVisitTracking(user,
          false);
      /* Track only for buisness users */
      if (StringUtils.isNotEmpty(user.getCompanyId())) {
        libraryVisitTracking.setCompanyId(user.getCompanyId());
        trackingRepository.storeTrackingInfomation(libraryVisitTracking);
        httpSession.setAttribute("visitedTrainingLibrary", true);
      }
      
    }
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
  }
  
  @Async
  private void trackArticles(TrackingBean trackingBean) {
    trackingRepository.storeTrackingInfomation(trackingBean);
  }
  
  /**
   * <code>shareArticle</code> method will share the article to the list of the user passed
   * 
   * @param user
   *          logged in user who wants to share the article.
   * @param param
   *          contains the parameters.
   * @return the resposne whether success or failures.
   */
  public SPResponse shareArticle(User user, Object[] param) {
    
    String articleId = (String) param[1];
    
    String comment = (String) param[2];
    @SuppressWarnings("unchecked")
    List<String> memberEmails = (List<String>) param[0];
    
    /* fetch the article detials */
    Article article = articlesFactory.getArticle(articleId);
    
    if (article == null || memberEmails == null || memberEmails.isEmpty()) {
      throw new InvalidRequestException("Article not found or no user to share with present.");
    }
    String shortDescription = getShortDescription(article);
    
    Map<String, Object> params = new HashMap<String, Object>();
    if (StringUtils.isBlank(comment)) {
      comment = null;
    }
    params.put("comment", comment);
    params.put("shortDescription", shortDescription);
    params.put("article", article);
    /* Adding the content here, as conditional text is not yet supported in stg file. */
    switch (article.getArticleType()) {
    case AUDIO:
      params.put(ARTICLE_LINK_TEXT, MessagesHelper.getMessage(Constants.PARAM_PLAY_AUDIO));
      params.put(ARTICLE_ORANGE_BAND_TEXT,
          MessagesHelper.getMessage(Constants.PARAM_ORANGEBAND_AUDIO));
      break;
    case PODCAST:
      params.put(ARTICLE_LINK_TEXT, MessagesHelper.getMessage(Constants.PARAM_PLAY_PODCAST));
      params.put(ARTICLE_ORANGE_BAND_TEXT,
          MessagesHelper.getMessage(Constants.PARAM_ORANGEBAND_PODCAST));
      break;
    case SLIDESHARE:
      params.put(ARTICLE_LINK_TEXT, MessagesHelper.getMessage(Constants.PARAM_PLAY_SLIDESHARE));
      params.put(ARTICLE_ORANGE_BAND_TEXT,
          MessagesHelper.getMessage(Constants.PARAM_ORANGEBAND_SLIDSHARE));
      break;
    case VIDEO:
      params.put(ARTICLE_LINK_TEXT, MessagesHelper.getMessage(Constants.PARAM_PLAY_VIDEO));
      params.put(ARTICLE_ORANGE_BAND_TEXT,
          MessagesHelper.getMessage(Constants.PARAM_ORANGEBAND_VIDEO));
      break;
    case TEXT:
    default:
      params.put(ARTICLE_LINK_TEXT, MessagesHelper.getMessage(Constants.PARAM_PLAY_READARTICLE));
      params.put(ARTICLE_ORANGE_BAND_TEXT,
          MessagesHelper.getMessage(Constants.PARAM_ORANGEBAND_ARTICLE));
      
      break;
    }
    params.put("articleType", article.getArticleType().toString());
    CompanyDao company = themeCacheableFactory.getCompanyByIdForTheme(user.getCompanyId());
    List<User> users = userRepository.findByEmail(memberEmails);
    params.put(Constants.PARAM_COMPANY, company);
    params.put(Constants.PARAM_NOTIFICATION_URL_PARAM, articleId);
    params.put(Constants.PARAM_NOTIFICATION_MESSAGE, MessagesHelper.getMessage(
        LogActionType.ArticleShare.getMessageKey(),user.getLocale(), new UserMarkerDTO(user).getName(),
        article.getArticleLinkLabel()));
    
    users.stream().forEach(
        mem -> {
          EmailParams emailParams = new EmailParams();
          emailParams.setTemplateName(NotificationType.ShareArticle.getTemplateName());
          emailParams.addParam(Constants.PARAM_MEMBER, user);
          List<String> tos = new ArrayList<String>();
          tos.add(mem.getEmail());
          emailParams.setTos(tos);
          emailParams.addParam(Constants.PARAM_FOR_USER, mem);
          emailParams.setLocale(mem.getProfileSettings().getLocale().toString());
          emailParams.getValueMap().putAll(params);
          emailParams.setSubject(MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
              + NotificationType.ShareArticle, mem.getLocale(),
              user.getFirstName(), user.getLastName(), article.getArticleLinkLabel()));
          notificationProcessor.process(emailParams, user, NotificationType.ShareArticle, false);
          ;
        });
    
    // log activity
    LogRequest logRequest = new LogRequest(LogActionType.ArticleShare, user);
    logRequest.addActivityMessage(MessagesHelper.getMessage(
        LogActionType.ArticleShare.getActivityKey(), article.getArticleLinkLabel(),
        article.getArticleLinkUrl()));
    logGateway.logActivity(logRequest);
    
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
  
  /**
   * <code>shareArticle</code> method will share the article to the list of the user passed
   * 
   * @param user
   *          logged in user who wants to share the article.
   * @param param
   *          contains the parameters.
   * @return the resposne whether success or failures.
   */
  public SPResponse getShortDescriptionForShare(User user, Object[] param) {
    
    String articleId = (String) param[0];
    
    /* fetch the article detials */
    Article article = articlesFactory.getArticle(articleId);
    
    if (article == null) {
      throw new InvalidRequestException("Article not found or no user to share with present.");
    }
    String shortDescription = getShortDescription(article);
    
    SPResponse response = new SPResponse();
    response.add("shortDescription", shortDescription);
    response.isSuccess();
    return response;
    
  }
  
  private String getShortDescription(Article article) {
    
    StringBuffer sb = new StringBuffer();
    List<String> content = article.getContent();
    if (content != null) {
      for (String contentPara : content) {
        // break if the content has an img tag
        if (contentPara.contains("img")) {
          break;
        }
        sb.append(contentPara);
        if (sb.length() > 300) {
          break;
        }
      }
      // sb.append("...");
    }
    return sb.toString();
  }
  
  /**
   * Recommend the article.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the response to the recommend article
   */
  public SPResponse useFullArticle(User user, Object[] param) {
    LOG.info("Enter useFullArticle method");
    String articleId = (String) param[0];
    String isUseFull = (String) param[1];
    
    SPResponse response = new SPResponse().add("articleUseFullnessCount",
        articlesFactory.addUseFullnessCount(articleId, Boolean.valueOf(isUseFull)));
    response.isSuccess();
    
    /* Track the article */
    ArticleUsefullArticleTracking articleTracking = new ArticleUsefullArticleTracking(user, false);
    articleTracking.setUseFull(Boolean.valueOf(isUseFull));
    trackArticles(articleTracking);
    
    return response;
  }
  
}
