package com.sp.web.model;

import com.sp.web.form.FeedbackUserForm;
import com.sp.web.form.ReferencesForm;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 *
 *         The entity bean for providing feedback.
 */
public class FeedbackUser extends User {

  /**
   * Serial version.
   */
  private static final long serialVersionUID = 673843613001224697L;
  private String feedbackFor;
  private String referenceType;
  private String memberId;
  private FeatureType featureType;
  private String tokenId;

  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public FeedbackUser() {
    setType(UserType.Member);
    featureType = FeatureType.PrismLens;
  }
  
  /**
   * Constructor from user type and feature type.
   * 
   * @param type
   *          - user type
   * @param featureType
   *          - feature type
   */
  public FeedbackUser(UserType type, FeatureType featureType) {
    setType(type);
    setFeatureType(featureType);
    addRole(RoleType.FeedbackUser);
  }

  /**
   * Constructor.
   * 
   * @param rif
   *          - reference form
   */
  public FeedbackUser(ReferencesForm rif) {
    BeanUtils.copyProperties(rif, this);
    addRole(RoleType.FeedbackUser);
    setUserStatus(UserStatus.INVITATION_NOT_SENT);
    featureType = FeatureType.PrismLens;
    setCreatedOn(LocalDate.now());
  }

  /**
   * Constructor from hiring tool for candidate and employee lens.
   * 
   * @param rif
   *        - reference form
   * @param user
   *        - user
   */
  public FeedbackUser(HiringLensForm rif, HiringUser user) {
    BeanUtils.copyProperties(rif, this);
    featureType = FeatureType.PrismLensHiring;
    feedbackFor = user.getId();
    setType(user.getType());
    setCompanyId(user.getCompanyId());
  }

  public String getFeedbackFor() {
    return feedbackFor;
  }

  public void setFeedbackFor(String feedbackFor) {
    this.feedbackFor = feedbackFor;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  /**
   * Updates the data for the FB user from the user passed.
   * 
   * @param member
   *          - member
   */
  public void updateFromUser(User member) {
    setCreatedOn(LocalDate.now());
    setTitle(member.getTitle());
    setProfileImage(member.getProfileImage());
    setType(UserType.Member);
    this.memberId = member.getId();
  }
  
  /**
   * This method updates the feedback user from the information provided in the user object.
   * 
   * @param user
   *          - user object to copy the information from
   */
  public void updateFrom(User user) {
    updateFromUser(user);
    setFirstName(user.getFirstName());
    setLastName(user.getLastName());
    setEmail(user.getEmail());
    setGender(user.getGender());
    addRole(RoleType.FeedbackUser);
    setCompanyId(user.getCompanyId());
    setProfileSettings(user.getProfileSettings());
    setUserTodoRequestsId(user.getUserTodoRequestsId());
  }

  /**
   * Update the data of the feedback user from the form.
   * 
   * @param feedbackUserForm
   *            - feedback user form
   */
  public void updateFrom(FeedbackUserForm feedbackUserForm) {
    String firstName = feedbackUserForm.getFirstName();
    if (!StringUtils.isEmpty(firstName)) {
      setFirstName(firstName);
    }
    
    String lastName = feedbackUserForm.getLastName();
    if (!StringUtils.isEmpty(lastName)) {
      setLastName(lastName);
    }
  }
  
  /**
   * Create a new feedback user from the form provided.
   * 
   * @param referencesForm
   *            - the form
   * @return
   *      the newly created external feedback user 
   */
  public static FeedbackUser newExternalUser(ReferencesForm referencesForm) {
    FeedbackUser feedbackUser = new FeedbackUser(referencesForm);
    feedbackUser.setUserStatus(UserStatus.INVITATION_SENT);
    feedbackUser.setType(UserType.External);
    return feedbackUser;
  }

  public FeatureType getFeatureType() {
    return featureType;
  }

  public void setFeatureType(FeatureType featureType) {
    this.featureType = featureType;
  }

  public String getTokenId() {
    return tokenId;
  }

  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }

  /**
   * Update the token information from the given token.
   * 
   * @param token
   *          - token
   */
  public void saveTokenInformation(Token token) {
    setTokenUrl(token.getTokenUrl());
    setTokenId(token.getTokenId());
  }

  /**
   * Method to update the data from the token.
   * 
   * @param token
   *          - token
   */
  public void updateFromToken(Token token) {
    this.tokenId = token.getId();
    setTokenUrl(token.getTokenUrl());
  }
  
  public FeedbackUser getUpdatedUser(UserFactory userFactory) {
    return userFactory.getFeedbackUser(getId());
  }
  
}
