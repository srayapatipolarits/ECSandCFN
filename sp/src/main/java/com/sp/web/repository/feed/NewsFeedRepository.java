package com.sp.web.repository.feed;

import com.sp.web.model.feed.CompanyNewsFeed;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * @author Dax Abraham
 * 
 *         The repository interface for the user news feed.
 */
public interface NewsFeedRepository extends GenericMongoRepository<CompanyNewsFeed> {
  
}
