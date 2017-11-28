package com.sp.web.service.blueprint;

import com.sp.web.Constants;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.blueprint.BlueprintSettings;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.repository.blueprint.BlueprintRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author vikram
 *
 *         The factory class to provide functions to maintain blueprint settings for user.
 */
@Component
public class BlueprintFactory {
  
  @Autowired
  private BlueprintRepository blueprintRepository;
  
  /**
   * Get the blueprint settings for the given company id.
   * 
   * @param companyId
   *          - companyId id
   * @return the BlueprintSettings
   */
  @Cacheable(value = "blueprintSettings")
  public BlueprintSettings getBlueprintSettings(String companyId) {
    BlueprintSettings blueprintSettings = blueprintRepository
        .findBlueprintSettingsByCompanyId(companyId);
    if (blueprintSettings == null) {
      blueprintSettings = blueprintRepository
          .findBlueprintSettingsByCompanyId(Constants.BLUEPRINT_DEFAULT_COMPANY_ID);
      if (blueprintSettings == null) {
        blueprintSettings = new BlueprintSettings();
        blueprintSettings.optimizeBlueprint();
      } else {
        blueprintSettings.setId(null);
      }
    }
    return blueprintSettings;
  }
  
  /**
   * Update the blueprintSettings.
   * 
   * @param blueprintSettings
   *          - blueprintSettings to update
   */
  @CacheEvict(value = "blueprintSettings", allEntries = true)
  public void updateBlueprintSettings(BlueprintSettings blueprintSettings) {
    blueprintSettings.optimizeBlueprint();
    blueprintRepository.updateBlueprintSettings(blueprintSettings);
  }
  
  /**
   * Method to delete blueprint settings for given company and also to clear the cache.
   * 
   * @param companyId
   *          - company id
   */
  @CacheEvict(value = "blueprintSettings", key = "#companyId")
  public void deleteBlueprintSettings(String companyId) {
    blueprintRepository.deleteBlueprintSettings(companyId);
  }
  
  /**
   * Method to get all blueprint settings.
   *
   * @return the List
   */
  public List<BlueprintSettings> getAllBlueprintSettings() {
    return blueprintRepository.findAllBlueprintSettings();
  }

  /**
   * Validate the blueprint.
   * 
   * @param blueprint
   *            - blueprint
   * @param blueprintSettings
   *            - blueprint settings
   * @return
   *     true if blueprint valid else false
   */
  public boolean validateBlueprint(Blueprint blueprint, BlueprintSettings blueprintSettings) {
    final List<DSActionCategory> objectivesList = blueprint.getDevStrategyActionCategoryList();
    if (validateList(objectivesList,
        blueprintSettings.getMinObjectives(), blueprintSettings.getMaxObjectives())) {
      int minInitiatives = blueprintSettings.getMinInitiatives();
      int maxInitiatives = blueprintSettings.getMaxInitiatives();
      int minSuccessMeasures = blueprintSettings.getMinSuccessMeasures();
      int maxSuccessMeasures = blueprintSettings.getMaxSuccessMeasures();
      for (DSActionCategory objective : objectivesList) {
        final List<DSAction> initiativesList = objective.getActionList();
        if (validateList(initiativesList, minInitiatives, maxInitiatives)) {
          for (DSAction initiative : initiativesList) {
            if (!validateList(initiative.getActionData(), minSuccessMeasures, maxSuccessMeasures)) {
              return false;
            }
          }
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }
  
  /**
   * Validate the list for min and maximum counts.
   * 
   * @param listToValidate
   *          - list to validate
   * @param minCount
   *          - minimum count
   * @param maxCount
   *          - maximum count
   * @return true if valid else false
   */
  private boolean validateList(List<?> listToValidate, int minCount, int maxCount) {
    if (!CollectionUtils.isEmpty(listToValidate)) {
      int size = listToValidate.size();
      return (size >= minCount && size <= maxCount);
    }
    return false;
  }

  /**
   * Create or update a new blueprint backup object.
   * 
   * @param blueprintBackup
   *            - blueprint backup
   */
  public void updateBlueprintBackup(BlueprintBackup blueprintBackup) {
    blueprintRepository.updateBlueprintBackup(blueprintBackup);
  }

  /**
   * Remove the blueprint backup for the given blueprint id.
   * 
   * @param blueprintId
   *            - blueprint id
   */
  public void removeBlueprintBackupForBlueprintId(String blueprintId) {
    blueprintRepository.removeBlueprintBackupByBlueprintId(blueprintId);
  }

  /**
   * Get the blueprint backup for the given blueprint id.
   * 
   * @param blueprintId
   *        - blueprint id
   * @return
   *    the blueprint backup
   */
  public BlueprintBackup getBlueprintBackupFromBlueprintId(String blueprintId) {
    return blueprintRepository.getBlueprintBackupFromBlueprintId(blueprintId);
  }

  /**
   * Remove the given blueprint backup.
   * 
   * @param blueprintBackup
   *            - blueprint backup
   */
  public void removeBlueprintBackup(BlueprintBackup blueprintBackup) {
    blueprintRepository.removeBlueprintBackup(blueprintBackup);
  }
  
}
