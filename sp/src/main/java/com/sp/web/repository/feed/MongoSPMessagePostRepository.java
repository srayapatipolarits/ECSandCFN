package com.sp.web.repository.feed;

import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of SP Message post.
 */
@Repository
public class MongoSPMessagePostRepository extends GenericMongoRepositoryImpl<SPMessagePost>
    implements SPMessagePostRepository {
  
}
