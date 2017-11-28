package com.sp.web.controller.hiring.lens;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.hiring.lens.HiringLensDTO;
import com.sp.web.dto.hiring.lens.HiringLensListingDTO;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller class for the external lens requests.
 */
@Controller
@RequestMapping("/hiring/lens")
public class HiringLensController
    extends
    GenericController<FeedbackUser, HiringLensListingDTO, HiringLensDTO, HiringLensForm, HiringLensControllerHelper> {
  
  /**
   * Constructor.
   * 
   * @param helper
   *          - helper
   */
  @Inject
  public HiringLensController(HiringLensControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Controller method to get all requests for a given user.
   * 
   * @param token
   *          - user
   * @return the response to get all
   */
  @RequestMapping(value = "/getAllRequests", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllRequests(HiringLensForm form, Authentication token) {
    return process(helper::getAllRequests, token, form);
  }
 
  /**
   * Controller method to send the lens request for the reference.
   * 
   * @param token
   *          - user
   * @return the response to get all
   */
  @RequestMapping(value = "/sendRequest", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendRequest(HiringLensForm form, Authentication token) {
    return process(helper::sendRequest, token, form);
  }

  
}
