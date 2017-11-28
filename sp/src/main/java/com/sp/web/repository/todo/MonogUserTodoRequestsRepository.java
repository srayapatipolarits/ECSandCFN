package com.sp.web.repository.todo;

import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the User todo requests repository interface.
 */
@Repository
public class MonogUserTodoRequestsRepository extends GenericMongoRepositoryImpl<UserTodoRequests>
    implements UserTodoRequestsRepository {
  
}
