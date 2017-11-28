package com.sp.web.scheduler;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleReadability;
import com.sp.web.model.article.ArticleType;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.library.ArticleMetaData;
import com.sp.web.model.library.NewsCredImage;
import com.sp.web.model.library.NewsCredTextArticle;
import com.sp.web.model.library.NewsCredVideoArticle;
import com.sp.web.model.newscred.AuthorSet;
import com.sp.web.model.newscred.CollectionSet;
import com.sp.web.model.newscred.Content;
import com.sp.web.model.newscred.CustomFieldsSet;
import com.sp.web.model.newscred.ImageSet;
import com.sp.web.model.newscred.NewsCred;
import com.sp.web.model.newscred.NewsCredCollections;
import com.sp.web.model.readability.ReadabilityScore;
import com.sp.web.readability.Readability;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;
import com.sp.web.repository.readability.ReadabilityRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.utils.GenericUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <code>NewsCredSchedular</code> will push the content from news cred to SP system.
 * 
 * @author pradeep
 *
 */
@Component
public class NewsCredSchedular {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(NewsCredSchedular.class);
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  private TrainingLibraryArticleRepository trainingLibrary;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  UserGoalsRepository userGoalsRepository;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private ReadabilityRepository readabilityRepository;
  
  private int readabilityArticlesCount = 0;
  
  private double articleAvergeScoreSummation = 0.0;
  
