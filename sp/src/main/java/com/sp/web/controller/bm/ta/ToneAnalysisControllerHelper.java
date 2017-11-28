package com.sp.web.controller.bm.ta;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.bm.ToneAnalyserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller helper method to process all controller requests.
 */
@Component
public class ToneAnalysisControllerHelper {
  
  @Autowired
  ToneAnalyserFactory toneAnalyserFactory;
  
  /**
   * Helper method to get all the tone analysis aggregate scores for the given company.
   * 
   * @param user
   *          - user
   * @param params
   *          - company id
   * @return the response to the get all request
   */
  public SPResponse getAll(User user, Object[] params) {
    String companyId = (String) params[0];
    return new SPResponse().add(Constants.PARAM_TONE_ANALYSIS_AGGREGATE,
        toneAnalyserFactory.getAll(companyId));
  }

  /**
   * Helper method to get all user tone analysis records.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the get all request.
   */
  public SPResponse getAllUserToneAnalysis(User user, Object[] params) {
    String userId = (String) params[0];
    return new SPResponse().add(Constants.PARAM_TONE_ANALYSIS_DETAILS,
        toneAnalyserFactory.getAllUserToneAnalysis(userId));
  }
  
}
