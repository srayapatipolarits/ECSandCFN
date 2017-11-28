package com.sp.web.dto;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserMessage;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         This is the DTO class for the user bean to mask out password and other sensitive
 *         information.
 */
public class UserDTO extends BaseUserDTO {

  private static final long serialVersionUID = 392449875289369705L;
  
  /** unique id for the user, used in mongo db. */
  private String statusMessage = null;
  private List<GroupAssociation> groupAssociationList;
  private List<String> tagList;
  private Set<RoleType> roles;
  private String companyId;
  private String companyName;
  private boolean deactivated;
  private String createdOn;
  private List<UserMessage> messages;
  private Map<RangeType, String> personalityMap;
  
  /**
   * Default constructor.
   */
  public UserDTO() { 
    super();
  }

  /**
   * Constructor to create the UserDTO from the given user object.
   * 
   * @param user
   *          - user object
   */
  public UserDTO(User user) {
    super(user);
    BeanUtils.copyProperties(user, this);

    /* bean utils, does not copy arraylist */
    this.setRoles(user.getRoles());
    this.setTagList(user.getTagList());
    // setting the message to return
    this.statusMessage = MessagesHelper.getMessage(statusMessage);
    if (user.getCreatedOn() != null) {
      this.createdOn = MessagesHelper.formatDate(user.getCreatedOn());
    }
    
    // setting the personality
    AnalysisBean analysis = user.getAnalysis();
    if (analysis != null) {
      HashMap<RangeType, PersonalityBeanResponse> personality = analysis.getPersonality();
      personalityMap = new HashMap<RangeType, String>();
      addPersonality(personality, RangeType.Primary);
      addPersonality(personality, RangeType.UnderPressure);
    }
  }

  /**
   * Adding the given personality type.
   * 
   * @param personality
   *            - personality data map
   * @param type
   *            - personality type
   */
  private void addPersonality(HashMap<RangeType, PersonalityBeanResponse> personality,
      RangeType type) {
    personalityMap.put(type,
        MessagesHelper.getMessage(personality.get(type).getPersonalityType().toString()));
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  /**
   * @param roles
   *          the roles to set.
   */
  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }

  /**
   * @return 
   *      - the roles.
   */
  public Set<RoleType> getRoles() {
    return roles;
  }

  /**
   * Get the group association list.
   * 
   * @return
   *      the group association list
   */
  public List<GroupAssociation> getGroupAssociationList() {
    groupAssociationList = Optional.ofNullable(groupAssociationList).orElse(
        new ArrayList<GroupAssociation>());
    return groupAssociationList;
  }

  public void setGroupAssociationList(List<GroupAssociation> groupAssociationList) {
    this.groupAssociationList = groupAssociationList;
  }

  public List<String> getTagList() {
    return tagList;
  }

  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }

  public void addGroupAssociation(GroupAssociation ga) {
    getGroupAssociationList().add(ga);
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  
  public boolean isDeactivated() {
    return deactivated;
  }

  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }
  
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getCreatedOn() {
    return createdOn;
  }

  public List<UserMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<UserMessage> messages) {
    this.messages = messages;
  }

  public Map<RangeType, String> getPersonalityMap() {
    return personalityMap;
  }

  public void setPersonalityMap(Map<RangeType, String> personalityMap) {
    this.personalityMap = personalityMap;
  }
}
