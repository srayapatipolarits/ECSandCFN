package com.sp.web.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.admin.group.GroupAssociationComparator;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.task.UserTask;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * 
 *         The User model bean.
 */
public class User implements Serializable {
  
  /**
   * default serial version id.
   */
  private static final long serialVersionUID = -7854420459165792692L;
  
  /**
   * The date when the user was created.
   */
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate createdOn;
  
  /** unique id for the user, used in mongo db. */
  private String id;
  
  /** First name. */
  private String firstName;
  
  /** Last name. */
  private String lastName;
  
  /** Users title (director/teamlead). */
  private String title;
  
  /** Users email. */
  private String email;
  
  /** password. */
  private String password;
  
  /** Users account. */
  private String accountId;
  
  /** Address. */
  private Address address;
  
  /** Phone number. */
  private String phoneNumber;
  
  /** Users status. */
  private UserStatus userStatus;
  
  /** Company id. */
  private String companyId;
  
  /** Assessment results bean. */
  private AnalysisBean analysis;
  
  /** User roles. */
  private Set<RoleType> roles;
  
  /** Gender of the user. */
  private Gender gender;
  
  /** DOB. */
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate dob;
  
  /** List of groups the user is associated to. */
  private ArrayList<GroupAssociation> groupAssociationList;
  
  /** List of Tags associated to the user. */
  private List<String> tagList;
  
  /** The profile setting for individual users. */
  private UserProfileSettings profileSettings;
  
  /** The linked in URL of the user. */
  private String linkedInUrl;
  
  private String userGoalId;
  
  private String profileImage;
  
  private String tokenUrl;
  
  private int imageCount;
  
  private String certificateNumber;
  
  private List<UserTask> taskList;
    
  private String userActionPlanId;
  
  private String blueprintId;
  
  private UserType type;
  
  private String competencyProfileId;
  
  private boolean deactivated;
  
  private String userNoteId;
  
  private String userGroupDiscussionId;
  
  private List<UserMessage> messages;
  
  private String userTodoRequestsId;
  
  private String userToneAnalysisId;
  
  private Password passwords;
  
  private LocalDateTime remindedOn;
  
  /**
   * Creating this field, to check whether roles in user is updated in session or not. This is
   * needed to update the authentication detail of the user in session, which can be done only with
   * having a request object. So for cases where comany updates features, the backed SSE doesn;t
   * have a request object to update the authenticationd detail. Adding this hack to fix the issue.
   */
  @Transient
  private boolean userUpdatedInSession;
  
  /**
   * Default constructor.
   */
  public User() {
  }
  
  /**
   * Create a user with the email passed.
   * 
   * @param email
   *          - user email
   * @param companyId
   *          - company id
   */
  public User(String email, String companyId) {
    this.email = email.toLowerCase();
    this.companyId = companyId;
    
    // setting the default status
    this.userStatus = UserStatus.PROFILE_INCOMPLETE;
    addRole(RoleType.User);
    createdOn = LocalDate.now();
  }
  
  /**
   * Constructor.
   * 
   * @param userMarker
   *          - user marker
   */
  public User(UserMarkerDTO userMarker) {
    BeanUtils.copyProperties(userMarker, this);
  }
  
  /**
   * Add a user from the hiring user details.
   * 
   * @param hiringUser
   *          - hiring user data
   */
  public User(HiringUser hiringUser) {
    updateProfile(hiringUser);
    this.type = UserType.Member;
    this.userStatus = hiringUser.getUserStatus();
  }

  /**
   * Update the current users profile from the user.
   * 
   * @param user
   *          - user to update from
   */
  public void updateProfile(User user) {
    this.createdOn = LocalDate.now();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.title = user.getTitle();
    this.email = user.getEmail();
    this.phoneNumber = user.getPhoneNumber();
    this.address = user.getAddress();
    this.certificateNumber = user.getCertificateNumber();
    this.companyId = user.getCompanyId();
    this.dob = user.getDob();
    this.gender = user.getGender();
    this.linkedInUrl = user.getLinkedInUrl();
    this.analysis = user.getAnalysis();
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public Address getAddress() {
    return address;
  }
  
  public void setAddress(Address address) {
    this.address = address;
  }
  
  public String getPhoneNumber() {
    return phoneNumber;
  }
  
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  public UserStatus getUserStatus() {
    return userStatus;
  }
  
  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }
  
  public AnalysisBean getAnalysis() {
    return analysis;
  }
  
  public void setAnalysis(AnalysisBean analysis) {
    this.analysis = analysis;
  }
  
