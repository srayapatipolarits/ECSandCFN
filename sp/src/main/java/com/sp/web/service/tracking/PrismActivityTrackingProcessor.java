
package com.sp.web.service.tracking;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.model.usertracking.UserTrackingType;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.message.MessageHandlerType;
import com.sp.web.service.message.SPMessageEnvelop;
import com.sp.web.service.message.SPMessageGateway;
import com.sp.web.utils.MessagesHelper;
import com.sp.web.utils.RandomGenerator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The prism activity tracking processor.
 */
@Component("prismActivityTrackingProcessor")
public class PrismActivityTrackingProcessor implements ActivityTrackingProcessor {
  
  private static final Logger log = Logger.getLogger(PrismActivityTrackingProcessor.class);
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private SPGoalFactory spGoalFactory;
  
  @Autowired
  private SPMessageGateway  messageGateway;
  
  @Override
  public boolean updateActivityTracking(User user, LogActionType actionType, Object[] params,
      ActivityTracking activityTracking) {
    
    if (log.isDebugEnabled()) {
      log.debug("Activity tracking request :" + user.getFirstName() + ": Action type :"
          + actionType);
    }
    
    boolean logActivity = true;
    switch (actionType) {
    case ArticleCompleted:
      updateArticleCompleted(user, params, actionType, activityTracking);
      SPMessageEnvelop messageEnvelop = new SPMessageEnvelop();
      messageEnvelop.setMessageHandler(MessageHandlerType.EngagementMatrix);
      messageEnvelop.addData("userId", user.getId());
      messageEnvelop.addData("activityType", UserTrackingType.ArticleCompleted);
      messageGateway.sendMessage(messageEnvelop);
      break;
      
    case FeedbackCompleted:
      updateFeedbackCompleted(user, params, actionType, activityTracking);
      break;
      
    case RelationshipAdvisor:
      updateRelationshipAdvisor(user, params, actionType, activityTracking);
      break;

    default:
      logActivity = false;
      break;
    }
    return logActivity;
  }
  
  /**
   * Update the relationship advisor activity.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @param actionType
   *          - action type
   * @param activityTracking
   *          - activity tracking to update
   */
  private void updateRelationshipAdvisor(User user, Object[] params, LogActionType actionType,
      ActivityTracking activityTracking) {

    BaseUserDTO userDTO = new BaseUserDTO(user);
    activityTracking.setMessage(Comment.newCommment(user,
        MessagesHelper.getMessage(actionType.getActivityKey() + RandomGenerator.getRandomInteger(),
            userDTO.getName())));
  }

  /**
   * Update the feedback completed activity.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @param actionType
   *          - action type
   * @param activityTracking
   *          - activity tracking to update
   */
  private void updateFeedbackCompleted(User user, Object[] params, LogActionType actionType,
      ActivityTracking activityTracking) {
    
    final User userBy = (User) params[0];
    BaseUserDTO baseUserDTO = new BaseUserDTO(user);
    BaseUserDTO userByDTO = new BaseUserDTO(userBy);
    activityTracking.setMessage(Comment.newCommment(user, MessagesHelper.getMessage(
        actionType.getActivityKey() + RandomGenerator.getRandomInteger(), baseUserDTO.getName(),
        userByDTO.getName())));
  }
  
  /**
   * Update the article completed activity.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @param actionType
   *          - action type
   * @param activityTracking
   *          - activity tracking to update
   */
  private void updateArticleCompleted(User user, Object[] params, LogActionType actionType,
      ActivityTracking activityTracking) {
    String articleId = (String) params[0];
    Article article = articlesFactory.getArticle(articleId);
    
    String goalName = article.getGoals().stream().findFirst().map(id -> spGoalFactory.getGoal(id).getName()).get();
    int random = RandomGenerator.getRandomInteger();
    
    String text = random == 0 ? MessagesHelper.getMessage(actionType.getActivityKey() + random,
        goalName) : MessagesHelper.getMessage(actionType.getActivityKey() + random);
    text = MessagesHelper.genderNormalize(text, user);
    Comment comment = Comment.newCommment(user,text);
    ContentReference contentReference = new ContentReference(article);
    comment.setContentReference(contentReference);
    activityTracking.setMessage(comment);
  }
  
}
