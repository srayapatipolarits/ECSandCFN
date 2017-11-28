package com.sp.web.form.competency;

import com.sp.web.form.FeedbackUserForm;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The request form for all competency evaluation requests.
 */
public class CompetencyRequestForm {
  
  private FeedbackUserForm manager;
  private List<FeedbackUserForm> peerList;
  private String userForEmail;
  
  public FeedbackUserForm getManager() {
    return manager;
  }
  
  public void setManager(FeedbackUserForm manager) {
    this.manager = manager;
  }
  
  public List<FeedbackUserForm> getPeerList() {
    return peerList;
  }
  
  public void setPeerList(List<FeedbackUserForm> peerList) {
    this.peerList = peerList;
  }
  
  public String getUserForEmail() {
    return userForEmail;
  }
  
  public void setUserForEmail(String userForEmail) {
    this.userForEmail = userForEmail;
  }
}
