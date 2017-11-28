package com.sp.web.mvc.assessment;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.Constants;
import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.AssessmentType;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 
 * @author daxabraham
 * 
 *         The Controller class for the assessment functionality.
 *
 */

@Controller
@Scope("session")
public class AssessmentController {
  
  private AssessmentControllerHelper helper;
  
  @Autowired
  AssessmentQuestionFactory abstractQuestionsFactory;
  
  /**
   * Get the assessment completed view.
   * 
   * @param token
   *          - logged in user
   * @return the view
   */
  @RequestMapping(value = "/assessment/completed", method = RequestMethod.GET)
  public String assessmentCompletedView(Authentication token) {
    
    User userFromAuthentication = GenericUtils.getUserFromAuthentication(token);
    if (userFromAuthentication.hasRole(RoleType.Hiring)) {
      return "assessmentCompletedPA";
    } else if (userFromAuthentication.hasRole(RoleType.PartnerCandidate)) {
      return "partnerThankYou";
    } else {
      return "assessmentCompleted";
    }
    
  }
  
  /**
   * The main assessment class.
   * 
   * @return the response
   */
  @RequestMapping(value = "/assessment", method = RequestMethod.GET)
  public String getAssessment(Authentication token, ModelMap modelMap) {
    // get the logged in user
    User user = GenericUtils.getUserFromAuthentication(token);
    if (user instanceof HiringUser) {
      modelMap.addAttribute(Constants.PARAM_HIRING_USER_ID, user.getId());
    }
    return "assessment";
  }
  
  /**
   * The main assessment class.
   * 
   * @return the response
   */
  @RequestMapping(value = "/assessment/start", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse start(@RequestParam(defaultValue = "Default") AssessmentType type,
      Authentication token) {
    
    init(token);
    return process(helper::start, token, type);
  }
  
  /**
   * Initialize the assessment helper.
   * 
   * @param token
   *          - logged in user
   */
  public void init(Authentication token) {
    User user = GenericUtils.getUserFromAuthentication(token);
    QuestionsFactory questionsFactory = abstractQuestionsFactory.getQuestionFactory(
        user.getUserLocale(), QuestionFactoryType.SPAssessmentQuestion);
    if (helper == null) {
      helper = new AssessmentControllerHelper(user, questionsFactory, false);
    }
  }
  
  /**
   * Method for processing the subsequent question requests.
   * 
   * @param questionNum
   *          - question number
   * @param ans
   *          - selected answer
   * @return the next question
   */
  @RequestMapping(value = "/assessment/process", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse processAssessment(@RequestParam int questionNum,
      @RequestParam List<String> ans, Authentication token) {
    
    if (helper == null) {
      init(token);
    }
    
    // process the validate the user email's from user controller helper
    return ControllerHelper.doProcess(helper::processAssessment, questionNum, ans);
  }
  
  /**
   * Processes the previous question and also resets the answer for the previous question.
   * 
   * @param token
   *          - logged in user
   * @return the response for the previous question request
   */
  @RequestMapping(value = "/assessment/previous", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse processPrev(Authentication token) {
    if (helper == null) {
      init(token);
    }
    return process(helper::processPrev, token);
  }
}
