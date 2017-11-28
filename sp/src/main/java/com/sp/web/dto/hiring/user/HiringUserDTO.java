package com.sp.web.dto.hiring.user;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.FeedbackUserDTO;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.SPLinkedInProfile;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.model.hiring.user.HiringComment;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the hiring candidates.
 */
public class HiringUserDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 8218320291973957778L;

  /** Gender of the user. */
  private Gender gender;

  /** DOB. */
  private LocalDate dob;

  /** List of Tags associated to the user. */
  private List<String> tagList;

  /* The hiring roles for the candidate */
  private Set<String> hiringRoles;
  /* Candidate public URL */
  private String url;
  /* The due date for the assessment */
  private LocalDate assessmentDueDate = null;
  /* 
   * If the user is already an individual sp user 
   * the sp id of the user.
   */
  private String spUserId;
  /* The reference list for the candidate */
  private Set<String> references;
  /* The comments for the candidate */
  private List<HiringComment> comments;
  /* The linked in profile of the candidate */
  private SPLinkedInProfile linkedInProfile;
  private List<FeedbackUserDTO> referencesDetails;
  private String phoneNumber;
  private String formattedAssessmentDueDate;
  private String linkedInUrl;
  /** The profile setting for individual users. */
  private UserProfileSettings profileSettings;
  
  private String createdOn;

  /**
   * Constructor.
   * 
   * @param user
   *          - hiring user
   */
  public HiringUserDTO(HiringUser user) {
    super(user);
    BeanUtils.copyProperties(user, this);
    formattedAssessmentDueDate = (assessmentDueDate != null) ? MessagesHelper.formatDate(assessmentDueDate,
        MessagesHelper.getMessage("sorting.date.format")) : null;
    if (user.getCreatedOn() != null) {
      this.createdOn = MessagesHelper.formatDate(user.getCreatedOn());
    }
  }


  public Set<String> getHiringRoles() {
    return hiringRoles;
  }


  public void setHiringRoles(Set<String> hiringRoles) {
    this.hiringRoles = hiringRoles;
  }


  public String getUrl() {
    return url;
  }


  public void setUrl(String url) {
    this.url = url;
  }


  public LocalDate getAssessmentDueDate() {
    return assessmentDueDate;
  }

  public String getFormattedAssessmentDueDate() {
    return formattedAssessmentDueDate;
  }

  public void setAssessmentDueDate(LocalDate assessmentDueDate) {
    this.assessmentDueDate = assessmentDueDate;
  }


  public String getSpUserId() {
    return spUserId;
  }


  public void setSpUserId(String spUserId) {
    this.spUserId = spUserId;
  }


  public Set<String> getReferences() {
    return references;
  }


  public void setReferences(Set<String> references) {
    this.references = references;
  }

  public SPLinkedInProfile getLinkedInProfile() {
    return linkedInProfile;
  }


  public void setLinkedInProfile(SPLinkedInProfile linkedInProfile) {
    this.linkedInProfile = linkedInProfile;
  }


  public void addReferencesDetails(FeedbackUser referenceUser) {
    getReferencesDetails().add(new FeedbackUserDTO(referenceUser));
  }


  public List<FeedbackUserDTO> getReferencesDetails() {
    return Optional.ofNullable(referencesDetails).orElseGet(() -> {
        referencesDetails = new ArrayList<FeedbackUserDTO>();
        return referencesDetails;
      });
  }


  public void setReferencesDetails(List<FeedbackUserDTO> referencesDetails) {
    this.referencesDetails = referencesDetails;
  }


  public String getPhoneNumber() {
    return phoneNumber;
  }


  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }


  public String getLinkedInUrl() {
    return linkedInUrl;
  }


  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }


  public UserProfileSettings getProfileSettings() {
    return profileSettings;
  }


  public void setProfileSettings(UserProfileSettings profileSettings) {
    this.profileSettings = profileSettings;
  }


  public List<String> getTagList() {
    return tagList;
  }


  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }


  public Gender getGender() {
    return gender;
  }


  public void setGender(Gender gender) {
    this.gender = gender;
  }


  public LocalDate getDob() {
    return dob;
  }


  public void setDob(LocalDate dob) {
    this.dob = dob;
  }
  
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getCreatedOn() {
    return createdOn;
  }


  public List<HiringComment> getComments() {
    return comments;
  }


  public void setComments(List<HiringComment> comments) {
    this.comments = comments;
  }
}