  /**
   * @return get the roles for the user.
   */
  public Set<RoleType> getRoles() {
    roles = Optional.ofNullable(roles).orElseGet(() -> {
      roles = new HashSet<RoleType>();
      return roles;
    });
    return roles;
  }
  
  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }
  
  public String getAccountId() {
    return accountId;
  }
  
  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
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
  
  public ArrayList<GroupAssociation> getGroupAssociationList() {
    return Optional.ofNullable(groupAssociationList).orElseGet(() -> {
      groupAssociationList = new ArrayList<GroupAssociation>();
      return groupAssociationList;
    });
  }
  
  public void setGroupAssociationList(ArrayList<GroupAssociation> groupAssociationList) {
    this.groupAssociationList = groupAssociationList;
  }
  
  public List<String> getTagList() {
    return tagList;
  }
  
  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }
  
  /**
   * Adds the group association to the user group associations.
   * 
   * @param groupAssociation
   *          - the group association to add
   */
  public void addGroupAssociation(GroupAssociation groupAssociation) {
    if (groupAssociationList == null) {
      groupAssociationList = new ArrayList<GroupAssociation>();
      
    } else {
      // removing any previous group association from the list
      // for the same group
      groupAssociationList.remove(groupAssociation);
    }
    // adding the new group association
    groupAssociationList.add(groupAssociation);
    groupAssociationList.sort(GroupAssociationComparator.comparator);
    updateGroupLeadRole();
  }
  
  /**
   * Method checks if the user is still a group lead and updates the user roles accordingly.
   */
  private void updateGroupLeadRole() {
    boolean isGroupLead = false;
    if (groupAssociationList != null) {
      for (GroupAssociation ga : groupAssociationList) {
        if (ga.isGroupLead()) {
          isGroupLead = true;
          break;
        }
      }
      // if group lead then add the role as group lead
      if (isGroupLead) {
        if (!hasRole(RoleType.GroupLead)) {
          getRoles().add(RoleType.GroupLead);
        }
        return;
      }
    }
    getRoles().remove(RoleType.GroupLead);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return email.hashCode();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof User)) {
      return false;
    }
    return email.equals(((User) obj).getEmail());
  }
  
  /**
   * Checks the equals of the given email string to the one set in the user object.
   * 
   * @param email
   *          - the email to check
   * @return true if same
   */
  public boolean equals(String emailToCheck) {
    return (emailToCheck == null) ? false : emailToCheck.equalsIgnoreCase(this.email);
  }
  
  /**
   * Removes the group association for the user.
   * 
   * @param groupAssociation
   *          - the group association to remove
   * @return true if the association was found else false
   */
  public boolean removeGroupAssociation(GroupAssociation groupAssociation) {
    boolean isRemoved = (groupAssociationList == null) ? false : groupAssociationList
        .remove(groupAssociation);
    
    // update the roles
    updateGroupLeadRole();
    return isRemoved;
  }
  
  /**
   * Adds the given role to the user role list.
   * 
   * @param role
   *          the role to add
   */
  public void addRole(RoleType role) {
    getRoles().add(role);
  }
  
  /**
   * Remove the role for the given user.
   * 
   * @param role
   *          role to remove
   */
  public void removeRole(RoleType role) {
    getRoles().remove(role);
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }
  
  /**
   * Checks if the user belongs to the same company as the current user.
   * 
   * @param user
   *          - user to check
   * @return true if they belong to same company
   */
  public boolean isSameCompany(User user) {
    if (user == null || companyId == null || !companyId.equalsIgnoreCase(user.getCompanyId())) {
      throw new InvalidRequestException("Unauthroized request !!!");
    }
    return true;
  }
  
  /**
   * Helper method to check the user role.
   * 
   * @param role
   *          - role to check
   * @return true if user has the role
   */
  public boolean hasRole(RoleType role) {
    return getRoles().contains(role);
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public UserProfileSettings getProfileSettings() {
    return Optional.ofNullable(profileSettings).orElseGet(() -> {
      profileSettings = new UserProfileSettings();
      profileSettings.updateToken();
      return profileSettings;
    });
  }
  
  public void setProfileSettings(UserProfileSettings profileSettings) {
    this.profileSettings = profileSettings;
  }
  
  public String getLinkedInUrl() {
    return linkedInUrl;
  }
  
  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }
  
  public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }
  
  public String getProfileImage() {
    return profileImage;
  }
  
  public String getTokenUrl() {
    return tokenUrl;
  }
  
  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }
  
  public void setImageCount(int imageCount) {
    this.imageCount = imageCount;
  }
  
  public int getImageCount() {
    return imageCount;
  }
  
  public String getUserGoalId() {
    return userGoalId;
  }
  
  public void setUserGoalId(String userGoalId) {
    this.userGoalId = userGoalId;
  }
  
  public String getCertificateNumber() {
    return certificateNumber;
  }
  
  public void setCertificateNumber(String certificateNumber) {
    this.certificateNumber = certificateNumber;
  }
  
  public List<UserTask> getTaskList() {
    return Optional.ofNullable(taskList).orElseGet(() -> {
      taskList = new ArrayList<UserTask>();
      return taskList;
    });
  }
  
  public void setTaskList(List<UserTask> taskList) {
    this.taskList = taskList;
  }
  
  /**
   * Helper method to check if the user has any of the given roles.
   * 
   * @param roleTypes
   *          - roles types
   * @return true if the user has any of the given roles
   */
  public boolean hasAnyRole(RoleType... roleTypes) {
    for (RoleType r : roleTypes) {
      if (hasRole(r)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Generate the key for the navigation caching.
   * 
   * @return the role key
   */
  public String getNavKey() {
    if (roles != null) {
      String navKey = roles.stream().sorted().map(RoleType::toString)
          .collect(Collectors.joining(":"));
      navKey = navKey.concat(getUserLocale());
      return navKey;
    }
    return null;
  }
  
  @Override
  public String toString() {
    return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
  }

  public String getUserActionPlanId() {
    return userActionPlanId;
  }
  
  public void setUserActionPlanId(String userActionPlanId) {
    this.userActionPlanId = userActionPlanId;
  }
  
  /**
   * Method to increment the image counter.
   */
  public void incrementImageCount() {
    imageCount++;
  }
  
  public String getBlueprintId() {
    return blueprintId;
  }
  
  public void setBlueprintId(String blueprintId) {
    this.blueprintId = blueprintId;
  }
  
  public UserType getType() {
    return type;
  }
  
  public void setType(UserType type) {
    this.type = type;
  }
  
  /**
   * Check whether the users group association matches the group association list passed.
   * 
   * @param groupAssociationListToCheck
   *          - group association to check
   * @return true if found else false
   */
  public boolean hasGroupAssociation(ArrayList<GroupAssociation> groupAssociationListToCheck) {
    if (groupAssociationListToCheck == null || groupAssociationList == null) {
      return false;
    }
    // check if the user's group association matches the list passed
    Optional<GroupAssociation> findFirst = groupAssociationList.stream()
        .filter(ga -> groupAssociationListToCheck.contains(ga)).findFirst();
    return findFirst.isPresent();
  }
  
  /**
   * Get email or first name.
   * 
   * @return the first name if present else email
   */
  @Transient
  public String getFirstNameOrEmail() {
    if (StringUtils.isEmpty(firstName)) {
      return getEmail();
    }
    return firstName;
  }
  
  /**
   * Get email or first name.
   * 
   * @return the first name if present else email
   */
  @Transient
  public String getFullNameOrEmail() {
    if (StringUtils.isEmpty(firstName)) {
      return getEmail();
    } else {
      return (firstName.concat(" " + lastName));
    }
  }
  
  public String getCompetencyProfileId() {
    return competencyProfileId;
  }
  
  public void setCompetencyProfileId(String competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }
  
  public boolean isDeactivated() {
    return deactivated;
  }
  
  public void addTask(UserTask userTask) {
    getTaskList().add(userTask);
  }
  
  public void setUserNoteId(String userNoteId) {
    this.userNoteId = userNoteId;
  }
  
  public String getUserNoteId() {
    return userNoteId;
  }
  
  public String getUserGroupDiscussionId() {
    return userGroupDiscussionId;
  }
  
  public void setUserGroupDiscussionId(String userGroupDiscussionId) {
    this.userGroupDiscussionId = userGroupDiscussionId;
  }
  
  /**
   * Add the message to the user.
   * 
   * @param feature
   *          - feature
   * @param message
   *          - message
   */
  public void addMessage(SPFeature feature, String message) {
    getMessages().add(new UserMessage(getNextUID(), feature, message));
  }
  
  /**
   * @return - gets a unique id for the user.
   */
  public String getNextUID() {
    if (id == null) {
      // action plan must be saved previously to get a ID
      throw new InvalidRequestException("Not initialized.");
    }
    return id + imageCount++;
  }
  
  /**
   * Get the messages list.
   * 
   * @return the messages list
   */
  public List<UserMessage> getMessages() {
    if (messages == null) {
      messages = new ArrayList<UserMessage>();
    }
    return messages;
  }
  
  public void setMessages(List<UserMessage> messages) {
    this.messages = messages;
  }
  
  public String getUserTodoRequestsId() {
    return userTodoRequestsId;
  }
  
  public void setUserTodoRequestsId(String userTodoRequestsId) {
    this.userTodoRequestsId = userTodoRequestsId;
  }
  
  public String getUserToneAnalysisId() {
    return userToneAnalysisId;
  }
  
  public void setUserToneAnalysisId(String userToneAnalysisId) {
    this.userToneAnalysisId = userToneAnalysisId;
  }
  
  public void setUserUpdatedInSession(boolean userUpdatedInSession) {
    this.userUpdatedInSession = userUpdatedInSession;
  }
  
  public boolean isUserUpdatedInSession() {
    return userUpdatedInSession;
  }
  
  @Transient
  public String getUserLocale() {
    return getProfileSettings().getLocale() != null ? getProfileSettings().getLocale().toString()
        : Constants.DEFAULT_LOCALE;
  }
  
  @Transient
  public Locale getLocale() {
    return getProfileSettings().getLocale() != null ? getProfileSettings().getLocale()
        : new Locale("en", "US");
  }
  
  public void setPasswords(Password passwords) {
    this.passwords = passwords;
  }
  
  public Password getPasswords() {
    return passwords;
  }

  public LocalDateTime getRemindedOn() {
    return remindedOn;
  }

  public void setRemindedOn(LocalDateTime remindedOn) {
    this.remindedOn = remindedOn;
  }

  public User getUpdatedUser(UserFactory userFactory) {
    return userFactory.getUser(id);
  }
  
  public UserProfileSettings profileSettings() {
    return profileSettings;
  }

}
