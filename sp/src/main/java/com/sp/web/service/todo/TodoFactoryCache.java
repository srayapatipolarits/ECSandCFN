package com.sp.web.service.todo;

import com.sp.web.model.User;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.repository.todo.UserTodoRequestsRepository;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The factory cache interface for extensibility in case we would like to cache the todo
 *         requests for the user.
 */
@Component
public class TodoFactoryCache {

  @Autowired
  private UserTodoRequestsRepository userTodoRequestsRepo;

  @Autowired
  private UserFactory userFactory;
  
  /**
   * Get the instance of the user todo requests.
   * 
   * @param user
   *          - user
   * @return
   *      the user todo requests
   */
  public UserTodoRequests getUserTodoRequests(User user) {
    UserTodoRequests userTodoRequests = null;
    final String userTodoRequestsId = user.getUserTodoRequestsId();
    if (userTodoRequestsId == null) {
      userTodoRequests = UserTodoRequests.newInstance();
      userTodoRequestsRepo.save(userTodoRequests);
      user.setUserTodoRequestsId(userTodoRequests.getId());
      userFactory.updateUserAndSession(user);
    } else {
      userTodoRequests = userTodoRequestsRepo.findById(userTodoRequestsId);
    }
    return userTodoRequests;
  }

  /**
   * Update the given user todo request.
   * 
   * @param userTodoRequests
   *            - user todo requests
   */
  public void updateUserTodoRequests(UserTodoRequests userTodoRequests) {
    userTodoRequestsRepo.save(userTodoRequests);
  }

  /**
   * @param userRequests
   *          - delete the given user requests.
   */
  public void deleteUserTodoRequests(UserTodoRequests userRequests) {
    userTodoRequestsRepo.delete(userRequests);  
  }
  
}
