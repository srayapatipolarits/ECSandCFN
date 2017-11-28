package com.sp.web.repository.library;

import com.sp.web.model.article.Article;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.library.ArticleMetaData;
import com.sp.web.model.library.TrainingLibraryHomeArticle;

import java.util.List;
import java.util.Set;

/**
 * <code>TrainingLibraryArticle</code> method will perform operation on the articles present inthe library.
 * 
 * @author pradeep
 *
 */
public interface TrainingLibraryArticleRepository {

  /**
   * <code>findArticleById</code> method will retrieve the article depending upon the id passed.
   * 
   * @param articleId
   *          of the article
   * @return the article associated with article.
   */
  Article findArticleById(String articleId);

  /**
   * creatTextArticle method will create the text article.
   * 
   * @param credTextArticle
   *            - newscred text article
   */
  void saveUpdateTextArticle(Article credTextArticle);

  /**
   * <code>findNewsCredTextArticleById</code> method will find the news cred text article and will display.
   * 
   * @param id
   *          for which articles to be returned
   * @return
   *      the article
   */
  Article findNewsCredTextArticleById(String id);

  /**
   * <code>create</code> the news cred video article.
   * 
   * @param credVideoArticle
   *            - the video article
   */
  void saveUpdateVideoArticle(Article credVideoArticle);

  /**
   * <code>getAllThemes</code> method will return all the themes present in the Sure People database.
   * 
   * @return
   *      all the goals
   */
  List<SPGoal> getAllThemes();

  /**
   * <code>getAllAuthors</code> methdo will return all the authors present in the articles.
   * 
   * @return all the authors in the system.
   */
  Set<String> getAllAuthors();

  /**
   * <code>getAllArticleSources</code> method will return all the article sources present in the system.
   * 
   * @return all the article sources.
   */
  Set<String> getAllArticleSources();

  /**
   * Get the articles for the given topic category and value.
   * 
   * @param topicCategory
   *              - topic category
   * @param topicValue
   *              - topic value
   */
  List<String> getArtilces(String topicCategory, String topicValue);

  /**
   * <code>getAllArticles</code> will return all the articles.
   * 
   * @return all the articles.
   */
  List<Article> getAllArticles();

  void updateGenericArticle(Object object);

  /**
   * Save the article meta data.
   * 
   * @param articleMetaData
   *            - article meta data
   */
  void saveArticleMetaData(ArticleMetaData articleMetaData);

  /**
   * Get the list of all the top rated articles that have high article recommend count.
   * 
   * @return
   *    the list of article id's that are top recommended 
   */
  List<String> findTopRatedArticle();

  /**
   * Find the article with the given article link label name.
   * 
   * @param articleLinkLabel
   *              - article link label 
   * @return
   *      the article if found
   */
  Article findArticleByName(String articleLinkLabel);

  /**
   * Remove the given article.
   * 
   * @param article
   *          - article
   */
  void remove(Article article);

  /**
   * Add the given home page article to the repository.
   * 
   * @param homepageArticle
   *            - home page article
   */
  void addHomepageArticle(TrainingLibraryHomeArticle homepageArticle);

  /**
   * Get the list of articles for the given space.
   * 
   * @param articleLocation
   *          - the article location
   * @param companyId 
   *          - company id
   * @return
   *      the list of articles
   */
  List<TrainingLibraryHomeArticle> findHomepageArticlesByLocation(ArticleLocation articleLocation,
      String companyId);

  /**
   * Get all the articles configured for the training library home.
   * 
   * @return
   *    the list of all the articles for the home page
   */
  List<TrainingLibraryHomeArticle> findAllHomePageArticles();

  /**
   * Delete the home page articles for the given company id.
   * 
   * @param companyId
   *          - company id
   *          
   * @return
   *    the number of records affected         
   */
  int deleteHomePageArticles(String companyId);
  
  /**
   * <code>findArticleById</code> method will retrieve the article depending upon the id passed.
   * 
   * @param articleId
   *          of the article
   * @return the article associated with article.
   */
  Article findArticleByOrginalGuidId(String articleId);
}
