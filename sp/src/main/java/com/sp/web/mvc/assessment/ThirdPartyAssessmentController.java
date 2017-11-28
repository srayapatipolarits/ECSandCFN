package com.sp.web.mvc.assessment;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.Constants;
import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.AssessmentType;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.FeedbackRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 *
 *         The controller for third party assessments.
 */
@Controller
@Scope("session")
public class ThirdPartyAssessmentController {
  
  private static final Logger log = Logger.getLogger(ThirdPartyAssessmentController.class);
  
  @Autowired
  AssessmentQuestionFactory abstractQuestionsFactory;
  
  @Autowired
  FeedbackRepository feedbackRepository;
  
  private Map<String, AssessmentControllerHelper> assementControllerHelperMap 
        = new HashMap<String, AssessmentControllerHelper>();
  
  Lock lockObj = new ReentrantLock();
  
  @RequestMapping(value = "/assessment360/confirmDetails", method = RequestMethod.GET)
  public String confirmDetailsView(Authentication token) {
    return "confirmDetails360";
  }
 
  /**
   * The main assessment class.
   * 
   * @return the response
   */
  @RequestMapping(value = "/assessment360", method = RequestMethod.GET)
  public String getAssessmentView(Authentication token) {
    return "360Assessment";
  }
  
  /**
   * The main assessment class.
   * 
   * @return the response
   */
  @RequestMapping(value = "/assessment360/{userId}", method = RequestMethod.GET)
  public String getAssessmentView(@PathVariable String userId, Authentication token,
      ModelMap model, HttpSession httpSession) {
    // get the user for the given user id
    try {
      Assert.hasText(userId, "User id cannot be empty or null !!!");
      
      /* get the feedback user from the session corresponding to the token */
      init(httpSession, userId, token);
      model.addAttribute(Constants.PARAM_FEEDBACK_USERID, userId);
      return "360Assessment";
    } catch (Exception e) {
      log.warn("Error initializing the assessment !!!", e);
    }
    return "error";
  }
  
  /**
   * The main assessment class.
   * 
   * @return the response
   */
  @RequestMapping(value = "/assessment360/start/{userId}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse assessmentStart(@PathVariable String userId, Authentication token,
      HttpSession httpSession) {
    return process(init(httpSession, userId, token)::start, token, AssessmentType.Personality);
  }
  
  /**
   * Method for processing the subsequent question requests.
   * 
   * @param userId
   *          - user id
   * @param questionNum
   *          - question number
   * @param ans
   *          - selected answer
   * @param token
   *          - logged in user
   * @param httpSession
   *          - current user session                   
   * @return 
   *    - the next question
   */
  @RequestMapping(value = "/assessment360/process/{userId}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse processAssessment(@PathVariable String userId, @RequestParam int questionNum,
      @RequestParam List<String> ans, Authentication token, HttpSession httpSession) {
    
    // process the validate the user email's from user controller helper
    return ControllerHelper.doProcess(init(httpSession, userId, token)::processAssessment,
        questionNum, ans);
  }
  
  /**
   * Processes the previous question and also resets the answer for the previous question.
   * 
   * @param token
   *          - logged in user
   * @return the response for the previous question request
   */
  @RequestMapping(value = "/assessment360/previous/{userId}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse processPrev(@PathVariable String userId, Authentication token,
      HttpSession httpSession) {
    return process(init(httpSession, userId, token)::processPrev, token);
  }
  
  /**
   * Initializes the assessment for the given user.
   * 
   * @param user
   *          - user
   * @return
   *    the instance of the assessment controller helper to use
   */
  private AssessmentControllerHelper init(HttpSession httpSession, String feedbackUserId,
      Authentication token) {
    
    AssessmentControllerHelper assessmentControllerHelper = assementControllerHelperMap
        .get(feedbackUserId);
    
    if (assessmentControllerHelper == null) {
      lockObj.lock();
      try {
        assessmentControllerHelper = assementControllerHelperMap.get(feedbackUserId);
        if (assessmentControllerHelper == null) {
          User feedbackuser = feedbackRepository.findByIdValidated(feedbackUserId);
          QuestionsFactory questionsFactory = abstractQuestionsFactory.getQuestionFactory(
              feedbackuser.getUserLocale(), QuestionFactoryType.ThirdPartyQuestion);
          assessmentControllerHelper = new AssessmentControllerHelper(feedbackuser,
              questionsFactory, true);
          assementControllerHelperMap.put(feedbackUserId, assessmentControllerHelper);
        }
      } catch (Exception e) {
        log.warn("Exception while creating the assessment controller helper !!!", e);
        throw new InvalidRequestException(
            "Exception while creating the assessment controller helper !!!");
      } finally {
        lockObj.unlock();
      }
    }
    return assessmentControllerHelper;
  }
}
