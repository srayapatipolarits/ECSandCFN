package com.sp.web.repository.discussion;

import com.sp.web.model.discussion.GroupDiscussion;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the group discussion repository interface.
 */
@Repository
public class MongoGroupDiscussionRepository extends GenericMongoRepositoryImpl<GroupDiscussion> implements
    GroupDiscussionRepository {
  
}
