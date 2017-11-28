package com.sp.web.mvc.assessment;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AssessmentProcessor;
import com.sp.web.assessment.questions.AssessmentType;
import com.sp.web.assessment.questions.CategoryBean;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.controller.external.rest.SPExternalServiceFactory;
import com.sp.web.controller.notifications.AssessmentCompletedNotificationsProcessor;
import com.sp.web.controller.notifications.HiringCandidateNotificationsProcessor;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.QuestionBean360DTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.external.rest.PartnerFactory;
import com.sp.web.service.sse.ActionType;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The helper class for assessment controller.
 */

public class AssessmentControllerHelper {
  
  private AssessmentProcessor assessmentProcessor;
  
  private UserRepository userRepository;
  private QuestionsFactory questionsFactory;
  private boolean is360;
  private Object[] formatValues;
  private User user360;
  private PartnerFactory partnerFactory;
  
  /*
   * The user who is taking the assessment either for themselves or on behalf.
   */
  private User user;
  
  private static final Logger log = Logger.getLogger(AssessmentControllerHelper.class);
  
  /**
   * Constructor the common assessment controller for taking assessments for all types of users.: -
   * Member - Hiring Candidate - 360 Feedback - Candidate Reference
   * 
   * @param user
   *          - user could be either the Member, Hiring Candidate, Feedback User
   * @param questionsFactory
   *          - the questions to use
   * @param is360
   *          - if it 360 feedback or not
   */
  public AssessmentControllerHelper(User user, QuestionsFactory questionsFactory, boolean is360) {
    this.user = user;
    assessmentProcessor = ApplicationContextUtils.getBean(AssessmentProcessor.class, user,
        questionsFactory);
    this.questionsFactory = questionsFactory;
    this.partnerFactory = ApplicationContextUtils.getBean(PartnerFactory.class);
    userRepository = ApplicationContextUtils.getBean(UserRepository.class);
    this.is360 = is360;
    if (is360) {
      if (user instanceof FeedbackUser) {
        final FeedbackUser feedbackUser = (FeedbackUser) user;
        final String feedbackForId = feedbackUser.getFeedbackFor();
        Assert.hasText(feedbackForId, "Feedback for not found in feedback user");
        if (feedbackUser.getFeatureType() == FeatureType.PrismLensHiring) {
          HiringRepository hiringRepository = ApplicationContextUtils
              .getBean(HiringRepository.class);
          user360 = hiringRepository.findById(feedbackForId);
        } else {
          user360 = userRepository.findUserById(feedbackForId);
        }
        if (user360 == null) {
          throw new InvalidRequestException("Feedback user not found !!!");
        }
        formatValues = (user360.getGender() != null && user360.getGender() == Gender.M) ? Constants.ASSESSMENT_QUESTIONS_MALE_FORMAT
            : ((user360.getGender() == Gender.F) ? Constants.ASSESSMENT_QUESTIONS_FEMALE_FORMAT
                : Constants.ASSESSMENT_QUESTIONS_NEUTRAL_FORMAT);
      } else {
        throw new InvalidRequestException("User must be feedback user type !!!");
      }
    }
  }
  
  /**
   * Helper method to process the assessment.
   * 
   * @param params
   *          - params
   * @return the response to the process request
   */
  @SuppressWarnings("unchecked")
  public SPResponse processAssessment(Object[] params) {
    final SPResponse resp = new SPResponse();
    
    int questionNumber = (int) params[0];
    List<String> answers = (List<String>) params[1];
    
    if (log.isDebugEnabled()) {
      log.debug("Received request Q:" + questionNumber + ":A:" + answers);
    }
    
    /* check user status if it assessment progress or not */
    if (user.getUserStatus() != UserStatus.ASSESSMENT_PROGRESS) {
      user.setUserStatus(UserStatus.ASSESSMENT_PROGRESS);
      userRepository.updateGenericUser(user);
      
      partnerFactory.updatePartner(user, ActionType.PartnerPrismStatus);
      
    }
    // process the assessment response from the user
    assessmentProcessor.process(questionNumber, answers);
    
    // add the next question or the final analysis to the response
    getQuestion(resp);
    return resp;
  }
  
