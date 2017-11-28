package com.sp.web.controller.tracking;

import com.sp.web.Constants;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dto.library.ArticleDetailsDto;
import com.sp.web.dto.library.ArticleDto;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.TrackingForm;
import com.sp.web.model.ArticleTrackingBean;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <code>TrakingControllerHelper</code> class will return the tracking of the recently visited
 * articles by the user.
 * 
 * @author pradeep
 *
 */
@Component
public class TrackingControllerHelper {

  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(TrackingControllerHelper.class);

  @Autowired
  private TrackingRepository trackingRepository;

  @Autowired
  private ArticlesFactory articleFactory;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  /**
   * <code>getRecentlyVisitedArticle</code> method will return the recently visited article by the
   * user
   * 
   * @param user
   *          logged in
   * @return the SPResponse with a max of 10 articles.
   */
  public SPResponse getRecentlyVisitedArticle(User user) {
    SPResponse response = new SPResponse();

    List<ArticleTrackingBean> articlesTrBean = trackingRepository.findAllArticleTrackingBean(user
        .getId(), Constants.DEFAULT_ARTICLES_COUNT);

    response.add("trackingResponse", process(articlesTrBean, ArticleDto::new, user.getUserLocale()));
    response.isSuccess();
    return response;

  }

  /**
   * Processes the given list of article tracking bean.
   * 
   * @param trackingBeanList
   *          - article tracking bean list
   * @param dtoSupplier
   *          - the dto to create         
   * @param locale of the user
   * @return
   *      the list of Article DTO's
   */
  public List<ArticleDto> process(List<? extends ArticleTrackingBean> trackingBeanList,
      Function<ArticleDao, ArticleDto> dtoSupplier, String locale) {
    final List<ArticleTrackingBean> articlesTrackingToRemove = new ArrayList<ArticleTrackingBean>();
    
    List<ArticleDto> trackingBeanDto = trackingBeanList.stream().map((tb -> {
        Article article = null;
        try {
          article = articleFactory.getArticle(tb.getArticleId());
        } catch (Exception e) {
          LOG.warn("Article not found :" + tb.getArticleId(), e);
          articlesTrackingToRemove.add(tb);
        }
        return new ArticleDao(article, goalsFactory, locale);
      })).filter(trB -> trB != null).map(dtoSupplier).collect(Collectors.toList());

    // check if any tracking beans for removal
    if (!articlesTrackingToRemove.isEmpty()) {
      articlesTrackingToRemove.forEach(trackingRepository::remove);
    }
    return trackingBeanDto;
  }

  /**
   * <code>TrackInforamtion</code> method will track the user information and will store it in the
   * databse.
   * 
   * @param user
   *         for which information is to be tracked.
   * @param parms
   *         contains the information
   * @return the SPResponse
   */
  public SPResponse trackInformation(User user, Object[] parms) {
    final SPResponse response = new SPResponse();

    if (parms == null || parms.length == 0) {
      throw new InvalidRequestException("INvalid Parmaters");
    }
    
    TrackingForm trackingForm = (TrackingForm) parms[0];
    
    /* get the information for which we are tracking the info */
    String articleId = trackingForm.getArticleId();
    ArticleTrackingBean articleTrackingBean = trackingRepository.findArticleTrackingBean(
        user.getId(), articleId);
    
    if (articleTrackingBean == null) {
      articleTrackingBean = new ArticleTrackingBean(articleId, user.getId());
    } else {
      articleTrackingBean.updateAccessTime();
    }
    trackingRepository.storeTrackingInfomation(articleTrackingBean);
    
    response.isSuccess();
    return response;
  }

  /**
   * <code>TrackInforamtion</code> method will track the user information and will store it in the
   * databse.
   * 
   * @param user
   *          for which information is to be tracked.
   * @param parms
   *          contains the information.
   * @return the SPResponse
   */
  public SPResponse bookMarkArticle(User user, Object[] parms) {
    SPResponse response = new SPResponse();

    if (parms == null || parms.length == 0) {
      throw new InvalidRequestException("INvalid Parmaters");
    }
    
    TrackingForm trackingForm = (TrackingForm) parms[0];

    // get the bookmark status
    boolean isBookMarked = (boolean) parms[1];
    goalsFactory.bookMarkArticle(isBookMarked, trackingForm.getArticleId(), user);
    
    /*
     * check if bookmark alrady exist for the user , Tracking Bean is required for book marking for
     * Spectrum, where in groups to show the book mark count.
     */
    BookMarkTracking articleTrackingBean = trackingRepository.findBookMarkTrackingBean(
        user.getId(), trackingForm.getArticleId());
    if (articleTrackingBean == null) {
      if (!isBookMarked) {
        throw new InvalidRequestException("Article not bookmarked.");
      }
      // bookmark the article
      articleTrackingBean = trackingForm.getBookMarkTrackingBean(user);
      articleTrackingBean.setBookMarked(isBookMarked);
      articleTrackingBean.setCompanyId(user.getCompanyId());
      trackingRepository.storeTrackingInfomation(articleTrackingBean);
    } else {
      if (isBookMarked) {
        throw new InvalidRequestException("Article already bookmarked.");
      }
      // removing the bookmark
      trackingRepository.remove(articleTrackingBean);
    }

    response.isSuccess();
    return response;

  }

  /**
   * <code>getAllBookMarkedArticles</code> method will return the book marked articles.
   * 
   * @param user
   *          logged in user
   * @param params
   *          params 
   *          
   * @return the SPREsposne containing the book marked articles.
   */
  public SPResponse getBookMarkedArticles(User user, Object[] params) {

    final SPResponse response = new SPResponse();
    
    int count = (int) params[0];
    
    // the type of response to send back
    // detailed ones for the bookmark listing and 
    // the smaller one for the right rail
    final Function<ArticleDao, ArticleDto> dtoSupplier = (count == -1) ? ArticleDetailsDto::new
        : ArticleDto::new;
    
    // get the count of articles to get
    if ( count == 0) {
      count = Constants.DEFAULT_ARTICLES_COUNT;
    }
    
    List<BookMarkTracking> allBookMarkedArticles = trackingRepository
        .findAllBookMarkTrackingBean(user.getId(), count);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Tracking bean present ofr the user " + user.getEmail() + ", are :"
          + allBookMarkedArticles);
    }
    
    response.add("articles", process(allBookMarkedArticles, dtoSupplier, user.getUserLocale()));
    return response;
  }
}