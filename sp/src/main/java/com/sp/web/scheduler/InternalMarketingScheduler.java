package com.sp.web.scheduler;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.Company;
import com.sp.web.model.InternalMarketingAnalytics;
import com.sp.web.model.InternalMarketingAnalytics.BusinessAnalyticsDetail;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.article.Article;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.respository.systemadmin.SystemAdminRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The internal marketing scheduler that will send out email's to the registered users.
 */
@Component
public class InternalMarketingScheduler {
  
  private static final Logger log = Logger.getLogger(InternalMarketingScheduler.class);
  
  private static final String DEFAULT_IMAGE = "images/Article_Default.jpg";
  private static final String DEFAULT_VIDEO_IMAGE = "images/Video_Default.jpg";
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  SPGoalFactoryHelper goalsFactoryHelper;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  @Autowired
  private SystemAdminRepository systemAdminRepository;
  
  private static final Random rand = new Random();
  
  // private List<TipOfTheDay> allTipsOfTheDay;
  
  private String baseUrl;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private CommunicationGateway gateway;
  
  /**
   * Constructor.
   * 
   * @param environment
   *          - environment
   */
  @Inject
  public InternalMarketingScheduler(Environment environment) {
    baseUrl = environment.getProperty("base.serverUrl", "http://www.surepeople.com/");
  }
  
  /**
   * The method that is executed everyday for the internal marketing messaging processing.
   */
  @Scheduled(cron = "${internalMarketing.schedule}")
  public void process() {
    
    if (!GenericUtils.isJobServerNode(environment)) {
      return;
    }
    
    if (log.isInfoEnabled()) {
      log.info("Thread " + Thread.currentThread().getName()
          + ", Internal marketing scheduler executing.");
    }
    // going to get all the members in the system
    List<User> allUsers = userRepository.findAllMembers(false);
    sendEmail(allUsers);
  }
  
  /**
   * sendEmail method is use to send marketting email to the user.
   * 
   * @param allUsers
   *          list of users to whom emailis to be sent.
   */
  private void sendEmail(List<User> allUsers) {
    try {
      // update the tip of the day
      InternalMarketingAnalytics internalMarketingAnalytics = new InternalMarketingAnalytics();
      Map<String, BusinessAnalyticsDetail> map = new HashMap<String, InternalMarketingAnalytics.BusinessAnalyticsDetail>();
      // updateTipOfTheDay();
      
      List<Company> findAllCompanies = accountRepository.findAllCompanies();
      Map<String, Company> companyMap = findAllCompanies.stream().collect(
          Collectors.toMap(Company::getId, c -> c));
      
      Company company;
      String companyId;
      
      // loop over all the users and send out the appropriate email's as required.
      for (User user : allUsers) {
        
        // only users that have completed the assessment
        if (user.getUserStatus() != UserStatus.VALID
            || !user.getProfileSettings().isAutoUpdateLearning()) {
          continue;
        }
        
        companyId = user.getCompanyId();
        if (companyId != null) {
          company = companyMap.get(companyId);
          if (company == null) {
            company = accountRepository.findCompanyById(companyId);
            if (company == null) {
              log.warn("Company not found for company id :" + companyId);
              continue;
            }
          }
          if (company.isBlockAllMembers() || company.isDeactivated() || company.isErtiDeactivated()) {
            continue;
          }
          
          internalMarketingAnalytics.setAllBusinessEmails(internalMarketingAnalytics
              .getAllBusinessEmails() + 1);
          
          BusinessAnalyticsDetail businessAnalyticsDetail = map.get(company.getName());
          if (businessAnalyticsDetail == null) {
            businessAnalyticsDetail = new BusinessAnalyticsDetail();
            map.put(company.getName(), businessAnalyticsDetail);
          }
          businessAnalyticsDetail.setEmailSent(businessAnalyticsDetail.getEmailSent() + 1);
          businessAnalyticsDetail.setName(company.getName());
        } else {
          internalMarketingAnalytics.setAllIndividualEmails(internalMarketingAnalytics
              .getAllIndividualEmails() + 1);
        }
        // check if the user has set their goals
        String userGoalId = user.getUserGoalId();
        UserGoalDao userGoals;
        if (userGoalId == null) {
          // user has not set their goals
          userGoals = goalsFactoryHelper.addGoalsForUser(user);
        } else {
          userGoals = goalsFactoryHelper.getUserGoal(user);
        }
        
        // get the article to send
        sendNotification(user, userGoals);
        internalMarketingAnalytics.setAllEmails(internalMarketingAnalytics.getAllEmails() + 1);
      }
      
      internalMarketingAnalytics.getAnalyticsDetails().addAll(map.values());
      EmailParams emailParams = new EmailParams(NotificationType.InternalMarkettingAnalytics.getTemplateName(),
          environment.getProperty(Constants.PARAM_INTERNAL_MARKETING_ANALYTICS_EMAIL,
              "internalmarketing@surepeople.com"),
          MessagesHelper.getMessage(Constants.PARAM_INTERNAL_MARKETING_EMAIL_SUBJECT), Constants.DEFAULT_LOCALE);
      emailParams.addParam(Constants.PARAM_INTERNAL_MARKETING_ANALYTIC, internalMarketingAnalytics);
      emailParams.addParam(Constants.PARAM_NOTIFICATION_TYPE, NotificationType.InternalMarkettingAnalytics);
      gateway.sendMessage(emailParams);
    } catch (Exception e) {
      log.fatal("Error processing the internal marketing messages.", e);
    }
  }
  
