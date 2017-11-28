package com.sp.web.repository.discussion;

import com.sp.web.model.discussion.UserGroupDiscussion;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation for the UserGroupDiscussionRepository interface.
 */
@Repository
public class MongoUserGroupDiscussionRepository extends GenericMongoRepositoryImpl<UserGroupDiscussion> implements
    UserGroupDiscussionRepository {
  
}
