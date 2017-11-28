package com.sp.web.dto;

import com.sp.web.model.GrowthRequest;
import com.sp.web.model.RequestType;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * GrowthRequestSummary contains the summary for growth request.
 * 
 * @author pradeepruhil
 */
public class GrowthRequestSummaryDTO {

  private BaseUserDTO user;

  private String id;

  private String requestStatus;

  private RequestType requestType;

  private boolean allGoalsSentForRequest;

  /**
   * Constructor for growthRequestSummay
   * 
   * @param growthRequest
   *          growth Request
   * @param memberUser
   *          user member user.
   */
  public GrowthRequestSummaryDTO(GrowthRequest growthRequest, User memberUser) {
    BeanUtils.copyProperties(growthRequest, this);
    user = new BaseUserDTO();
    user.load(memberUser);
    this.setRequestStatus(MessagesHelper.getMessage("growthStatus." + growthRequest.getRequestStatus().toString()));
  }

  public BaseUserDTO getUser() {
    return user;
  }

  public void setUser(BaseUserDTO user) {
    this.user = user;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRequestStatus() {
    return requestStatus;
  }

  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public boolean isAllGoalsSentForRequest() {
    return allGoalsSentForRequest;
  }

  public void setAllGoalsSentForRequest(boolean allGoalsSentForRequest) {
    this.allGoalsSentForRequest = allGoalsSentForRequest;
  }

}