  /**
   * Update the tips of the day from the database.
   * 
   * private void updateTipOfTheDay() { allTipsOfTheDay =
   * systemAdminRepository.getAllTipsOfTheDay(); for (TipOfTheDay tod : allTipsOfTheDay) { String
   * linkUrl = tod.getLinkUrl(); if (linkUrl != null) {
   * tod.setLinkUrl(linkUrl.replaceAll("\\[baseUrl\\]", baseUrl)); } String iconUrl =
   * tod.getIconImage(); if (iconUrl != null) { tod.setIconImage(iconUrl.replaceAll("\\[baseUrl\\]",
   * baseUrl)); } } }
   */
  
  private void sendNotification(User user, UserGoalDao userGoals) {
    
    /* select the random goal from user selected goals */
    List<UserGoalProgressDao> selectedGoalsProgressList = userGoals.getSelectedGoalsProgressList();
    Article article = null;
    int selectedGoalSize = selectedGoalsProgressList.size();
    List<Integer> goalsSizeList = IntStream.rangeClosed(0, selectedGoalSize - 1).boxed()
        .collect(Collectors.toList());
    
    Collections.shuffle(goalsSizeList);
    Map<String, UserArticleProgressDao> articleProgressMap = userGoals
        .getArticleProgressMap();
    for (Integer index : goalsSizeList) {
      if (article != null) {
        break;
      }
      /* get random goal */
      UserGoalProgressDao selectedUgp = selectedGoalsProgressList.get(index.intValue());
      
      /* get all the articles for the goals */
      List<ArticleDao> artilces = articlesFactory.getArtilces(Constants.THEME, selectedUgp
          .getGoal().getId(),user.getUserLocale());
      
      boolean foundArticle = false;
      // Get completed user article progress */
      
      List<Integer> articleSizeList = IntStream.rangeClosed(0, artilces.size() - 1).boxed()
          .collect(Collectors.toList());
      Collections.shuffle(articleSizeList);
      
      for (Integer articleIndex : articleSizeList) {
        if (foundArticle) {
          break;
        }
        ArticleDao articleDao = artilces.get(articleIndex);
        /* check if article is not completed by user */
        String articleId = articleDao.getArticle().getId();
        if (!articleProgressMap.containsKey(articleId)) {
          article = articleDao.getArticle();
          foundArticle = true;
        }
      }
    }
    
    // check if any article present in the list
    if (article != null) {
      
      Map<String, Object> params = new HashMap<String, Object>();
      
      Optional<String> findFirst = article.getGoals().stream().findFirst();
      SPGoal goal = goalsFactoryHelper.getGoal(findFirst.get(),user.getUserLocale());
      if (goal != null) {
        addArticlePrarameters(goal, params, article);
        notificationProcessor.process(NotificationType.InternalMarketingArticle, user, user,
            params, false);
      } else {
        log.warn("Article not sent for user :" + user.getEmail()
            + ": as goal not found for article :" + article.getId() + ": Goal id :"
            + findFirst.get());
      }
      return;
    }
    
    // Not sending anytnig in case user has completed all the articles from the selected practice areas.
    
    // checking from the goals progress list for articles from others goals for the user
  /*  ArrayList<UserGoalProgressDao> goalsArrayList = new ArrayList<UserGoalProgressDao>(
        userGoals.getGoalsProgressList());
    while (goalsArrayList.size() > 0) {
      UserGoalProgressDao userGoalProgressDao = goalsArrayList.get(rand.nextInt(goalsArrayList
          .size()));
      SPGoal goal = userGoalProgressDao.getGoal();
      if(userGoalProgressDao.getPrismLensWeight()>0 || userGoalProgressDao.getPrismWeight()>0){
        List<ArticleDao> artilces = articlesFactory.getArtilces(Constants.THEME, goal
            .getId());
        for (ArticleDao articleDao : artilces) {
          UserArticleProgressDao userArticleProgressDao = articleProgressMap.get(articleDao.getArticle().getId());
          if (userArticleProgressDao == null
              || userArticleProgressDao.getArticleStatus() != ArticleStatus.COMPLETED) {
            Map<String, Object> params = new HashMap<String, Object>();
            addArticlePrarameters(goal, params, articleDao.getArticle());
            notificationProcessor.process(NotificationType.InternalMarketingGoalsNotSet, user, user,
                params, false);
            return;
          }
        }  
      }
      goalsArrayList.remove(userGoalProgressDao);
    }*/
  }
  
