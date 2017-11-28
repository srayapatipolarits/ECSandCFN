package com.sp.web.form.competency;

import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.media.SPMediaPlaceholder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The form class for competencies.
 */
public class CompetencyProfileForm {
  private String id;
  private String name;
  private String description;
  private String companyId;
  private List<SPMediaPlaceholder> mediaList;
  private boolean active;
  private List<CompetencyForm> competencyList;
  private List<String> deletedCompetencyList;
  private RatingConfiguration ratingConfiguration; 
  
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
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public List<SPMediaPlaceholder> getMediaList() {
    return mediaList;
  }
  
  public void setMediaList(List<SPMediaPlaceholder> mediaList) {
    this.mediaList = mediaList;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public List<CompetencyForm> getCompetencyList() {
    return competencyList;
  }
  
  public void setCompetencyList(List<CompetencyForm> competencyList) {
    this.competencyList = competencyList;
  }
  
  public List<String> getDeletedCompetencyList() {
    return deletedCompetencyList;
  }
  
  public void setDeletedCompetencyList(List<String> deletedCompetencyList) {
    this.deletedCompetencyList = deletedCompetencyList;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Validate the form data.
   */
  public void validate() {
    Assert.hasText(name, "Name is required.");
    Assert.hasText(companyId, "Company id is required.");
    Assert.notEmpty(competencyList, "At least one competency required.");
    validateRatingConfiguration();
    competencyList.forEach(cf -> cf.validate(ratingConfiguration));
  }
  
  private void validateRatingConfiguration() {
    Assert.notNull(ratingConfiguration, "Rating configuration required.");
    Assert.notNull(ratingConfiguration.getType(), "Rating configuration type required.");
    Assert.isTrue(ratingConfiguration.getSize() >= 2, "Rating configuration size at least 2.");
  }

  /**
   * Add or update the competency profile from the data in the form.
   * 
   * @param competencyProfile
   *          - competency profile to update
   * @param competencyFactory 
   *          - goals repository to add update the goals
   */
  public void addUpdateCompetencyProfile(CompetencyProfileDao competencyProfile, CompetencyFactory competencyFactory) {
    // update the data for the competency profile
    BeanUtils.copyProperties(this, competencyProfile, "id", "competencyList", "deletedCompetencyList");
    if (CollectionUtils.isEmpty(competencyProfile.getCompetencyList())) {
      competencyProfile.setCompetencyList(new ArrayList<CompetencyDao>());
    }
    // create a new list to store the new order
    final List<CompetencyDao> finalCompetencyList = new ArrayList<CompetencyDao>();
    // get the existing competency list
    final List<CompetencyDao> existingCompetencyList = competencyProfile.getCompetencyList();
    // update the data for the competencies
    competencyList.stream().forEach(
        c -> addUpdatedCompetency(finalCompetencyList, existingCompetencyList, c, competencyProfile::getNextUID));
    // loop through all the competencies and update them to the DB
    finalCompetencyList.forEach(competencyFactory::updateCompetency);
    if (!CollectionUtils.isEmpty(deletedCompetencyList)) {
      // find out if there are any competencies to delete
      List<SPGoal> collect = existingCompetencyList.stream().filter(c -> deletedCompetencyList.contains(c.getId()))
          .collect(Collectors.toList());
      existingCompetencyList.removeAll(collect);
      Assert.notEmpty(existingCompetencyList, "Need at least one competency.");
      collect.forEach(competencyFactory::deleteCompetency);
    }
    // setting the new competency list
    competencyProfile.setCompetencyList(finalCompetencyList);
    // update the practice area id list
    competencyProfile.setCompetencyIdList(finalCompetencyList.stream().collect(
        Collectors.mapping(CompetencyDao::getId, Collectors.toList())));
  }
  
  /**
   * Update the data for the competencies.
   * 
   * @param finalCompetencyList
   *          - the final competency list
   * @param existingCompetencyList
   *          - competency profile to update
   * @param competencyToUpdate
   *          - competency to update
   * @param uidGenerator
   *          - the uid generator         
   */
  private void addUpdatedCompetency(List<CompetencyDao> finalCompetencyList,
      List<CompetencyDao> existingCompetencyList, CompetencyForm competencyToUpdate,
      Supplier<String> uidGenerator) {
    final String competencyId = competencyToUpdate.getId();
    CompetencyDao competency = null;
    if (StringUtils.isEmpty(competencyId)) {
      // check if it is already existing competency
      final String competencyName = competencyToUpdate.getName();
      Optional<CompetencyDao> findFirst = existingCompetencyList.stream()
          .filter(c -> c.getName().equals(competencyName)).findFirst();
      if (findFirst.isPresent()) {
        throw new InvalidRequestException("Competency with the same name already exists.");
      }
      // new competency
      competency = new CompetencyDao();
      competency.setCategory(GoalCategory.Competency);
      existingCompetencyList.add(competency);
    } else {
      Optional<CompetencyDao> findFirst = existingCompetencyList.stream()
          .filter(c -> c.getId().equals(competencyId)).findFirst();
      // find the competency to update
      if (!findFirst.isPresent()) {
        throw new InvalidRequestException("Competency not found.");
      }
      competency = findFirst.get();
    }
    competencyToUpdate.addUpdate(competency, uidGenerator);
    finalCompetencyList.add(competency);
  }

  public RatingConfiguration getRatingConfiguration() {
    return ratingConfiguration;
  }

  public void setRatingConfiguration(RatingConfiguration ratingConfiguration) {
    this.ratingConfiguration = ratingConfiguration;
  }
  
}
