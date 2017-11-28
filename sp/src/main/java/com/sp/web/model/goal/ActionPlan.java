package com.sp.web.model.goal;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.pc.PublicChannelHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The action plans that are configured in the system.
 */
public class ActionPlan implements Serializable {
  
  private static final long serialVersionUID = -431381072893661905L;
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private boolean active;
  private List<String> practiceAreaIdList;
  @Deprecated
  private String companyId;
  private int uidCount;
  @Deprecated
  private List<String> userIdList;
  private int actionCount;
  private boolean editAllowed;
  private ActionPlanType type;
  private boolean allCompanies;
  private boolean allMembers;
  private List<String> companyIds;
  private StepType stepType;
  private String createdByCompanyId;
  private LocalDateTime createdOn;
  private UserMarkerDTO createdBy;
  private LocalDateTime editedOn;
  private UserMarkerDTO editedBy;
  private LocalDateTime publishedOn;
  private UserMarkerDTO publishedBy;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public List<String> getPracticeAreaIdList() {
    return practiceAreaIdList;
  }
  
  public void setPracticeAreaIdList(List<String> practiceAreaList) {
    this.practiceAreaIdList = practiceAreaList;
  }
  
  @Deprecated
  public String getCompanyId() {
    return companyId;
  }
  
  @Deprecated
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
    
  public int getUidCount() {
    return uidCount;
  }
  
  public void setUidCount(int uidCount) {
    this.uidCount = uidCount;
  }
    
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  /**
   * Get the user id list.
   * 
   * @return get the user id list
   */
  @Deprecated
  public List<String> getUserIdList() {
    if (CollectionUtils.isEmpty(userIdList)) {
      synchronized (this) {
        if (CollectionUtils.isEmpty(userIdList)) {
          userIdList = new ArrayList<String>();
        }
      }
    }
    return userIdList;
  }
  
  @Deprecated
  public void setUserIdList(List<String> userIdList) {
    this.userIdList = userIdList;
  }
  
  public int getActionCount() {
    return actionCount;
  }
  
  public void setActionCount(int actionCount) {
    this.actionCount = actionCount;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public boolean isEditAllowed() {
    return editAllowed;
  }
  
  public void setEditAllowed(boolean editAllowed) {
    this.editAllowed = editAllowed;
  }
  
  public ActionPlanType getType() {
    return type;
  }
  
  public void setType(ActionPlanType type) {
    this.type = type;
  }
  
  public boolean isAllCompanies() {
    return allCompanies;
  }
  
  public void setAllCompanies(boolean allCompanies) {
    this.allCompanies = allCompanies;
  }
  
  public boolean isAllMembers() {
    return allMembers;
  }
  
  public void setAllMembers(boolean allMembers) {
    this.allMembers = allMembers;
  }
  
  public List<String> getCompanyIds() {
    return companyIds;
  }
  
  public void setCompanyIds(List<String> companyIds) {
    this.companyIds = companyIds;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public UserMarkerDTO getCreatedBy() {
    return createdBy;
  }
  
  public void setCreatedBy(UserMarkerDTO createdBy) {
    this.createdBy = createdBy;
  }
  
  public LocalDateTime getEditedOn() {
    return editedOn;
  }
  
  public void setEditedOn(LocalDateTime editedOn) {
    this.editedOn = editedOn;
  }
  
  public UserMarkerDTO getEditedBy() {
    return editedBy;
  }
  
  public void setEditedBy(UserMarkerDTO editedBy) {
    this.editedBy = editedBy;
  }
  
  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }
  
  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  
  public UserMarkerDTO getPublishedBy() {
    return publishedBy;
  }
  
  public void setPublishedBy(UserMarkerDTO publishedBy) {
    this.publishedBy = publishedBy;
  }
  
  /**
   * Creates a new practice area list if the existing one is not present.
   * 
   * @return the practice area list or a new list if none existed
   */
  public List<String> createOrGetPracticeAreaIdList() {
    if (practiceAreaIdList == null) {
      practiceAreaIdList = new ArrayList<String>();
    }
    return practiceAreaIdList;
  }
  
  /**
   * Gets the next UID.
   * 
   * @return the next UID
   */
  public String getNextUID() {
    if (id == null) {
      // action plan must be saved previously to get a ID
      throw new InvalidRequestException("Action plan not initialized.");
    }
    return id + uidCount++;
  }
  
  /**
   * Add the company to the given action plan.
   * 
   * @param companyId
   *          - company id
   * @param actionPlanFactory
   *          - action plan factory
   * @return the new company action plan settings
   */
  public synchronized CompanyActionPlanSettings addCompany(String companyId,
      ActionPlanFactory actionPlanFactory) {
    if (companyIds == null) {
      companyIds = new ArrayList<String>();
    }
    
    companyIds.add(companyId);
    return actionPlanFactory.createCompanyActionPlanSettings(companyId, this);
  }
  
  /**
   * Authorize the company for access to the given action plan.
   * 
   * @param companyId
   *          - company id
   * @return true if allowed else false
   */
  public boolean authorizeCompany(String companyId) {
    if (allCompanies) {
      return true;
    }
    
    if (!CollectionUtils.isEmpty(companyIds)) {
      return companyIds.contains(companyId);
    }
    
    if (StringUtils.equals(createdByCompanyId, companyId)) {
      return true;
    }
    
    return false;
  }
  
  public StepType getStepType() {
    return stepType;
  }
  
  public void setStepType(StepType stepType) {
    this.stepType = stepType;
  }
  
  public String getCreatedByCompanyId() {
    return createdByCompanyId;
  }
  
  public void setCreatedByCompanyId(String createdByCompanyId) {
    this.createdByCompanyId = createdByCompanyId;
  }
  
  public void reduceActionCount(int actionCount) {
    this.actionCount -= actionCount;
  }
  
  /**
   * Delete the practice area and also it's public channels.
   * 
   * @param practiceAreaId
   *          - practice area id
   * @param channelHelper
   *          - channel helper
   * @return true if deleted else false
   */
  public boolean removePracticeArea(String practiceAreaId, PublicChannelHelper channelHelper) {
    boolean isDeleted = false;
    if (!CollectionUtils.isEmpty(practiceAreaIdList)) {
      isDeleted = practiceAreaIdList.remove(practiceAreaId);
      if (isDeleted) {
        if (!CollectionUtils.isEmpty(companyIds)) {
          companyIds.forEach(c -> channelHelper.deletePublicChannel(c, practiceAreaId));
        }
      }
    }
    return isDeleted;
  }
  
  public boolean hasCompany(String companyId) {
    return (companyIds != null) ? companyIds.contains(companyId) : false;
  }
  
  /**
   * Remove the company id from the action plan.
   * 
   * @param companyId
   *          - company id
   */
  public void removeCompany(String companyId) {
    if (!CollectionUtils.isEmpty(companyIds)) {
      companyIds.remove(companyId);
    }
  }

  /**
   * Set the created+updated by and created+updated on.
   * 
   * @param user
   *        - user
   */
  public void updateCreatedBy(User user) {
    createdOn = editedOn = LocalDateTime.now();
    createdBy = editedBy = new UserMarkerDTO(user);
  }

  /**
   * Set the updated by and updated on.
   * 
   * @param user
   *        - user
   */
  public void updateEditedBy(User user) {
    editedOn = LocalDateTime.now();
    editedBy = new UserMarkerDTO(user);
  }

  /**
   * Set the published by and published on.
   * 
   * @param user
   *        - user
   */
  public void updatePublishedBy(User user) {
    publishedOn = LocalDateTime.now();
    publishedBy = new UserMarkerDTO(user);
  }
  
}
