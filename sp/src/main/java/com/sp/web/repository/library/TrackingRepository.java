package com.sp.web.repository.library;

import com.sp.web.model.ArticleTrackingBean;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.TrackingBean;
import com.sp.web.model.tracking.ArticlesCompletedArticleTracking;
import com.sp.web.model.tracking.RecommendedArticleTracking;
import com.sp.web.model.tracking.SP360RequestTracking;
import com.sp.web.model.tracking.TrainingLibraryVisitTracking;

import java.util.List;

/**
 * <code>TrackingRepository</code> will store the tracking information for the user in the database.
 * 
 * @author pradeep
 *
 */
public interface TrackingRepository {
  
  /**
   * <code>storeTrackingInforation</code> method will store the tracking bean in the database.
   * 
   * @param trackingBean
   *          to be stored.
   */
  void storeTrackingInfomation(TrackingBean trackingBean);
  
  /**
   * <code>findTrackingBean</code> method will retreive the tracking beean from the mongo for the
   * current logged inuser
   * 
   * @param userId
   *          for which tracking info to be retreived.
   * @param articleId
   *          - article id
   * @return the tracking bean.
   */
  ArticleTrackingBean findArticleTrackingBean(String userId, String articleId);
  
  /**
   * <code>findTrackingBean</code> method will retrieve the tracking bean from the mongo for the
   * current logged inuser
   * 
   * @param userId
   *          for which tracking info to be retrieved.
   * @param count
   *          the count of articles -1 for all
   * @return the list of tracking bean.
   */
  List<ArticleTrackingBean> findAllArticleTrackingBean(String userId, int count);
  
  /**
   * @return - get all the tracking beans.
   */
  List<ArticleTrackingBean> findAllArticleTrackingBean();
  
  /**
   * Get the bookmark tracking bean for the user and the article.
   * 
   * @param userId
   *          - user
   * @param articleId
   *          - article id
   * @return the bookmark tracking bean if found
   */
  BookMarkTracking findBookMarkTrackingBean(String userId, String articleId);
  
  /**
   * Gets the list of articles for the given count. If article count is -1 then all the articles are
   * retrieved.
   * 
   * @param userId
   *          - user id
   * @param count
   *          - count of articles
   * @return the list of bookmarked articles
   */
  List<BookMarkTracking> findAllBookMarkTrackingBean(String userId, int count);
  
  /**
   * Get a list of all the bookmark in the database.
   * 
   * @return the list of all bookmark
   */
  List<BookMarkTracking> findAllBookMarkTrackingBean();
  
  /**
   * Remove the tracking bean.
   * 
   * @param trackingBean
   *          - tracking bean
   */
  void remove(TrackingBean trackingBean);
  
  /**
   * Get the list of all the articles that have high article access count.
   * 
   * @return the list of article id's that are trending
   */
  List<String> findTrendingArticle();
  
  /**
   * findRecommendedTrackingBean method will return the tracking beans for the company and the list
   * of users passed.
   * 
   * @param companyId
   *          of the user.
   * @param users
   *          list of users.
   * @return list of recommendedArticles.
   */
  List<RecommendedArticleTracking> findRecommendedTrackingBean(String companyId, List<String> users);
  
  List<TrainingLibraryVisitTracking> getTrainingLibraryVist(String companyId, List<String> users);
  
  List<BookMarkTracking> getAllBookMarKTrackingBean(List<String> users, String companyId);
  
  List<ArticlesCompletedArticleTracking> getAllArticlesCompleted(List<String> users,
      String companyId);
  
  List<SP360RequestTracking> findAllSp360RequestTracking(String companyId, List<String> users);
  
  /**
   * Get the recommendation tracking for the given user and article.
   * 
   * @param id
   *          - user id
   * @param articleId
   *          - article id
   * @return recommendation tracking if found
   */
  RecommendedArticleTracking findReccomendationArticleTrackingBean(String id, String articleId);
  
  /**
   * Remove all book marking tracking bean will remove all the book marks from the system.
   * 
   * @param userId
   */
  void removeAllBoookMarksTracking(String userId);
  
}
