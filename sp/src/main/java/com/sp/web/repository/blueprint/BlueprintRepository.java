package com.sp.web.repository.blueprint;

import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.blueprint.BlueprintSettings;

import java.util.List;

/**
 * @author vikram
 *
 *         The Blueprint repository interface.
 */
public interface BlueprintRepository {

  /**
   * <code>findAllBlueprintSettings</code> method will return the all the blueprint settings for the
   * all the companies
   *           
   * @return the list of BlueprintSettings.
   */
  List<BlueprintSettings> findAllBlueprintSettings();
  
 
  /**
   * <code>updateBlueprintSettings</code> method will update the blueprint settings.
   * 
   * @param blueprintSettings
   *          update the blueprint settings.
   */
  void updateBlueprintSettings(BlueprintSettings blueprintSettings);

  /**
   * <code>findBlueprintSettingsById</code> method will return the all the blueprint settings for id
   * 
   * @param blueprintSettingsId
   *          
   * @return BlueprintSettingst.
   */
  BlueprintSettings findBlueprintSettingsById(String blueprintSettingsId);

  /**
   * <code>findBlueprintSettingsByCompanyId</code> method will return the all the blueprint settings for company id
   * 
   * @param companyId
   *          
   * @return BlueprintSettings.
   */
  BlueprintSettings findBlueprintSettingsByCompanyId(String companyId);
  
  /**
   * Delete the blueprint settings for the given company id.
   * 
   * @param companyId
   *          - company id
   *          
   * @return
   *    the number of records affected         
   */
  int deleteBlueprintSettings(String companyId);

  /**
   * Method to create or update blueprint backup.
   * 
   * @param blueprintBackup
   *          - blueprint backup object
   */
  void updateBlueprintBackup(BlueprintBackup blueprintBackup);


  /**
   * Remove the blueprint backup for the given blueprint id.
   * 
   * @param blueprintId
   *            - blueprint id
   * @return
   *    count of records update
   */
  int removeBlueprintBackupByBlueprintId(String blueprintId);

  /**
   * The method to get the blueprint backup for the given blueprint id.
   * 
   * @param blueprintId
   *            - blueprint id
   * @return
   *    the blueprint backup
   */
  BlueprintBackup getBlueprintBackupFromBlueprintId(String blueprintId);

  /**
   * Remove the given blueprint backup.
   * 
   * @param blueprintBackup
   *            - blueprint backup
   */
  void removeBlueprintBackup(BlueprintBackup blueprintBackup);

}
