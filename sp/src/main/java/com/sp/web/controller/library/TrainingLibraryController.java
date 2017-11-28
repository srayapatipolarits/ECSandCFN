package com.sp.web.controller.library;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * <code>TrainingLibraryController</code> will provide the methods for the training articles.
 * 
 * @author pradeep
 *
 */
@Controller
public class TrainingLibraryController {

  public static final String TRAINING_LIBRARY_ARTICLE_URL = "/trainingLibrary/article";
  
  /** Training library controller helper. */
  @Autowired
  private TrainingLibraryControllerHelper trainingLibraryHelper;

  @RequestMapping(value = TRAINING_LIBRARY_ARTICLE_URL, method = RequestMethod.GET)
  public String trainingLibraryArticleView(Authentication token, HttpSession httpSession) {
    ControllerHelper.process(trainingLibraryHelper::trackTrainingLibraryVisits, token, httpSession);
    return "trainingLibraryArticle";
  }

  @RequestMapping(value = "/trainingLibrary")
  public String trainingHomePage(Authentication token, HttpSession httpSession) {
    ControllerHelper.process(trainingLibraryHelper::trackTrainingLibraryVisits, token, httpSession);
    return "trainingHome";
  }
  
  @RequestMapping(value = "/trainingLibrary/allBookmarks")
  public String allBookmarksView(Authentication token, HttpSession httpSession) {
    ControllerHelper.process(trainingLibraryHelper::trackTrainingLibraryVisits, token, httpSession);
    return "allBookmarks";
  }
  
  @RequestMapping(value = "/trainingLibrary/search")
  public String searchView(Authentication token, HttpSession httpSession) {
    ControllerHelper.process(trainingLibraryHelper::trackTrainingLibraryVisits, token, httpSession);
    return "search";
  }
  
  /**
   * <code>getAllTopics</code> method will return all the goals/authors/source for which article to be serached.
   * 
   * @param topicParam
   *          is parameter specifying whether to return all the goals/authors or source.
   * @return SPResponse.
   */
  @RequestMapping(value = "/trainingLibrary/getAllTopics", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllTopics(@RequestParam String topic, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::getAllTopics, token, topic);
  }

  /**
   * <code>getAllArticles</code> method will return the articles depending upon the filter present. IF not filter is
   * present then all articles will be returned
   * 
   * @param authenticationToken
   *          Username password token
   * @return the All Artilces in json format.
   */
  @RequestMapping(value = "/trainingLibrary/results", method = RequestMethod.GET)
  public String getAllArticles(Authentication token, HttpSession httpSession) {
    ControllerHelper.process(trainingLibraryHelper::trackTrainingLibraryVisits, token, httpSession);
    return "articleResults";
  }

  @RequestMapping(value = "/trainingLibrary/getResults", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllArticlesForCategory(
      @RequestParam(required = false, defaultValue = "") String topicCategory,
      @RequestParam(required = false) String topicValue, Authentication authenticationToken) {
    return ControllerHelper.process(trainingLibraryHelper::getAllArticles, authenticationToken,
        topicCategory, topicValue);
  }

  @RequestMapping(value = "/trainingLibrary/getArticleDetail/{articleId}", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getArticleDetail(@PathVariable String articleId, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::getArticleDetail, token, articleId);
  }

  /**
   * <code>updateArticleToUser</code> method will update the article to user dashboard for
   * completion.
   * 
   * @param articleId
   *          which will be updated in user profile.
   * @param toke
   *          for the logged in user
   * @return the sp resposne
   */
  @RequestMapping(value = "/trainingLibrary/udpateArticleToProfile", method = RequestMethod.POST)
  @ResponseBody
  @Audit(actionType = LogActionType.ArticleCompleted, type = ServiceType.LEARNING_LIBRARY)
  public SPResponse updateArticleToProfile(String articleId, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::updateArticleToProfile, token, articleId);
  }