  private void addArticlePrarameters(SPGoal goal, Map<String, Object> params, Article article) {
    params.put(Constants.PARAM_GOAL, goal);
    params.put(Constants.PARAM_ARTICLE, article);
    params.put(Constants.PARAM_AUTHOR, getAuthor(article));
    params.put(Constants.PARAM_ARTICLE_SOURCE, article.getArticleSource());
    params.put(Constants.PARAM_ARTICLE_IMAGE, getArticleImage(article));
    params.put(Constants.PARAM_ARTICLE_LINK_NAME,
        MessagesHelper.getMessage("article.email.link." + article.getArticleType()));
    params.put(Constants.PARAM_ARTICLE_CONTENT, getArticleContent(article));
    // params.put(Constants.PARAM_TIP_OF_THE_DAY, getRandomTipOfTheDay(goal, article));
  }
  
  /**
   * Get the content for the given article.
   * 
   * @param article
   *          - article
   * @return the content to display for the article
   */
  private String getArticleContent(Article article) {
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
   * Get a random tip of the day.
   * 
   * @param article
   *          - article
   * @param goal
   *          - theme
   * 
   * @return - random tip of the day
   * 
   *         private TipOfTheDay getRandomTipOfTheDay(SPGoal goal, Article article) { if
   *         (!CollectionUtils.isEmpty(allTipsOfTheDay)) { TipOfTheDay tipOfTheDay =
   *         allTipsOfTheDay.get(rand.nextInt(allTipsOfTheDay.size())); // do the replacements
   *         String description = tipOfTheDay.getDescription(); if (description != null) {
   *         description = description.replaceAll("\\[THEME\\]", goal.getName());
   *         tipOfTheDay.setDescription(description); } return tipOfTheDay; } return null; }
   */
  /**
   * Get the article image URL.
   * 
   * @param article
   *          - article
   * @return the article image url or default url
   */
  private String getArticleImage(Article article) {
    String imageUrl = article.getImageUrl();
    if (imageUrl == null) {
      switch (article.getArticleType()) {
      case VIDEO:
        imageUrl = baseUrl + DEFAULT_VIDEO_IMAGE;
        break;
      default:
        imageUrl = baseUrl + DEFAULT_IMAGE;
      }
    }
    
    // add the base url to any relative url for the images
    if (!imageUrl.startsWith("http")) {
      imageUrl = baseUrl + imageUrl;
    }
    return imageUrl;
  }
  
  private String getAuthor(Article article) {
    List<String> author = article.getAuthor();
    return CollectionUtils.isEmpty(author) ? null : author.get(0);
  }
  
  /**
   * sendMarketingEmail methdo is used from thea dminstration consoole to send email to a specific
   * user.
   * 
   * @param user
   *          list of ser.
   */
  public void sendMarketingEmail(List<User> user) {
    sendEmail(user);
    ;
  }
  
}
