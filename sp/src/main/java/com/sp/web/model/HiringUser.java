package com.sp.web.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.model.hiring.user.HiringComment;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.user.UserFactory;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The model to store the hiring candidate.
 */
public class HiringUser extends User {
  
  /**
   * Serial version uid.
   */
  private static final long serialVersionUID = -4920731524800125789L;
  /* The hiring roles for the candidate */
  private Set<String> hiringRoleIds;
  /* Candidate public URL */
  private List<SPMedia> urls;
  
  /* Candidate public URL */
  private List<SPMedia> profileUrls;
  
  /* The due date for the assessment */
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate assessmentDueDate;
  /*
   * If the user is already an individual sp user the sp id of the user.
   */
  private String spUserId;
  /* The comments for the candidate */
  private List<HiringComment> comments;
  /*
   * The id of the hiring coordinator who is responsible for the candidate.
   */
  private Set<String> hiringCoordinatorIds;
  private boolean hired;
  private boolean inErti;
  private boolean isThirdParty;
  
  public HiringUser() {
  }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - sp user
   */
  public HiringUser(User user) {
    setFirstName(user.getFirstName());
    setLastName(user.getLastName());
    setEmail(user.getEmail());
    setAddress(user.getAddress());
    setCompanyId(user.getCompanyId());
    setCreatedOn(Optional.ofNullable(user.getCreatedOn()).orElse(LocalDate.now()));
    setAnalysis(user.getAnalysis());
    setUserStatus(user.getUserStatus());
    setCertificateNumber(user.getCertificateNumber());
    setDob(user.getDob());
    setPhoneNumber(user.getPhoneNumber());
    setGender(user.getGender());
    setProfileImage(user.getProfileImage());
    setSpUserId(user.getId());
    setTagList(user.getTagList());
    setTitle(user.getTitle());
    setType(UserType.Member);
    addRole(RoleType.HiringEmployee);
  }
  
  public HiringUser(HiringUserArchive existingSPUser) {
    BeanUtils.copyProperties(existingSPUser, this);
  }
  
  public String getSpUserId() {
    return spUserId;
  }
  
  public void setSpUserId(String spUserId) {
    this.spUserId = spUserId;
  }
  
  public List<HiringComment> getComments() {
    return Optional.ofNullable(comments).orElseGet(() -> createNew());
  }
  
  private List<HiringComment> createNew() {
    comments = new ArrayList<HiringComment>();
    return comments;
  }
  
  public void setComments(List<HiringComment> comments) {
    this.comments = comments;
  }
  
  /**
   * Add the comment by the user.
   * 
   * @param user
   *          - user
   * @param comment
   *          - comment
   * @return the new comment created
   */
  public HiringComment addComment(User user, String comment) {
    final HiringComment newComment = new HiringComment(user, comment);
    getComments().add(0, newComment);
    return newComment;
  }
  
  public LocalDate getAssessmentDueDate() {
    return assessmentDueDate;
  }
  
  public void setAssessmentDueDate(LocalDate assessmentDueDate) {
    this.assessmentDueDate = assessmentDueDate;
  }
  
  public boolean removeHiringRole(String roleId) {
    return (!CollectionUtils.isEmpty(hiringRoleIds)) ? hiringRoleIds.remove(roleId) : false;
  }
  
  /**
   * Add the given role id to the roles for the user.
   * 
   * @param roleId
   *          - role id
   * @return true if role added else false
   */
  public boolean addHiringRole(String roleId) {
    if (hiringRoleIds == null) {
      hiringRoleIds = new HashSet<String>();
    }
    return hiringRoleIds.add(roleId);
  }
  
  public boolean addHiringRole(HiringRole role) {
    return addHiringRole(role.getId());
  }
  
  public Set<String> getHiringRoleIds() {
    return hiringRoleIds;
  }
  
  public void setHiringRoleIds(Set<String> hiringRoleIds) {
    this.hiringRoleIds = hiringRoleIds;
  }
  
  public Set<String> getHiringCoordinatorIds() {
    return hiringCoordinatorIds;
  }
  
  public void setHiringCoordinatorIds(Set<String> hiringCoordinatorIds) {
    this.hiringCoordinatorIds = hiringCoordinatorIds;
  }
  
  public boolean isHired() {
    return hired;
  }
  
  public void setHired(boolean hired) {
    this.hired = hired;
  }
  
  public List<SPMedia> getUrls() {
    return urls;
  }
  
  public void setUrls(List<SPMedia> urls) {
    this.urls = urls;
  }
  
  /**
   * Add the URL to the list of URL's for the user.
   * 
   * @param url
   *          - URL to add
   */
  public void addUrl(SPMedia url) {
    if (urls == null) {
      urls = new ArrayList<SPMedia>();
    }
    urls.add(url);
  }
  
  /**
   * Remove the given URL from the url's list.
   * 
   * @param url
   *          - URL to remove
   */
  public void removeUrl(String url) {
    if (urls != null) {
      urls.stream().filter(media -> media.getUrl().equals(url)).findFirst().ifPresent(urls::remove);
    }
  }
  
  public boolean isInErti() {
    return inErti;
  }
  
  public void setInErti(boolean inErti) {
    this.inErti = inErti;
  }
  
  public void setProfileUrls(List<SPMedia> profileUrls) {
    this.profileUrls = profileUrls;
  }
  
  public List<SPMedia> getProfileUrls() {
    return profileUrls;
  }
  
  /**
   * Add the URL to the list of profile URL's for the user.
   * 
   * @param url
   *          - URL to add
   */
  public void addProfileUrl(SPMedia url) {
    if (profileUrls == null) {
      profileUrls = new ArrayList<SPMedia>();
    }
    profileUrls.add(url);
  }
  
  /**
   * Add the URL to the list of profile URL's for the user.
   * 
   * @param url
   *          - URL to add
   */
  public void updateProfileUrl(SPMedia url, int index) {
    if (profileUrls == null) {
      profileUrls = new ArrayList<SPMedia>();
    }
    profileUrls.set(index, url);
  }
  
  /**
   * Remove the given profile URL from the url's list.
   * 
   * @param url
   *          - URL to remove
   */
  public void removeProfileUrl(String url) {
    if (profileUrls != null) {
      profileUrls.stream().filter(media -> media.getUrl().equals(url)).findFirst()
          .ifPresent(profileUrls::remove);
    }
  }
  
  /**
   * Set the flat whether the user is a third party user or not.
   * 
   * @param isThirdParty
   *          flag for third party.
   */
  public void setThirdParty(boolean isThirdParty) {
    this.isThirdParty = isThirdParty;
  }
  
  /**
   * Flag whether the user is third party or not.
   * 
   * @return whether hiringUser is a third party user or not.
   */
  public boolean isThirdParty() {
    return isThirdParty;
  }
  public HiringUser getUpdatedUser(UserFactory userFactory) {
    return userFactory.getHiringUser(getId());

  }
}