  @RequestMapping(value = "/trainingLibrary/recommendArticle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse recommendArticle(@RequestParam String articleId,
      @RequestParam(defaultValue = "true") boolean doReccomend,
      Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::recommendArticle, token, articleId, doReccomend);
  }

  @RequestMapping(value = "/trainingLibrary/addComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addComment(@RequestParam String comment, Authentication token,
      @RequestParam String articleId) {
    return ControllerHelper.process(trainingLibraryHelper::addComment, token, comment, articleId);
  }

  @RequestMapping(value = "/trainingLibrary/updateComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateComment(@RequestParam String candidateEmail, @RequestParam int index,
      @RequestParam String comment, Authentication token, @RequestParam String articleId) {
    return ControllerHelper.process(trainingLibraryHelper::updateComment, token, candidateEmail, articleId, index,
        comment);
  }

  @RequestMapping(value = "/trainingLibrary/removeComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeComment(@RequestParam String commentorEmail, @RequestParam int index,
      Authentication token, @RequestParam String articleId) {
    return ControllerHelper.process(trainingLibraryHelper::removeComment, token, commentorEmail, articleId, index);
  }

  /**
   * Controller method to get the related article for the given article.
   * 
   * @param articleId
   *          - article id
   * @param token
   *          - logged in user
   * @return
   *       the response to the related articles call
   */
  @RequestMapping(value = "/trainingLibrary/trendingAndTopRated", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse trendingAndTopRated(Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::trendingAndTopRated, token);
  }
  

  /**
   * The controller method to get the training spotlight video.
   * 
   * @param goalId
   *          - goal id
   * @param token
   *          - logged in user
   * @return
   *    the response to the get training spotlight video
   */
  @RequestMapping(value = "/trainingLibrary/trainingSpotlight", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse trainingSpotlight(@RequestParam(required = false) String goalId,
      Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::trainingSpotlight, token, goalId);
  }

  /**
   * Controller method to add the article at the hero section.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description optional
   * @param position
   *          - position
   * @param token
   *          - logged in user
   * @return
   *      the response to the add request
   */
// No longer user as the hero articles have been removed from design.
// 10/06/2015 - Dax Abraham  
//  @RequestMapping(value = "/trainingLibrary/admin/add/heroArticles", method = RequestMethod.POST)
//  @ResponseBody
//  public SPResponse addHeroArticles(@RequestParam String articleId,
//      @RequestParam(required = false) String shortDescription, @RequestParam int position,
//      Authentication token) {
//    return ControllerHelper.process(trainingLibraryHelper::addHomePageArticles, token, articleId,
//        shortDescription, position, ArticleLocation.Hero);
//  }

  /**
   * The controller method to add the article at the content section.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description optional
   * @param position
   *          - position
   * @param token
   *          - logged in user
   * @return
   *      the response to the add request
   */
  @RequestMapping(value = "/trainingLibrary/admin/add/contentArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addContentArticles(@RequestParam String articleId,
      @RequestParam(required = false) String shortDescription, @RequestParam int position,
      @RequestParam String companyId, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::addHomePageArticles, token, articleId,
        shortDescription, position, companyId, ArticleLocation.Content);
  }

  /**
   * The controller method to add the article at the content section.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description optional
   * @param position
   *          - position
   * @param token
   *          - logged in user
   * @return
   *      the response to the add request
   */
  @RequestMapping(value = "/trainingLibrary/homeArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getHeroArticles(Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::getHomePageArticles, token);
  }
  
  
  /**
   * The controller method to share the article to the other users.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description optional
   * @param position
   *          - position
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/trainingLibrary/shareArticle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareArticle(@RequestParam List<String> toList, @RequestParam String articleId,
      @RequestParam(required = false) String comment, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::shareArticle, token, toList, articleId,comment);
  }
  
  /**
   * The controller method to share the article to the other users.
   * 
   * @param articleId
   *          - article id
   * @param shortDescription
   *          - short description optional
   * @param position
   *          - position
   * @param token
   *          - logged in user
   * @return the response to the add request
   */
  @RequestMapping(value = "/trainingLibrary/getShortDescription", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getShortDescriptionForShare(@RequestParam String articleId,
      Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::getShortDescriptionForShare, token, articleId);
  }
  
  /**
   * View For Share.
   * 
   */
  
  @RequestMapping(value = "/trainingLibrary/share", method = RequestMethod.GET)
  public String validateCancelSP(Authentication token,
      @RequestParam(required = false) String theme) {
    return "shareArticle";
  }
  
  @RequestMapping(value = "/trainingLibrary/usefullArticle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse useFullArticle(@RequestParam String articleId,
      @RequestParam String isUseFull, Authentication token) {
    return ControllerHelper.process(trainingLibraryHelper::useFullArticle, token, articleId,
        isUseFull);
  }

}
