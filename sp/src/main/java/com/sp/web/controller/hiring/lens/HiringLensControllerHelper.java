package com.sp.web.controller.hiring.lens;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.hiring.lens.HiringLensDTO;
import com.sp.web.dto.hiring.lens.HiringLensListingDTO;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.lens.HiringLensFactory;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The helper class for the hiring lens controller.
 */
@Component
public class HiringLensControllerHelper
    extends
    GenericControllerHelper<FeedbackUser, HiringLensListingDTO, HiringLensDTO, HiringLensForm, HiringLensFactory> {
  
  private static final String MODULE_NAME = "hiringLens";
  
  /**
   * Constructor.
   * 
   * @param factory
   *          - factory
   */
  @Inject
  public HiringLensControllerHelper(HiringLensFactory factory) {
    super(MODULE_NAME, factory);
  }
  
  /**
   * Get the details for the given instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getAllRequests(User user, Object[] params) {
    
    // get the id
    HiringLensForm form = (HiringLensForm) params[0];
    form.validateGetAllRequest();
    
    // sending the response
    return new SPResponse().add(moduleName + "Listing", factory.getAllRequests(user, form));
  }
  
  /**
   * Helper method to send the lens request.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the send request
   */
  public SPResponse sendRequest(User user, Object[] params) {
    
    // get the id
    HiringLensForm form = (HiringLensForm) params[0];
    form.validateGet();
    
    // sending the response
    return new SPResponse().add(moduleName, factory.sendRequest(user, form));
  }  
  
}
