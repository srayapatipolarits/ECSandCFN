package com.sp.web.controller.lndfeedback;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackListingDTO;
import com.sp.web.dto.lndfeedback.DevelopmentFeedbackRequestDTO;
import com.sp.web.form.lndfeedback.DevelopmentForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

/**
 * <code>DevelopmentFeedbackControllerHelper</code> is the helper for development feedback.
 * 
 * @author pradeepruhil
 *
 */
@Component
@Qualifier("developmentFeedbackControllerHelper")
public class DevelopmentFeedbackControllerHelper
    extends
    GenericControllerHelper<DevelopmentFeedback, 
                            DevelopmentFeedbackListingDTO, 
                            DevelopmentFeedbackDTO, 
                            DevelopmentForm, 
                            DevelopmentFeedbackFactory> {
  
  /**
   * Constructor initalizing the helper.
   * 
   * @param factory
   *          development feedback.
   */
  @Inject
  public DevelopmentFeedbackControllerHelper(
      @Qualifier("developmentFeedback") DevelopmentFeedbackFactory factory) {
    super("developmentFeedback", factory);
  }
  
  /**
   * <code>getAllByDevFeedRefId</code> will return all the dev feeds.
   * 
   * @return the response.
   */
  public SPResponse getAllByDevFeedRefId(User user, Object[] params) {
    String devFeedRefId = (String) params[0];
    SPFeature spFeature = (SPFeature) params[1];
    Assert.hasText(devFeedRefId, "Invalid request");
    Assert.notNull(spFeature, "Invalid request");
    return new SPResponse().add(moduleName + "Listing",
        factory.getAllByDevFeedRefId(user, devFeedRefId, spFeature));
  }
  
  /**
   * email feedback will email the all the email feedback or single feedback to the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the development form.
   * @return the response.
   */
  public SPResponse emailFeedbacks(User user, Object[] params) {
    
    DevelopmentForm form = (DevelopmentForm) params[0];
    
    if (StringUtils.isNotBlank(form.getDevFeedRefId())) {
      Assert.notNull(form.getSpFeature(), "Invalid Request, SPFeature is not present");
    } else {
      Assert.hasText(form.getId(), "Feedback Id is not prsent to email single feedback.");
    }
    factory.emailFeedbacks(user, form);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>getAllFeedbackRequest</code> method will return all the feedback request of the user.
   * 
   * @param user
   *          feedback user.
   * @return the all the pending feedback request for the user.
   */
  public SPResponse getAllFeedbackRequest(User user, Object[] params) {
    
    HttpSession session = (HttpSession) params[0];
    String id = (String) params[1];
    List<DevelopmentFeedbackRequestDTO> allFeedbackRequest = factory.getAllFeedbackRequest(user);
    SPResponse response = new SPResponse();
    if (StringUtils.isBlank(id)) {
      id = (String) session.getAttribute("id");
    } 
    
    DevelopmentFeedbackDTO developmentFeedbackDTO = factory.get(user, id);
    response.add(moduleName + "listing", allFeedbackRequest);
    response.add(moduleName + "detail", developmentFeedbackDTO);
    return response;
  }
  
  /**
   * Get all the feedback responses for the given users.
   * 
   * @param user
   *          - user
   * @return
   *    the response for the get all request
   */
  public SPResponse getAllFeedbackResponses(User user) {
    final SPResponse resp = new SPResponse();
    return resp.add(moduleName + "ResponseListing", factory.getAllUserFeedbackResponses(user));
  }
  
}
