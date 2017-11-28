/**
 * 
 */
package com.sp.web.form;

import com.sp.web.model.UserType;

import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author pradeepruhil
 *
 */
public class SystemAdminForm {

  private UserType userType;

  private String email;

  private String companyId;

  private String feedbackFor;

  /**
   * @return the userType
   */
  public UserType getUserType() {
    return userType;
  }

  /**
   * @param userType
   *          the userType to set
   */
  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *          the email to set
   */
  public void setEmail(String email) {
    Optional.ofNullable(email).ifPresent(e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }

  /**
   * @return the companyId
   */
  public String getCompanyId() {
    return companyId;
  }

  /**
   * @param companyId
   *          the companyId to set
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * @param feedbackFor
   *          the feedbackFor to set
   */
  public void setFeedbackFor(String feedbackFor) {
    this.feedbackFor = feedbackFor;
  }

  /**
   * @return the feedbackFor
   */
  public String getFeedbackFor() {
    return feedbackFor;
  }
}