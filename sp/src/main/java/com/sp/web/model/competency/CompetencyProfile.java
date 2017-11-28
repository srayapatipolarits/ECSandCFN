package com.sp.web.model.competency;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.media.SPMediaPlaceholder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The entity class to store all the competency profiles.
 */
public class CompetencyProfile implements Serializable {
  
  private static final long serialVersionUID = 3366607226482418344L;
  private String id;
  private String name;
  private String companyId;
  private String description;
  private List<SPMediaPlaceholder> mediaList;
  private boolean active;
  private List<String> competencyIdList;
  private int uidCount;
  private RatingConfiguration ratingConfiguration;

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
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
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
  
  public List<String> getCompetencyIdList() {
    return competencyIdList;
  }
  
  public void setCompetencyIdList(List<String> competencyIdList) {
    this.competencyIdList = competencyIdList;
  }
  
  /**
   * Gets the next UID.
   * 
   * @return
   *      the next UID
   */
  public String getNextUID() {
    if (id == null) {
      // action plan must be saved previously to get a ID
      throw new InvalidRequestException("Action plan not initialized.");
    }
    return id + uidCount++;
  }

  public RatingConfiguration getRatingConfiguration() {
    return ratingConfiguration;
  }

  public void setRatingConfiguration(RatingConfiguration ratingConfiguration) {
    this.ratingConfiguration = ratingConfiguration;
  }
  
}
