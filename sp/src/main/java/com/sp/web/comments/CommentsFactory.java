package com.sp.web.comments;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CommentsDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Comments;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The factory class for all the comments updates.
 */
@Component
public class CommentsFactory {
  
  @Autowired
  UserRepository userRepository;
  
  /*
   * The default user to be used in case the user has left the company.
   */
  private BaseUserDTO defaultUser;
    
  /**
   * Get the comments DTO updated with the users.
   * 
   * @param comments
   *          - the list of comments
   * @return the list of comments DTO
   */
  public List<CommentsDTO> getCommentsDTO(List<Comments> comments) {
    return comments.stream().map(this::getCommentDTO).collect(Collectors.toList());
  }

  /**
   * The improved method to user the user cache.
   * 
   * @param comments
   *          - comments
   * @param userCache
   *          - user cache
   * @return
   *      the list of comments DTO
   */
  public List<CommentsDTO> getCommentsDTO(List<Comments> comments, Map<String, BaseUserDTO> userCache) {
    return comments.stream().map(comment -> getCommentDTO(comment, userCache)).collect(Collectors.toList());
  }
  
  /**
   * The helper method to get the comments DTO.
   * 
   * @param commentsMap
   *            - comments map
   * @return
   *    the comments DTO list
   */
  public List<CommentsDTO> getCommentsDTO(Map<String, Comments> commentsMap) {
    if (commentsMap == null || commentsMap.isEmpty()) {
      return null;
    }
    return getCommentsDTO(new ArrayList<>(commentsMap.values()));
  }
  
  /**
   * The helper method to get the comments DTO.
   * 
   * @param commentsMap
   *            - comments map
   * @param userCache
   *            - temporary user cache           
   * @return
   *    the comments DTO list
   */
  public List<CommentsDTO> getCommentsDTO(Map<String, Comments> commentsMap, Map<String, BaseUserDTO> userCache) {
    if (commentsMap == null || commentsMap.isEmpty()) {
      return null;
    }
    return getCommentsDTO(new ArrayList<>(commentsMap.values()), userCache);
  }
  
  /**
   * Get the comment DTO for the given comment.
   * 
   * @param comment
   *          - the comment
   * @return
   *    the comment DTO
   */
  private CommentsDTO getCommentDTO(Comments comment) {
    CommentsDTO commentsDTO = new CommentsDTO(comment);
    User findUserById = null;
    final UserType userType = comment.getUserType();
    if (userType != null && userType == UserType.Feedback) {
      findUserById = userRepository.findFeedbackUser(comment.getBy());
    } else {
      findUserById = userRepository.findUserById(comment.getBy());
    }
    
    BaseUserDTO commentsUser = null;
    if (findUserById != null) {
      commentsUser = new BaseUserDTO(findUserById);
    } else {
      commentsUser = getDefaultUser();
    }
    commentsDTO.setBy(commentsUser);
    return commentsDTO;
  }
  
  /**
   * New improved method to cache the user data.
   * 
   * @param comment
   *          - comment
   * @param userCache
   *          - user cache
   * @return
   *    the comments DTO
   */
  private CommentsDTO getCommentDTO(Comments comment, Map<String, BaseUserDTO> userCache) {
    CommentsDTO commentsDTO = new CommentsDTO(comment);
    final String by = comment.getBy();
    BaseUserDTO commentsUser = userCache.get(by);
    
    if (commentsUser == null) {
      User findUserById = null;
      final UserType userType = comment.getUserType();
      if (userType != null && userType == UserType.Feedback) {
        findUserById = userRepository.findFeedbackUser(comment.getBy());
      } else {
        findUserById = userRepository.findUserById(comment.getBy());
      }
      if (findUserById != null) {
        commentsUser = new BaseUserDTO(findUserById);
      } else {
        commentsUser = getDefaultUser();
      }
      userCache.put(by, commentsUser);
    }
    commentsDTO.setBy(commentsUser);
    return commentsDTO;
  }
  
  /**
   * Get the default user.
   * 
   * @return
   *      - the default user
   */
  private BaseUserDTO getDefaultUser() {
    if (defaultUser == null) {
      defaultUser = new BaseUserDTO();
      defaultUser.setFirstName(MessagesHelper.getMessage("anonymousUser.name"));
      defaultUser.setSmallProfileImage("/sp/resources/images/default-header-accountprofile.png");
    }
    return defaultUser;
  }

  /**
   * Remove the comment.
   * 
   * @param commentUser
   *          - comment user
   * @param comments
   *          - comments
   * @param index
   *          - index to remove
   */
  public void removeComment(User commentUser, List<Comments> comments, int index) {
    // update the data and save
    validateCommentsOwner(commentUser, index, comments);
    comments.remove(index);
  }
  
  /**
   * Validate if the comment is by the currently logged in user.
   * 
   * @param user
   *          - logged in user
   * @param index
   *          - index of the comment
   * @param comments
   *          - list of comments
   * @return the validated comment
   */
  private boolean validateCommentsOwner(User user, int index, List<Comments> comments) {
    if (index >= 0 && index < comments.size()) {
      Comments comment = comments.get(index);
      if (!comment.getBy().equals(user.getId())) {
        throw new InvalidRequestException(
            "Comment can be only deleted/updated by owner of the comment !!!");
      }
      return true;
    } else {
      throw new InvalidRequestException("Invalid comments index.");
    }
  }
  
  /**
   * Update the comment.
   * 
   * @param commentUser
   *          - user
   * @param comments
   *          - comments
   * @param index
   *          - index
   * @param commentStr
   *          - comment
   */
  public void updateComment(User commentUser, List<Comments> comments, int index, String commentStr) {
    validateCommentsOwner(commentUser, index, comments);
    comments.get(index).setComment(commentStr);
  }

}
