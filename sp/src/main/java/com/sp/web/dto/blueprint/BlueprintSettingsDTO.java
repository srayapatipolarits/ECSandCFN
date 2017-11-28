package com.sp.web.dto.blueprint;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Company;
import com.sp.web.model.blueprint.BlueprintSettings;

import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * @author vikram
 *
 *         The DTO class for BlueprintSettings.
 */
public class BlueprintSettingsDTO {

  private Map<String, Object> dataMap;

  private BaseCompanyDTO companyDTO;

  public BlueprintSettingsDTO(BlueprintSettings blueprintSettings, Company company) {
    this.setCompanyDTO(new BaseCompanyDTO(company));
    BeanUtils.copyProperties(blueprintSettings, this);
  }

  public BlueprintSettingsDTO(BlueprintSettings blueprintSettings) {
    BeanUtils.copyProperties(blueprintSettings, this);
  }
  
  public Map<String, Object> getDataMap() {
    return dataMap;
  }
  
  public void setDataMap(Map<String, Object> dataMap) {
    this.dataMap = dataMap;
  }
  public BaseCompanyDTO getCompanyDTO() {
    return companyDTO;
  }

  public void setCompanyDTO(BaseCompanyDTO companyDTO) {
    this.companyDTO = companyDTO;
  }


  
}
