package com.sp.web.controller.hiring.match;

import com.sp.web.form.hiring.match.HiringPortraitMatchForm;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller helper for hiring portrait match controller.
 */
@Component
public class HiringPortraitMatchControllerHelper {
  
  private static final String MODULE_NAME = "portraitMatch";
  
  @Autowired
  HiringPortraitMatchFactory factory;
  
  /**
   * Helper method to get all the portraits for the given company.
   * 
   * @param user
   *          - user
   * @return
   *      the response to the get all request
   */
  public SPResponse getAll(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    final boolean basePortrait = (boolean)params[0];
    return resp.add(MODULE_NAME + "Listing", (basePortrait) ? factory.getAllBasicPortrait(user)
        : factory.getAll(user));
  }
  
  /**
   * Helper method to get details of the portrait for the given company.
   * 
   * @param user
   *          - user
   * @return
   *      the response to the get all request
   */
  public SPResponse get(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String portraitId = (String) params[0];
    Assert.hasText(portraitId, "Portrait id required.");
    
    return resp.add(MODULE_NAME, factory.get(user, portraitId));
  }
  
  /**
   * Helper method for the match portrait request.
   * 
   * @param user
   *          - user
   * @return
   *      the response to the match portrait request
   */
  public SPResponse matchPortrait(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringPortraitMatchForm form = (HiringPortraitMatchForm) params[0];
    form.validate();
    return resp.add(MODULE_NAME, factory.processMatch(user, form));
  }  
}