  /**
   * Helper method to process previous.
   * 
   * @param user
   *          - user
   * @return the response to prev request
   */
  public SPResponse processPrev(User user) {
    final SPResponse resp = new SPResponse();
    
    if (assessmentProcessor == null) {
      throw new InvalidRequestException("Assessment not started yet !!!");
    }
    
    if (log.isDebugEnabled()) {
      log.debug("Received request for previous !!!");
    }
    
    // process with the decrement of the previous question
    assessmentProcessor.processPrevious();
    
    // add the next question or the final analysis to the response
    getLastQuestion(resp);
    
    return resp;
  }
  
  /**
   * Get the last question answered.
   * 
   * @param resp
   *          - response
   */
  private void getLastQuestion(SPResponse resp) {
    Optional<QuestionsBean> lastQuestion = assessmentProcessor.getQuestion();
    
    if (is360) {
      resp.add(Constants.PARAM_QUESTION, new QuestionBean360DTO(lastQuestion.get(), formatValues,
          user360.getFirstName()));
    } else {
      resp.add(Constants.PARAM_QUESTION, lastQuestion.get());
    }
    resp.add(Constants.PARAM_LAST_ANSWER, assessmentProcessor.getLastAnswer());
  }
  
  /**
   * Start the assessment.
   * 
   * @param user
   *          - user
   * @return the response
   */
  public SPResponse start(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    AssessmentType assessmentType = (AssessmentType) params[0];
    
    ArrayList<CategoryType> categories = new ArrayList<CategoryType>(assessmentType.getCategories());
    assessmentProcessor.categoriesToProcess(categories);
    
    List<CategoryBean> categoryInfo = questionsFactory.getCategoryInfo(categories);
    
    if (is360) {
      categoryInfo = categoryInfo.stream()
          .map(categoryBean -> new CategoryBean(categoryBean, user360))
          .collect(Collectors.toList());
    }
    
    // create the response to send
    resp.add("categories", categoryInfo);
    resp.add("answeredCount", assessmentProcessor.getTotalQuestionsAnswered());
    
    // add the first question to the response
    getQuestion(resp);
    
    return resp;
  }
  
  /**
   * Get the next question.
   * 
   * @param resp
   *          - the response
   */
  private void getQuestion(SPResponse resp) {
    Optional<QuestionsBean> nextQuesiton = assessmentProcessor.getQuestion();
    
    if (nextQuesiton.isPresent()) {
      if (is360) {
        resp.add(Constants.PARAM_QUESTION, new QuestionBean360DTO(nextQuesiton.get(), formatValues,
            user360.getFirstName()));
      } else {
        resp.add(Constants.PARAM_QUESTION, nextQuesiton.get());
      }
    } else {
      resp.isSuccess();
      try {
        if (is360) {
          UserType type = ((FeedbackUser) user).getType();
          if (type == UserType.Member || type == UserType.External) {
            NotificationsProcessor notificationProcessor = (NotificationsProcessor) ApplicationContextUtils
                .getBean("feedbackNotificationsProcessor");
            notificationProcessor.process(NotificationType.FeedbackCompleted, user, user360);
          }
        } else {
          // getting the updated user
          user = assessmentProcessor.getUser();
          if (user instanceof HiringUser) {
            // user is instance of hiring candidate
            HiringCandidateNotificationsProcessor notificationProcessor = (HiringCandidateNotificationsProcessor) ApplicationContextUtils
                .getBean("hiringCandidateNotificationProcessor");
            if (user.getType() == UserType.Member) {
              // send notification to the hiring candidate
              notificationProcessor.process(NotificationType.AssessmentCompletedEmployee, user);
            }
            notificationProcessor.notifyHiringCoordinator(user);
          } else {
            AssessmentCompletedNotificationsProcessor notificationProcessor = (AssessmentCompletedNotificationsProcessor) ApplicationContextUtils
                .getBean("assessmentsCompletedNotificationsProcessor");
            
            notificationProcessor.process(user);
          }
        }
      } catch (Exception e) {
        log.warn("Error updating goals and sending notification !!!", e);
      }
    }
  }
}
