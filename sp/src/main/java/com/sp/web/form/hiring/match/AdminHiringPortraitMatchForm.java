package com.sp.web.form.hiring.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class for the admin portrait match data, i.e. creating, updating etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminHiringPortraitMatchForm implements GenericForm<HiringPortrait> {
  
  private String id;
  private String name;
  private String description;
  private Set<String> tags;
  private Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap;
  private Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap;
  
  // fields for other actions like assign, company doc's etc.
  private String companyId;
  private String documentUrl;
  
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
  
  public Map<CategoryType, Map<String, PortraitDataMatch>> getCategoryDataMap() {
    return categoryDataMap;
  }
  
  public void setCategoryDataMap(Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap) {
    this.categoryDataMap = categoryDataMap;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getDocumentUrl() {
    return documentUrl;
  }

  public void setDocumentUrl(String documentUrl) {
    this.documentUrl = documentUrl;
  }

  public Map<CategoryPriority, BigDecimal> getCategoryPriorityWeightMap() {
    return categoryPriorityWeightMap;
  }

  public void setCategoryPriorityWeightMap(Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap) {
    this.categoryPriorityWeightMap = categoryPriorityWeightMap;
  }
  
  @Override
  public void validate() {
    Assert.hasText(name, "Name required.");
    Assert.hasText(description, "Description required.");
    Assert.notEmpty(categoryDataMap, "Portrait match data required.");
    categoryDataMap.values().forEach(p -> validate(p));
    Assert.notEmpty(categoryPriorityWeightMap, "Portrait match weight data required.");
  }

  private void validate(Map<String, PortraitDataMatch> categoryPortraitData) {
    Assert.notEmpty(categoryPortraitData, "Category portrait match data required.");
  }

  @Override
  public void validateUpdate() {
    validateGet();
    validate();
  }

  @Override
  public void validateGet() {
    Assert.hasText(id, "Id required.");
  }

  @Override
  public HiringPortrait create(User user) {
    HiringPortrait hiringPortrait = new HiringPortrait();
    update(user, hiringPortrait);
    hiringPortrait.setCreatedOn(hiringPortrait.getUpdatedOn());
    hiringPortrait.setCreatedBy(hiringPortrait.getUpdatedBy());
    return hiringPortrait;
  }

  @Override
  public void update(User user, HiringPortrait hiringPortrait) {
    hiringPortrait.setName(name);
    hiringPortrait.setDescription(description);
    hiringPortrait.setCategoryDataMap(categoryDataMap);
    hiringPortrait.setCategoryPriorityWeightMap(categoryPriorityWeightMap);
    if (CollectionUtils.isNotEmpty(tags)) {
      hiringPortrait.setTags(tags.stream().map(WordUtils::capitalizeFully)
          .collect(Collectors.toSet()));
    } else {
      Optional.ofNullable(hiringPortrait.getTags()).ifPresent(Set::clear);
    }
    
    hiringPortrait.setUpdatedOn(LocalDateTime.now());
    hiringPortrait.setUpdatedBy(new UserMarkerDTO(user));
  }

  public void validateAssign() {
    validateGet();
    Assert.hasText(companyId, "Company id required.");
  }

  public void validateDocumentUrl() {
    validateAssign();
    Assert.hasText(documentUrl, "Document URL required.");
  }
  
}
