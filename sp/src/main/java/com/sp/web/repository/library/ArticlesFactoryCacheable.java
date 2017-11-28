package com.sp.web.repository.library;

import com.sp.web.dao.article.ArticleDao;
import com.sp.web.exception.ArticleNotFoundException;
import com.sp.web.model.article.Article;
import com.sp.web.service.goals.SPGoalFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 * 
 *         The class to store all the catchable methods for articles factory.
 */
@Component
public class ArticlesFactoryCacheable {
  
  private TrainingLibraryArticleRepository articleRepository;
  private ArticlesComparator articlesComparator;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Inject
  public ArticlesFactoryCacheable(TrainingLibraryArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
    articlesComparator = new ArticlesComparator();
  }
  
  /**
   * Get the article for the given article id.
   * 
   * @param articleId
   *          - article id
   * @return the article
   */
  @Cacheable("article")
  public Article getArticle(String articleId) {
    return Optional.ofNullable(articleRepository.findArticleById(articleId)).orElseThrow(
        () -> new ArticleNotFoundException(articleId));
  }
  
  /**
   * Get all the list of articles.
   * 
   * @return - list of articles
   */
  @Cacheable(value = "article", key = "#root.methodName+#locale")
  public List<ArticleDao> getAllArticles(String locale) {
    List<Article> artilcesList = articleRepository.getAllArticles();
    return sortAndConvertToDao(artilcesList, locale);
  }
  
  /**
   * Method to sort and convert to dao.
   * 
   * @param artilcesList
   *          - the article list
   * @param locale
   *          of the user.
   * @return the article dao list
   */
  public List<ArticleDao> sortAndConvertToDao(List<Article> artilcesList, String locale) {
    artilcesList.sort(articlesComparator);
    List<ArticleDao> articleDaoList = new ArrayList<ArticleDao>();
    for (Article article : artilcesList) {
      ArticleDao articleDao = new ArticleDao(article, goalsFactory, locale);
      articleDaoList.add(articleDao);
    }
    return articleDaoList;
  }
}
