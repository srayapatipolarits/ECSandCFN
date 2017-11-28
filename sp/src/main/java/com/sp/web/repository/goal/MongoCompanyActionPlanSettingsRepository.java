package com.sp.web.repository.goal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the company action plan repository.
 */
@Repository
public class MongoCompanyActionPlanSettingsRepository extends
    GenericMongoRepositoryImpl<CompanyActionPlanSettings> implements
    CompanyActionPlanSettingsRepository {

  @Override
  public CompanyActionPlanSettings findByCompanyIdAndActionPlanId(String apId, String companyId) {
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).and(Constants.ENTITY_ACTION_PLAN_ID)
            .is(apId)), CompanyActionPlanSettings.class);
  }
  
}
