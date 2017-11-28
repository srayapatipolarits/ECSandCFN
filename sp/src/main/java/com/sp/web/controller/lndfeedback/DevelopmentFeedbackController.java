package com.sp.web.controller.lndfeedback;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackListingDTO;
import com.sp.web.form.lndfeedback.DevelopmentForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

/**
 * DevelopmentFeedbackController is the controller for the developmentn feedback request.
 * 
 * @author pradeepruhil
 *
 */
@Controller
@RequestMapping(value = "/developmentfeedback")
public class DevelopmentFeedbackController
    extends
    GenericController<DevelopmentFeedback, 
                      DevelopmentFeedbackListingDTO,
                      DevelopmentFeedbackDTO, 
                      DevelopmentForm, 
                      DevelopmentFeedbackControllerHelper> {
  
  /**
   * Constructor initializing the helper.
   * 
   * @param helper
   *          for devleopment feedback controller.
   */
  @Inject
  public DevelopmentFeedbackController(
      @Qualifier("developmentFeedbackControllerHelper") DevelopmentFeedbackControllerHelper helper) {
    super(helper);
  }
  
  /**
   * <code>getAllByDevFeedRefId</code> will return all the development feedback request for the
   * passed ref id.
   * 
   * @param devFeedRefId
   *          to return all the development feedback request by feed ref id.
   * @param spFeature
   *          is the spfeature
   * @param authentication
   *          is the logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/getAllByDevFeedRefId", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllByDevFeedRefId(@RequestParam String devFeedRefId,
      @RequestParam SPFeature spFeature, Authentication authentication) {
    
    return ControllerHelper.process(helper::getAllByDevFeedRefId, authentication, devFeedRefId,
        spFeature);
  }
  
  /**
   * <code>getAllFeedbackRequest</code> will return all the development feedback request for
   * feedback user.
   * 
   * @param id
   *          to return all the development feedback request by feed ref id.
   * @param authentication
   *          is the logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/getAllFeedbackRequest", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllFeedbackRequest(@RequestParam(required = false) String id, Authentication authentication,
      HttpSession httpSession) {
    return ControllerHelper.process(helper::getAllFeedbackRequest,
        authentication, httpSession, id);
  }
  
  /**
   * Email Feedbacks will email the individual feedback or all the feedback to self.
   * 
   * @param developmentForm
   *          form containing whether to send
   * @param authentication
   *          is the logged in user.
   * @return the email feedbacks
   */
  @RequestMapping(value = "/emailFeedbacks", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse emailFeedbacks(@RequestBody DevelopmentForm developmentForm,
      Authentication authentication) {
    return ControllerHelper.process(helper::emailFeedbacks, authentication, developmentForm);
  }

  /**
   * Controller method to get all the feedback responses for the given user.
   * 
   * @param authentication
   *            - logged in user
   * @return
   *    the response to the get all request
   */
  @RequestMapping(value = "/getAllFeedbackResponses", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllFeedbackResponses(Authentication authentication) {
    return ControllerHelper.process(helper::getAllFeedbackResponses, authentication);
  }
  
  @RequestMapping(value = "/feedbackRequests")
  public String getAllFeedbackRequestView(
      @RequestParam(required = false) String id, Authentication authentication,
      HttpSession httpSession) {
    httpSession.setAttribute("id", id);
    return "submitRequestFeedbackLogged";
  }
  /**
   * View For Feedback Page.
   * 
   */
  @RequestMapping(value = "/home", method = RequestMethod.GET)
  public String validatefeedbackDataView(Authentication token,
      @RequestParam(required = false) String theme) {
    return "feedbackDataView";
  }  
}
