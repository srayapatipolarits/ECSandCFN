package com.sp.web.repository.library;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.model.ArticleTrackingBean;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.TrackingBean;
import com.sp.web.model.article.ArticleTrackingAggregate;
import com.sp.web.model.tracking.ArticlesCompletedArticleTracking;
import com.sp.web.model.tracking.RecommendedArticleTracking;
import com.sp.web.model.tracking.SP360RequestTracking;
import com.sp.web.model.tracking.TrainingLibraryVisitTracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pradeep
 *
 *         The mongo implementation of the tracking repository.
 */
@Repository
public class MongoTrackingRepository implements TrackingRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
    
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.trainingLibrary.TrackingRepository#storeTrackingInfomation(com.sp.web
   * .model.TrackingBean)
   */
  @Override
  public void storeTrackingInfomation(TrackingBean trackingBean) {
    mongoTemplate.save(trackingBean);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.trainingLibrary.TrackingRepository#findTrackingBean(java.lang.String)
   */
  @Override
  public ArticleTrackingBean findArticleTrackingBean(String userId, String articleId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("userId").is(userId)
            .andOperator(Criteria.where("articleId").is(articleId))), ArticleTrackingBean.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * @see com.sp.web.repository.trainingLibrary.TrackingRepository#findBookMarkTrackingBean(java.lang.String,
   * java.lang.String)
   */
  @Override
  public BookMarkTracking findBookMarkTrackingBean(String userId, String articleId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("userId").is(userId)
            .andOperator(Criteria.where("articleId").is(articleId))), BookMarkTracking.class);
  }

  @Override
  public List<BookMarkTracking> findAllBookMarkTrackingBean(String userId, int count) {
    
    Assert.hasText(userId, "User id is required.");
    // create the collection
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(BookMarkTracking.class));
    
    // create the query
    final Query query = query(where(Constants.ENTITY_USER_ID).is(userId));

    // add the sorting
    //query.with(new Sort(Sort.Direction.DESC, Constants.ENTITY_ACCESS_TIME));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_ACCESS_TIME, -1);
    List<DBObject> objectList = (count != -1) ? collection.find(query.getQueryObject())
        .sort(orderBy).limit(count).toArray() : collection.find(query.getQueryObject())
        .sort(orderBy).toArray();
    
    // get the mongo converter and convert the objects     
    final MongoConverter converter = mongoTemplate.getConverter();
    return objectList.stream().map(o -> {
        return converter.read(BookMarkTracking.class, o);
      }).collect(Collectors.toList());
  }

  @Override
  public List<BookMarkTracking> findAllBookMarkTrackingBean() {
    return mongoTemplate.findAll(BookMarkTracking.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.trainingLibrary.TrackingRepository#findAllArticleTrackingBean(java.lang.String)
   */
  @Override
  public List<ArticleTrackingBean> findAllArticleTrackingBean(String userId, int count) {
    Assert.hasText(userId, "User id is required.");
    
    // send back empty list of count requested is zero
    if ( count == 0) {
      return new ArrayList<ArticleTrackingBean>();
    }
    
    // create the collection
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(ArticleTrackingBean.class));
    
    // create the query
    final Query query = query(where(Constants.ENTITY_USER_ID).is(userId));

    // add the sorting
    //query.with(new Sort(Sort.Direction.DESC, Constants.ENTITY_ACCESS_TIME));
    final BasicDBObject orderBy = new BasicDBObject(Constants.ENTITY_ACCESS_TIME, -1);
    List<DBObject> objectList = (count != -1) ? collection.find(query.getQueryObject())
        .sort(orderBy).limit(count).toArray() : collection.find(query.getQueryObject())
        .sort(orderBy).toArray();
    
    // get the mongo converter and convert the objects     
    final MongoConverter converter = mongoTemplate.getConverter();
    return objectList.stream().map(o -> {
        return converter.read(ArticleTrackingBean.class, o);
      }).collect(Collectors.toList());
  }

  @Override
  public List<ArticleTrackingBean> findAllArticleTrackingBean() {
    return mongoTemplate.findAll(ArticleTrackingBean.class);
  }
  
  @Override
  public void remove(TrackingBean trackingBean) {
    if (trackingBean != null) {
      mongoTemplate.remove(trackingBean);
    }
  }

  @Override
  public List<String> findTrendingArticle() {
    TypedAggregation<ArticleTrackingBean> aggregation = newAggregation(ArticleTrackingBean.class,
        project("articleId", "accessCount"),
        group("articleId").sum("accessCount").as("totalCount"), sort(Direction.DESC, "totalCount"),
        limit(Constants.DEFAULT_TRENDING_ARTICLE_COUNT));
    AggregationResults<ArticleTrackingAggregate> aggregateResult = mongoTemplate.aggregate(
        aggregation, ArticleTrackingAggregate.class);
    List<ArticleTrackingAggregate> mappedResults = aggregateResult.getMappedResults();
    return mappedResults.stream().map(ArticleTrackingAggregate::getId).collect(Collectors.toList());
  }
  
  /**
   * @see com.sp.web.repository.library.TrackingRepository#findRecommendedTrackingBeanCount(java.lang
   *      .String, java.util.List)
   */
  @Override
  public List<RecommendedArticleTracking> findRecommendedTrackingBean(String companyId,
      List<String> users) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("userId")
            .in(users)), RecommendedArticleTracking.class);
    
  }
  
  /**
   * @see com.sp.web.repository.library.TrackingRepository#getTrainingLibraryVistCount(java.lang.String,
   *      java.util.List)
   */
  @Override
  public List<TrainingLibraryVisitTracking> getTrainingLibraryVist(String companyId,
      List<String> users) {
    
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("userId").in(users)),
        TrainingLibraryVisitTracking.class);
    
  }
  
  /**
   * @see com.sp.web.repository.library.TrackingRepository#getAllBookMarKTrackingBeanCount(java.util.
   *      List)
   */
  @Override
  public List<BookMarkTracking> getAllBookMarKTrackingBean(List<String> users, String companyId) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("userId")
            .in(users)), BookMarkTracking.class);
    
  }
  
  /**
   * <code>getAllArticlesCompletedCount</code> method will return the completd articels for the user
   * passed in.
   * 
   * @see com.sp.web.repository.library.TrackingRepository#getAllArticlesCompletedCount(java.util.List)
   */
  @Override
  public List<ArticlesCompletedArticleTracking> getAllArticlesCompleted(List<String> users,
      String companyId) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("userId")
            .in(users)), ArticlesCompletedArticleTracking.class);
  }
  
  /**
   * @see com.sp.web.repository.library.TrackingRepository#findAllSp360RequestTracking(java.lang.String,
   *      java.util.List)
   */
  @Override
  public List<SP360RequestTracking> findAllSp360RequestTracking(String companyId, List<String> users) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("userId")
            .in(users)), SP360RequestTracking.class);
  }

  @Override
  public RecommendedArticleTracking findReccomendationArticleTrackingBean(String id,
      String articleId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("userId").is(id)
            .andOperator(Criteria.where("articleId").is(articleId))), RecommendedArticleTracking.class);
  }

  /**
   * @see com.sp.web.repository.library.TrackingRepository#removeAllBoookMarksTracking(java.lang.String)
   */
  @Override
  public void removeAllBoookMarksTracking(String userId) {
    mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), BookMarkTracking.class);
  }
}
