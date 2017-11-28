/**
 * 
 */
package com.sp.web.model;

import com.sp.web.form.GrowthInviteForm;

/**
 * External user are the users which are not part of surepoeple and there details are fetched from
 * externa system like linkedin.
 * 
 * @author pradeep
 *
 */
public class ExternalUser extends User {

  /**
   * default serial version id.
   */
  private static final long serialVersionUID = 3616292802353507239L;

  private String howDoYouKnow;

  /**
   * Constructor
   */
  public ExternalUser() {
  }

  public ExternalUser(GrowthInviteForm growthInviteForm) {
    this.setFirstName(growthInviteForm.getFirstName());
    this.setLastName(growthInviteForm.getLastName());
    this.setEmail(growthInviteForm.getMemberEmail());
    this.howDoYouKnow = growthInviteForm.getHowDoYouKnow();
    this.getRoles().add(RoleType.User);
    this.getRoles().add(RoleType.GrowthExternal);
  }

  /**
   * @param howDoYouKnow
   *          the howDoYouKnow to set
   */
  public void setHowDoYouKnow(String howDoYouKnow) {
    this.howDoYouKnow = howDoYouKnow;
  }

  /**
   * @return the howDoYouKnow
   */
  public String getHowDoYouKnow() {
    return howDoYouKnow;
  }

}
