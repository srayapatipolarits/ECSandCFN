package com.sp.web.repository.feed;

import com.sp.web.model.feed.CompanyNewsFeed;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the news feed repository interface.
 */
@Repository
public class MongoNewsFeedRepository extends GenericMongoRepositoryImpl<CompanyNewsFeed> implements
    NewsFeedRepository {
  
}
