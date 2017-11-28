package com.sp.web.controller.systemadmin.library;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.library.ArticleSelectListingDTO;
import com.sp.web.dto.library.BaseArticleDto;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The controller helper for training library home.
 */
@Component
public class TrainingLibraryAdminControllerHelper {
  
  @Autowired
  TrainingLibraryArticleRepository articleRepository;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  /**
   * Helper method to get all the companies from the db.
   * 
   * @param user
   *          - logged in user
   * @return the list of companies from the db
   */
  public SPResponse getAll(User user) {
    SPResponse resp = new SPResponse();
    List<TrainingLibraryHomeArticle> homeArticles = articleRepository.findAllHomePageArticles();
    // get the list of unique company id's
    Set<String> collect = homeArticles
        .stream()
        .map(TrainingLibraryHomeArticle::getCompanyId)
        .filter(
            cid -> (cid != null && !(cid.equalsIgnoreCase(Constants.TRAINING_LIBRARY_HOME_BUSINESS) || (cid
                .equalsIgnoreCase(Constants.TRAINING_LIBRARY_HOME_INDIVIDUAL)))))
        .collect(Collectors.toSet());
    // get the list of company entities from the db for the given company id's
    List<Company> companyList = accountRepository.findCompanyById(collect);
    // send the company back in the response
    resp.add(Constants.PARAM_COMPANY,
        companyList.stream().map(CompanyDTO::new).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Get all the articles for the given company.
   * 
   * @param user
   *          - user
   * @return the articles for the given company
   */
  public SPResponse getArticles(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    // get the company id
    String companyId = (String) params[0];
    // get the articles for the given company id
    ArticleLocation content = ArticleLocation.Content;
    List<TrainingLibraryHomeArticle> findHomepageArticlesByLocation = articleRepository
        .findHomepageArticlesByLocation(content, companyId);
    Company company = accountRepository.findCompanyById(companyId);
    resp.add(content + "", findHomepageArticlesByLocation);
    resp.add(Constants.PARAM_COMPANY + "", company);
    return resp;
  }
  
  /**
   * Helper method to delete the articles for the given company id.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the success or failure fags
   */
  public SPResponse delete(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    // get the company id
    String companyId = (String) params[0];
    articlesFactory.deleteHomePageArticles(companyId);
    return resp.isSuccess();
  }
  
  /**
   * getAllArticles will return all the articles present in the system.
   * 
   * @param user
   *          logged in user.
   * @return the SPResponse.
   */
  public SPResponse getAllArticles(User user) {
    SPResponse response = new SPResponse();
    List<ArticleDao> allArticles = articlesFactory.getAllArticles(Constants.DEFAULT_LOCALE);
    List<BaseArticleDto> collect = allArticles.stream().map(BaseArticleDto::new)
        .collect(Collectors.toList());
    return response.add(Constants.PARAM_ARTICLE_LIST, collect);
  }
}
