package com.sp.web.service.discussion;

import com.sp.web.model.User;
import com.sp.web.model.discussion.GroupDiscussion;
import com.sp.web.model.discussion.UserGroupDiscussion;
import com.sp.web.repository.discussion.GroupDiscussionRepository;
import com.sp.web.repository.discussion.UserGroupDiscussionRepository;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The factory class for managing the cached group discussion objects.
 */
@Component
public class GroupDiscussionFactory {
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  UserGroupDiscussionRepository userGDRepo;
  
  @Autowired
  GroupDiscussionRepository gdRepo;
  
  
  /**
   * Get the user group discussion for the given user.
   * 
   * @param user
   *          - user
   * @return
   *    the user group discussion
   */
  @Cacheable(value = "groupDiscussion", key = "#user.id")
  public UserGroupDiscussion getUserGroupDiscussion(User user) {
    String userGroupDiscussionId = user.getUserGroupDiscussionId();
    UserGroupDiscussion userGroupDiscussion = null;
    if (userGroupDiscussionId == null) {
      userGroupDiscussion = new UserGroupDiscussion(user.getId());
      userGDRepo.save(userGroupDiscussion);
      user.setUserGroupDiscussionId(userGroupDiscussion.getId());
      userFactory.updateUserAndSession(user);
    } else {
      userGroupDiscussion = userGDRepo.findById(userGroupDiscussionId);
    }
    return userGroupDiscussion;
  }

  /**
   * Get the group discussion for the given group discussion id.
   * 
   * @param id
   *          - group discussion id
   * @return
   *    the group discussion
   */
  @Cacheable(value = "groupDiscussion")
  public GroupDiscussion getGroupDiscussion(String id) {
    return gdRepo.findById(id);
  }

  /**
   * Create a new group discussion object.
   * 
   * @param groupDiscussion
   *            - group discussion
   */
  public void createGroupDiscussion(GroupDiscussion groupDiscussion) {
    gdRepo.save(groupDiscussion);
  }

  /**
   * Method to update user group discussion in the db.
   * 
   * @param userGroupDiscussion
   *            - user group discussion
   */
  @CacheEvict(value = "groupDiscussion", key = "#userGroupDiscussion.uid")
  public void updateUserGroupDiscussion(UserGroupDiscussion userGroupDiscussion) {
    userGDRepo.save(userGroupDiscussion);
  }

  /**
   * Update the given group discussion.
   * 
   * @param groupDiscussion
   *            - group discussion to update
   */
  @CacheEvict(value = "groupDiscussion", key = "#groupDiscussion.id")
  public void updateGroupDiscussion(GroupDiscussion groupDiscussion) {
    gdRepo.save(groupDiscussion);
  }

  /**
   * Remove the group discussion from the database.
   * 
   * @param groupDiscussion
   *          - group discussion
   */
  @CacheEvict(value = "groupDiscussion", key = "#groupDiscussion.id")
  public void removeGroupDiscussion(GroupDiscussion groupDiscussion) {
    gdRepo.delete(groupDiscussion);
  }

  /**
   * Delete the user group discussion.
   * 
   * @param userGroupDiscussion
   *          - user group discussion
   */
  @Cacheable(value = "groupDiscussion", key = "#userGroupDiscussion.uid")
  public void removeUserGroupDiscussion(UserGroupDiscussion userGroupDiscussion) {
    userGDRepo.delete(userGroupDiscussion);
  }
  
}
