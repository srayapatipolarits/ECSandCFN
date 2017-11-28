package com.sp.web.model.blueprint;

import com.sp.web.Constants;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vikram
 *
 *         The Blueprint settings that are configured by admin.
 */

public class BlueprintSettings implements Serializable {

  private static final long serialVersionUID = 6695754084626379054L;
  
  private String id;
  private Map<String, Object> dataMap;
  private String companyId;
  private int minObjectives;
  private int maxObjectives;
  private int minInitiatives;
  private int maxInitiatives;
  private int minSuccessMeasures;
  private int maxSuccessMeasures;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Map<String, Object> getDataMap() {
    return dataMap;
  }
  
  public void setDataMap(Map<String, Object> dataMap) {
    this.dataMap = dataMap;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public int getMinObjectives() {
    return minObjectives;
  }

  public void setMinObjectives(int minObjectives) {
    this.minObjectives = minObjectives;
  }

  public int getMaxObjectives() {
    return maxObjectives;
  }

  public void setMaxObjectives(int maxObjectives) {
    this.maxObjectives = maxObjectives;
  }

  public int getMinInitiatives() {
    return minInitiatives;
  }

  public void setMinInitiatives(int minInitiatives) {
    this.minInitiatives = minInitiatives;
  }

  public int getMaxInitiatives() {
    return maxInitiatives;
  }

  public void setMaxInitiatives(int maxInitiatives) {
    this.maxInitiatives = maxInitiatives;
  }

  public int getMinSuccessMeasures() {
    return minSuccessMeasures;
  }

  public void setMinSuccessMeasures(int minSuccessMeasures) {
    this.minSuccessMeasures = minSuccessMeasures;
  }

  public int getMaxSuccessMeasures() {
    return maxSuccessMeasures;
  }

  public void setMaxSuccessMeasures(int maxSuccessMeasures) {
    this.maxSuccessMeasures = maxSuccessMeasures;
  }
  
  /**
   * Method to optimize the blueprint data.
   */
  @SuppressWarnings("unchecked")
  public void optimizeBlueprint() {
    if (CollectionUtils.isEmpty(dataMap)) {
      dataMap = new HashMap<String, Object>();
    }
    
    // updating the objectives data
    Map<String, Object> objectivesMap = (Map<String, Object>) dataMap
        .get(Constants.BLUEPRINT_OBJECTIVES);
    if (objectivesMap != null) {
      minObjectives = (int) objectivesMap.getOrDefault(Constants.BLUEPRINT_MIN_OBJECTIVES,
          Constants.BLUEPRINT_DEFAULT_MIN);
      maxObjectives = (int) objectivesMap.getOrDefault(Constants.BLUEPRINT_MAX_OBJECTIVES,
          Constants.BLUEPRINT_DEFAULT_MAX);
    } else {
      minObjectives = Constants.BLUEPRINT_DEFAULT_MIN;
      maxObjectives = Constants.BLUEPRINT_DEFAULT_MAX;
    }
    
    // updating the initiatives data
    Map<String, Object> intiativesMap = (Map<String, Object>) dataMap
        .get(Constants.BLUEPRINT_INITIATIVES);
    if (intiativesMap != null) {
      minInitiatives = (int) intiativesMap.getOrDefault(Constants.BLUEPRINT_MIN_INITIATIVES,
          Constants.BLUEPRINT_DEFAULT_MIN);
      maxInitiatives = (int) intiativesMap.getOrDefault(Constants.BLUEPRINT_MAX_INITIATIVES,
          Constants.BLUEPRINT_DEFAULT_MAX);
    } else {
      minInitiatives = Constants.BLUEPRINT_DEFAULT_MIN;
      maxInitiatives = Constants.BLUEPRINT_DEFAULT_MAX;
    }
    
    // updating the success measure data
    Map<String, Object> successMeasureMap = (Map<String, Object>) dataMap
        .get(Constants.BLUEPRINT_SUCCESS_MEASURES);
    if (successMeasureMap != null) {
      minSuccessMeasures = (int) successMeasureMap.getOrDefault(
          Constants.BLUEPRINT_MIN_SUCCESS_MEASURES, Constants.BLUEPRINT_DEFAULT_MIN);
      maxSuccessMeasures = (int) successMeasureMap.getOrDefault(
          Constants.BLUEPRINT_MAX_SUCCESS_MEASURES, Constants.BLUEPRINT_DEFAULT_MAX);
    } else {
      minSuccessMeasures = Constants.BLUEPRINT_DEFAULT_MIN;
      maxSuccessMeasures = Constants.BLUEPRINT_DEFAULT_MAX;
    }
  }
}
