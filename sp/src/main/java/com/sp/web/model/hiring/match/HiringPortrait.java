package com.sp.web.model.hiring.match;

import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.user.UserMarkerDTO;

import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The Entity class for hiring portrait match information.
 */
public class HiringPortrait {
  
  private String id;
  private String name;
  private String description;
  private Set<String> tags;
  private LocalDateTime createdOn;
  private UserMarkerDTO createdBy;
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  private Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap;
  private Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap;
  private Set<String> companyIds;
  private Map<String, String> companyDocMap;
  
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
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Set<String> getTags() {
    return tags;
  }
  
  public void setTags(Set<String> tags) {
    this.tags = tags;
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
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  public Map<CategoryType, Map<String, PortraitDataMatch>> getCategoryDataMap() {
    return categoryDataMap;
  }
  
  public void setCategoryDataMap(Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap) {
    this.categoryDataMap = categoryDataMap;
  }
  
  public Set<String> getCompanyIds() {
    return companyIds;
  }
  
  public void setCompanyIds(Set<String> companyIds) {
    this.companyIds = companyIds;
  }
  
  public Map<String, String> getCompanyDocMap() {
    return companyDocMap;
  }
  
  public void setCompanyDocMap(Map<String, String> companyDocMap) {
    this.companyDocMap = companyDocMap;
  }
  
  public Map<CategoryPriority, BigDecimal> getCategoryPriorityWeightMap() {
    return categoryPriorityWeightMap;
  }

  public void setCategoryPriorityWeightMap(Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap) {
    this.categoryPriorityWeightMap = categoryPriorityWeightMap;
  }
  
  /**
   * Add the given company id to the list of company id's.
   * 
   * @param companyId
   *          - company id to add
   * @return 
   *    true if added else false
   */
  public boolean addCompany(String companyId) {
    if (companyIds == null) {
      companyIds = new HashSet<String>();
    }
    return companyIds.add(companyId);
  }
  
  /**
   * Remove the given company id from the list of company ids.
   * 
   * @param companyId
   *          - company id
   * @return
   *    true if removed else false
   */
  public boolean removeCompany(String companyId) {
    if (companyIds != null) {
      if (companyIds.remove(companyId)) {
        if (companyDocMap != null) {
          companyDocMap.remove(companyId);
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * Add the document URL for the given company.
   * 
   * @param companyId
   *          - company id
   * @param documentUrl
   *          - document URL
   */
  public void addDocumentUrl(String companyId, String documentUrl) {
    if (companyDocMap == null) {
      companyDocMap = new HashMap<String, String>();
    }
    companyDocMap.put(companyId, documentUrl);
  }

  /**
   * Remove the document URL associated with the company.
   * 
   * @param companyId
   *            - company id
   * @return
   *    true if removed else false
   */
  public boolean removeDocumentUrl(String companyId) {
    if (companyDocMap != null) {
      return companyDocMap.remove(companyId) != null;
    }
    return false;
  }
  
  /**
   * Get the document URL for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the document URL
   */
  public String getCompanyDocumentUrl(String companyId) {
    if (companyDocMap != null) {
      return companyDocMap.get(companyId);
    }
    return null;
  }

  /**
   * Get the portrait match category weight to apply.
   *  
   * @param categoryPriority
   *          - category priority
   * @return
   *    the weight
   */
  public BigDecimal getCategoryPriorityWeight(CategoryPriority categoryPriority) {
    return categoryPriorityWeightMap.get(categoryPriority);
  }
  
  /**
   * Check if the company is allowed access to the given portrait.
   * 
   * @param companyId
   *          - company id
   * @return
   *    true if allowed else false
   */
  public boolean isCompanyAllowed(String companyId) {
    if (CollectionUtils.isNotEmpty(companyIds)) {
      return companyIds.contains(companyId);
    }
    return false;
  }
  
}
