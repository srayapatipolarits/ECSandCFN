package com.sp.web.service.og;

import com.sp.web.controller.library.TrainingLibraryController;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.library.NewsCredVideoArticle;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.utils.MessagesHelper;
import com.sp.web.utils.og.OpenGraph;
import com.sp.web.utils.og.OpenGraphNamespace;
import com.sp.web.utils.og.SPOgType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SPPogFactory class provides the OG meta data for SP pages.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SPOgFactory {
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * getOGData will populate the og data.
   * 
   * @param graph
   *          open graph.
   * @param url
   *          contains the url of the
   * @param user
   *          logged in user
   * @throws Exception
   *           in case eny exception thrown.
   */
  public void getOGData(OpenGraph graph, String url, User user) throws Exception {
    
    SPOgType spOgType = SPOgType.getOgType(url);
    /* check if user has access to the URL */
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    if (!company.getFeatureList().contains(spOgType.getSpFeature())) {
      throw new SPException("Unauthorized URL");
    }
    
    OpenGraphNamespace openGraphNamespace = new OpenGraphNamespace("og", "http:// ogp.me/ns#");
    switch (spOgType) {
    case Article:
      String[] urlId = url.split("\\?");
      String articleId = urlId.length == 2 ? urlId[1] : null;
      
      if (StringUtils.isNotBlank(articleId)) {
        Article article = articlesFactory.getArticle(articleId);
        graph.setProperty(openGraphNamespace, "title", article.getArticleLinkLabel());
        graph.setProperty(openGraphNamespace, "description", article.getShortDescription(140));
        graph.setProperty(openGraphNamespace, "image", article.getImageUrl());
        
        String author = StringUtils.join(article.getAuthor(), ",");
        graph.setProperty(openGraphNamespace, "authorSource",
            author + " - " + article.getArticleSource());
        graph.setProperty(openGraphNamespace, "url", "/sp"
            + TrainingLibraryController.TRAINING_LIBRARY_ARTICLE_URL + "?" + article.getId());
        switch (article.getArticleType()) {
        case VIDEO:
          graph.setProperty(openGraphNamespace, "type", "video");
          graph.setProperty(openGraphNamespace, "videoUrl",
              ((NewsCredVideoArticle) article).getVideoUrl());
          
          break;
        default:
          graph.setProperty(openGraphNamespace, "type", "web");
          break;
        }
      }
      break;
    case Default:
      break;
    default:
      graph
          .setProperty(
              openGraphNamespace,
              "description",
              MessagesHelper.getMessage("og." + spOgType.toString() + ".description",
                  user.getLocale()));
      graph.setProperty(openGraphNamespace, "title",
          MessagesHelper.getMessage("og." + spOgType.toString() + ".title", user.getLocale()));
      graph.setProperty(openGraphNamespace, "image",
          MessagesHelper.getMessage("og." + spOgType.toString() + ".imageUrl", user.getLocale()));
      graph.setProperty(openGraphNamespace, "url",
          MessagesHelper.getMessage("og." + spOgType.toString() + ".url", user.getLocale()));
      graph.setProperty(openGraphNamespace, "type", "web");
      break;
    }
  }
  
}