  /**
   * The newscred scheduler.
   */
  @Scheduled(cron = "${newsCred.schedule}")
  public void processNewsCredArticles() {
    
    if (!GenericUtils.isJobServerNode(environment)) {
      return;
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName() + ":The News Cred scheduller got called !!!");
    }
    processSync();
    
  }
  
  /**
   * Process sync method is useful when we have to reload the sync without callng the schduelar.
   */
  public void processSync() {
    try {
      NewsCredCollections credCollections = getNewsCredDataCollection();
      
      if (credCollections == null) {
        LOG.error("Collections are not getting returne from NEWS CRED, please check ");
        return;
      }
      
      Map<SPGoal, NewsCred> newsCredDataMap = new HashMap<>();
      ArticleMetaData articleMetaData = new ArticleMetaData();
      Set<String> skipCollectionList = getCollectionSkipList();
      for (CollectionSet cs : credCollections.getCollectionSet()) {
        if (!skipCollectionList.contains(cs.getProviderId())) {
          // check if the goal is present in the system
          SPGoal goal = goalsRepository
              .findGoalByName(cs.getProviderId(), GoalCategory.GrowthAreas);
          if (goal == null) {
            goal = goalsRepository.findGoalByName(cs.getProviderId(), GoalCategory.Business);
          }
          if (goal != null) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Going to process for theme :" + goal.getName());
            }
            if (!newsCredDataMap.containsKey(goal)) {
              newsCredDataMap.put(goal, getNewsCredData(cs.getGuid()));
              articleMetaData.getThemes().add(cs.getProviderId());
            } else {
              LOG.warn("Ignoring duplicate feed :" + goal.getName() + ":" + cs.getGuid());
            }
          } else {
            LOG.error("Ignoring feed goal not present :" + cs.getProviderId());
          }
        }
      }
      
      ArrayList<String> articleNames = new ArrayList<String>();
      /* fetch all the articles from the NewsCred */
      
      for (Entry<SPGoal, NewsCred> ncd : newsCredDataMap.entrySet()) {
        final NewsCred newsCredData = ncd.getValue();
        if (newsCredData != null) {
          LOG.info("News Cred Data recived is" + newsCredData.getNumFound());
          List<Article> popoulateNewsCredArticle = popoulateNewsCredArticle(ncd, articleMetaData,
              articleNames);
          LOG.info("Total News Cred Articles populated are " + popoulateNewsCredArticle.size()
              + ": for goal id :" + ncd.getKey().getId());
          LOG.info(popoulateNewsCredArticle.stream().map(a -> "\"" + a.getId() + "\"")
              .collect(Collectors.joining(",")));
        }
      }
      
      trainingLibrary.saveArticleMetaData(articleMetaData);
      /*
       * This step is not required now, as we are only storing the completed articles in the article
       * progress. All the articles present in the theme will be displayed to the user. // get all
       * the goals final Map<String, SPGoal> goalsMap = goalsRepository.findAllGoals().stream()
       * .collect(Collectors.toMap(SPGoal::getId, g -> g)); // update the mandatory articles list
       * for each user // get all the user goals List<UserGoal> userGoalList =
       * userGoalsRepository.getAllUserGoals(); for (UserGoal userGoal : userGoalList) { // get the
       * article progress list of the user goal final List<UserArticleProgress> articleProgress =
       * userGoal.getArticleProgress(); // get the list of article id's final List<String>
       * userArticleIdList = articleProgress.stream()
       * .map(UserArticleProgress::getArticleId).collect(Collectors.toList()); boolean isUpdated =
       * false; // iterate over all the user goals and check if the mandatory // articles for the
       * selected goals have been added to the article // progress list if not add the same. for
       * (UserGoalProgress ugp : userGoal.getGoalProgress()) { if (ugp.isSelected()) { SPGoal spGoal
       * = goalsMap.get(ugp.getGoalId()); if (spGoal != null) { for (String articleId :
       * spGoal.getMandatoryArticles()) { if (!userArticleIdList.contains(articleId)) {
       * articleProgress.add(new UserArticleProgress(articleId, true)); isUpdated = true; } } } } }
       * // if the article progress list was updated then save the user goal if (isUpdated) {
       * userGoalsRepository.save(userGoal); } }
       */
    } catch (URISyntaxException e) {
      LOG.fatal(e.getMessage(), e);
    }
    // reset all the caches
    articlesFactory.resetAllCache();
    
    // Update the overall readability score along with History
    // Id is hard coded as it is for internal purposes only.
    try {
      ReadabilityScore score = readabilityRepository.getScoreById("zxy12345abc");
      ArrayList<ReadabilityScore> scoreHistory = null;
      if (score != null) {
        scoreHistory = score.getScoreHistory();
      } else {
        score = new ReadabilityScore();
        scoreHistory = new ArrayList<ReadabilityScore>();
        score.setId("zxy12345abc");
      }
      ReadabilityScore score1 = new ReadabilityScore();
      LocalDate now = LocalDate.now();
      
      score.setCurrentScore(articleAvergeScoreSummation / readabilityArticlesCount);
      score.setCalculatedOn(now);
      
      score1.setCalculatedOn(now);
      score1.setCurrentScore(articleAvergeScoreSummation / readabilityArticlesCount);
      scoreHistory.add(0, score1);
      score.setScoreHistory(scoreHistory);
      
      // Keep history to maximum 20 elements with the latest one added to the top.
      if (scoreHistory.size() > 20) {
        scoreHistory.subList(20, 21).clear();
      }
      
      readabilityRepository.saveReadabilityScore(score);
      
    } catch (Exception e) {
      LOG.warn("Error saving the overall average readability score :" + e);
    }
  }
  
  /**
   * <code>getNewsCredDataCollection</code> contains the themes from newscred.
   * 
   * @return - the newscred collection
   * @throws URISyntaxException
   *           - if there was an error in the url
   */
  private NewsCredCollections getNewsCredDataCollection() throws URISyntaxException {
    LOG.info("Enter getNewsCredData, getRestTemplate");
    String url = environment.getProperty(Constants.NEWS_CRED_COLLECTION_URL);
    URI url2 = new URI(url);
    NewsCredCollections newsCred = restTemplate.getForObject(url2, NewsCredCollections.class);
    LOG.info("news cred collection returned " + newsCred);
    return newsCred;
  }
  
  /**
   * <code>getNewsCreData</code> method will get the news cred data from news cred sysstem
   * 
   * @param themeGuid
   *          contains the theme Guid for the articles.
   * 
   * @return the NewsCred
   */
  private NewsCred getNewsCredData(String themeGuid) {
    
    LOG.info("Enter getNewsCredData, getRestTemplate " + themeGuid);
    
    String url = environment.getProperty(Constants.NEWS_CRED_URL);
    String formattedUrl = MessageFormat.format(url, themeGuid);
    
    URI url2 = null;
    try {
      url2 = new URI(formattedUrl);
    } catch (URISyntaxException e) {
      LOG.error(e.getMessage(), e);
    }
    // if (themeGuid.equalsIgnoreCase("3047e986b35ec96f307dfe3f433dffcb")) {
    // NewsCred newsCred = restTemplate.getForObject(url2, NewsCred.class);
    // LOG.info("news cred articles returned " + newsCred.getNumFound());
    // return newsCred;
    // }
    // return null;
    NewsCred newsCred = restTemplate.getForObject(url2, NewsCred.class);
    LOG.info("news cred articles returned " + newsCred.getNumFound());
    return newsCred;
    
  }
  
  private List<Article> popoulateNewsCredArticle(Entry<SPGoal, NewsCred> ncd,
      ArticleMetaData articleMetaData, ArrayList<String> articleNames) {
    
    Optional.ofNullable(ncd).orElseThrow(() -> new SPException("News Cred data is null"));
    
    LOG.info("Total articles returend from news cred are " + ncd.getValue().getNumFound());
    List<Article> newsCredArticles = new ArrayList<Article>();
    final SPGoal goal = ncd.getKey();
    // cleaning up mandatory articles from goal
    goal.getMandatoryArticles().clear();
    /* Iterating the articles */
    ncd.getValue().getCollectionItemSet().stream().forEach(newsCred -> {
      Content content = newsCred.getContent();
      try {
        newsCredArticles.add(processNewsCredArticle(content, articleMetaData, goal, articleNames));
      } catch (Exception e) {
        LOG.error("Error processing article :" + newsCred, e);
      }
      /* check the content type */
    });
    /* update SpGoal in the repository */
    goalsRepository.updateGoal(goal);
    return newsCredArticles;
    
  }
  
  /**
   * <code>processNewsCredTextArticle</code> method will processCredTetArticle and will give the
   * result News cred article back.
   * 
   * @param content
   *          - content
   * @param articleMetaData
   *          - article meta data
   * @param spGoal
   *          - theme name
   * @param articleNames
   *          - article names
   * @return the article
   */
  private Article processNewsCredArticle(Content content, ArticleMetaData articleMetaData,
      SPGoal spGoal, ArrayList<String> articleNames) {
    
    // check if article is existing in the system
    final String articleLinkLabel = ((String) content.getAdditionalProperties().get("title"))
        .trim();
    if (articleNames.contains(articleLinkLabel)) {
      throw new InvalidRequestException("Duplicate article :" + articleLinkLabel + ": Goal:"
          + spGoal.getId());
    }
    
    final String articleGuid = content.getGuid();
    final String originalGuid = content.getMetadata().getOriginalGuid();
    Article article = null;
    // Get the Article Type from Meta Data set of newscred content object
    ArticleType articleType = getArticleType(content);
    try {
      
      // check if an article of the same name exists
      article = trainingLibrary.findArticleByName(articleLinkLabel);
      // check if the article with the given id exists
      if (article == null && originalGuid != null) {
        article = trainingLibrary.findArticleByOrginalGuidId(originalGuid);
      }
      if (article == null) {
        article = trainingLibrary.findArticleById(articleGuid);
        article.setOriginalGuid(originalGuid);
      }
    } catch (Exception e1) {
      /* IN case article is not found, then create a new article. */
      article = createNewArticle(articleType, articleGuid, originalGuid);
    }
    
    // For Existing Article check if the article type has been updated in newscred
    if (!article.getArticleType().equals(articleType)) {
      Article tempArticle = null;
      switch (articleType) {
      case TEXT:
        tempArticle = new NewsCredTextArticle(ArticleType.TEXT);
        break;
      case VIDEO:
        tempArticle = new NewsCredVideoArticle(ArticleType.VIDEO);
        break;
      case SLIDESHARE:
        tempArticle = new NewsCredVideoArticle(ArticleType.SLIDESHARE);
        break;
      case PODCAST:
        tempArticle = new NewsCredVideoArticle(ArticleType.PODCAST);
        break;
      default:
        break;
      }
      // Copy the article properties to temp except the article type.
      BeanUtils.copyProperties(article, tempArticle, new String[] { "articleType" });
      article = tempArticle;
    }
    // set the article label
    article.setArticleLinkLabel(articleLinkLabel);
    
    /* set the author */
    final List<String> authorSet = article.getAuthor();
    authorSet.clear();
    final Set<String> metadataAuthorNames = articleMetaData.getAuthorNames();
    for (AuthorSet authorName : content.getAuthorSet()) {
      final String name = authorName.getName();
      authorSet.add(name);
      metadataAuthorNames.add(name);
    }
    
    article.setCreatedOn(content.getCreated_at());
    switch (article.getArticleType()) {
    case TEXT:
      processNewsCredTextArticle((NewsCredTextArticle) article, content);
      break;
    case VIDEO:
      processNewsCredVideoArticle((NewsCredVideoArticle) article, content);
      break;
    case PODCAST:
      processNewsCredVideoArticle((NewsCredVideoArticle) article, content);
      break;
    case SLIDESHARE:
      processNewsCredVideoArticle((NewsCredVideoArticle) article, content);
      break;
    default:
      break;
    }
    
    if (content.getSource() != null) {
      article.setArticleSource(content.getSource().getName());
      articleMetaData.getSources().add(content.getSource().getName());
    }
    
    /** add the current theme collection to the list. */
    final Set<String> articleGoals = article.getGoals();
    if (!articleGoals.isEmpty()) {
      articleGoals.clear();
    }
    articleGoals.add(spGoal.getId());
    
    article.setArticleReadability(setReadability(article));
    
    // saving the article
    try {
      trainingLibrary.saveUpdateTextArticle(article);
    } catch (Exception e) {
      LOG.warn("Error saving the article :" + article.getId(), e);
    }
    
    // add mandatory articles to themes and update the user with the mandatory articles.
    if (content.getMetadata() != null && content.getMetadata().getCustomFieldsSet() != null) {
      /*
       * check if current article belongs to another theme or not or contains mandatory article
       * list.
       */
      for (CustomFieldsSet cfs : content.getMetadata().getCustomFieldsSet()) {
        if (StringUtils.isNotBlank(cfs.getLabel())
            && cfs.getLabel().equalsIgnoreCase("mandatory Themes")) {
          if (!cfs.getValueSet().isEmpty()) {
            LOG.debug("Theme " + spGoal.getName() + "Mandatory article  "
                + article.getArticleLinkLabel());
            spGoal.getMandatoryArticles().add(article.getId());
          }
        }
      }
    }
    articleNames.add(articleLinkLabel);
    return article;
  }
  
  private Article createNewArticle(ArticleType articleType, String articleGuid, String originalGuid) {
    Article article = null;
    switch (articleType) {
    case TEXT:
      article = new NewsCredTextArticle(ArticleType.TEXT);
      break;
    case VIDEO:
      article = new NewsCredVideoArticle(ArticleType.VIDEO);
      break;
    case SLIDESHARE:
      article = new NewsCredVideoArticle(ArticleType.SLIDESHARE);
      break;
    case PODCAST:
      article = new NewsCredVideoArticle(ArticleType.PODCAST);
      break;
    default:
      break;
    }
    
    if (article != null) {
      article.setArticleLinkUrl(articleGuid);
      article.setId(articleGuid);
      article.setOriginalGuid(originalGuid);
    }
    return article;
  }
  
  private void processNewsCredTextArticle(NewsCredTextArticle newsCredTextArticle, Content content) {
    
    // resetting any article images that were previously there
    newsCredTextArticle.getImages().clear();
    newsCredTextArticle.setImageUrl(null);
    
    content.getImageSet().stream().forEach(imgset -> {
      if (imgset != null && imgset.getUrls() != null) {
        NewsCredImage credImage = new NewsCredImage();
        credImage.setUrl(imgset.getUrls().getLarge());
        credImage.setCaption(imgset.getCaption());
        credImage.setGuid(imgset.getGuid());
        credImage.setOrientation(imgset.getOrientation());
        credImage.setWidth(imgset.getWidth());
        newsCredTextArticle.getImages().add(credImage);
      }
    });
    
    ParagraphExtractor extractor = new ParagraphExtractor(content.getDescription());
    List<String> textParagraph = extractor.getTextParagraph();
    /* get the first text paragraph and search for image if, present in it */
    String paragraph = textParagraph.isEmpty() ? null : textParagraph.get(0);
    
    if (StringUtils.isNotBlank(paragraph) && paragraph.contains("<img")) {
      ImageExtractor imageExtractor = new ImageExtractor(paragraph);
      List<NewsCredImage> images = imageExtractor.getImages();
      /* check if content contains images */
      if (!CollectionUtils.isEmpty(images)) {
        /* Remove first pargrah from the content */
        textParagraph.remove(0);
        newsCredTextArticle.getImages().addAll(images);
      }
    }
    newsCredTextArticle.setContent(textParagraph);
    
    // get the image URL for the video if present
    Set<NewsCredImage> images = newsCredTextArticle.getImages();
    if (!images.isEmpty()) {
      newsCredTextArticle.setImageUrl(images.iterator().next().getUrl());
    }
    
  }
  
  private void processNewsCredVideoArticle(NewsCredVideoArticle newsCredVideoArticle,
      Content content) {
    VideoUrlExtractor videoUrlExtractor = new VideoUrlExtractor(content.getDescription());
    
    newsCredVideoArticle.setVideoUrl(videoUrlExtractor.getVideoUrl());
    newsCredVideoArticle.setContent(videoUrlExtractor.getTextParagraph());
    
    // get the image URL for the video if present
    final List<ImageSet> imageSet = content.getImageSet();
    if (imageSet != null && !imageSet.isEmpty()) {
      newsCredVideoArticle.setImageUrl(imageSet.get(0).getUrls().getLarge());
    }
  }
  
  private class ParagraphExtractor {
    
    public ParagraphExtractor(String content) {
      extractParagraph(content);
    }
    
    private List<String> textParagraph;
    
    /**
     * @return - the textParagraph.
     */
    public List<String> getTextParagraph() {
      if (textParagraph == null) {
        textParagraph = new ArrayList<String>();
        
      }
      return textParagraph;
    }
    
    public void extractParagraph(String content) {
      
      Document document = Jsoup.parse(content);
      Elements select = document.select("p,ol,ul,h1,h2,h3,h4");
      
      if (select != null) {
        for (Element htmlPElement : select) {
          String html = htmlPElement.outerHtml().replaceAll("<br>", "").replaceAll("<br/>", "");
          /* remove all br tags from the content */
          getTextParagraph().add(html);
        }
      }
    }
  }
  
  private class ImageExtractor {
    
    public ImageExtractor(String content) {
      extractImage(content);
    }
    
    private List<NewsCredImage> images;
    
    /**
     * @return - the textParagraph.
     */
    public List<NewsCredImage> getImages() {
      if (images == null) {
        images = new ArrayList<NewsCredImage>();
        
      }
      return images;
    }
    
    private void extractImage(String content) {
      
      Document document = Jsoup.parse(content);
      Elements image = document.select("img");
      for (Element imageNode : image) {
        NewsCredImage credImage = new NewsCredImage();
        String attr = imageNode.attr("src");
        String altText = imageNode.attr("alt");
        credImage.setCaption(altText);
        credImage.setUrl(attr);
        getImages().add(credImage);
      }
    }
  }
  
  private class VideoUrlExtractor {
    
    private String videoUrl;
    
    private List<String> textParagraph;
    
    public VideoUrlExtractor(String description) {
      extractParagraph(description);
    }
    
    public List<String> getTextParagraph() {
      if (textParagraph == null) {
        textParagraph = new ArrayList<String>();
      }
      return textParagraph;
    }
    
    public String getVideoUrl() {
      return videoUrl;
    }
    
    public void extractParagraph(String content) {
      ParagraphExtractor paragraphExtractor = new ParagraphExtractor(content);
      for (String paraGraph : paragraphExtractor.getTextParagraph()) {
        Document document = Jsoup.parse(paraGraph);
        Elements select = document.select("iframe");
        if (select != null && !select.isEmpty()) {
          for (Element iframe : select) {
            this.videoUrl = iframe.attr("src");
          }
        } else {
          getTextParagraph().add(paraGraph);
        }
      }
    }
  }
  
  private Set<String> getCollectionSkipList() {
    String[] property = environment.getProperty("newscred.collection.skipList").split(",");
    Set<String> skipCollection = new HashSet<String>();
    for (String skipList : property) {
      skipCollection.add(skipList);
    }
    return skipCollection;
  }
  
  /*
   * Method which sets the readability value for article
   */
  private ArticleReadability setReadability(Article article) {
    ArticleReadability readability = new ArticleReadability();
    if (article.getArticleType() == ArticleType.TEXT
        || article.getArticleType() == ArticleType.TEXT_IMAGE
        || article.getArticleType() == ArticleType.PODCAST
        || article.getArticleType() == ArticleType.SLIDESHARE
        || (article.getArticleType() == ArticleType.VIDEO && !article.getArticleSource()
            .equalsIgnoreCase("SUREPEOPLE"))) {
      try {
        readabilityArticlesCount = readabilityArticlesCount + 1;
        Readability r = new Readability(article.getContent().toString());
        readability.setAutomatedReadabilityIndex(r.getARI());
        readability.setColemanLiauIndex(r.getColemanLiau());
        readability.setFleschKincaidGradeLevel(r.getFleschKincaidGradeLevel());
        readability.setFleschKincaidReadingEase(r.getFleschReadingEase());
        readability.setGunningFogScore(r.getGunningFog());
        readability.setSmogIndex(r.getSMOGIndex());
        readability.setAverage(r.getAverageScore());
        articleAvergeScoreSummation = articleAvergeScoreSummation + readability.getAverage();
        
      } catch (Exception e) {
        LOG.error("Fail to measure Readabaility for Article --> :" + article.getId());
      }
    }
    return readability;
  }
  
  /*
   * Method to determine the article type from NewsCred Metadata Info.
   */
  private ArticleType getArticleType(Content content) {
    if (content.getMetadata() != null && content.getMetadata().getCustomFieldsSet() != null
        && content.getMetadata().getCustomFieldsSet().size() > 0) {
      for (CustomFieldsSet cfs : content.getMetadata().getCustomFieldsSet()) {
        if (StringUtils.isNotBlank(cfs.getLabel())
            && cfs.getLabel().equalsIgnoreCase("Template Name")) {
          if (cfs.getValue() == null || cfs.getValue().isEmpty()) {
            return ArticleType.TEXT;
          } else if (cfs.getValue().equalsIgnoreCase("slideshare")) {
            return ArticleType.SLIDESHARE;
          } else if (cfs.getValue().equalsIgnoreCase("podcast")) {
            return ArticleType.PODCAST;
          } else if (cfs.getValue().equalsIgnoreCase("video")) {
            return ArticleType.VIDEO;
          }
        }
      }
    }
    return ArticleType.TEXT;
  }
}