package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.TokenType;
import com.sp.web.service.token.TokenRequest;

import org.springframework.util.Assert;

public class PortraitShareTokenRequest extends TokenRequest {

  /**
   * Constructor.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public PortraitShareTokenRequest(FeedbackUser feedbackUser) {
    super(TokenType.PERPETUAL);
    final String feedbackUserId = feedbackUser.getId();
    Assert.notNull(feedbackUserId, "Feedback user id required.");
    getParamsMap().put(Constants.PARAM_FEEDBACK_USERID, feedbackUserId);
  }
  
}
