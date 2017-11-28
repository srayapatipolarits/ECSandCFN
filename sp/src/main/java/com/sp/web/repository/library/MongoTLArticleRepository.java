package com.sp.web.repository.library;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.article.Article;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.library.ArticleMetaData;
import com.sp.web.model.library.NewsCredTextArticle;
import com.sp.web.model.library.NewsCredVideoArticle;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.utils.LocaleHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pradeep
 *
 *         The mongo implementation of the article repository.
 */
@Repository("mongoArticleRepository")
public class MongoTLArticleRepository implements TrainingLibraryArticleRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  /**
   * @see com.sp.web.repository.trainingLibrary.TrainingLibraryArticle#findArticleById(java.lang.String)
   */
  @Override
  public Article findArticleById(String articleId) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(Article.class));
    DBObject dbObject = collection.findOne(query(where("_id").is(articleId)).getQueryObject());
    MongoConverter converter = mongoTemplate.getConverter();
    if (dbObject != null) {
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.library.NewsCredTextArticle":
        return converter.read(NewsCredTextArticle.class, dbObject);
      case "com.sp.web.model.library.NewsCredVideoArticle":
        return converter.read(NewsCredVideoArticle.class, dbObject);
      default:
        return converter.read(Article.class, dbObject);
      }
    }
    throw new InvalidRequestException("Article :" + articleId + " not found.");
  }
  
  @Override
  public void saveUpdateTextArticle(Article credTextArticle) {
    mongoTemplate.save(credTextArticle);
  }
  
  @Override
  public Article findArticleByName(String articleLinkLabel) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(Article.class));
    DBObject dbObject = collection.findOne(query(
        where(Constants.ENTITY_ARTICLE_LINK_LABEL).is(articleLinkLabel)).getQueryObject());
    MongoConverter converter = mongoTemplate.getConverter();
    if (dbObject != null) {
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.library.NewsCredTextArticle":
        return converter.read(NewsCredTextArticle.class, dbObject);
      case "com.sp.web.model.library.NewsCredVideoArticle":
        return converter.read(NewsCredVideoArticle.class, dbObject);
      default:
        return converter.read(Article.class, dbObject);
      }
    }
    return null;
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#findNewsCredTextArticleById
   *      (java.lang.String)
   */
  @Override
  public Article findNewsCredTextArticleById(String id) {
    Assert.hasText(id, "News Cred is mandatory to retreive the data");
    return mongoTemplate.findById(id, Article.class);
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#saveUpdateVideoArticle
   *      (com.sp.web.model.library.NewsCredVideoArticle)
   */
  @Override
  public void saveUpdateVideoArticle(Article credVideoArticle) {
    mongoTemplate.save(credVideoArticle);
    
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#getAllThemes()
   */
  @Override
  public List<SPGoal> getAllThemes() {
    return mongoTemplate.find(query(where("category").in(GoalCategory.GrowthAreas)), SPGoal.class);
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#getAllAuthors()
   */
  @Override
  public Set<String> getAllAuthors() {
    /** get all the authros in the articles. */
    List<ArticleMetaData> articles = mongoTemplate.findAll(ArticleMetaData.class);
    // Assert.notEmpty(articles, "No articles present in the system");
    
    Set<String> authors = new HashSet<String>();
    articles.stream().forEach(artMeta -> authors.addAll(artMeta.getAuthorNames()));
    return authors;
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#getAlblArticleSources()
   */
  @Override
  public Set<String> getAllArticleSources() {
    /* get al the artilces */
    List<ArticleMetaData> articles = mongoTemplate.findAll(ArticleMetaData.class);
    // Assert.notEmpty(articles, "No articles present in the system");
    
    Set<String> articleSources = new HashSet<String>();
    articles.stream().forEach(artMeta -> articleSources.addAll(artMeta.getSources()));
    return articleSources;
  }
  
  /**
   * @see com.sp.web.repository.library.TrainingLibraryArticleRepository#getArtilces(java.lang
   *      .String, java.lang.String)
   */
  @Override
  public List<String> getArtilces(String topicCategory, String topicValue) {
    Assert.hasText(topicValue, "No Articles present.");
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(Article.class));
    DBObject keys = new BasicDBObject(Constants.ENTITY_ID, "1");
    List<DBObject> articleIdList = new ArrayList<DBObject>();
    
    switch (topicCategory) {
    case "Themes":
      articleIdList = collection.find(query(where("goals").in(topicValue)).getQueryObject(), keys)
          .toArray();
      break;
    case "Author":
      articleIdList = collection.find(query(where("author").in(topicValue)).getQueryObject(), keys)
          .toArray();
      break;
    case "Source":
      articleIdList = collection.find(
          query(where("articleSource").is(topicValue)).getQueryObject(), keys).toArray();
      break;
    default:
      TextCriteria textCriteria = TextCriteria.forLanguage(LocaleHelper.locale().getLanguage())
        .matching(topicValue);
      articleIdList = collection.find(textCriteria.getCriteriaObject())
      .toArray();
    }
    return articleIdList.stream().map(dbo -> {
      return (String) dbo.get(Constants.ENTITY_ID);
    }).collect(Collectors.toList());
  }
  
  /**
   * <code>getAllArticles</code> method will return all the articles.
   * 
   * @return the list of articles
   */
  @Override
  public List<Article> getAllArticles() {
    return mongoTemplate.findAll(Article.class);
  }
  
  public void updateGenericArticle(Object article) {
    mongoTemplate.save(article);
  }
  
  @Override
  public void saveArticleMetaData(ArticleMetaData articleMetaData) {
    mongoTemplate.save(articleMetaData);
  }
  
  @Override
  public List<String> findTopRatedArticle() {
    
    ArrayList<Object> values = new ArrayList<Object>();
    values.add("$articleReccomendations");
    values.add(new ArrayList<Object>());
    final DBObject projectFields = new BasicDBObject("_id", 1);
    projectFields.put("rCount", new BasicDBObject("$size", new BasicDBObject("$ifNull", values)));
    final DBObject project = new BasicDBObject("$project", projectFields);
    
    final DBObject sortFields = new BasicDBObject("rCount", -1);
    final DBObject sort = new BasicDBObject("$sort", sortFields);
    
    final DBObject limit = new BasicDBObject("$limit", Constants.DEFAULT_TOP_RATED_ARTICLE_COUNT);
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(Article.class));
    
    ArrayList<DBObject> pipeline = new ArrayList<DBObject>();
    pipeline.add(project);
    pipeline.add(sort);
    pipeline.add(limit);
    AggregationOutput aggregate = collection.aggregate(pipeline);
    ArrayList<String> articleList = new ArrayList<String>();
    for (DBObject dbo : aggregate.results()) {
      articleList.add((String) dbo.get(Constants.ENTITY_ID));
    }
    
    return articleList;
  }
  
  @Override
  public void remove(Article article) {
    mongoTemplate.remove(article);
  }
  
  @Override
  public void addHomepageArticle(TrainingLibraryHomeArticle homepageArticle) {
    TrainingLibraryHomeArticle findOne = mongoTemplate.findOne(
        query(where(Constants.ENTITY_ARTICLE_POSITION).is(homepageArticle.getArticlePosition())
            .andOperator(where(Constants.ENTITY_COMPANY_ID).is(homepageArticle.getCompanyId()))),
        TrainingLibraryHomeArticle.class);
    if (findOne != null) {
      mongoTemplate.remove(findOne);
    }
    mongoTemplate.save(homepageArticle);
  }
  
  @Override
  public List<TrainingLibraryHomeArticle> findHomepageArticlesByLocation(
      ArticleLocation articleLocation, String companyId) {
    final Query query = query(where(Constants.ENTITY_ARTICLE_LOCATION).is(articleLocation));
    query.addCriteria(where(Constants.ENTITY_COMPANY_ID).is(companyId));
    query.with(new Sort(Direction.ASC, Constants.ENTITY_ARTICLE_POSITION));
    return mongoTemplate.find(query, TrainingLibraryHomeArticle.class);
  }
  
  @Override
  public List<TrainingLibraryHomeArticle> findAllHomePageArticles() {
    return mongoTemplate.findAll(TrainingLibraryHomeArticle.class);
  }
  
  @Override
  public int deleteHomePageArticles(String companyId) {
    return mongoTemplate.remove(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        TrainingLibraryHomeArticle.class).getN();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.library.TrainingLibraryArticleRepository#findArticleByOrginalGuidId(java
   * .lang.String)
   */
  @Override
  public Article findArticleByOrginalGuidId(String originalGuid) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(Article.class));
    DBObject dbObject = collection.findOne(query(where("originalGuid").is(originalGuid))
        .getQueryObject());
    MongoConverter converter = mongoTemplate.getConverter();
    if (dbObject != null) {
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.library.NewsCredTextArticle":
        return converter.read(NewsCredTextArticle.class, dbObject);
      case "com.sp.web.model.library.NewsCredVideoArticle":
        return converter.read(NewsCredVideoArticle.class, dbObject);
      default:
        return converter.read(Article.class, dbObject);
      }
    }
    return null;
  }
}
