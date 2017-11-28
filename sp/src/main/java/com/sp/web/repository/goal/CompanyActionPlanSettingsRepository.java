package com.sp.web.repository.goal;

import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * @author Dax Abraham
 * 
 *         The repository interface for company action plan settings repository.
 */
public interface CompanyActionPlanSettingsRepository extends
    GenericMongoRepository<CompanyActionPlanSettings> {

  /**
   * Get the company action plan id for the given action plan id and company id.
   * 
   * @param apId
   *          - action plan id
   * @param companyId
   *          - company id
   * @return
   *    the company action plan settings
   */
  CompanyActionPlanSettings findByCompanyIdAndActionPlanId(String apId, String companyId);
  
}
