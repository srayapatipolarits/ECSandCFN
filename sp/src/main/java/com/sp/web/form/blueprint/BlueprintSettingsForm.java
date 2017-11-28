package com.sp.web.form.blueprint;

import com.sp.web.model.blueprint.BlueprintSettings;

import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * @author vikram
 *
 *         The form to capture/update the details for the blueprint settings.
 */
public class BlueprintSettingsForm {
  
  private Map<String, Object> dataMap;
  private String companyId;
  
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
  
  public BlueprintSettings newBlueprint() {
    BlueprintSettings blueprintSettings = new BlueprintSettings();
    return updateBlueprint(blueprintSettings);
  }
  
  public BlueprintSettings updateBlueprint(BlueprintSettings blueprintSettings) {
    BeanUtils.copyProperties(this, blueprintSettings);
    return blueprintSettings;
  }
  
}
